package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 账户表日志
 * </p>
 *
 * @author warape
 * @since 2023-04-02 05:10:18
 */
@Getter
@Setter
@TableName("account_log")
@Schema(description = "账户表日志")
public class AccountLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @TableField("account_id")
    private Long accountId;

    /**
     * 变动金额
     */
    @Schema(description = "变动金额")
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 变动后余额
     */
    @Schema(description = "变动后余额")
    @TableField("balance_amount")
    private BigDecimal balanceAmount;

    /**
     * 请求ID
     */
    @Schema(description = "请求ID")
    @TableField("request_id")
    private String requestId;
    /**
     * 订单号
     */
    @Schema(description = "订单号")
    @TableField("outside_code")
    private String outsideCode;

    /**
     * 描述
     */
    @Schema(description = "描述")
    @TableField("log_description")
    private String logDescription;

    /**
     * 1: 充值 2: 失效回收 3: 退款 4: 消费
     */
    @Schema(description = "操作类型")
    @TableField("log_description_type")
    private Integer logDescriptionType;

    /**
     * 类型 10: 转入 20:转出
     */
    @Schema(description = "类型 10: 转入 20:转出")
    @TableField("direction_type")
    private Integer directionType;


}
