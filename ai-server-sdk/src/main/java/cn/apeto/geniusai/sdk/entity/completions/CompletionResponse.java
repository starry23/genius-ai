package cn.apeto.geniusai.sdk.entity.completions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cn.apeto.geniusai.sdk.entity.common.Choice;
import cn.apeto.geniusai.sdk.entity.common.OpenAiResponse;
import cn.apeto.geniusai.sdk.entity.common.Usage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 描述： 答案类
 *
 * @author https:www.unfbx.com
 *  2023-02-11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponse extends OpenAiResponse implements Serializable {
    private String id;
    private String object;
    private long created;
    private String model;
    private Choice[] choices;
    private Usage usage;
}
