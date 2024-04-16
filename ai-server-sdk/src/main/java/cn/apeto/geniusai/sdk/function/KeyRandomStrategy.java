package cn.apeto.geniusai.sdk.function;

import cn.hutool.core.util.RandomUtil;
import cn.apeto.geniusai.sdk.entity.KeyInfo;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;

import java.util.List;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @since 2023-04-03
 */
public class KeyRandomStrategy implements KeyStrategy {

    @Override
    public KeyInfo strategy(ChatCompletion.ModelType modelType, String modelName) {
        return RandomUtil.randomEle(getKeys(modelType,modelName));
    }

    @Override
    public List<KeyInfo> getKeys(ChatCompletion.ModelType modelType, String modelName) {
        return LOCAL_KEYS;
    }


}
