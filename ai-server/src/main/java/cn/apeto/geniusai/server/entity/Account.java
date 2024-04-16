package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 账户表
 * </p>
 *
 * @author warape
 * @since 2023-04-02 05:10:17
 */
@Getter
@Setter
@TableName("account")
public class Account extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /**
   * 用户ID
   */
  @TableField("user_id")
  private Long userId;

  /**
   * 账户余额
   */
  @TableField("account_balance")
  private BigDecimal accountBalance;

  /**
   * 账户余额
   */
  @TableField("account_type")
  private Integer accountType;

}
