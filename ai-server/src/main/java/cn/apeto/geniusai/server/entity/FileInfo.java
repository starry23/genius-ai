package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 附件信息
 * </p>
 *
 * @author apeto
 * @since 2024-01-30 04:09:53
 */
@Getter
@Setter
@TableName("file_info")
public class FileInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 地址
     */
    @TableField("path")
    private String path;

    /**
     * 文件原名
     */
    @TableField("original_file_name")
    private String originalFileName;

    /**
     * oss类型 0本地 1七牛云 2腾讯云 3阿里云
     */
    @TableField("type")
    private Integer type;

    /**
     * 显示类型 1:c端  2:b端
     */
    @TableField("view_type")
    private Integer viewType;


}
