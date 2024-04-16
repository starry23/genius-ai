package cn.apeto.geniusai.server.domain;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author apeto
 * @create 2023/4/6 10:17 下午
 */
public interface SystemConstants {


    @Getter
    @AllArgsConstructor
    enum RedisKeyEnum {
        QUESTIONS("questions:{}:{}", "提问缓存key userId,reqId"),
        SESSION_ROLE_SYSTEM("session_role_system:{}:{}", "提问缓存key userId reqId  value:提示词"),
        DALLE_GENERATE("dalle_generate:{}:{}", "DALLE防重"),
        MJ_GENERATE("mj_generate:{}:{}", "MJ防重"),
        QUESTIONS_LOCK("questionsLock:{}:{}", "提问缓存key userId,reqId"),
        ITEM_UPLOAD_LOCK("item_upload:{}:{}", "知识库上传 itemId,userId"),
        DEL_RESOURCES_LOCK("delResources:{}", "删除资源 userId"),
        CREATE_ITEM_LOCK("createItem:{}", "key:userId"),
        SMS_IMAGE_CODE("SMS_IMAGE_CODE:{}", "图片验证码"),
        SMS_LOCK("SMS_LOCK:{}", "发送验证码锁"),
        SMS_CODE("SMS_CODE:{}:{}", "短信&游戏验证码key"),
        SMS_CODE_COUNT_LIMIT("SMS_CODE_COUNT_LIMIT:{}:{}", "短信验证码次数限制key"),
        SEE_KEY("SEE_KEY:{}:{}", "seeKey"),
        SMS_SIGN_UP_LOCK("SMS_SIGN_UP_LOCK:{}", "注册锁"),
        SMS_SEND_LOCK("SMS_SEND_LOCK:{}", "发送短信"),
        UPDATE_PASSWORD_LOCK("UPDATE_PASSWORD_LOCK:{}", "修改密码"),
        WECHAT_LOGIN_LOCK("WECHAT_LOGIN_LOCK:{}", "微信登录"),
        BUY_MEMBER_LOCK("BUY_MEMBER_LOCK:{}", "购买会员"),
        FEEDBACK_SEND_LOCK("FEEDBACK_SEND_LOCK:{}", "反馈建议"),
        WECHAT_QR_LOGIN_CODE("WECHAT_QR_LOGIN_CODE:{}", "微信扫码登录编码"),
        WECHAT_GIVE_LOCK("WECHAT_GIVE_LOCK:{}", "微信领取次数锁"),
        WEB_CONFIG("WEB_CONFIG", "站点配置"),
        OSS_CONFIG("OSS_CONFIG", "OSS配置"),
        CHAT_CONFIG("CHAT_CONFIG", "chatGPT参数配置"),
        BAIDU_ACCESS_TOKEN("BAIDU_ACCESS_TOKEN:{}", "百度access_token"),
        MJ_STATUS_JOB("MJ_STATUS_JOB", "mj状态定时任务 FILE_ID"),
        EXPIRE_TASK("EXPIRE_TASK", "mj过期任务"),
        EXPIRE_TASK30("EXPIRE_TASK30", "mj 30天过期任务"),
        PAY_QUERY("payQuery", "pay job"),
        EXPIRE_MEMBER("expireMember", "member job"),
        CARD_EXPIRE_MEMBER("cardExpireMember", "card job"),
        PAYMENT_INFO_LOCK("PAYMENT_INFO_LOCK:{}", "支付状态修改 paysn"),
        PAY_SETTING("pay_setting", "支付配置"),
        SHARE_LINK_SESSION("SHARE_LINK_SESSION:{}","分享会话REQID"),
        MILVUS("MILVUS","向量数据库设置"),
        ALERT_GIVE_CURRENCY("alert_give_currency:{}:{}","uid:yyyy-MM-dd"),
        KNOWLEDGE_WELCOME_SETTING("KNOWLEDGE_WELCOME_SETTING:{}","itemId"),
        KNOWLEDGE_MENUS_SETTING("KNOWLEDGE_MENUS_SETTING:{}","itemId"),
        GPTS_LIST("GPTS_LIST:{}", "gpts列表"),
        GPTS_LIST_EXP("GPTS_LIST_EXP:{}", "gpts列表"),
        WX_BOT_CHAT_LOG("WX_BOT_CHAT_LOG:{}:{}", "微信机器人问答日志"),
        WX_BOT_CHAT_REQ_LOCK("WX_BOT_CHAT_REQ_LOCK:{}:{}:{}", "LOCK"),
        WX_BOT_CHAT_REQ_TARGET_LOCK("WX_BOT_CHAT_REQ_TARGET_LOCK:{}:{}:{}", "LOCK"),
        ;

        private String key;
        private String desc;

        public String getKey(Object... params) {
            return StrUtil.format(this.key, params);
        }
    }
}
