package cn.apeto.geniusai.server.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wanmingyu
 * @create 2023/12/23 3:15 下午
 */
@Data
public class GptsRootBean {

    private Data data;
    private int error;
    private String error_des;


    @lombok.Data
    public static class Data {

        private List<Gpts> list;
    }

}
