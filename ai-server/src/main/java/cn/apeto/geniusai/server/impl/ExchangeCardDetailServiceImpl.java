package cn.apeto.geniusai.server.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.CalculateRealInfo;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.Constants.ExchangeCardStateEnum;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.*;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.handler.rights.Benefit;
import cn.apeto.geniusai.server.handler.rights.BenefitFactory;
import cn.apeto.geniusai.server.handler.rights.BenefitPair;
import cn.apeto.geniusai.server.mapper.ExchangeCardDetailMapper;
import cn.apeto.geniusai.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import static cn.apeto.geniusai.server.domain.Constants.ResponseEnum.BALANCE_INSUFFICIENT;
import static cn.apeto.geniusai.server.domain.Constants.ResponseEnum.NO_MEMBER;

/**
 * <p>
 * 兑换卡详情 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-04-05 04:51:24
 */
@Service
public class ExchangeCardDetailServiceImpl extends ServiceImpl<ExchangeCardDetailMapper, ExchangeCardDetail> implements ExchangeCardDetailService {


    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private  MemberRightsService memberRightsService;
    @Autowired
    private  AccountService accountService;
    @Autowired
    private  MemberCardService memberCardService;
    @Autowired
    private  ProductConsumeConfigService productConsumeConfigService;
    @Autowired
    private  UserConsumerLogService userConsumerLogService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consume(Long userId, Integer productType, String reqId, Long tokens) {
        Constants.ProductTypeEnum productTypeEnum = Constants.ProductTypeEnum.getByEnum(productType);
        CalculateRealInfo calculateRealInfo = calculateRealCurrency(userId, productType, tokens);
        BigDecimal realAmount = calculateRealInfo.getRealAmount();
        Constants.LogDescriptionTypeEnum recharge = productTypeEnum.getLogDescriptionTypeEnum();
        Constants.DirectionTypeEnum directionTypeEnum = Constants.DirectionTypeEnum.OUT;
        String orderNo = IdUtil.fastSimpleUUID();
        Account account = accountService.commonUpdateAccount(userId, realAmount, reqId, orderNo, recharge, directionTypeEnum, String.valueOf(userId));

        UserConsumerLog consumerLog = new UserConsumerLog();
        consumerLog.setUserId(userId);
        consumerLog.setAccountId(account.getId());
        consumerLog.setOrderNo(orderNo);
        consumerLog.setProductType(productType);
        consumerLog.setRealAmount(calculateRealInfo.getRealAmount());
        consumerLog.setOriginalAmount(calculateRealInfo.getOriginalAmount());
        consumerLog.setMemberAmount(calculateRealInfo.getMemberAmount());
        consumerLog.setDiscountAmount(calculateRealInfo.getDiscountAmount());

        userConsumerLogService.save(consumerLog);
    }

    @Override
    public Constants.ProductTypeEnum checkConsume(Long userId, Integer productType, Long tokens) {
        Constants.ProductTypeEnum productTypeEnum = Constants.ProductTypeEnum.getByEnum(productType);
        if (productTypeEnum == null) {
            throw new RuntimeException(StrUtil.format("不存在此:{} ProductTypeEnum", productType));
        }
        CalculateRealInfo calculateRealInfo = calculateRealCurrency(userId, productTypeEnum.getType(), tokens);
        Account account = accountService.getByUserIdAndType(userId, Constants.AccountTypeEnum.TOKEN_BALANCE.getType());
        if (account.getAccountBalance().compareTo(calculateRealInfo.getRealAmount()) < 0) {
            throw new ServiceException(BALANCE_INSUFFICIENT);
        }
        return productTypeEnum;
    }


