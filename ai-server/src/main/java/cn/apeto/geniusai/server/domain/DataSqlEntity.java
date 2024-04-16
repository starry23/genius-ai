package cn.apeto.geniusai.server.domain;

import lombok.Data;

import java.util.List;

/**
 * @author apeto
 * @create 2023/7/31 13:55
 */
@Data
public class DataSqlEntity {

    /**
     * 分段后的每一段的向量
     */
    private List<List<Float>> ll;

    /**
     * 每一段的内容
     */
    private List<String> content;

    /**
     * 总共token数量
     */
    private Integer total_token;

    private String indexId;

    /**
     * * 各个分片的ID - UUID
     */
    private List<Long> docId;
}
