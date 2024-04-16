package cn.apeto.geniusai.sdk;


import cn.apeto.geniusai.sdk.entity.weixin.BaiduCompletion;
import cn.apeto.geniusai.sdk.entity.weixin.BaiduCompletionResponse;
import cn.apeto.geniusai.sdk.entity.weixin.ResponseToken;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 描述： open ai官方api接口
 *
 * @author https:www.unfbx.com
 * 2023-02-15
 */
public interface BaiduWenXinAiApi {

    String BASE_URL = "https://aip.baidubce.com";

    String COMPLETIONS_URL = "/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions";

    @POST(COMPLETIONS_URL)
    Single<BaiduCompletionResponse> completions(@Body BaiduCompletion baiduCompletion);


    @GET("/oauth/2.0/token")
    Single<ResponseToken> getToken(@Query("grant_type") String grant_type,
                                   @Query("client_id") String client_id,
                                   @Query("client_secret") String client_secret);
}
