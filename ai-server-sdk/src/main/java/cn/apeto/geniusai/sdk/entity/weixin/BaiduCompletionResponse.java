package cn.apeto.geniusai.sdk.entity.weixin;


import lombok.Data;

import java.io.Serializable;

/**
 * @author wanmingyu
 * @create 2023/7/20 15:09
 */
@Data
public class BaiduCompletionResponse implements Serializable {


    /**
     * 本轮对话的id
     */
    private String id;
    /**
     * 回包类型
     * chat.completion：多轮对话返回
     */
    private String object;
    /**
     * 时间戳
     */
    private Integer created;
    /**
     * 表示当前子句的序号。只有在流式接口模式下会返回该字段
     */
    private Integer sentence_id;
    /**
     * 表示当前子句是否是最后一句。只有在流式接口模式下会返回该字段
     */
    private Boolean is_end;
    /**
     * 表示当前子句是否是最后一句。只有在流式接口模式下会返回该字段
     */
    private String result;
    /**
     * 当前生成的结果是否被截断
     */
    private Boolean is_truncated;
    /**
     * 表示用户输入是否存在安全，是否关闭当前会话，清理历史回话信息
     * true：是，表示用户输入存在安全风险，建议关闭当前会话，清理历史会话信息
     * false：否，表示用户输入无安全风险
     */
    private Boolean need_clear_history;
    private Usage usage;
    private String finish_reason;


    @Data
    public static class Usage {
        /**
         * 问题tokens数
         */
        private Integer prompt_tokens;
        /**
         * 回答tokens数
         */
        private Integer completion_tokens;
        /**
         * tokens总数
         */
        private Integer total_tokens;

    }

}
