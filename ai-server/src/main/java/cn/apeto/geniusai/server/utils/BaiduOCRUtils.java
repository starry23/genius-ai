package cn.apeto.geniusai.server.utils;

import cn.hutool.core.collection.CollUtil;
import com.baidu.aip.ocr.AipOcr;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/03/17:32
 * @Description:
 */
@Slf4j
public class BaiduOCRUtils {


    /**
     * * 图片转文字
     *
     * @param image 图片二进制数组
     * @return
     */
    public static String pictureToText(byte[] image) {
        if (image.length/1024 <500) {
            // 小于500k过滤
            return "";
        }
        SaveSettingDTO.BaiduSetting baiduSetting = CommonUtils.getPackageWebConfig().getBaiduSetting();
        AipOcr client = new AipOcr(baiduSetting.getAppId(), baiduSetting.getAppKey(), baiduSetting.getSecretKey());
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        HashMap<String, String> options = new HashMap<>();
        // 中文
        options.put("language_type", "CHN_ENG");
        // 是否检测图像朝向，默认不检测，即：false。朝向是指输入图像是正常方向、逆时针旋转90/180/270度。可选值包括:
        options.put("detect_direction", "true");
        // 是否检测语言
        options.put("detect_language", "true");
        // 是否返回识别结果中每一行的置信度
        options.put("probability", "false");
        JSONObject res = client.basicGeneral(image, options);
        if (log.isDebugEnabled()) {

            log.debug("百度OCR 返回信息:{}", res);
        }
        if (Objects.isNull(res)) {
            throw new RuntimeException("请求图片转文字失败");
        }
        JSONArray wordsResult = res.getJSONArray("words_result");
        if (CollUtil.isEmpty(wordsResult)) {
            return "";
        }
        List<Object> resList = wordsResult.toList();

        List<String> values = resList.stream().map(o -> {
            HashMap<String, String> hashMap = (HashMap) o;
            return hashMap.get("words");
        }).collect(Collectors.toList());

        return String.join("", values);
    }
}
