package cn.apeto.geniusai.sdk.entity.fineTune;

import lombok.Data;

import java.io.Serializable;

@Data
public class FineTuneDeleteResponse implements Serializable {

    private String id;

    private String object;

    private boolean deleted;

}
