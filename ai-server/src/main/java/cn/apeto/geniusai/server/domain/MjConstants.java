package cn.apeto.geniusai.server.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author apeto
 * @create 2023/6/12 19:42
 */
public interface MjConstants {

    /**
     * DISCORD_SERVER_URL.
     */
    String DISCORD_SERVER_URL = "https://discord.com";
    /**
     * DISCORD_CDN_URL.
     */
    String DISCORD_CDN_URL = "https://cdn.discordapp.com";
    /**
     * DISCORD_WSS_URL.
     */
    String DISCORD_WSS_URL = "wss://gateway.discord.gg";


    @Getter
    @AllArgsConstructor
    enum ActionEnum {
        /**
         * 生成图片.
         */
        IMAGINE,
        /**
         * 选中放大.
         */
        UPSCALE,
        /**
         * 选中其中的一张图，生成四张相似的.
         */
        VARIATION,
        /**
         * 重新生成.
         */
        REROLL,
        /**
         * 图转prompt.
         */
        DESCRIBE,
        /**
         * 多图混合.
         */
        BLEND,


        ;

    }


    @Getter
    @AllArgsConstructor
    enum TaskStatus {
        /**
         * 未启动.
         */
        NOT_START,
        /**
         * 已提交.
         */
        SUBMITTED,
        /**
         * 执行中.
         */
        IN_PROGRESS,
        /**
         * 失败.
         */
        FAILURE,
        /**
         * 成功.
         */
        SUCCESS,
        EXPIRE,
        ;

    }

    @Getter
    @AllArgsConstructor
    enum PublishStateEnum {
        /**
         * 未发布.
         */
        NOT_PUBLISH,
        /**
         * 已发布.
         */
        PUBLISHED,
        ;

    }

    @Getter
    @AllArgsConstructor
    enum ResponseEnum implements StatusEnumSupport {

        SUCCESS(CommonRespCode.SUCCESS.getCode(), 0, "SUCCESS"),
        EXISTED(2031, 2010000005, "数据已过期 不支持变换~"),
        IN_QUEUE(2032, 2010000006, "排队中"),
        QUEUE_REJECTED(2033, 2010000007, "队列已满 请稍后重试~"),
        BANNED_PROMPT(2034, 2010000008, "您输入的参数包含敏感词 请修正~"),

        ;

        private Integer code;
        private Integer tcode;
        private String message;

        public static StatusEnumSupport getByCode(Integer code) {
            for (ResponseEnum value : ResponseEnum.values()) {
                if (value.getTcode().equals(code)) {
                    return value;
                }
            }
            return CommonRespCode.ERROR;
        }
    }

    @Getter
    @AllArgsConstructor
    enum AiImageTypeEnum {
        APETO("apeto", "群主"),
        BLTCY("bltcy", "柏拉图"),

        ;

        private String type;
        private String desc;
    }


    @Getter
    @AllArgsConstructor
    enum AiImageInterfaceEnum {
        CHANGE,
        IMAGINE,
        FETCH,
        ;
    }

    @Getter
    @AllArgsConstructor
    enum ModeEnum {
        FAST("fast", "快速", Constants.ProductTypeEnum.MJ_FAST.getType()),
        //        TURBO("turbo", "涡轮"),
        RELAX("relax", "慢速", Constants.ProductTypeEnum.MJ.getType()),
        ;

        private String mode;
        private String desc;
        private Integer productType;

        public static String getByProductType(Integer productType) {
            for (ModeEnum value : ModeEnum.values()) {
                if (value.getProductType().equals(productType)) {
                    return value.getMode();
                }
            }

            return ModeEnum.RELAX.getMode();
        }
    }
}
