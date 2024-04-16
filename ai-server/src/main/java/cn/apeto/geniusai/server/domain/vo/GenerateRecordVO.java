package cn.apeto.geniusai.server.domain.vo;

import lombok.Data;

/**
 * @author wanmingyu
 * @create 2023/11/15 14:59
 */
@Data
public class GenerateRecordVO {

    private String prompt;

    private String url;

    private String fileId;
}
