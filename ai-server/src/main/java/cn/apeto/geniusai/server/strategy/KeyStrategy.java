package cn.apeto.geniusai.server.strategy;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/07/31/15:03
 * @Description:
 */

import java.util.ArrayList;
import java.util.List;

/**
 * *    策略接口
 */
public interface KeyStrategy {
    // 本地密钥
    List<String> LOCAL_KEYS = new ArrayList();

    List<String> getKeys();

    String strategy();


    /**
     * * 初始化key
     * @param keys
     * @return
     */
    default KeyStrategy initKey(List<String> keys) {
        LOCAL_KEYS.addAll(keys);
        return this;
    }

    /**
     * * 移除key
     * @param key
     */
    default void removeErrorKey(String key) {
        LOCAL_KEYS.remove(key);
    }

    /**
     * *    所有key失效是的预警
     */
    default void keysWarring() {
    }
}
