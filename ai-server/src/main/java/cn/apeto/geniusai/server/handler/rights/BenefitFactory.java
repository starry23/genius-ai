package cn.apeto.geniusai.server.handler.rights;

import cn.hutool.core.collection.CollUtil;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.entity.MemberRights;

import java.util.*;

/**
 * @author wanmingyu
 * @create 2023/11/1 14:38
 */
public class BenefitFactory {

    /**
     * 权益策略
     */
    private static final Map<Integer, BenefitPair> BENEFITS_MAP = new HashMap<>();

    static {
        BENEFITS_MAP.put(Constants.MemberRightsTypeEnum.CONSUME_DISCOUNT.getType(), new BenefitPair(new BenefitConsumeDiscount(), 0));

        BENEFITS_MAP.put(Constants.MemberRightsTypeEnum.GPT3_FREE.getType(), new BenefitPair(new BenefitFree(), 1));
        BENEFITS_MAP.put(Constants.MemberRightsTypeEnum.GPT4_FREE.getType(), new BenefitPair(new BenefitFree(), 1));
        BENEFITS_MAP.put(Constants.MemberRightsTypeEnum.WXQF_FREE.getType(), new BenefitPair(new BenefitFree(), 1));
        BENEFITS_MAP.put(Constants.MemberRightsTypeEnum.SPARK_DESK_FREE.getType(), new BenefitPair(new BenefitFree(), 1));
        BENEFITS_MAP.put(Constants.MemberRightsTypeEnum.MJ_FREE.getType(), new BenefitPair(new BenefitFree(), 1));
        BENEFITS_MAP.put(Constants.MemberRightsTypeEnum.KNOWLEDGE_FREE.getType(), new BenefitPair(new BenefitFree(), 1));
        BENEFITS_MAP.put(Constants.MemberRightsTypeEnum.CHAT_KNOWLEDGE_FREE.getType(), new BenefitPair(new BenefitFree(), 1));
        BENEFITS_MAP.put(Constants.MemberRightsTypeEnum.DALLE_FREE.getType(), new BenefitPair(new BenefitFree(), 1));
        BENEFITS_MAP.put(Constants.MemberRightsTypeEnum.GPTS_FREE.getType(), new BenefitPair(new BenefitFree(), 1));


    }

    /**
     * 从给定的MemberRights列表中获取优先级队列的权益
     *
     * @param memberRightsList MemberRights列表
     * @return 优先级队列的权益
     */
    public static PriorityQueue<BenefitPair> getBenefit(List<MemberRights> memberRightsList,Integer productType) {
        PriorityQueue<BenefitPair> benefitPairPriorityQueue = new PriorityQueue<>(Comparator.comparingInt(BenefitPair::getWeight).reversed());

        if (CollUtil.isEmpty(memberRightsList)) {
            return benefitPairPriorityQueue;
        }

        for (MemberRights memberRights : memberRightsList) {
            Integer rightsType = memberRights.getRightsType();
            Constants.MemberRightsTypeEnum memberRightsTypeEnum = Constants.MemberRightsTypeEnum.getByType(rightsType);
            List<Integer> productTypes;
            if (memberRightsTypeEnum == null || CollUtil.isEmpty(productTypes = memberRightsTypeEnum.getProductTypes())) {
                continue;
            }

            if(!productTypes.contains(productType)){
                continue;
            }

            BenefitPair benefitPair = BENEFITS_MAP.get(rightsType);
            if (benefitPair == null) {
                continue;
            }
            benefitPair.setMemberRights(memberRights);
            Optional.of(benefitPair).ifPresent(benefitPairPriorityQueue::add);
        }

        return benefitPairPriorityQueue;
    }


}
