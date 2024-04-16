package cn.apeto.geniusai.server.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author apeto
 * @create 2023/4/8 6:56 下午
 */
@Data
public class MemberCardVo {

  /**
   * 卡天数
   */
  @Schema(description = "会员卡ID")
  private Long memberId;
  /**
   * 会员卡名
   */
  @Schema(description = "会员卡名")
  private String cardName;

  /**
   * 卡金额
   */
  @Schema(description = "卡金额")
  private BigDecimal amount;

  /**
   * 卡天数
   */
  @Schema(description = "卡天数")
  private Integer cardDay;

  /**
   * 划线金额
   */
  @Schema(description = "划线金额")
  private BigDecimal lineAmount;

  /**
   * 推荐
   */
  @Schema(description = "推荐")
  private Boolean recommend;

  @Schema(description = "权益列表")
  private List<RightConfigVo> rightList;
}
