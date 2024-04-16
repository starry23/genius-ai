package cn.apeto.geniusai.server.utils;


import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.stereotype.Component;

/**
 * @author apeto
 * @create 2023/7/6 17:18
 */
@Component
public class StpManagerUtil {

    private StpManagerUtil() {
    }

    /**
     * 账号类型标识
     */
    public static final String TYPE = "manager";

    /**
     * 底层的 StpLogic 对象
     */
    public static StpLogic stpLogic = new StpLogic(TYPE);

    public static void login(Object id, long timeout) {
        stpLogic.login(id, timeout);
    }

    public static String getId() {
        return stpLogic.getLoginIdAsString();
    }

    /**
     * 会话注销，根据账号id
     *
     * @param loginId 账号id
     */
    public static void logout(Object loginId) {
        stpLogic.logout(loginId);
    }

    public static String getLoginType() {
        return stpLogic.getLoginType();
    }

    /**
     * 重置 StpLogic 对象
     * <br> 1、更改此账户的 StpLogic 对象
     * <br> 2、put 到全局 StpLogic 集合中
     * <br> 3、发送日志
     *
     * @param newStpLogic /
     */
    public static void setStpLogic(StpLogic newStpLogic) {
        // 重置此账户的 StpLogic 对象
        stpLogic = newStpLogic;

        // 添加到全局 StpLogic 集合中
        // 以便可以通过 SaManager.getStpLogic(type) 的方式来全局获取到这个 StpLogic
        SaManager.putStpLogic(newStpLogic);

        // $$ 全局事件
        SaTokenEventCenter.doSetStpLogic(stpLogic);
    }

    /**
     * 获取 StpLogic 对象
     *
     * @return /
     */
    public static StpLogic getStpLogic() {
        return stpLogic;
    }
}
