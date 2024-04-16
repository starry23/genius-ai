package cn.apeto.geniusai.sdk.function;


import cn.apeto.geniusai.sdk.entity.KeyInfo;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 描述：key 的获取策略 jdk默认实现
 *
 * @author https:www.unfbx.com
 * @see Function
 * @since 2023-04-03
 */
public interface KeyStrategy {


  List<KeyInfo> LOCAL_KEYS = new ArrayList<>();

  KeyInfo strategy (ChatCompletion.ModelType modelType, String modelName);


  List<KeyInfo> getKeys (ChatCompletion.ModelType modelType, String modelName);

  default KeyStrategy initKey (List<KeyInfo> keys) {
    this.LOCAL_KEYS.addAll(keys);
    return this;
  }

  default void removeErrorKey (String key,String desc) {

  }

  default void keysWarring () {
  }
}
