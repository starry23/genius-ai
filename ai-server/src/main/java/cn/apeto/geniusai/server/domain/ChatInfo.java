package cn.apeto.geniusai.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author wanmingyu
 * @create 2023/12/7 14:23
 */
@Data
public class ChatInfo {

    @NotBlank
    private String reqId;
    @NotBlank
    private String prompt;
    @NotNull
    private Integer productType;

    private Boolean internet = false;
    private String logType = "CHAT";
    private String model;

    @JsonIgnore
    private Long userId;

    private String wxBotOpenId;

    private String wxBotToken;

    private Long knowledgeId;
}
