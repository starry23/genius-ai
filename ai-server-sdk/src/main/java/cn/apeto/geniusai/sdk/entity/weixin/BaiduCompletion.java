package cn.apeto.geniusai.sdk.entity.weixin;

import lombok.Data;

import java.util.List;

/**
 * @author wanmingyu
 * @create 2023/7/20 13:09
 */
@Data
public class BaiduCompletion {

    private List<Message> messages;

    private Boolean stream;

    private String user_id;

    /**
     * 说明：
     * （1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定
     * （2）默认0.95，范围 (0, 1.0]，不能为0
     * （3）建议该参数和top_p只设置1个
     * （4）建议top_p和temperature不要同时更改
     */
    private Double temperature;

    /**
     * 说明：
     * （1）影响输出文本的多样性，取值越大，生成文本的多样性越强
     * （2）默认0.8，取值范围 [0, 1.0]
     * （3）建议该参数和temperature只设置1个
     * （4）建议top_p和temperature不要同时更改
     */
    private Double top_p;

    /**
     * 通过对已生成的token增加惩罚，减少重复生成的现象。说明：
     * （1）值越大表示惩罚越大
     * （2）默认1.0，取值范围：[1.0, 2.0]
     */
    private Double penalty_score;

    @Data
    public static class Message{

        private String role;
        private String content;

    }
}
