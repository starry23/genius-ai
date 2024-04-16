package cn.apeto.geniusai.server.domain;

import lombok.Data;

/**
 * @author apeto
 * @create 2023/6/10 4:41 下午
 */
@Data
public class StateDTO {

    private Long userId;
    private String fileId;
    private String apiKey;
}
