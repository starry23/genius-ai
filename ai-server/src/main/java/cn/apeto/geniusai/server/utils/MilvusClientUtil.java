package cn.apeto.geniusai.server.utils;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import cn.apeto.geniusai.sdk.utils.TikTokensUtil;
import cn.apeto.geniusai.server.domain.DataSqlEntity;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.domain.vo.MilvusVO;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.GetCollectionStatisticsResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.credential.UpdateCredentialParam;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.response.GetCollStatResponseWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: huangpenglong / zxw
 * @Date: 2023/4/11 13:53
 */
@Slf4j
public class MilvusClientUtil {

    private MilvusClientUtil() {
    }

    private static final Integer SEARCH_K = 6;
    private static final String SEARCH_PARAM = "{\"nprobe\":128}";
    private static final String INDEX_PARAM = "{\"nlist\":1024}";

    private static final Integer VECTOR_DIM = 1536;
    private static final Integer MAX_LENGTH = 8192;

    private static final String ID_FIELD = "docID";
    public static final String CONTENT_FIELD = "docContent";
    private static final String VECTOR_FIELD = "docEmbed";
    public static final String DEFAULT_PARTITION_NAME = "Default_partition";
    private static final Integer SHARDS_NUM = 8;

    private static final String INDEX_NAME = "docEmbedIndex";

    private static final IndexType INDEX_TYPE = IndexType.IVF_FLAT;

    /**
     * milvusClient
     */
    private static MilvusServiceClient milvusClient = null;

    public static MilvusServiceClient setMilvusClient(MilvusVO milvusVO) {
        if (milvusClient != null) {
            milvusClient.close();
        }
        ConnectParam.Builder builder = ConnectParam.newBuilder()
                .withHost(milvusVO.getIp())
                .withPort(milvusVO.getPort());
        builder.withAuthorization(milvusVO.getName(), milvusVO.getPassword());
        milvusClient = new MilvusServiceClient(builder.build());
        return milvusClient;
    }

    public static void updateCredential(String username, String oldPassword, String newPassword) {
        R<RpcStatus> credential = getMilvusClient().updateCredential(UpdateCredentialParam.newBuilder().withUsername(username).withOldPassword(oldPassword).withNewPassword(newPassword).build());
        handleResponseStatus(credential);
    }

    public static MilvusServiceClient getMilvusClient() {
        if (milvusClient == null) {
            // 初始化milvus
            MilvusVO milvusVO = JSONUtil.toBean(StringRedisUtils.get(SystemConstants.RedisKeyEnum.MILVUS.getKey()), MilvusVO.class);
            if (milvusVO != null && StrUtil.isNotBlank(milvusVO.getIp())) {
                MilvusClientUtil.setMilvusClient(milvusVO);
            }
        }
        return milvusClient;
    }

    public static void createIndex(String collectionName) {
        // 索引
        R<RpcStatus> index = getMilvusClient().createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName(VECTOR_FIELD)
                .withIndexName(INDEX_NAME)
                .withIndexType(INDEX_TYPE)
                .withMetricType(MetricType.IP)
                .withExtraParam(INDEX_PARAM)
                .withSyncMode(Boolean.TRUE)
                .build());

