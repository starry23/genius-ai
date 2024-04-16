package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.apeto.geniusai.server.entity.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * mj图片信息
 * </p>
 *
 * @author apeto
 * @since 2023-11-15 02:48:01
 */
@Getter
@Setter
@TableName("dall_e_image_info")
public class DallEImageInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 文件ID
     */
    @TableField("file_id")
    private String fileId;

    /**
     * openai图片地址
     */
    @TableField("openai_image_url")
    private String openaiImageUrl;

    /**
     * url
     */
    @TableField("cos_url")
    private String cosUrl;

    /**
     * 提示词
     */
    @TableField("file_prompt")
    private String filePrompt;

    /**
     * 失败原因
     */
    @TableField("fail_reason")
    private String failReason;


}
