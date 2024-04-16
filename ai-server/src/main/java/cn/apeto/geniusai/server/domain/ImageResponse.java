package cn.apeto.geniusai.server.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author apeto
 * @create 2023/6/13 14:14
 */
@Data
public class ImageResponse {
    private int code;

    private String description;

    private String result;

    private Map<String, Object> properties = new HashMap<>();

}
