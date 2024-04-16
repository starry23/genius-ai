package cn.apeto.geniusai.server.domain.vo;

import lombok.Data;

/**
 * @author apeto
 * @create 2023/8/14 17:55
 */
@Data
public class MilvusVO {

    private String ip;
    private Integer port = 19530;
    private String name = "root";
    private String password = "Milvus";
    private String oldPassword;
}
