package cn.apeto.geniusai.server.handler.rights;

import cn.apeto.geniusai.server.domain.CalculateRealInfo;
import cn.apeto.geniusai.server.entity.MemberRights;

import java.math.BigDecimal;

/**
 * @author wanmingyu
 * @create 2023/11/1 11:29
 */
public class BenefitFree extends AbsBenefit {

    @Override
    public Boolean doProcessBenefit(MemberRights memberRights, CalculateRealInfo calculateRealInfo) {
        calculateRealInfo.setRealAmount(BigDecimal.ZERO);
        return false;
    }


}
