package cn.apeto.geniusai.server.domain;

import cn.hutool.core.collection.ListUtil;
import cn.apeto.geniusai.server.core.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/3/27 11:31
 */
public interface Constants {

    String VERSION = "4.1.0";




    @Getter
    @AllArgsConstructor
    enum ResponseEnum implements StatusEnumSupport {
        LOGIN_ERROR(4001, "登录失败~"),
        NOT_LOGIN(4002, "请登录后再进行使用~"),
        BALANCE_INSUFFICIENT(4003, "您的余额不足请充值后再使用~"),
        MEMBER_EXP(4004, "您的会员已过期，请充值~"),
        MEMBER_LIMIT_COUNT(4005, "您的提问次数不足，请充值~"),
        USER_EXIST(4006, "该账号已存在"),
        USER_NOT_EXIST(4008, "用户不存在,请先注册~"),
        QR_INVALID(4009, "二维码已失效"),
        WAITING_FOLLOW(4010, "正在登陆请稍后"),
        MEMBER_RIGHTS_EXIST(4011, "此会员卡权益已存在，请更换绑定~"),
        ACTIVITY_END(4013, "活动以结束~"),
        ACCOUNT_BALANCE_NOT_ENOUGH(4014, "账户余额不足~"),
        NO_MEMBER(4015, "此功能只支持VIP会员使用哦~ 请先购买会员！"),
        FILE_NOT_CONTENT(4016, "解析的内容不存在。请检查文件是否有内容~"),
        VIP_COUNT_INSUFFICIENT(4017, "尊敬的VIP用户，您的会员次数不足。清先充值~"),
        VIP_COUNT_HAH(4018, "尊敬的用户，此功能需要会员次数"),

        ;

        private Integer code;
        private String message;
    }


    @Getter
    @AllArgsConstructor
    enum PayStateEnum {
        // 支付状态 0: 支付中 1: 已支付 2: 支付失败 3: 支付取消

        PAYING(0, "支付中"),
        SUCCESS(1, "支付成功"),
        ERROR(2, "支付失败"),
        CANCEL(3, "支付取消"),
        REFUND(4, "已退款"),
        ;
        private Integer state;
        private String desc;

    }

    @Getter
    @AllArgsConstructor
    enum AliPayStateEnum {

        TRADE_CLOSED("TRADE_CLOSED", "交易关闭", PayStateEnum.CANCEL),
        TRADE_FINISHED("TRADE_FINISHED", "交易结束，不可退款", PayStateEnum.REFUND),
        TRADE_SUCCESS("TRADE_SUCCESS", "支付成功", PayStateEnum.SUCCESS),
        WAIT_BUYER_PAY("WAIT_BUYER_PAY", "交易创建", PayStateEnum.PAYING),
        ;
        private String state;
        private String desc;
        private PayStateEnum sysPayStateEnum;

        public static PayStateEnum getByPayStateEnum(String state) {
            for (AliPayStateEnum value : AliPayStateEnum.values()) {
                if (value.getState().equals(state)) {
                    return value.getSysPayStateEnum();
                }
            }
            return AliPayStateEnum.TRADE_CLOSED.getSysPayStateEnum();
        }
    }


    @Getter
    @AllArgsConstructor
    enum PayTypeEnum {
//    支付类型 10:微信 20:支付宝

