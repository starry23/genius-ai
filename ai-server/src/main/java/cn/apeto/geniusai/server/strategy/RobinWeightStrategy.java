package cn.apeto.geniusai.server.strategy;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gblhjh
 * @Date: 2023/07/31/14:57
 * @Description:
 */
public interface RobinWeightStrategy extends KeyStrategy{
    KeyStrategy initKey(List<String> keys, int[] weights) ;
}

