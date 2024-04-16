package cn.apeto.geniusai.server.handler.rights;

import cn.apeto.geniusai.server.domain.CalculateRealInfo;
import cn.apeto.geniusai.server.entity.MemberRights;

/**
 * @author wanmingyu
 * @create 2023/11/1 11:29
 */
public abstract class AbsBenefit implements Benefit {

    @Override
    public Boolean processBenefit(MemberRights memberRights, CalculateRealInfo calculateRealInfo) {

        return doProcessBenefit(memberRights, calculateRealInfo);
    }

    protected abstract Boolean doProcessBenefit(MemberRights memberRights, CalculateRealInfo calculateRealInfo);


}
