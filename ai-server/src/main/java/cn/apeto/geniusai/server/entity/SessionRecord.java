package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 会话记录
 * </p>
 *
 * @author apeto
 * @since 2023-12-23 11:50:55
 */
@Getter
@Setter
@TableName("session_record")
public class SessionRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 请求id
     */
    @TableField("request_id")
    private String requestId;

    /**
     * 名字
     */
    @TableField("session_name")
    private String sessionName;

    /**
     * 创建人
     */
    @TableField("session_desc")
    private String sessionDesc;

    /**
     * model
     */
    @TableField("model")
    private String model;

    /**
     * 1:GPT3.5  2:GPT4.0  3:MJ绘画 4: wxqf
     */
    @TableField("product_type")
    private Integer productType;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 日志类型  CHAT: 聊天 KNOWLEDGE: 知识库
     */
    @TableField("log_type")
    private String logType;

    /**
     * 日志类型  CHAT: 聊天 KNOWLEDGE: 知识库
     */
    @TableField("file_id")
    private Long fileId;


}
