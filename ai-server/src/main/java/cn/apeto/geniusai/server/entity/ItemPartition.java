package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/03/9:40
 * @Description:
 */
@Data
@TableName("item_partition")
public class ItemPartition extends BaseEntity{

    @TableField("item_id")
    private long itemId;

    @TableField("user_id")
    private long userId;

    /**
     * * 分区名称
     */
    @TableField("partition_name")
    private String partitionName;

    @TableField("partition_desc")
    private String partitionDesc;

    /**
     * * 向量数据库分区名
     */
    @TableField("partition_code")
    private String partitionCode;
}
