package cn.apeto.geniusai.sdk;

import cn.hutool.core.util.StrUtil;
import cn.apeto.geniusai.sdk.entity.billing.BillingUsage;
import cn.apeto.geniusai.sdk.entity.billing.CreditGrantsResponse;
import cn.apeto.geniusai.sdk.entity.billing.Subscription;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletionResponse;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.entity.common.DeleteResponse;
import cn.apeto.geniusai.sdk.entity.common.OpenAiResponse;
import cn.apeto.geniusai.sdk.entity.completions.Completion;
import cn.apeto.geniusai.sdk.entity.completions.CompletionResponse;
import cn.apeto.geniusai.sdk.entity.edits.Edit;
import cn.apeto.geniusai.sdk.entity.edits.EditResponse;
import cn.apeto.geniusai.sdk.entity.embeddings.Embedding;
import cn.apeto.geniusai.sdk.entity.embeddings.EmbeddingResponse;
import cn.apeto.geniusai.sdk.entity.engines.Engine;
import cn.apeto.geniusai.sdk.entity.files.File;
import cn.apeto.geniusai.sdk.entity.files.UploadFileResponse;
import cn.apeto.geniusai.sdk.entity.fineTune.Event;
import cn.apeto.geniusai.sdk.entity.fineTune.FineTune;
import cn.apeto.geniusai.sdk.entity.fineTune.FineTuneDeleteResponse;
import cn.apeto.geniusai.sdk.entity.fineTune.FineTuneResponse;
import cn.apeto.geniusai.sdk.entity.images.*;
import cn.apeto.geniusai.sdk.entity.models.Model;
import cn.apeto.geniusai.sdk.entity.models.ModelResponse;
import cn.apeto.geniusai.sdk.entity.moderations.Moderation;
import cn.apeto.geniusai.sdk.entity.moderations.ModerationResponse;
import cn.apeto.geniusai.sdk.entity.whisper.Transcriptions;
import cn.apeto.geniusai.sdk.entity.whisper.Translations;
import cn.apeto.geniusai.sdk.entity.whisper.WhisperResponse;
import cn.apeto.geniusai.sdk.exception.BaseException;
import cn.apeto.geniusai.sdk.exception.CommonError;
import cn.apeto.geniusai.sdk.function.KeyStrategy;
import cn.apeto.geniusai.sdk.interceptor.DefaultOpenAiAuthInterceptor;
import cn.apeto.geniusai.sdk.interceptor.DynamicKeyOpenAiAuthInterceptor;
import cn.apeto.geniusai.sdk.interceptor.OpenAiAuthInterceptor;
import io.reactivex.Single;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 描述： open ai 客户端
 *
 * @author https:www.unfbx.com
 * @since 2023-02-11
 */

@Slf4j
public abstract class OpenAiClient {


  /**
   * 自定义api host使用builder的方式构造client
   */
  @Getter
  protected String apiHost;
  @Getter
  protected OpenAiApi openAiApi;
  /**
   * 自定义的okHttpClient 如果不自定义 ，就是用sdk默认的OkHttpClient实例
   */
  @Getter
  protected OkHttpClient okHttpClient;
  /**
   * api key的获取策略
   */
  @Getter
  protected KeyStrategy keyStrategy;

  /**
   * 自定义鉴权处理拦截器<br/> 可以不设置，默认实现：DefaultOpenAiAuthInterceptor <br/> 如需自定义实现参考：DealKeyWithOpenAiAuthInterceptor
   *
   * @see DynamicKeyOpenAiAuthInterceptor
   * @see DefaultOpenAiAuthInterceptor
   */
  @Getter
  protected OpenAiAuthInterceptor authInterceptor;


  /**
   * 创建默认OkHttpClient
   *
   * @return
   */
  protected OkHttpClient okHttpClient (KeyStrategy keyStrategy) {
    if (Objects.isNull(this.authInterceptor)) {
      this.authInterceptor = new DefaultOpenAiAuthInterceptor(keyStrategy);
    }
    return new OkHttpClient
        .Builder()
        .addInterceptor(this.authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS).build();
  }

