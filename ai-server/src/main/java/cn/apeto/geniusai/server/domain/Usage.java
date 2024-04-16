package cn.apeto.geniusai.server.domain;

import lombok.Data;


@Data
public class Usage {
    private Long promptTokens = 0L;
    private Long completionTokens = 0L;
    private Long totalTokens = 0L;
}
