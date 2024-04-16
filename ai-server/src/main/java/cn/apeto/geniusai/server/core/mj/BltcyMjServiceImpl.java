package cn.apeto.geniusai.server.core.mj;

import cn.hutool.http.HttpRequest;
import cn.apeto.geniusai.server.domain.MjConstants;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import org.springframework.stereotype.Component;

/**
 * @author wanmingyu
 * @create 2024/1/31 11:04 下午
 */
@Component
public class BltcyMjServiceImpl extends AbstractMJService {

    public BltcyMjServiceImpl() {
        SaveSettingDTO.AiImageSetting aiImageBltcySetting = getWebConfig().getAiImageBltcySetting();
        if (aiImageBltcySetting != null) {
            String apiHost = aiImageBltcySetting.getApiHost();
            String MJ_CHANGE = apiHost + "/mj-{}/mj/submit/change";
            String MJ_SUBMIT = apiHost + "/mj-{}/mj/submit/imagine";
            String MJ_TASK_FETCH = apiHost + "/mj/task/{}/fetch";

            super.apiUrl.put(MjConstants.AiImageInterfaceEnum.CHANGE.name(), MJ_CHANGE);
            super.apiUrl.put(MjConstants.AiImageInterfaceEnum.IMAGINE.name(), MJ_SUBMIT);
            super.apiUrl.put(MjConstants.AiImageInterfaceEnum.FETCH.name(), MJ_TASK_FETCH);
        }
    }

    @Override
    public String getType() {
        return MjConstants.AiImageTypeEnum.BLTCY.getType();
    }

    @Override
    public HttpRequest auth(HttpRequest request) {
        SaveSettingDTO.AiImageSetting aiImageSetting = getWebConfig().getAiImageBltcySetting();
        String apiKey = aiImageSetting.getApiKey();
        return request.header("Authorization", "Bearer " + apiKey);
    }

    @Override
    public SaveSettingDTO.AiImageSetting getConfig() {
        return getWebConfig().getAiImageBltcySetting();
    }

}
