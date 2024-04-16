package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.domain.CalculateRealInfo;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.ExchangeCardDetail;
import cn.apeto.geniusai.server.entity.MemberCard;
import cn.apeto.geniusai.server.entity.MemberRights;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 兑换卡详情 服务类
 * </p>
 *
 * @author warape
 * @since 2023-04-05 04:51:24
 */
public interface ExchangeCardDetailService extends IService<ExchangeCardDetail> {

    void consume(Long userId, Integer productType, String reqId, Long tokens);

    Constants.ProductTypeEnum checkConsume(Long userId, Integer productType, Long tokens);

    CalculateRealInfo calculateRealCurrency(Long userId, Integer productType, Long tokens);

    ExchangeCardDetail getByFirstOne(Long userId);

    ExchangeCardDetail getSurplusCountByFirstOne(Long userId);

    MemberRights getUserFirsMemberRights(Long userId, Constants.MemberRightsTypeEnum memberRightsTypeEnum);

    List<MemberRights> getUserAllMemberRights(ExchangeCardDetail exchangeCardDetail);

    void exchange(Long userId, MemberCard memberCard, String operatorName, boolean isManager);

    List<ExchangeCardDetail> selectByUserId(Long userId);

    List<ExchangeCardDetail> selectExpire();

    List<TrendVO<Integer>> exchangeCardTrend(Integer day);

    void consumeCard(Long id, BigDecimal changeAmount);
}
