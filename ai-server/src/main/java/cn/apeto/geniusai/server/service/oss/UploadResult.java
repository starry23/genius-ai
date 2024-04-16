package cn.apeto.geniusai.server.service.oss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description： 文件长传
 * @modified By：
 */
@Data
public class UploadResult {
    @Schema(description = "编码")
    private Integer code=0;

    @Schema(description = "消息")
    private String message="success";

    @Schema(description = "文件路径")
    private String path;

    @Schema(description = "完整地址")
    private String fullUrl;

    @Schema(description = "图片域名")
    private String host;

    public UploadResult(String host,String path) {
        if(!path.startsWith("/")) {
            path = "/" + path;
        }
        this.host = host;
        this.path = path;
        this.fullUrl= host + path;
    }

    public UploadResult(String host,String path,String fullUrl) {
        this.host = host;
        this.path = path;
        this.fullUrl = fullUrl;
    }

    public UploadResult() {
    }


}

