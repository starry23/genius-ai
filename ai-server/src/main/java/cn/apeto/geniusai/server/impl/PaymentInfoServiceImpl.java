package cn.apeto.geniusai.server.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.Constants.PayStateEnum;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.CurrencyConfig;
import cn.apeto.geniusai.server.entity.MemberCard;
import cn.apeto.geniusai.server.entity.PaymentInfo;
import cn.apeto.geniusai.server.handler.CommonHandler;
import cn.apeto.geniusai.server.mapper.PaymentInfoMapper;
import cn.apeto.geniusai.server.service.*;
import cn.apeto.geniusai.server.utils.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-03-29 08:14:15
 */
@Slf4j
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Autowired
    private MemberCardService memberCardService;
    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CurrencyConfigService currencyConfigService;
    @Autowired
    private CommonHandler commonHandler;

    @Override
    public PaymentInfo getByOrderNo(String orderNo) {
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getPaySn, orderNo);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callbackHandler(String outPaySn, String paySn, PayStateEnum sysPayStateEnum, Date payTime) {

        if (RedissonUtils.lockByTransaction(SystemConstants.RedisKeyEnum.PAYMENT_INFO_LOCK.getKey(paySn))) {
            log.warn("lock订单已经处理过了:{}", paySn);
            return;
        }

        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getPaySn, paySn);
        PaymentInfo paymentInfo = baseMapper.selectOne(queryWrapper);

        Integer payState = paymentInfo.getPayState();
        if (payState.equals(sysPayStateEnum.getState())) {
            log.warn("订单已经处理过了:{} 状态:{}", paySn, payState);
            return;
        }
        paymentInfo.setPayState(sysPayStateEnum.getState());
        paymentInfo.setPayTime(payTime);
        paymentInfo.setOutPaySn(outPaySn);
        Long userId = paymentInfo.getUserId();
        BigDecimal payAmount = paymentInfo.getPayAmount();

        if (!updateById(paymentInfo)) {
            throw new RuntimeException("修改订单状态失败:" + paySn);
        }
        String goodsCode = paymentInfo.getGoodsCode();
        if (sysPayStateEnum.equals(PayStateEnum.SUCCESS)) {
            Integer goodsType = paymentInfo.getGoodsType();
            if (goodsType.equals(Constants.GoodsTypeEnum.MEMBER.getType())) {
                // 会员
                MemberCard memberCard = memberCardService.getByCardCode(goodsCode);
                exchangeCardDetailService.exchange(userId, memberCard, "支付回调", false);
            } else if (goodsType.equals(Constants.GoodsTypeEnum.PACKAGE.getType())) {
                //流量包
                CurrencyConfig currencyConfig = currencyConfigService.getById(goodsCode);
                Constants.LogDescriptionTypeEnum recharge = Constants.LogDescriptionTypeEnum.RECHARGE;
                Constants.DirectionTypeEnum directionTypeEnum = Constants.DirectionTypeEnum.IN;
                accountService.commonUpdateAccount(userId, BigDecimal.valueOf(currencyConfig.getCurrencyCount()), paySn, paySn, recharge, directionTypeEnum, "支付回调");
            }
            commonHandler.rebate(paySn, userId, payAmount);

        }
    }


    @Override
    public List<TrendVO<Integer>> trend(Integer day, Integer payState) {
        Date end = new Date();
        DateTime start = DateUtil.offsetDay(end, -day);
        if (day == 0) {
            start = DateUtil.beginOfDay(end);
            end = DateUtil.endOfDay(end);
        }

        return baseMapper.trend(start, end, payState);
    }

    @Override
    public List<TrendVO<BigDecimal>> payAmountTrend(Integer day) {
        Date end = new Date();
        DateTime start = DateUtil.offsetDay(end, -day);
        if (day == 0) {
            start = DateUtil.beginOfDay(end);
            end = DateUtil.endOfDay(end);
        }
        return baseMapper.payAmountTrend(start, end);
    }

    @Override
    public void deleteByConditions(LambdaQueryWrapper<PaymentInfo> queryWrapper) {
        baseMapper.delete(queryWrapper);
    }
}