    @Override
    public CalculateRealInfo calculateRealCurrency(Long userId, Integer productType, Long tokens) {

        ProductConsumeConfig productConsumeConfig = productConsumeConfigService.getByType(productType);
        Integer useAuth = productConsumeConfig.getUseAuth();
        Integer consumeType = productConsumeConfig.getConsumeType();
        // 原价
        BigDecimal consumeCurrencyDecimal = BigDecimal.valueOf(Constants.ConsumeTypeEnum.TOKEN.getType().equals(consumeType) ? tokens * productConsumeConfig.getConsumeCurrency() : productConsumeConfig.getConsumeCurrency());
        boolean isMember = userInfoService.isMember(userId);
        if (useAuth.equals(Constants.ProductConsumeUseAuthEnum.MEMBER.getStatus()) && !isMember) {
            // 不是会员
            throw new ServiceException(NO_MEMBER);

        }

        // 会员权益获取
        ExchangeCardDetail exchangeCardDetail = getByFirstOne(userId);
        List<MemberRights> userAllMemberRights = new ArrayList<>();
        if (exchangeCardDetail != null) {
            userAllMemberRights = getUserAllMemberRights(exchangeCardDetail);
        }

        CalculateRealInfo calculateRealInfo = new CalculateRealInfo();
        calculateRealInfo.setOriginalAmount(consumeCurrencyDecimal);
        calculateRealInfo.setRealAmount(consumeCurrencyDecimal);

        PriorityQueue<BenefitPair> benefitPairPriorityQueue = BenefitFactory.getBenefit(userAllMemberRights, productType);
        for (BenefitPair benefitPair : benefitPairPriorityQueue) {
            Benefit benefit = benefitPair.getBenefit();
            MemberRights memberRights = benefitPair.getMemberRights();
            if (!benefit.processBenefit(memberRights, calculateRealInfo)) {
                break;
            }
        }
        BigDecimal originalAmount = calculateRealInfo.getOriginalAmount();
        BigDecimal realAmount = calculateRealInfo.getRealAmount();
        calculateRealInfo.setMemberAmount(realAmount);
        calculateRealInfo.setDiscountAmount(originalAmount.subtract(realAmount));
        return calculateRealInfo;

    }

    @Override
    public ExchangeCardDetail getByFirstOne(Long userId) {
        LambdaQueryWrapper<ExchangeCardDetail> eq = new LambdaQueryWrapper<ExchangeCardDetail>()
                .eq(ExchangeCardDetail::getUserId, userId)
                .eq(ExchangeCardDetail::getExchangeState, ExchangeCardStateEnum.EXCHANGE.getState())
                .orderByAsc(BaseEntity::getId)
                .last("limit 1");
        return baseMapper.selectOne(eq);
    }

    @Override
    public ExchangeCardDetail getSurplusCountByFirstOne(Long userId) {
        LambdaQueryWrapper<ExchangeCardDetail> eq = new LambdaQueryWrapper<ExchangeCardDetail>()
                .eq(ExchangeCardDetail::getUserId, userId)
                .eq(ExchangeCardDetail::getExchangeState, ExchangeCardStateEnum.EXCHANGE.getState())
                .gt(ExchangeCardDetail::getSurplusCount, 0)
                .orderByAsc(BaseEntity::getId)
                .last("limit 1");
        return baseMapper.selectOne(eq);
    }

    @Override
    public MemberRights getUserFirsMemberRights(Long userId, Constants.MemberRightsTypeEnum memberRightsTypeEnum) {
        ExchangeCardDetail exchangeCardDetail = getByFirstOne(userId);
        if (exchangeCardDetail == null) {
            return null;
        }
        MemberCard memberCard = getCurrentMember(exchangeCardDetail);
        if (memberCard == null) {
            return null;
        }
        return memberRightsService.getByRightsTypeAndMemberCodeOne(memberCard.getCardCode(), memberRightsTypeEnum);
    }

    @Override
    public List<MemberRights> getUserAllMemberRights(ExchangeCardDetail exchangeCardDetail) {
        MemberCard memberCard = getCurrentMember(exchangeCardDetail);
        if (memberCard == null) {
            return null;
        }
        return memberRightsService.getByMemberCode(memberCard.getCardCode());
    }

