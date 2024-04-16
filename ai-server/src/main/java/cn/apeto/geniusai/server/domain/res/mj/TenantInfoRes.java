package cn.apeto.geniusai.server.domain.res.mj;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author apeto
 * @create 2023/7/6 16:53
 */
@Data
@Schema(description = "租户信息")
public class TenantInfoRes {

    @Schema(description = "租户余额")
    private Integer accountCount;

    @Schema(description = "租户状态")
    private Integer status;

    @Schema(description = "联系人")
    private String contactMobile;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;
}