        handleResponseStatus(index);
    }

    public static void deleteCollection() {
        getMilvusClient().dropCollection(
                DropCollectionParam.newBuilder()
                        .withCollectionName("book")
                        .build());
    }

    /**
     * 创建用户的库
     */
    public static R<RpcStatus> createCollection(long timeoutMilliseconds, String collectionName) {

        FieldType docIdentification = FieldType.newBuilder()
                .withName(ID_FIELD)
                .withDescription("doc identification")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();


        FieldType docEmbedding = FieldType.newBuilder()
                .withName(VECTOR_FIELD)
                .withDescription("doc embedding")
                .withDataType(DataType.FloatVector)
                .withDimension(VECTOR_DIM)
                .build();

        FieldType docContent = FieldType.newBuilder()
                .withName(CONTENT_FIELD)
                .withDescription("doc content")
                .withDataType(DataType.VarChar)
                .withMaxLength(MAX_LENGTH)
                .build();


        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription("doc info")
                .withShardsNum(SHARDS_NUM)
                .addFieldType(docIdentification)
                .addFieldType(docEmbedding)
                .addFieldType(docContent)
                .build();

        R<RpcStatus> response = getMilvusClient().withTimeout(timeoutMilliseconds, TimeUnit.MILLISECONDS)
                .createCollection(createCollectionReq);

        handleResponseStatus(response);
        return response;
    }

    // 判断是否有了这个连接
    public static boolean hasCollection(String collectionName) {
        R<Boolean> response = getMilvusClient().hasCollection(HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        handleResponseStatus(response);
        return response.getData();
    }

    public static String jointCollectionName(Long itemId,Long userId){
        String collectionName = "id_" + userId + "_" + itemId;

        String collectionNameNew = "id_" + itemId;
        if(hasCollection(collectionName)){
            return collectionName;
        }else {
            return collectionNameNew;
        }


    }

    /*
   加载连接
 */
    public static R<RpcStatus> loadCollection(String collectionName) {
        R<RpcStatus> response = getMilvusClient().loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        handleResponseStatus(response);
        return response;
    }

    /**
     * 将文章向量插入向量库中
     */
    public static R<MutationResult> insert(DataSqlEntity dataSqlEntity, String collectionName) {
        return insert(dataSqlEntity, collectionName, DEFAULT_PARTITION_NAME);
    }

    /**
     * 将文章向量插入向量库中
     */
    public static R<MutationResult> insert(DataSqlEntity dataSqlEntity, String collectionName, String partitionName) {
        List<InsertParam.Field> fields = new ArrayList<>();

        fields.add(new InsertParam.Field(CONTENT_FIELD, dataSqlEntity.getContent()));
        fields.add(new InsertParam.Field(VECTOR_FIELD, dataSqlEntity.getLl()));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .withPartitionName(partitionName)
                .build();

        R<MutationResult> response = getMilvusClient().insert(insertParam);
        handleResponseStatus(response);
        return response;
    }

    /**
     * 释放连接
     */
    public static R<RpcStatus> releaseCollection(String collectionName) {
        R<RpcStatus> response = getMilvusClient().releaseCollection(ReleaseCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        handleResponseStatus(response);
        return response;
    }

    /**
     * 搜索相似文档
     *
     * @return
     */
    public static R<SearchResults> searchContent(List<List<Float>> vectors, String collectionName) {
        long begin = System.currentTimeMillis();

        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withMetricType(MetricType.IP)
                .withOutFields(ImmutableList.of(CONTENT_FIELD))
                .withTopK(SEARCH_K)
                .withVectors(vectors)
                .withVectorFieldName(VECTOR_FIELD)
                .withParams(INDEX_PARAM)
                .withGuaranteeTimestamp(Constant.GUARANTEE_EVENTUALLY_TS)
                .build();

        R<SearchResults> response = getMilvusClient().search(searchParam);
        long end = System.currentTimeMillis();
        long cost = (end - begin);
        log.info("search time cost: {}", cost);

        handleResponseStatus(response);

        return response;
    }

    public static String getSystemContent(Integer maxTokens, String modelName, List<MyMessage> messages, List<String> orderedCandidates, String promptTemplate) {

        maxTokens = maxTokens - TikTokensUtil.tokens(modelName, promptTemplate);
        StringBuilder context = new StringBuilder();
        int totalContextSplitToken = 0;
        int promptTokens = TikTokensUtil.tokens(modelName, messages.stream().map(MyMessage::getContent).collect(Collectors.joining()));
        for (String candidate : orderedCandidates) {
            totalContextSplitToken += TikTokensUtil.tokens(modelName, candidate);
            if (totalContextSplitToken + promptTokens > maxTokens) {
                break;
            }
            context.append(candidate);
        }
        return StrFormatter.formatWith(promptTemplate, "{text_content}", context.toString());

    }

    /**
     * 加载并搜索
     *
     * @param vectors
     * @param collectionName
     * @return
     */
    public static List<String> loadSearchContentAsync(List<List<Float>> vectors, String collectionName) {

        MilvusClientUtil.loadCollection(collectionName);

        R<SearchResults> response = searchContentAsync(vectors, collectionName);
        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
        // 分片文本
        List<String> orderedCandidates = (List<String>) wrapper.getFieldData(CONTENT_FIELD, 0);
        MilvusClientUtil.releaseCollection(collectionName);

        return orderedCandidates;
    }

    /**
     * *  异步查询
     *
     * @param vectors
     * @param collectionName
     * @return
     */
    public static R<SearchResults> searchContentAsync(List<List<Float>> vectors, String collectionName) {
        if (log.isDebugEnabled()) {
            log.debug("searchContentAsync vectors:{} collectionName:{}", JSONUtil.toJsonStr(vectors), collectionName);
        }
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withMetricType(MetricType.IP)
                .withOutFields(ImmutableList.of(CONTENT_FIELD))
                .withTopK(SEARCH_K)
                .withVectors(vectors)
                .withVectorFieldName(VECTOR_FIELD)
                .withParams(SEARCH_PARAM)
                .withGuaranteeTimestamp(Constant.GUARANTEE_EVENTUALLY_TS)
                .build();

        ListenableFuture<R<SearchResults>> future = getMilvusClient().searchAsync(searchParam);

        R<SearchResults> response = null;
        try {
            response = future.get();
            handleResponseStatus(response);
        } catch (InterruptedException | ExecutionException e) {
            log.error("异步搜索异常");
        }
        return response;
    }


    public static void handleResponseStatus(R<?> r) {
        if (r.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException(r.getMessage());
        }
    }

    // 统计当前连接的数据条目
    public static R<GetCollectionStatisticsResponse> getCollectionStatistics(String collectionName) {
        getMilvusClient().flushAll(true, 1000, 5000);

        R<GetCollectionStatisticsResponse> response = getMilvusClient().getCollectionStatistics(
                GetCollectionStatisticsParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build());
        handleResponseStatus(response);
        GetCollStatResponseWrapper wrapper = new GetCollStatResponseWrapper(response.getData());
        return response;
    }

    public static R<MutationResult> deleteData(String collectionName, String expr) {
        return deleteData(collectionName, expr, DEFAULT_PARTITION_NAME);
    }

    // 根据分区删除
    public static R<MutationResult> deleteData(String collectionName, String expr, String partitionName) {
        R<MutationResult> resultR = getMilvusClient().delete(
                DeleteParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withExpr(expr)
                        .withPartitionName(partitionName)
                        .build()
        );
        handleResponseStatus(resultR);
        return resultR;
    }

    public static R<RpcStatus> createPartition(String collectionName, String partitionName) {
        R<RpcStatus> partition = getMilvusClient().createPartition(
                CreatePartitionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withPartitionName(partitionName)
                        .build()
        );
        handleResponseStatus(partition);
        return partition;
    }
}