        WECHAT(10, "微信"),
        ALI_PAY(20, "支付宝"),
        ;
        private Integer payType;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum LogDescriptionTypeEnum {
        RECHARGE(1010, "充值"),
        INVITE(1011, "邀请有礼"),
        REGISTER(1012, "注册有礼"),
        MP(1013, "公众号领取"),
        MANAGER_DEPOSIT(1014, "后台充值"),
        REFUND(1020, "退款"),
        GPT3_5CONSUMPTION(1031, "GPT3.5消费"),
        GPT_4CONSUMPTION(1032, "GPT4.0消费"),
        MJ_CONSUMPTION(1033, "mj消费"),
        DALL_E(1130, "dall_e消费"),
        GPTS(1131, "GPTS消费"),

        MJ_REFUND(1034, "mj退款"),
        REBATE(1035, "返佣"),
        CARD_RECHARGE(1036, "兑换码充值"),
        REBATE_WITHDRAW(1037, "返佣提现"),

        WXQF(1050, "文心千帆"),
        KNOWLEDGE(1051, "知识库上传"),
        SHARE_KNOWLEDGE(1052, "3.5知识库分享问答"),
        CHAT_KNOWLEDGE(1053, "3.5知识库问答"),

        SPARK_DESK(1054, "星火"),
        CHAT_KNOWLEDGE_4(1055, "4.0知识库问答"),

        V_143(1000, "1.4.3前版本会员次数过期"),

        MEMBER_COUNT_EXP(2001, "会员次数过期"),
        ACTIVITY_GIVE(2021, "余额不足赠送活动"),
        ;

        private Integer type;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum DirectionTypeEnum {
        IN(10, "收入"),
        OUT(20, "支出"),
        ;

        private Integer type;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum ExchangeCardStateEnum {
        EXCHANGE(1, "已兑换"),
        EXPIRES(2, "已过期"),
        OLD(3, "老会员状态"),
        ;

        private Integer state;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum MemberStateEnum {
        DOWN(0, "已下线"),
        ONLINE(1, "已上线"),
        ;

        private Integer state;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum LoginTypeEnum {
        EMAIL(1, "邮箱"),
        PHONE(2, "手机"),
        MP(3, "公众号"),
        WECHAT_AUTH(4, "微信授权登录"),
        ;

        private Integer type;
        private String desc;

        public static LoginTypeEnum getByEnum(Integer type) {
            for (LoginTypeEnum value : LoginTypeEnum.values()) {
                if (value.getType().equals(type)) {
                    return value;
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    enum OpenApiLoginTypeEnum {
        PHONE("phone", "手机", LoginTypeEnum.PHONE.getType()),
        EMAIL("email", "邮箱", LoginTypeEnum.EMAIL.getType()),
        ;
        private String name;
        private String desc;
        private Integer type;

        public static Integer getByType(String name) {
            for (OpenApiLoginTypeEnum value : OpenApiLoginTypeEnum.values()) {
                if (value.getName().equals(name)) {
                    return value.getType();
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    enum MemberRightsTypeEnum {
        CURRENCY(1001, "代币权益", null),
        BUY_PACKAGE(1002, "购买加油包折扣权益", null),

        // 消耗使用
        CONSUME_DISCOUNT(1003, "消耗折扣权益", getConsumeProductTypes()),
        GPT3_FREE(1004, "3.5免费不限量权益", Collections.singletonList(ProductTypeEnum.GPT3_5.getType())),
        GPT4_FREE(1005, "4.0免费不限量权益", Collections.singletonList(ProductTypeEnum.GPT4.getType())),
        WXQF_FREE(1009, "文心一言不限量权益", Collections.singletonList(ProductTypeEnum.WXQF.getType())),
        SPARK_DESK_FREE(1010, "星火不限量权益", Collections.singletonList(ProductTypeEnum.SPARK_DESK.getType())),
        MJ_FREE(1011, "MJ慢速出图免费不限量权益", Collections.singletonList(ProductTypeEnum.MJ.getType())),
        KNOWLEDGE_FREE(1012, "知识库上传不限量权益", Collections.singletonList(ProductTypeEnum.KNOWLEDGE.getType())),
        CHAT_KNOWLEDGE_FREE(1013, "知识库问答不限量权益", Collections.singletonList(ProductTypeEnum.CHAT_KNOWLEDGE.getType())),
        DALLE_FREE(1014, "DALL·E3绘画不限量权益", Collections.singletonList(ProductTypeEnum.DALL_E.getType())),
        GPTS_FREE(1015, "GPTS不限量权益", Collections.singletonList(ProductTypeEnum.GPTS.getType())),

        ;

        private final Integer type;
        private final String desc;
        private final List<Integer> productTypes;
        private static final Map<MemberRightsTypeEnum, AccountTypeEnum> accountTypeEnumMap = new HashMap<>();

        public static List<Integer> getConsumeProductTypes() {
            return Arrays.stream(ProductTypeEnum.values()).map(ProductTypeEnum::getType).collect(Collectors.toList());
        }

        public static MemberRightsTypeEnum getByType(Integer type) {
            for (MemberRightsTypeEnum value : MemberRightsTypeEnum.values()) {
                if (value.getType().equals(type)) {
                    return value;
                }
            }
            return null;
        }


        /**
         * 不限量逻辑
         *
         * @return
         */
        public static List<MemberRightsTypeEnum> getFreeMemberRights() {
            return ListUtil.unmodifiable(ListUtil.of(MemberRightsTypeEnum.GPT3_FREE, MemberRightsTypeEnum.GPT4_FREE,
                    MemberRightsTypeEnum.WXQF_FREE, MemberRightsTypeEnum.SPARK_DESK_FREE, MemberRightsTypeEnum.MJ_FREE,
                    MemberRightsTypeEnum.KNOWLEDGE_FREE, MemberRightsTypeEnum.CHAT_KNOWLEDGE_FREE
            ));
        }

    }

    @Getter
    @AllArgsConstructor
    enum GoodsTypeEnum {
        MEMBER(10, "会员"),
        PACKAGE(20, "流量包"),
        ;

        private Integer type;
        private String desc;

    }

    @Getter
    @AllArgsConstructor
    enum ServiceEnum {
        GPT("GPT", "GPT"),
        MJ("MJ", "MJ"),
        WXQF("WXQF", "文心千帆"),
        APETO("APETO", "APETO"),
        SPARK_DESK("SPARK_DESK", "星火"),
        ;
        private String name;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum WxBotProductTypeEnum {
        GPT3_5(ProductTypeEnum.GPT3_5, WechatBotGpt3Impl.class),
        GPT4(ProductTypeEnum.GPT4, WechatBotGpt3Impl.class),
        GPT_CHAT_KNOWLEDGE_3_5(ProductTypeEnum.CHAT_KNOWLEDGE, WechatBotKnowledge3Impl.class),
        GPT_CHAT_KNOWLEDGE_4(ProductTypeEnum.CHAT_KNOWLEDGE_4, WechatBotKnowledge4Impl.class),
        ;

        private ProductTypeEnum type;
        private final Class<? extends AbstractWechatBotService> chatClass;

        public static WxBotProductTypeEnum getByType(Integer productType) {
            for (WxBotProductTypeEnum value : WxBotProductTypeEnum.values()) {
                if (value.getType().getType().equals(productType)) {
                    return value;
                }
            }
            return null;
        }
    }


    @Getter
    @AllArgsConstructor
    enum ProductTypeEnum {
        GPT3_5(1, "AI3.5", LogDescriptionTypeEnum.GPT3_5CONSUMPTION, Gp3AbstractChatServiceImpl.class, ServiceEnum.GPT),
        GPT4(2, "AI4.0", LogDescriptionTypeEnum.GPT_4CONSUMPTION, Gp4ChatServiceImpl.class, ServiceEnum.GPT),
        MJ(3, "mj绘画", LogDescriptionTypeEnum.MJ_CONSUMPTION, null, ServiceEnum.MJ),
        WXQF(4, "文心千帆", LogDescriptionTypeEnum.WXQF, WxqfChatServiceImpl.class, ServiceEnum.WXQF),
        CHAT_KNOWLEDGE(5, "3.5知识库问答", LogDescriptionTypeEnum.CHAT_KNOWLEDGE, KnowledgeChatServiceImpl.class, ServiceEnum.GPT),
        KNOWLEDGE(6, "知识库上传", LogDescriptionTypeEnum.KNOWLEDGE, null, ServiceEnum.APETO),
        SPARK_DESK(7, "星火", LogDescriptionTypeEnum.SPARK_DESK, SparkDeskChatServiceImpl.class, ServiceEnum.SPARK_DESK),
        DALL_E(8, "DALL_E", LogDescriptionTypeEnum.DALL_E, null, ServiceEnum.GPT),
        GPTS(9, "GPTS", LogDescriptionTypeEnum.GPTS, GPTSAbstractChatServiceImpl.class, ServiceEnum.GPT),
        MJ_FAST(10, "mj快速", LogDescriptionTypeEnum.MJ_CONSUMPTION, null, ServiceEnum.MJ),
        CHAT_KNOWLEDGE_4(11, "4.0知识库问答",  LogDescriptionTypeEnum.CHAT_KNOWLEDGE_4, KnowledgeChatServiceImpl.class, ServiceEnum.GPT),
        ;
        private Integer type;
        private String name;
        private LogDescriptionTypeEnum logDescriptionTypeEnum;
        private final Class<? extends AbstractChatService> chatClass;
        private ServiceEnum serviceEnum;

        public static ProductTypeEnum getByEnum(Integer type) {
            for (ProductTypeEnum value : ProductTypeEnum.values()) {
                if (value.getType().equals(type)) {
                    return value;
                }
            }
            return null;
        }

        public static List<Integer> getSessionViewList() {
            return ListUtil.toList(ProductTypeEnum.GPT3_5.getType(), ProductTypeEnum.GPT4.getType(), ProductTypeEnum.WXQF.getType(), ProductTypeEnum.SPARK_DESK.getType(), ProductTypeEnum.GPTS.getType());
        }
    }

    @Getter
    @AllArgsConstructor
    enum ConsumeTypeEnum {
        A(1, "次"),
        TOKEN(2, "token"),
        ;
        private Integer type;
        private String desc;

        public static ConsumeTypeEnum getByEnum(Integer type) {
            for (ConsumeTypeEnum value : ConsumeTypeEnum.values()) {
                if (value.getType().equals(type)) {
                    return value;
                }
            }
            return null;
        }

    }

    /**
     * 产品类型状态枚举
     */
    @Getter
    @AllArgsConstructor
    enum ProductConsumeStatusEnum {
        UP(1, "上线"),
        OFFLINE(2, "下线"),
        ;
        private Integer status;
        private String desc;

        public static ProductConsumeStatusEnum getByEnum(Integer status) {
            for (ProductConsumeStatusEnum value : ProductConsumeStatusEnum.values()) {
                if (value.getStatus().equals(status)) {
                    return value;
                }
            }
            return null;
        }

    }

    @Getter
    @AllArgsConstructor
    enum ProductConsumeUseAuthEnum {
        MEMBER(1, "会员使用"),
        DOMESTIC(2, "普通用户"),
        ;
        private Integer status;
        private String desc;

        public static ProductConsumeStatusEnum getByEnum(Integer status) {
            for (ProductConsumeStatusEnum value : ProductConsumeStatusEnum.values()) {
                if (value.getStatus().equals(status)) {
                    return value;
                }
            }
            return null;
        }

    }

    @Getter
    @AllArgsConstructor
    enum LeftMenuTypeEnum {
        CHAT(1, "聊天","/#/ai/home"),
        MJ(2, "MJ绘画","/#/ai/mj/generate"),
        AI_NAVIGATION(3, "AI导航","/#/ai/aiNavigation"),
        APP_SQUARE(4, "应用广场","/#/ai/aiAssistant"),
        KNOWLEDGE(5, "知识库","/#/doc/knowledge"),
        MIND_MAP(6, "脑图","/#/ai/mindMap"),
        DALLE(7, "DALLE","/#/ai/dalle3"),
        GPTS(8, "GPTS","/#/ai/gpts"),
        WECHATBOOT(9, "智能客服","/#/ai/wechatboot"),
        ;
        private Integer type;
        private String desc;
        private String urlsuffix;

        public static LeftMenuTypeEnum getByEnum(Integer type) {
            for (LeftMenuTypeEnum value : LeftMenuTypeEnum.values()) {
                if (value.getType().equals(type)) {
                    return value;
                }
            }
            return null;
        }

    }

    @Getter
    @AllArgsConstructor
    enum RoleTypeEnum {
        //        MY(0, "我的助手"),
//        HOT(1, "热门"),
        TOOL(2, "实用工具"),
        TEXT(3, "文案创作"),
        DOMAIN_EXPERT(4, "领域专家"),
        PROGRAM(5, "编程导航"),
        EDUCATION(6, "教育"),
        LIFE(7, "生活指南"),
        OTHER(8, "其他"),
        ;
        private Integer type;
        private String desc;

    }

    @Getter
    @AllArgsConstructor
    enum CardStatusEnum {
        WAIT(0, "待兑换"),
        SUCCESS(1, "已兑换"),
        EXPIRE(2, "已过期"),
        CANCEL(3, "已作废"),
        ;
        private final Integer status;
        private final String desc;

    }

    @Getter
    @AllArgsConstructor
    enum AccountTypeEnum {
        TOKEN_BALANCE(10, "代币账户"),
        RMB_BALANCE(20, "RMB账户"),
        KNOWLEDGE(30, "会员赠送知识库上传次数"),
        MJ(40, "会员赠送MJ次数"),
        ;
        private final Integer type;
        private final String desc;


    }

    @Getter
    @AllArgsConstructor
    enum ChatLogTypeEnum {

        //CHAT: 聊天 KNOWLEDGE: 知识库 MIND_MAP: 脑图
        CHAT,
        KNOWLEDGE,
        MIND_MAP,
        ;
    }

    @Getter
    @AllArgsConstructor
    enum RedisExpireTime {
        FIVE_HOURS(5 * 60 * 60 * 1000L),
        ONE_HOURS(60 * 60 * 1000L),
        FIVE_MINUTES(5L),
        ONE_DAY(1L),
        ;
        private final Long value;
    }
}
