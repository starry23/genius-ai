package cn.apeto.geniusai.server.core.mj;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.server.domain.MjConstants;
import cn.apeto.geniusai.server.domain.StatusEnumSupport;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.utils.ApetoMjUtils;
import org.springframework.stereotype.Component;

/**
 * @author wanmingyu
 * @create 2024/1/31 11:04 下午
 */
@Component
public class ApetoMjServiceImpl extends AbstractMJService {

    public static String AI_IMAGE_DOMAIN = "https://admin.apeto.cn";
    String MJ_SUBMIT = AI_IMAGE_DOMAIN + "/app-api/aigc/mj/imagine";

    String MJ_CHANGE = AI_IMAGE_DOMAIN + "/app-api/aigc/mj/change";

    String MJ_TASK_FETCH = AI_IMAGE_DOMAIN + "/app-api/aigc/mj/task/{}/fetch";

    public ApetoMjServiceImpl() {
        super.apiUrl.put(MjConstants.AiImageInterfaceEnum.CHANGE.name(), MJ_CHANGE);
        super.apiUrl.put(MjConstants.AiImageInterfaceEnum.IMAGINE.name(), MJ_SUBMIT);
        super.apiUrl.put(MjConstants.AiImageInterfaceEnum.FETCH.name(), MJ_TASK_FETCH);
    }

    @Override
    public String getType() {
        return MjConstants.AiImageTypeEnum.APETO.getType();
    }


    @Override
    public HttpRequest auth(HttpRequest request) {
        SaveSettingDTO.AiImageSetting aiImageSetting = getWebConfig().getAiImageSetting();
        String apiKey = aiImageSetting.getApiKey();
        Integer tenantId = aiImageSetting.getTenantId();
        return request.headerMap(ApetoMjUtils.headerMap(tenantId, apiKey), true);
    }

    @Override
    protected String resBefore(String res) {
        JSONObject jsonObject = checkImagesRes(res);
        return jsonObject.getStr("data");
    }

    @Override
    public SaveSettingDTO.AiImageSetting getConfig() {
        return getWebConfig().getAiImageSetting();
    }

    private JSONObject checkImagesRes(String res) {
        JSONObject jsonObject = JSONUtil.parseObj(res);
        String msg = jsonObject.getStr("msg");
        Integer code = jsonObject.getInt("code");

        if (code == null) {
            throw new RuntimeException("返回code异常" + msg);
        }
        if (MjConstants.ResponseEnum.IN_QUEUE.getTcode().equals(code)) {
            // 排队中
            return jsonObject;
        }

        if (MjConstants.ResponseEnum.SUCCESS.getTcode().equals(code)) {
            // 成功
            return jsonObject;
        }

        StatusEnumSupport statusEnumSupport = MjConstants.ResponseEnum.getByCode(code);
        if (statusEnumSupport == null || statusEnumSupport.getCode() == null) {
            throw new RuntimeException("请求异常:" + msg);
        } else {
            throw new ServiceException(statusEnumSupport);
        }

    }

}