    private MemberCard getCurrentMember(ExchangeCardDetail exchangeCardDetail) {
        Long memberCardId = exchangeCardDetail.getMemberCardId();
        return memberCardService.getById(memberCardId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exchange(Long userId, MemberCard memberCard, String operatorName, boolean isManager) {

        if (memberCard == null) {
            throw new RuntimeException("没有此会员卡");
        }

        DateTime memberTime = DateUtil.offsetDay(new Date(), memberCard.getCardDay());
        String cardCode = memberCard.getCardCode();
        List<MemberRights> memberRightsList = memberRightsService.getByMemberCode(cardCode);

        ExchangeCardDetail entity = new ExchangeCardDetail();
        entity.setUserId(userId);
        entity.setMemberCardId(memberCard.getId());
        entity.setExpiresTime(memberTime);
        entity.setExchangeState(ExchangeCardStateEnum.EXCHANGE.getState());

        if (!save(entity)) {
            throw new RuntimeException("保存兑换详情失败");
        }

        for (MemberRights memberRights : memberRightsList) {
            Integer rightsType = memberRights.getRightsType();
            Constants.MemberRightsTypeEnum memberRightsTypeEnum = Constants.MemberRightsTypeEnum.getByType(rightsType);
            if (memberRightsTypeEnum == null) {
                continue;
            }
            // 处理代币权益
            Integer count = memberRights.getCount();
            // 增加账户余额
            Constants.LogDescriptionTypeEnum recharge = isManager ? Constants.LogDescriptionTypeEnum.MANAGER_DEPOSIT : Constants.LogDescriptionTypeEnum.RECHARGE;
            Constants.DirectionTypeEnum directionTypeEnum = Constants.DirectionTypeEnum.IN;
            accountService.commonUpdateAccount(userId, BigDecimal.valueOf(count), entity.getId().toString(), cardCode, recharge, directionTypeEnum, operatorName);
        }


        UserInfo userInfo = userInfoService.getById(userId);
        Date memberValidTime = userInfo.getMemberValidTime();
        if (userInfoService.isMember(userId)) {
            userInfo.setMemberValidTime(DateUtil.offsetDay(memberValidTime, memberCard.getCardDay()));

        } else {
            userInfo.setMemberValidTime(memberTime);
        }
        userInfoService.updateById(userInfo);
    }

    @Override
    public List<ExchangeCardDetail> selectByUserId(Long userId) {
        LambdaQueryWrapper<ExchangeCardDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExchangeCardDetail::getUserId, userId);
        queryWrapper.eq(ExchangeCardDetail::getExchangeState, ExchangeCardStateEnum.EXCHANGE.getState());
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<ExchangeCardDetail> selectExpire() {
        Date now = new Date();
        LambdaQueryWrapper<ExchangeCardDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(ExchangeCardDetail::getExpiresTime, now);
        queryWrapper.ge(ExchangeCardDetail::getExpiresTime, DateUtil.offsetDay(now, -5));
        queryWrapper.in(ExchangeCardDetail::getExchangeState, ExchangeCardStateEnum.EXCHANGE.getState(), ExchangeCardStateEnum.OLD.getState());
        return baseMapper.selectList(queryWrapper);

    }

    @Override
    public List<TrendVO<Integer>> exchangeCardTrend(Integer day) {

        Date end = new Date();
        DateTime start = DateUtil.offsetDay(end, -day);
        if (day == 0) {
            start = DateUtil.beginOfDay(end);
            end = DateUtil.endOfDay(end);
        }

//        return baseMapper.exchangeCardTrend(start, end);
        return null;
    }

    @Override
    public void consumeCard(Long exchangeCardId, BigDecimal changeAmount) {
        ExchangeCardDetail exchangeCardDetail = baseMapper.selectById(exchangeCardId);
        exchangeCardDetail.setSurplusCount(exchangeCardDetail.getSurplusCount() - changeAmount.intValue());
        baseMapper.updateById(exchangeCardDetail);
    }

}
