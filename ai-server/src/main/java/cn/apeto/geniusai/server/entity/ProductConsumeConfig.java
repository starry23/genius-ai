package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 产品类型消耗配置表
 * </p>
 *
 * @author warape
 * @since 2023-06-17 06:38:37
 */
@Schema(description = "产品类型消耗配置表")
@Getter
@Setter
@TableName("product_consume_config")
public class ProductConsumeConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 1:GPT3.5  2:GPT4.0  3:MJ绘画
     */
    @Schema(description = "1:GPT3.5  2:GPT4.0  3:MJ绘画")
    @TableField("product_type")
    private Integer productType;

    /**
     * 消耗代币数量
     */
    @Schema(description = "消耗代币数量")
    @TableField("consume_currency")
    private Long consumeCurrency;

    /**
     * 1.次数，2.token
     */
    @Schema(description = "1.次数，2.token 一期不上token")
    @TableField("consume_type")
    private Integer consumeType;

    @Schema(description = "状态 1: 上线 2:下线 默认1")
    @TableField("status")
    private Integer status;

    @Schema(description = "使用权限 1: 会员 2: 常规用户 默认:2")
    @TableField("use_auth")
    private Integer useAuth;


}
