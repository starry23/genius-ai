package cn.apeto.geniusai.sdk.entity.embeddings;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Item implements Serializable {
    private String object;
    private List<Float> embedding;
    private Integer index;
}