  /**
   * openAi模型列表
   *
   * @return Model  list
   */
  public List<Model> models () {
    Single<ModelResponse> models = this.openAiApi.models();
    List<Model> modelList = models.blockingGet().getData();
    return modelList;
  }

  /**
   * openAi模型详细信息
   *
   * @param id
   * @return Model
   */
  public Model model (String id) {
    if (Objects.isNull(id) || "".equals(id)) {
      throw new BaseException(CommonError.PARAM_ERROR);
    }
    Single<Model> model = this.openAiApi.model(id);
    return model.blockingGet();
  }


  /**
   * 问答接口
   *
   * @param completion 问答参数
   * @return CompletionResponse
   */
  public CompletionResponse completions (Completion completion) {
    Single<CompletionResponse> completions = this.openAiApi.completions(completion);
    return completions.blockingGet();
  }

  /**
   * 问答接口-简易版
   *
   * @param question 问题描述
   * @return CompletionResponse
   */
  public CompletionResponse completions (String question) {
    Completion q = Completion.builder()
        .prompt(question)
        .build();
    Single<CompletionResponse> completions = this.openAiApi.completions(q);
    return completions.blockingGet();
  }

  /**
   * 文本修改
   *
   * @param edit 图片对象
   * @return EditResponse
   */
  public EditResponse edit (Edit edit) {
    Single<EditResponse> edits = this.openAiApi.edits(edit);
    return edits.blockingGet();
  }

  /**
   * 根据描述生成图片
   *
   * @param prompt
   * @return ImageResponse
   */
  public ImageResponse genImages (String prompt) {
    Image image = Image.builder().prompt(prompt).build();
    return this.genImages(image);
  }

  /**
   * 根据描述生成图片
   *
   * @param image 图片参数
   * @return ImageResponse
   */
  public ImageResponse genImages (Image image) {
    Single<ImageResponse> edits = this.openAiApi.genImages(image);
    return edits.blockingGet();
  }

  /**
   * Creates an edited or extended image given an original image and a prompt. 根据描述修改图片
   *
   * @param image  图片对象
   * @param prompt 描述信息
   * @return Item  list
   */
  public List<Item> editImages (java.io.File image, String prompt) {
    ImageEdit imageEdit = ImageEdit.builder().prompt(prompt).build();
    return this.editImages(image, null, imageEdit);
  }

  /**
   * Creates an edited or extended image given an original image and a prompt. 根据描述修改图片
   *
   * @param image     图片对象
   * @param imageEdit 图片参数
   * @return Item  list
   */
  public List<Item> editImages (java.io.File image, ImageEdit imageEdit) {
    return this.editImages(image, null, imageEdit);
  }

  /**
   * Creates an edited or extended image given an original image and a prompt. 根据描述修改图片
   *
   * @param image     png格式的图片，最大4MB
   * @param mask      png格式的图片，最大4MB
   * @param imageEdit 图片参数
   * @return Item list
   */
  public List<Item> editImages (java.io.File image, java.io.File mask, ImageEdit imageEdit) {
    checkImage(image);
    checkImageFormat(image);
    checkImageSize(image);
    if (Objects.nonNull(mask)) {
      checkImageFormat(image);
      checkImageSize(image);
    }
    // 创建 RequestBody，用于封装构建RequestBody
    RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
    MultipartBody.Part imageMultipartBody = MultipartBody.Part.createFormData("image", image.getName(), imageBody);
    MultipartBody.Part maskMultipartBody = null;
    if (Objects.nonNull(mask)) {
      RequestBody maskBody = RequestBody.create(MediaType.parse("multipart/form-data"), mask);
      maskMultipartBody = MultipartBody.Part.createFormData("mask", image.getName(), maskBody);
    }
    Map<String, RequestBody> requestBodyMap = new HashMap<>();
    requestBodyMap.put("prompt", RequestBody.create(MediaType.parse("multipart/form-data"), imageEdit.getPrompt()));
    requestBodyMap.put("n", RequestBody.create(MediaType.parse("multipart/form-data"), imageEdit.getN().toString()));
    requestBodyMap.put("size", RequestBody.create(MediaType.parse("multipart/form-data"), imageEdit.getSize()));
    requestBodyMap.put("response_format", RequestBody.create(MediaType.parse("multipart/form-data"), imageEdit.getResponseFormat()));
    if (!(Objects.isNull(imageEdit.getUser()) || "".equals(imageEdit.getUser()))) {
      requestBodyMap.put("user", RequestBody.create(MediaType.parse("multipart/form-data"), imageEdit.getUser()));
    }
    Single<ImageResponse> imageResponse = this.openAiApi.editImages(
        imageMultipartBody,
        maskMultipartBody,
        requestBodyMap
    );
    return imageResponse.blockingGet().getData();
  }

