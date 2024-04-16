package cn.apeto.geniusai.server.controller;

import cn.apeto.geniusai.server.config.properties.AliPayProperties;
import cn.apeto.geniusai.server.domain.Constants.AliPayStateEnum;
import cn.apeto.geniusai.server.domain.PaySetting;
import cn.apeto.geniusai.server.service.PaymentInfoService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author apeto
 * @create 2023/4/13 19:17
 */
@Tag(name = "支付宝网关")
@Slf4j
@RestController
@RequestMapping("/api/ali")
public class AliGatewayController {

    @Autowired
    private PaymentInfoService paymentInfoService;

    @PostMapping("/gateway")
    public Object gateway(HttpServletRequest request) {

        Map<String, String> map = CommonUtils.convertToMap(request);
        log.info("支付宝网关回调参数:{}", JSONUtil.toJsonStr(map));
        return "SUCCESS";
    }

    @Operation(description = "支付回调")
    @RequestMapping("/precreatePayCallback")
    public Object precreatePayCallback(HttpServletRequest request) {

        String failure = "failure";

        Map<String, String> map = CommonUtils.convertToMap(request);
        log.info("支付宝扫码付支付回调结果:{}", JSONUtil.toJsonStr(map));
        JSONObject jsonObject = JSONUtil.parseObj(map);
        BigDecimal payAmount = jsonObject.getBigDecimal("buyer_pay_amount");
        String tradeNo = jsonObject.getStr("trade_no");
        String outTradeNo = jsonObject.getStr("out_trade_no");
        Date payTime = jsonObject.getDate("gmt_payment");
        AliPayStateEnum aliPayStateEnum = jsonObject.getEnum(AliPayStateEnum.class, "trade_status");

        try {
            PaySetting paySetting = CommonUtils.getPaySetting();
            AliPayProperties aliPayProperties = paySetting.getAliPayProperties();
            if (!AlipaySignature.rsaCheckV1(map, aliPayProperties.getAliPublicKey(), "UTF-8", "RSA2")) {
                return failure;
            }
        } catch (AlipayApiException e) {
            log.error("支付宝扫码支付回调验签异常", e);
            return failure;
        }
        try {
            paymentInfoService.callbackHandler(tradeNo, outTradeNo, aliPayStateEnum.getSysPayStateEnum(), payTime);
            return "success";
        } catch (Exception e) {
            return failure;
        }

    }


}
