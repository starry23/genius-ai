package cn.apeto.geniusai.server.core.mj;

import cn.apeto.geniusai.server.domain.ImageResponse;
import cn.apeto.geniusai.server.domain.SubmitRequest;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.dto.SubmitChangeRequest;
import cn.apeto.geniusai.server.domain.dto.TaskResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanmingyu
 * @create 2024/1/30 6:55 下午
 */
public interface MJService extends Cloneable {

    Map<String, MJService> MJ_MAP = new ConcurrentHashMap<>();

    SaveSettingDTO.AiImageSetting getConfig();

    ImageResponse imagine(SubmitRequest request);

    ImageResponse change(SubmitChangeRequest request);

    TaskResponse fetch(String taskId);
}
