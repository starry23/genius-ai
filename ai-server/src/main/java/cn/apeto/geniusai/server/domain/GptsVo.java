package cn.apeto.geniusai.server.domain;

import cn.apeto.geniusai.server.domain.vo.Gpts;
import lombok.Data;

import java.util.List;

/**
 * @author wanmingyu
 * @create 2023/12/23 2:06 下午
 */
@Data
public class GptsVo {

    private List<Gpts> gpts;
    private List<String> tag;


}
