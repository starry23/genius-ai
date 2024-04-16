package cn.apeto.geniusai.sdk.entity;

import lombok.Data;
import okhttp3.RequestBody;


@Data
public class KeyInfo {

    private String key;
    private String apiHost;
    private RequestBody requestBody;

    public KeyInfo(String key) {
        this.key = key;
    }

    public KeyInfo(String key, String apiHost) {
        this.key = key;
        this.apiHost = apiHost;
    }

    public KeyInfo() {
    }
}
