package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.Constants;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/6/1 13:20
 */
@Data
public class CreateSessionDTO {
    private Long roleId = -1L;
    private Integer productType;

    private String sessionName;
    private String model;
    private String sessionDesc;
    private String logType = Constants.ChatLogTypeEnum.CHAT.name();
}