  /**
   * Creates a variation of a given image.
   * <p>
   * 变化图片，类似ai重做图片
   *
   * @param image           图片对象
   * @param imageVariations 图片参数
   * @return ImageResponse
   */
  public ImageResponse variationsImages (java.io.File image, ImageVariations imageVariations) {
    checkImage(image);
    checkImageFormat(image);
    checkImageSize(image);
    RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("image", image.getName(), imageBody);
    Map<String, RequestBody> requestBodyMap = new HashMap<>();
    requestBodyMap.put("n", RequestBody.create(MediaType.parse("multipart/form-data"), imageVariations.getN().toString()));
    requestBodyMap.put("size", RequestBody.create(MediaType.parse("multipart/form-data"), imageVariations.getSize()));
    requestBodyMap.put("response_format", RequestBody.create(MediaType.parse("multipart/form-data"), imageVariations.getResponseFormat()));
    if (!(Objects.isNull(imageVariations.getUser()) || "".equals(imageVariations.getUser()))) {
      requestBodyMap.put("user", RequestBody.create(MediaType.parse("multipart/form-data"), imageVariations.getUser()));
    }
    Single<ImageResponse> variationsImages = this.openAiApi.variationsImages(
        multipartBody,
        requestBodyMap
    );
    return variationsImages.blockingGet();
  }

  /**
   * Creates a variation of a given image.
   *
   * @param image 图片对象
   * @return ImageResponse
   */
  public ImageResponse variationsImages (java.io.File image) {
    checkImage(image);
    checkImageFormat(image);
    checkImageSize(image);
    ImageVariations imageVariations = ImageVariations.builder().build();
    return this.variationsImages(image, imageVariations);
  }

  /**
   * 校验图片不能为空
   *
   * @param image
   */
  private void checkImage (java.io.File image) {
    if (Objects.isNull(image)) {
      log.error("image不能为空");
      throw new BaseException(CommonError.PARAM_ERROR);
    }
  }

  /**
   * 校验图片格式
   *
   * @param image
   */
  private void checkImageFormat (java.io.File image) {
    if (!(image.getName().endsWith("png") || image.getName().endsWith("PNG"))) {
      log.error("image格式错误");
      throw new BaseException(CommonError.PARAM_ERROR);
    }
  }

  /**
   * 校验图片大小
   *
   * @param image
   */
  private void checkImageSize (java.io.File image) {
    if (image.length() > 4 * 1024 * 1024) {
      log.error("image最大支持4MB");
      throw new BaseException(CommonError.PARAM_ERROR);
    }
  }

  /**
   * 向量计算：单文本
   *
   * @param input
   * @return EmbeddingResponse
   */
  public EmbeddingResponse embeddings (String input) {
    List<String> inputs = new ArrayList<>(1);
    inputs.add(input);
    Embedding embedding = Embedding.builder().input(inputs).build();
    return this.embeddings(embedding);
  }

  /**
   * 向量计算：集合文本
   *
   * @param input 文本集合
   * @return EmbeddingResponse
   */
  public EmbeddingResponse embeddings (List<String> input) {
    Embedding embedding = Embedding.builder().input(input).build();
    return this.embeddings(embedding);
  }

