package cn.apeto.geniusai.server.domain;

import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author apeto
 * @create 2023/5/10 13:00
 */
@Data
@Schema(description = "ChatConfig")
public class ChatConfigEntity {
    @Schema(description = "gpt3")
    private Entity entity3 = new Entity(ChatCompletion.ModelType.GPT3);
    @Schema(description = "gpt4")
    private Entity entity4 = new Entity(ChatCompletion.ModelType.GPT4);
    @Schema(description = "dalle")
    private Entity dalle = new Entity(ChatCompletion.ModelType.DALL_E);
    @Schema(description = "GPTS配置")
    private Entity gptsEntity  = new Entity(ChatCompletion.ModelType.GPTS);

    @Schema(description = "文心千帆")
    private WXQFEntity wxqfEntity;
    @Schema(description = "星火")
    private SparkDeskEntity sparkDesk;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Entity extends ChatEntity {
        public Entity(ChatCompletion.ModelType modelType) {
            super(modelType);
        }

        public Entity() {
        }

        @Schema(description = "apiKeys")
        private List<String> apiKeys;

        @Schema(description = "温度")
        private double temperature = 0.8;

        @Schema(description = "token")
        private Integer maxTokens = 2048;

        @Schema(description = "如果想要生成更有创造性和独特性的文本，可以使用presencePenalty参数")
        private double presencePenalty = 0;

        @Schema(description = "而如果想要生成更加细节丰富的、多样化的文本，可以使用frequencyPenalty参数。")
        private double frequencyPenalty = 0;

        @Schema(description = "mode前缀 默认gpt-4-gizmo-")
        private String modelPrefix = "gpt-4-gizmo-";

        @Schema(description = "路由 请求gpts应用列表的路由")
        private String route = "2";
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class WXQFEntity extends ChatEntity {

        @Schema(description = "appId")
        private String appId;
        @Schema(description = "client_id")
        private String client_id;
        @Schema(description = "client_secret")
        private String client_secret;
        /**
         * 说明：
         * （1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定
         * （2）默认0.95，范围 (0, 1.0]，不能为0
         * （3）建议该参数和top_p只设置1个
         * （4）建议top_p和temperature不要同时更改
         */
        @Schema(description = "（1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定\n" +
                "（2）默认0.95，范围 (0, 1.0]，不能为0\n" +
                "（3）建议该参数和top_p只设置1个\n" +
                "（4）建议top_p和temperature不要同时更改")
        private Double temperature;

        /**
         * 说明：
         * （1）影响输出文本的多样性，取值越大，生成文本的多样性越强
         * （2）默认0.8，取值范围 [0, 1.0]
         * （3）建议该参数和temperature只设置1个
         * （4）建议top_p和temperature不要同时更改
         */
        @Schema(description = "（1）影响输出文本的多样性，取值越大，生成文本的多样性越强\n" +
                "（2）默认0.8，取值范围 [0, 1.0]\n" +
                "（3）建议该参数和temperature只设置1个\n" +
                "（4）建议top_p和temperature不要同时更改")
        private Double top_p;

        /**
         * 通过对已生成的token增加惩罚，减少重复生成的现象。说明：
         * （1）值越大表示惩罚越大
         * （2）默认1.0，取值范围：[1.0, 2.0]
         */
        @Schema(description = "通过对已生成的token增加惩罚，减少重复生成的现象。说明：\n" +
                "（1）值越大表示惩罚越大\n" +
                "（2）默认1.0，取值范围：[1.0, 2.0]")
        private Double penalty_score;


    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SparkDeskEntity extends ChatEntity {

        @Schema(description = "appid")
        private String appid;
        @Schema(description = "apiSecret")
        private String apiSecret;
        @Schema(description = "apiKey")
        private String apiKey;
        @Schema(description = "domain 取值为[general,generalv2,generalv3]",defaultValue = "generalv2",example = "[generalv1,generalv2,generalv3]")
        private String domain="generalv2";

        @Schema(description = "host 文档: https://www.xfyun.cn/doc/spark/Web.html",example = "https://spark-api.xf-yun.com/v2.1/chat",defaultValue = "https://spark-api.xf-yun.com/v2.1/chat")
        private String host = "https://spark-api.xf-yun.com/v2.1/chat";

        @Schema(description = "模型回答的tokens的最大长度", defaultValue = "2048")
        public Integer maxTokens = 8192;

        /**
         * 取值为[1，6],默认为4
         * 从k个候选中随机选择⼀个（⾮等概率）
         */
        @Schema(description = "从k个候选中随机选择⼀个（⾮等概率）", example = "取值为[1，6],默认为4\t", defaultValue = "4")
        private Integer topK;

        @Schema(description = "温度: 核采样阈值。用于决定结果随机性，取值越高随机性越强即相同的问题得到的不同答案的可能性越高", example = "取值为[0,1],默认为0.5", defaultValue = "0.5")
        private double temperature = 0.3;
    }
}
