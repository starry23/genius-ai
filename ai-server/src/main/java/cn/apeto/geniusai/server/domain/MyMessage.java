package cn.apeto.geniusai.server.domain;

import lombok.Data;

@Data
public class MyMessage {
    private String role;
    private String content;
    private String name;

    public MyMessage() {
    }

    public MyMessage(String role, String content, String name) {
        this.role = role;
        this.content = content;
        this.name = name;
    }

    public MyMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
