package cn.apeto.geniusai.server.handler.rights;

import cn.apeto.geniusai.server.domain.CalculateRealInfo;
import cn.apeto.geniusai.server.entity.MemberRights;

import java.math.BigDecimal;

/**
 * @author wanmingyu
 * @create 2023/11/1 14:34
 */
public class BenefitConsumeDiscount extends AbsBenefit {

    public BenefitConsumeDiscount() {

    }

    @Override
    public Boolean doProcessBenefit(MemberRights memberRights, CalculateRealInfo calculateRealInfo) {
        BigDecimal realAmount = memberRights.getDiscount().multiply(calculateRealInfo.getRealAmount());
        calculateRealInfo.setRealAmount(realAmount);
        return true;
    }

}
