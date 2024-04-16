package cn.apeto.geniusai.server.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.apeto.geniusai.sdk.OpenAiStreamClient;
import cn.apeto.geniusai.sdk.entity.chat.ChatChoice;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletionResponse;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.entity.embeddings.EmbeddingResponse;
import cn.apeto.geniusai.sdk.entity.embeddings.Item;
import cn.apeto.geniusai.sdk.utils.TikTokensUtil;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.entity.ItemPartition;
import cn.apeto.geniusai.server.entity.ItemResource;
import cn.apeto.geniusai.server.entity.ProductConsumeConfig;
import cn.apeto.geniusai.server.entity.ResourceVector;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.service.*;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.service.oss.UploadResult;
import cn.apeto.geniusai.server.utils.*;
import io.milvus.grpc.MutationResult;
import io.milvus.param.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/7/31 14:25
 */
@Slf4j
@Component
public class FileHandler {

    @Autowired
    private OpenAiStreamClient openAiStreamClient;
    @Autowired
    private ItemResourceService itemResourceService;
    @Autowired
    private ResourceVectorService resourceVectorService;

    @Autowired
    private ItemPartitionService itemPartitionService;
    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;
    @Autowired
    private ProductConsumeConfigService productConsumeConfigService;

    @Transactional(rollbackFor = Exception.class)
    public String updateFile(MultipartFile multipartFile, Long userId, Long knowledgeItemId, Long partitionId) {

        log.info("开始上传资源... uid:{} knowledgeItemId:{}", userId, knowledgeItemId);

        StringBuilder doc = new StringBuilder();
        String filePath;
        String fullUrl;
        String fileName;
        String originalFilename;
        Date now = new Date();
        File file;


        Integer productType = Constants.ProductTypeEnum.KNOWLEDGE.getType();
        ProductConsumeConfig consumeConfigServiceByType = productConsumeConfigService.getByType(productType);
        exchangeCardDetailService.checkConsume(userId, productType, consumeConfigServiceByType.getConsumeCurrency());

        try {
            originalFilename = multipartFile.getOriginalFilename();
            String[] filename = originalFilename.split("\\.");

            String prefix = DateUtil.format(now, DatePattern.PURE_DATETIME_PATTERN) + "_" + knowledgeItemId;
            String name = filename[filename.length - 1];
            String suffix = "." + name;
            fileName = prefix + suffix;
            file = File.createTempFile(prefix, suffix);
            multipartFile.transferTo(file);

            UploadResult uploadResult = OSSFactory.build().upload(FileUtil.getInputStream(file), fileName);
            filePath = uploadResult.getPath();
            fullUrl = uploadResult.getFullUrl();

            switch (name) {
                case "pdf":
                    PDDocument document = PDDocument.load(file);
                    doc = new StringBuilder(PDFUtils.readPDF(file));
                    file.deleteOnExit();
                    document.close();
                    break;
                case "txt":
                    // 文本
                    // 创建一个BufferedReader对象，用于逐行读取文件内容
                    try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
                        String line;
                        // 逐行读取文件内容并输出
                        while ((line = reader.readLine()) != null) {
                            if (StrUtil.isBlankIfStr(line)) {
                                // 过滤掉空行和仅包含空格的行
                                continue;
                            }
                            doc.append(line);
                        }
                    }
                    break;
                case "doc":
                    doc.append(DocReaderUtil.readDoc(file));
                    break;
                case "docx":
                    doc.append(DocReaderUtil.readDocx(file));
                    break;
                default:
                    throw new RuntimeException("不支持");
            }


        } catch (IOException e) {
            throw new RuntimeException("上传文件异常:" + e.getMessage());
        }

        if (StrUtil.isBlank(doc.toString())) {
            throw new ServiceException(Constants.ResponseEnum.FILE_NOT_CONTENT);
        }

        ItemResource itemResource = new ItemResource();
        itemResource.setPartitionId(partitionId);
        itemResource.setUserId(userId);
        itemResource.setItemId(knowledgeItemId);
        itemResource.setFileName(fileName);
        itemResource.setOriginalName(originalFilename);
        itemResource.setFilePath(filePath);


        DataSqlEntity dataSqlEntity = docToEmbed(doc.toString());

        if (dataSqlEntity == null) {
            throw new RuntimeException(StrUtil.format("生成向量数据失败 knowledgeItemId:{} 文件名:{}", knowledgeItemId, fileName));
        }

        List<String> content = dataSqlEntity.getContent();

        StringBuilder preContent = new StringBuilder();
        ChatConfigEntity.Entity chatConfig = CommonUtils.getChatConfig().getEntity3();
        Integer maxTokens = chatConfig.getMaxTokens();
        String model = chatConfig.getModel();
        int curryTokens = 0;
        for (String str : content) {

            curryTokens += TikTokensUtil.tokens(model, str);
            if (curryTokens > maxTokens || curryTokens > 3000) {
                break;
            }
            preContent.append(str);
        }

        String q = StrFormatter.formatWith(PromptTemplate.SUMMARY, PromptTemplate.TEXT_SITE, preContent.toString());


        List<Message> messages = Collections.singletonList(new Message(Message.Role.SYSTEM.getName(), q, userId.toString()));
        ChatCompletion completion = ChatCompletion
                .builder()
                .modelType(ChatCompletion.ModelType.GPT3)
                .messages(messages)
                .stream(false)
                .maxTokens(chatConfig.getMaxTokens() - TikTokensUtil.tokens(model, messages))
                .model(model)
                .build();

