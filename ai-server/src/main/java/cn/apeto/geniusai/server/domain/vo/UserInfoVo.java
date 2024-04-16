package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author apeto
 * @create 2023/4/8 7:30 下午
 */
@Schema(description = "用户信息VO")
@Data
public class UserInfoVo {

  @Schema(description = "注册时间")
  private Date createTime;

  @Schema(description = "昵称")
  private String nikeName;

  @Schema(description = "头像")
  private String headImgUrl;

  @Schema(description = "会员标识")
  private Boolean memberFlag;

  @Schema(description = "注册类型")
  private Integer registerType;

  @Schema(description = "电话号码")
  private String phone;

  @Schema(description = "电话号码")
  private String email;

  @Schema(description = "返佣等级")
  private String rebateLevel;

  @Schema(description = "邀请数量")
  private Integer inviteCount;
}
