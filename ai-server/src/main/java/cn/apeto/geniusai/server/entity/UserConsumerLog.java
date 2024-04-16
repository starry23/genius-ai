package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 用户消耗日志
 * </p>
 *
 * @author apeto
 * @since 2023-08-30 03:37:25
 */
@Getter
@Setter
@TableName("user_consumer_log")
public class UserConsumerLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 账户id
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * orderNo
     */
    @TableField("order_no")
    private String orderNo;


    /**
     * 商品类型
     */
    @TableField("product_type")
    private Integer productType;

    /**
     * 原价
     */
    @TableField("original_amount")
    private BigDecimal originalAmount;

    /**
     * 实付
     */
    @TableField("real_amount")
    private BigDecimal realAmount;

    /**
     * 会员价
     */
    @TableField("member_amount")
    private BigDecimal memberAmount;

    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;


}
