package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 开放api商户配置
 * </p>
 *
 * @author apeto
 * @since 2023-12-30 03:13:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("open_api_mch_config")
public class OpenApiMchConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商户描述")
    @TableField("mch_desc")
    private String mchDesc;
    /**
     * 公钥
     */
    @Schema(description = "公钥")
    @TableField("public_key")
    private String publicKey;

    /**
     * 私钥
     */
    @Schema(description = "私钥")
    @TableField("private_key")
    private String privateKey;

    /**
     * 商户号
     */
    @Schema(description = "商户号")
    @TableField("mch_id")
    private String mchId;

    /**
     * 菜单编码
     */
    @Schema(description = "跳转菜单编码")
    @TableField("menu_code")
    private Integer menuCode;


}
