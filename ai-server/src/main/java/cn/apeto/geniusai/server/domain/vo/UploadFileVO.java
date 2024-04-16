package cn.apeto.geniusai.server.domain.vo;

import lombok.Data;

/**
 * @author apeto
 * @create 2023/7/1 6:27 下午
 */
@Data
public class UploadFileVO {

    private Long fileId;

    private String path;

    private String fullUrl;
}
