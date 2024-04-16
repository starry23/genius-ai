package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 知识库&聊天记录绑定
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
@Getter
@Setter
@TableName("knowledge_chat_binding")
public class KnowledgeChatBinding extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 知识库ID
     */
    @TableField("knowledge_id")
    private Long knowledgeId;

    /**
     * 聊天日志ID
     */
    @TableField("chat_log_req")
    private String chatLogReq;


}
