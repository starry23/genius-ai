package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 机器人配置
 * </p>
 *
 * @author apeto
 * @since 2024-01-25 06:00:08
 */
@Getter
@Setter
@TableName("bot_config")
public class BotConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * app_id
     */
    @TableField("app_id")
    private String appId;

    /**
     * aesKey
     */
    @TableField("aesKey")
    private String aesKey;

    /**
     * token
     */
    @TableField("token")
    private String token;

    /**
     * 机器人名称
     */
    @TableField("bot_name")
    private String botName;

    /**
     * 客服名称
     */
    @TableField("kefu_name")
    private String kefuName;

    /**
     * 机器人名称
     */
    @TableField("kefu_avatar")
    private String kefuAvatar;

    @TableField("knowledge_id")
    private Long knowledgeId;

    /**
     * 机器人描述
     */
    @TableField("bot_desc")
    private String botDesc;

    /**
     * H5机器人地址
     */
    @TableField("h5_url")
    private String h5Url;

    /**
     * 产品类型
     */
    @TableField("product_type")
    private Integer productType;

    /**
     * 状态 0下线 1上线
     */
    @TableField("state")
    private Integer state;


}
