package cn.apeto.geniusai.server.job;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.ijpay.wxpay.WxPayApiConfig;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.Constants.AliPayStateEnum;
import cn.apeto.geniusai.server.domain.Constants.PayStateEnum;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.entity.PaymentInfo;
import cn.apeto.geniusai.server.service.PaymentInfoService;
import cn.apeto.geniusai.server.utils.PayUtils;
import cn.apeto.geniusai.server.utils.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author apeto
 * @create 2023/4/9 5:41 下午
 */
@Slf4j
@Component
public class PaymentJob {

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Scheduled(cron = "5 * * * * ? ")
    public void payQuery() {
        String key = SystemConstants.RedisKeyEnum.PAY_QUERY.getKey();
        if (!RedissonUtils.tryLockNotWaitBoolean(key)) {
            return;
        }

        try {

            List<PaymentInfo> list =
                    paymentInfoService.lambdaQuery()
                            .eq(PaymentInfo::getPayState, PayStateEnum.PAYING.getState())
                            .le(PaymentInfo::getCreateTime, LocalDateTime.now().minusMinutes(2))
                            .ge(PaymentInfo::getCreateTime, LocalDateTime.now().minusMinutes(60))
                            .list();
            for (PaymentInfo paymentInfo : list) {
                Integer payType = paymentInfo.getPayType();
                if(payType.equals(Constants.PayTypeEnum.ALI_PAY.getPayType())){
                    AlipayClient alipayClient = PayUtils.alipayClient();
                    if (alipayClient == null) {
                        continue;
                    }
                    AlipayTradeQueryModel model = new AlipayTradeQueryModel();
                    model.setOutTradeNo(paymentInfo.getPaySn());
                    model.setTradeNo(paymentInfo.getOutPaySn());
                    AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                    request.setBizModel(model);
                    try {
                        AlipayTradeQueryResponse execute = alipayClient.execute(request);
                        if ("ACQ.TRADE_NOT_EXIST".equals(execute.getSubCode())) {
                            paymentInfo.setPayState(PayStateEnum.CANCEL.getState());
                            paymentInfoService.updateById(paymentInfo);
                            continue;
                        }
                        if (!execute.isSuccess()) {
                            paymentInfo.setPayState(PayStateEnum.ERROR.getState());
                            paymentInfoService.updateById(paymentInfo);
                            continue;
                        }

                        PayStateEnum payStateEnum = AliPayStateEnum.getByPayStateEnum(execute.getTradeStatus());
                        paymentInfoService.callbackHandler(execute.getTradeNo(), execute.getOutTradeNo(), payStateEnum, execute.getSendPayDate());
                    } catch (AlipayApiException e) {
                        log.error("阿里支付查询失败", e);
                    }
                }else {
                    // 微信
                    WxPayApiConfig wxPayApiConfig = PayUtils.wxPayApiConfig();
                }

            }
        } finally {
            RedissonUtils.unlock(key);
        }

    }

}