  /**
   * 文本转换向量
   *
   * @param embedding 入参
   * @return EmbeddingResponse
   */
  public EmbeddingResponse embeddings (Embedding embedding) {
    Single<EmbeddingResponse> embeddings = this.openAiApi.embeddings(embedding);
    return embeddings.blockingGet();
  }

  /**
   * 获取文件列表
   *
   * @return File  list
   */
  public List<File> files () {
    Single<OpenAiResponse<File>> files = this.openAiApi.files();
    return files.blockingGet().getData();
  }

  /**
   * 删除文件
   *
   * @param fileId 文件id
   * @return DeleteResponse
   */
  public DeleteResponse deleteFile (String fileId) {
    Single<DeleteResponse> deleteFile = this.openAiApi.deleteFile(fileId);
    return deleteFile.blockingGet();
  }

  /**
   * 上传文件
   *
   * @param purpose purpose
   * @param file    文件对象
   * @return UploadFileResponse
   */
  public UploadFileResponse uploadFile (String purpose, java.io.File file) {
    // 创建 RequestBody，用于封装构建RequestBody
    RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileBody);

    RequestBody purposeBody = RequestBody.create(MediaType.parse("multipart/form-data"), purpose);
    Single<UploadFileResponse> uploadFileResponse = this.openAiApi.uploadFile(multipartBody, purposeBody);
    return uploadFileResponse.blockingGet();
  }

  /**
   * 上传文件
   *
   * @param file
   * @return UploadFileResponse
   */
  public UploadFileResponse uploadFile (java.io.File file) {
    //purpose 官网示例默认是：fine-tune
    return this.uploadFile("fine-tune", file);
  }

  /**
   * 检索文件
   *
   * @param fileId 文件id
   * @return File
   */
  public File retrieveFile (String fileId) {
    Single<File> fileContent = this.openAiApi.retrieveFile(fileId);
    return fileContent.blockingGet();
  }

  /**
   * 检索文件内容
   * 免费用户无法使用此接口 #未经过测试
   *
   * @param fileId
   * @return ResponseBody
   */
