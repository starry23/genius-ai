package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 知识库项目资源表
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
@Getter
@Setter
@TableName("item_resource")
public class ItemResource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 知识库项目ID
     */
    @TableField("item_id")
    private Long itemId;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;
    /**
     * 文件名称
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 对象存储地址
     */
    @TableField("file_path")
    private String filePath;


    /**
     * * 分区ID
     */
    @TableField("partition_id")
    private Long partitionId;

    /**
     * 总结
     */
    @TableField("summary_desc")
    private String summaryDesc;


    /**
     * q1
     */
    @TableField("q1")
    private String q1;


    /**
     * q2
     */
    @TableField("q2")
    private String q2;
}
