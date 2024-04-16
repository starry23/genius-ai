package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 会员策略
 * </p>
 *
 * @author warape
 * @since 2023-05-01 12:47:39
 */
@Schema(description = "会员策略配置")
@Getter
@Setter
@TableName("member_rights")
public class MemberRights extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 策略名称
     */
    @Schema(description = "策略名称")
    @TableField("rights_name")
    private String rightsName;

    /**
     * 权益描述
     */
    @Schema(description = "权益描述")
    @TableField("rights_desc")
    private String rightsDesc;

    /**
     * 权益描述 {@link cn.apeto.geniusai.server.domain.Constants.MemberRightsTypeEnum}
     */
    @Schema(description = "权益描述")
    @TableField("rights_type")
    private Integer rightsType;
    /**
     * 会员编码
     */
    @Schema(description = "会员编码")
    @TableField("member_code")
    private String memberCode;

    /**
     * 数量
     */
    @Schema(description = "数量")
    @TableField("count")
    private Integer count;

    /**
     * 折扣
     */
    @Schema(description = "折扣")
    @TableField("discount")
    private BigDecimal discount;

}
