package cn.apeto.geniusai.server.domain.dto;

import lombok.Data;

/**
 * @author apeto
 * @create 2023/7/27 15:54
 */
@Data
public class QuestionsDTO {

    private String prompt;
    private String reqId;
    private Integer productType;
}
