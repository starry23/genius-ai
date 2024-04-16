package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

/**
 * @author apeto
 * @create 2023/4/8 7:28 下午
 */
@Schema(description = "会员用户信息")
@Data
public class MemberUserInfo {

    @Schema(description = "期限")
    private Long deadline = 0L;

    @Schema(description = "当前权益")
    private List<RightConfigVo> rightConfigVoList;


}
