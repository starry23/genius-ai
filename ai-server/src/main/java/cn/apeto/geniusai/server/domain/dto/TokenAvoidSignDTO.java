package cn.apeto.geniusai.server.domain.dto;

import lombok.Data;

/**
 * @author wanmingyu
 * @create 2023/12/30 3:03 下午
 */
@Data
public class TokenAvoidSignDTO {

    private String accountNum;
    private String accountType;
    private String password;
    private Long timestamp;
}
