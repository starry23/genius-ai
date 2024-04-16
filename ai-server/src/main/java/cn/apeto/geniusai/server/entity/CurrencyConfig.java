package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.apeto.geniusai.server.entity.BaseEntity;
import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 币配置
 * </p>
 *
 * @author warape
 * @since 2023-06-16 05:47:30
 */
@Getter
@Setter
@TableName("currency_config")
public class CurrencyConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 币数量
     */
    @Schema(description = "币数量")
    @TableField("currency_count")
    private Integer currencyCount;

    /**
     * 商品金额
     */
    @Schema(description = "商品金额")
    @TableField("currency_amount")
    private BigDecimal currencyAmount;
    /**
     * 划线金额
     */
    @Schema(description = "划线金额")
    @TableField("line_amount")
    private BigDecimal lineAmount;

    /**
     * 推荐
     */
    @Schema(description = "推荐")
    @TableField("recommend")
    private Boolean recommend;

}
