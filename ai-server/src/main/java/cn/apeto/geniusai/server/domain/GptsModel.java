package cn.apeto.geniusai.server.domain;

import lombok.Data;

/**
 * @author wanmingyu
 * @create 2023/12/7 15:49
 */
@Data
public class GptsModel {

    private String gptName;
    private String gptDesc;
    private String gptLogo;
    private String gptGizmoId;
    private Integer category;
}
