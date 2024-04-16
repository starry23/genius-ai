package cn.apeto.geniusai.server.core.mj;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.domain.ImageResponse;
import cn.apeto.geniusai.server.domain.MjConstants;
import cn.apeto.geniusai.server.domain.SubmitRequest;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.dto.SubmitChangeRequest;
import cn.apeto.geniusai.server.domain.dto.TaskResponse;
import cn.apeto.geniusai.server.exception.MjException;
import cn.apeto.geniusai.server.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanmingyu
 * @create 2024/1/31 11:15 下午
 */
@Slf4j
public abstract class AbstractMJService implements MJService {

    protected Map<String, String> apiUrl = new HashMap<>();

    public AbstractMJService() {
        try {
            MJ_MAP.put(getType(), (MJService) this.clone());
        } catch (Exception e) {
            log.error("初始化MJ绘画异常", e);
        }

    }

    protected void mjResLog(String imageInterfaceName, Object res) {
        log.info("接口类型:{} 返回结果为:{}", imageInterfaceName, res);
    }

    public abstract String getType();

    public abstract HttpRequest auth(HttpRequest request);

    @Override
    public ImageResponse imagine(SubmitRequest request) {
        String name = MjConstants.AiImageInterfaceEnum.IMAGINE.name();
        String res = auth(HttpRequest.post(getUrl(name, request.getMode())))
                .body(JSONUtil.toJsonStr(request))
                .execute().body();
        mjResLog(name, res);
        ImageResponse imageResponse = JSONUtil.toBean(resBefore(res), ImageResponse.class);
        checkRes(imageResponse);
        return imageResponse;
    }

    @Override
    public ImageResponse change(SubmitChangeRequest request) {
        String name = MjConstants.AiImageInterfaceEnum.CHANGE.name();
        String res = auth(HttpRequest.post(getUrl(name, request.getMode())))
                .body(JSONUtil.toJsonStr(request))
                .execute().body();
        mjResLog(name, res);
        ImageResponse imageResponse = JSONUtil.toBean(resBefore(res), ImageResponse.class);
        checkRes(imageResponse);
        return imageResponse;
    }

    @Override
    public TaskResponse fetch(String taskId) {
        String name = MjConstants.AiImageInterfaceEnum.FETCH.name();
        String res = HttpRequest.get(StrUtil.format(getUrl(name), taskId)).execute().body();
        return JSONUtil.toBean(resBefore(res), TaskResponse.class);
    }

    protected String resBefore(String res) {
        return res;
    }

    protected String getUrl(String name, String mode) {
        return StrUtil.format(getUrl(name), mode);
    }

    protected String getUrl(String name) {
        return StrUtil.format(apiUrl.get(name));
    }


    protected SaveSettingDTO getWebConfig() {
//        return new SaveSettingDTO();
        return CommonUtils.getPackageWebConfig();
    }


    protected void checkRes(ImageResponse imageResponse) {
        log.info("MJ返回信息为:{}", imageResponse);
        int code = imageResponse.getCode();
        String description = imageResponse.getDescription();

        if (code == 0 && StrUtil.isBlank(description)) {
            log.error("欠费或其他异常 请站长关注 中转站为:{}", getConfig().getApiHost());
            throw new MjException(CommonRespCode.ERROR.getMessage());
        }

        if (code != 1) {
            throw new MjException(description);
        }
    }


}
