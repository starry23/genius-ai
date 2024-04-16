package cn.apeto.geniusai.server.handler.rights;

import cn.apeto.geniusai.server.domain.CalculateRealInfo;
import cn.apeto.geniusai.server.entity.MemberRights;

/**
 * @author wanmingyu
 * @create 2023/11/1 11:29
 */
public interface Benefit {

    Boolean processBenefit(MemberRights memberRights, CalculateRealInfo calculateRealInfo);

}