//    public ResponseBody retrieveFileContent(String fileId) {
//        Single<ResponseBody> fileContent = this.openAiApi.retrieveFileContent(fileId);
//        return fileContent.blockingGet();
//    }

  /**
   * 文本审核
   *
   * @param input 待检测数据
   * @return ModerationResponse
   */
  public ModerationResponse moderations (String input) {
    List<String> content = new ArrayList<>(1);
    content.add(input);
    Moderation moderation = Moderation.builder().input(content).build();
    return this.moderations(moderation);
  }

  /**
   * 文本审核
   *
   * @param input 待检测数据集合
   * @return ModerationResponse
   */
  public ModerationResponse moderations (List<String> input) {
    Moderation moderation = Moderation.builder().input(input).build();
    return this.moderations(moderation);
  }

  /**
   * 文本审核
   *
   * @param moderation
   * @return ModerationResponse
   */
  public ModerationResponse moderations (Moderation moderation) {
    Single<ModerationResponse> moderations = this.openAiApi.moderations(moderation);
    return moderations.blockingGet();
  }

  /**
   * 创建微调模型
   *
   * @param fineTune 微调作业id
   * @return FineTuneResponse
   */
  public FineTuneResponse fineTune (FineTune fineTune) {
    Single<FineTuneResponse> fineTuneResponse = this.openAiApi.fineTune(fineTune);
    return fineTuneResponse.blockingGet();
  }

  /**
   * 创建微调模型
   *
   * @param trainingFileId 文件id，文件上传返回的id
   * @return FineTuneResponse
   */
  public FineTuneResponse fineTune (String trainingFileId) {
    FineTune fineTune = FineTune.builder().trainingFile(trainingFileId).build();
    return this.fineTune(fineTune);
  }

  /**
   * 微调模型列表
   *
   * @return FineTuneResponse list
   */
  public List<FineTuneResponse> fineTunes () {
    Single<OpenAiResponse<FineTuneResponse>> fineTunes = this.openAiApi.fineTunes();
    return fineTunes.blockingGet().getData();
  }

  /**
   * 检索微调作业
   *
   * @param fineTuneId 微调作业id
   * @return FineTuneResponse
   */
  public FineTuneResponse retrieveFineTune (String fineTuneId) {
    Single<FineTuneResponse> fineTune = this.openAiApi.retrieveFineTune(fineTuneId);
    return fineTune.blockingGet();
  }

  /**
   * 取消微调作业
   *
   * @param fineTuneId
   * @return FineTuneResponse
   */
  public FineTuneResponse cancelFineTune (String fineTuneId) {
    Single<FineTuneResponse> fineTune = this.openAiApi.cancelFineTune(fineTuneId);
    return fineTune.blockingGet();
  }

  /**
   * 微调作业事件列表
   *
   * @param fineTuneId 微调作业id
   * @return Event List
   */
  public List<Event> fineTuneEvents (String fineTuneId) {
    Single<OpenAiResponse<Event>> events = this.openAiApi.fineTuneEvents(fineTuneId);
    return events.blockingGet().getData();
  }

  /**
   * 删除微调作业模型 Delete a fine-tuned model. You must have the Owner role in your organization.
   *
   * @param model
   * @return FineTuneDeleteResponse
   */
  public FineTuneDeleteResponse deleteFineTuneModel (String model) {
    Single<FineTuneDeleteResponse> delete = this.openAiApi.deleteFineTuneModel(model);
    return delete.blockingGet();
  }


  /**
   * 引擎列表
   *
   * @return Engine List
   */
  @Deprecated
  public List<Engine> engines () {
    Single<OpenAiResponse<Engine>> engines = this.openAiApi.engines();
    return engines.blockingGet().getData();
  }

  /**
   * 引擎详细信息
   *
   * @param engineId 引擎id
   * @return Engine
   */
  @Deprecated
  public Engine engine (String engineId) {
    Single<Engine> engine = this.openAiApi.engine(engineId);
    return engine.blockingGet();
  }

  /**
   * 最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
   *
   * @param chatCompletion 问答参数
   * @return 答案
   */
  public ChatCompletionResponse chatCompletion (ChatCompletion chatCompletion) {
    Single<ChatCompletionResponse> chatCompletionResponse = this.openAiApi.chatCompletion(chatCompletion);
    return chatCompletionResponse.blockingGet();
  }

  /**
   * 简易版
   *
   * @param messages 问答参数
   * @return 答案
   */
  public ChatCompletionResponse chatCompletion (List<Message> messages) {
    ChatCompletion chatCompletion = ChatCompletion.builder().messages(messages).build();
    return this.chatCompletion(chatCompletion);
  }


  /**
   * 语音转文字
   *
   * @param transcriptions 参数
   * @param file           语音文件 最大支持25MB mp3, mp4, mpeg, mpga, m4a, wav, webm
   * @return 语音文本
   */
  public WhisperResponse speechToTextTranscriptions (java.io.File file, Transcriptions transcriptions) {
    //文件
    RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
    //自定义参数
    Map<String, RequestBody> requestBodyMap = new HashMap<>();
    if (StrUtil.isNotBlank(transcriptions.getLanguage())) {
      requestBodyMap.put(Transcriptions.Fields.language, RequestBody.create(MediaType.parse("multipart/form-data"), transcriptions.getLanguage()));
    }
    if (StrUtil.isNotBlank(transcriptions.getModel())) {
      requestBodyMap.put(Transcriptions.Fields.model, RequestBody.create(MediaType.parse("multipart/form-data"), transcriptions.getModel()));
    }
    if (StrUtil.isNotBlank(transcriptions.getPrompt())) {
      requestBodyMap.put(Transcriptions.Fields.prompt, RequestBody.create(MediaType.parse("multipart/form-data"), transcriptions.getPrompt()));
    }
    if (StrUtil.isNotBlank(transcriptions.getResponseFormat())) {
      requestBodyMap.put(Transcriptions.Fields.responseFormat, RequestBody.create(MediaType.parse("multipart/form-data"), transcriptions.getResponseFormat()));
    }
    if (Objects.nonNull(transcriptions.getTemperature())) {
      requestBodyMap.put(Transcriptions.Fields.temperature,
          RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(transcriptions.getTemperature())));
    }
    Single<WhisperResponse> whisperResponse = this.openAiApi.speechToTextTranscriptions(multipartBody, requestBodyMap);
    return whisperResponse.blockingGet();
  }

  /**
   * 简易版 语音转文字
   *
   * @param file 语音文件 最大支持25MB mp3, mp4, mpeg, mpga, m4a, wav, webm
   * @return 语音文本
   */
  public WhisperResponse speechToTextTranscriptions (java.io.File file) {
    Transcriptions transcriptions = Transcriptions.builder().build();
    return this.speechToTextTranscriptions(file, transcriptions);

  }


  /**
   * 语音翻译：目前仅支持翻译为英文
   *
   * @param translations 参数
   * @param file         语音文件 最大支持25MB mp3, mp4, mpeg, mpga, m4a, wav, webm
   * @return 翻译后文本
   */
  public WhisperResponse speechToTextTranslations (java.io.File file, Translations translations) {
    //文件
    RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
    //自定义参数
    Map<String, RequestBody> requestBodyMap = new HashMap<>();

    if (StrUtil.isNotBlank(translations.getModel())) {
      requestBodyMap.put(Translations.Fields.model, RequestBody.create(MediaType.parse("multipart/form-data"), translations.getModel()));
    }
    if (StrUtil.isNotBlank(translations.getPrompt())) {
      requestBodyMap.put(Translations.Fields.prompt, RequestBody.create(MediaType.parse("multipart/form-data"), translations.getPrompt()));
    }
    if (StrUtil.isNotBlank(translations.getResponseFormat())) {
      requestBodyMap.put(Translations.Fields.responseFormat, RequestBody.create(MediaType.parse("multipart/form-data"), translations.getResponseFormat()));
    }
    if (Objects.nonNull(translations.getTemperature())) {
      requestBodyMap.put(Translations.Fields.temperature,
          RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(translations.getTemperature())));
    }
    Single<WhisperResponse> whisperResponse = this.openAiApi.speechToTextTranslations(multipartBody, requestBodyMap);
    return whisperResponse.blockingGet();
  }

  /**
   * 简易版 语音翻译：目前仅支持翻译为英文
   *
   * @param file 语音文件 最大支持25MB mp3, mp4, mpeg, mpga, m4a, wav, webm
   * @return 翻译后文本
   */
  public WhisperResponse speechToTextTranslations (java.io.File file) {
    Translations translations = Translations.builder().build();
    return this.speechToTextTranslations(file, translations);
  }

  /**
   * 校验语音文件大小给出提示，目前官方限制25MB，后续可能会改动所以不报错只做提示
   *
   * @param file
   */
  private void checkSpeechFileSize (java.io.File file) {
    if (file.length() > 25 * 1204 * 1024) {
      log.warn("2023-03-02官方文档提示：文件不能超出25MB");
    }
  }

  /**
   * ## 官方已经禁止使用此api OpenAi账户余额查询
   *
   * @return
   * @see #subscription()
   * @see #billingUsage(LocalDate, LocalDate)
   */
  @Deprecated
  public CreditGrantsResponse creditGrants () {
    Single<CreditGrantsResponse> creditGrants = this.openAiApi.creditGrants();
    return creditGrants.blockingGet();
  }


  /**
   * 账户信息查询：里面包含总金额等信息
   *
   * @return 账户信息
   */
  public Subscription subscription () {
    Single<Subscription> subscription = this.openAiApi.subscription();
    return subscription.blockingGet();
  }

  /**
   * 账户调用接口消耗金额信息查询 最多查询100天
   *
   * @param starDate 开始时间
   * @param endDate  结束时间
   * @return 消耗金额信息
   */
  public BillingUsage billingUsage (@NotNull LocalDate starDate, @NotNull LocalDate endDate) {
    Single<BillingUsage> billingUsage = this.openAiApi.billingUsage(starDate, endDate);
    return billingUsage.blockingGet();
  }


}