        ChatCompletionResponse chatCompletionResponse = openAiStreamClient.chatCompletion(completion);
        List<ChatChoice> choices = chatCompletionResponse.getChoices();
        if (CollUtil.isNotEmpty(choices)) {
            ChatChoice chatChoice = choices.get(0);
            Message message = chatChoice.getMessage();
            String summaryAndQ = message.getContent();


            PromptTemplate.Result result = PromptTemplate.extractSummaryAndQuestions(summaryAndQ);

            itemResource.setSummaryDesc(result.getSummary());
            List<String> questions = result.getQuestions();
            if (CollUtil.isNotEmpty(questions)) {
                itemResource.setQ1(CollUtil.get(questions, 0));
                itemResource.setQ2(CollUtil.get(questions, 1));
            }


//            DataSqlEntity dataSqlEntitySummary = docToEmbed(preContent + "\n总结:" + summary + "\n\n问题1:" + question1 + "\n问题2" + question2);
        }

        // 设置为默认分区
        ItemPartition itemPartition;
        if (partitionId == -1) {
            itemPartition = itemPartitionService.getByItemIdAndPartitionName(knowledgeItemId, MilvusClientUtil.DEFAULT_PARTITION_NAME);
        } else {
            itemPartition = itemPartitionService.getById(partitionId); // 指定分区
        }
        itemResource.setPartitionId(itemPartition.getId());
        itemResourceService.save(itemResource);
        String collectionName = MilvusClientUtil.jointCollectionName(knowledgeItemId, userId);
        boolean flag = MilvusClientUtil.hasCollection(collectionName);
        if (!flag) {
            MilvusClientUtil.createCollection(8000, collectionName);
            MilvusClientUtil.createPartition(collectionName, itemPartition.getPartitionCode());
            MilvusClientUtil.createIndex(collectionName);
        }


        // 根据分 区插入
        R<MutationResult> insertResp = MilvusClientUtil.insert(dataSqlEntity, collectionName, itemPartition.getPartitionCode());
        // 分片Id
        List<Long> dataList = insertResp.getData().getIDs().getIntId().getDataList();
        MilvusClientUtil.loadCollection(collectionName);
        MilvusClientUtil.releaseCollection(collectionName);

        resourceVectorService.insertBranch(itemResource.getId(), dataList);
        exchangeCardDetailService.consume(userId, productType, collectionName + "_" + itemResource.getId(), 1L);
        return fullUrl;
    }

    /**
     * 文章转成向量
     *
     * @param doc
     * @return
     */
    private DataSqlEntity docToEmbed(String doc) {

        DataSqlEntity dse = new DataSqlEntity();
        int tokens = 0;
        RecursiveCharacterTextSplitter textSplitter = new RecursiveCharacterTextSplitter(null, 500, 80);

        List<String> content = textSplitter.splitText(doc);

        EmbeddingResponse embeddingResponse = openAiStreamClient.embeddings(content);
        if (embeddingResponse == null) {
            return null;
        }
        List<List<Float>> ll = embeddingResponse.getData().stream().map(Item::getEmbedding).collect(Collectors.toList());
        tokens += embeddingResponse.getUsage().getTotalTokens();

        dse.setLl(ll);
        dse.setContent(content);
        dse.setTotal_token(tokens);

        return dse;
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteResource(List<Long> ids, Long userId) {

        if (ids.isEmpty()) {
            return;
        }

        // 构造expr 文本向量集合 待优化
        StringBuilder expr = new StringBuilder();
        expr.append("docID in [");
        boolean flag = true;
        for (Long id : ids) {
            if (Objects.isNull(id)) {
                throw new ServiceException(CommonRespCode.VALID_SERVICE_ILLEGAL_ARGUMENT);
            }
            List<ResourceVector> resourceVectors = resourceVectorService.selectResourceVectorByResourceId(id);
            for (ResourceVector resourceVector : resourceVectors) {
                if (flag) {
                    flag = false;
                } else {
                    expr.append(",");
                }
                expr.append(resourceVector.getDocId());
            }
        }
        expr.append("]");


        // 获取项目ID 拼接集合名称
        ItemResource itemResource = itemResourceService.getById(ids.get(0));
        Long knowledgeItemId = itemResource.getItemId();
        String partitionCode = MilvusClientUtil.DEFAULT_PARTITION_NAME;
        if (itemResource.getPartitionId() != -1) {

            ItemPartition itemPartition = itemPartitionService.getById(itemResource.getPartitionId());
            partitionCode = itemPartition.getPartitionCode();
        }

        //  1 逻辑删除数据库记录 1.1 逻辑删除资源表 1.2 逻辑删除资源向量关联表
        if (!itemResourceService.removeByIds(ids)) {
            throw new RuntimeException("删除itemResourceService失败");
        }
        if (!resourceVectorService.removeResourceVectorInResourceId(ids)) {
            throw new RuntimeException("删除resourceVectorService失败");
        }


        //  2 删除向量数据库实体 后面考虑分区
        String collectionName = MilvusClientUtil.jointCollectionName(knowledgeItemId, userId);

        try {
            MilvusClientUtil.loadCollection(collectionName);

            MilvusClientUtil.deleteData(collectionName, expr.toString(), partitionCode);
        } finally {
            MilvusClientUtil.releaseCollection(collectionName);
        }

    }


}
