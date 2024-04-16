package cn.apeto.geniusai.server.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author apeto
 * @create 2023/8/6 4:54 下午
 */
public interface KnowledgeConstants {


    @Getter
    @AllArgsConstructor
    enum ShareTypeEnum {
        WEB(10, "web分享"),
        ;

        private Integer code;
        private String desc;
    }


    @Getter
    @AllArgsConstructor
    enum ShareStatusEnum {
        YES(1, "分享"),
        NOT(0, "关闭分享"),
        ;

        private Integer code;
        private String desc;
    }

}
