package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 兑换卡详情
 * </p>
 *
 * @author warape
 * @since 2023-04-05 04:51:24
 */
@Getter
@Setter
@TableName("exchange_card_detail")
@Schema(description = "ExchangeCardDetail")
public class ExchangeCardDetail extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /**
   * 用户ID
   */
  @Schema(description = "用户ID")
  @TableField("user_id")
  private Long userId;

  /**
   * 会员卡ID
   */
  @Schema(description = "会员卡ID")
  @TableField("member_card_id")
  private Long memberCardId;

  /**
   * 账户ID
   */
  @Schema(description = "账户ID")
  @TableField("account_id")
  private Long accountId;

  /**
   * 失效时间
   */
  @Schema(description = "失效时间")
  @TableField("expires_time")
  private Date expiresTime;

  /**
   * 状态 1:已兑换 2:过期
   */
  @Schema(description = "状态 1:已兑换 2:过期")
  @TableField("exchange_state")
  private Integer exchangeState;

  /**
   * 剩余次数
   */
  @Schema(description = "剩余次数")
  @TableField("surplus_count")
  private Integer surplusCount;

}
