package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 微信机器人聊天日志
 * </p>
 *
 * @author apeto
 * @since 2024-01-29 10:32:26
 */
@Getter
@Setter
@TableName("bot_chat_log")
public class BotChatLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 角色
     */
    @TableField("chat_role")
    private String chatRole;

    /**
     * 信息
     */
    @TableField("content")
    private String content;

    /**
     * openid
     */
    @TableField("open_id")
    private String openId;

    /**
     * openid
     */
    @TableField("bot_token")
    private String botToken;


}
