package cn.apeto.geniusai.server.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/05/18:04
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("share_item_detail_log")
public class ShareItemDetailLog extends BaseEntity{
    @TableField("share_id")
    private Long shareId;

    @TableField("question")
    private String question;

    @TableField("answer")
    private String answer;

    @TableField("req_id")
    private String reqId;

    @TableField("log_desc")
    private String logDesc;

    @TableField("product_type")
    private Integer productType;

    @TableField("token")
    private Long token;
}
