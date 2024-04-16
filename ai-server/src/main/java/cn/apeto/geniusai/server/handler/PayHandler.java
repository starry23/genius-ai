package cn.apeto.geniusai.server.handler;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.model.UnifiedOrderModel;
import cn.apeto.geniusai.server.config.properties.AliPayProperties;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.PaySetting;
import cn.apeto.geniusai.server.domain.RequestPayInfo;
import cn.apeto.geniusai.server.domain.WechatPayConfig;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.pay.TradeTypeEnum;
import cn.apeto.geniusai.server.entity.PayRequest;
import cn.apeto.geniusai.server.entity.PaymentInfo;
import cn.apeto.geniusai.server.entity.WechatUserInfo;
import cn.apeto.geniusai.server.service.PaymentInfoService;
import cn.apeto.geniusai.server.service.WechatUserInfoService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.MathUtils;
import cn.apeto.geniusai.server.utils.PayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author apeto
 * @create 2023/7/28 17:17
 */
@Component
@Slf4j
public class PayHandler {

    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private WechatUserInfoService wechatUserInfoService;

    public RequestPayInfo doPay(PayRequest payRequest) {

        long userId = payRequest.getUserId();
        BigDecimal amount = payRequest.getAmount();
        Integer type = payRequest.getType();
        Integer payType = payRequest.getPayType();
        String orderNo = payRequest.getOrderNo();
        String goodsCode = payRequest.getGoodsCode();
        String goodsName = payRequest.getGoodsName();
        String clientIP = payRequest.getClientIP();

        RequestPayInfo requestPayInfo = new RequestPayInfo();
        requestPayInfo.setOrderNo(orderNo);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setUserId(userId);
        paymentInfo.setPaySn(orderNo);
        paymentInfo.setOutPaySn("");
        paymentInfo.setGoodsCode(goodsCode);
        paymentInfo.setGoodsType(type);
        paymentInfo.setPayAmount(amount);
        paymentInfo.setPayState(Constants.PayStateEnum.PAYING.getState());
        paymentInfo.setPayType(payType);
        paymentInfo.setGoodsName(goodsName);
        paymentInfo.setCreateBy(type + "_用户支付" + userId);
        if (!paymentInfoService.save(paymentInfo)) {
            throw new RuntimeException("插入paymentInfoService异常");
        }

        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        PaySetting paySetting = CommonUtils.getPaySetting();
        String domain = packageWebConfig.getDomain();
        if (StrUtil.isBlank(domain)) {
            domain = SpringUtil.getProperty("appServer.domain");
        }
        String tradeType = payRequest.getTradeType();
        if (payType.equals(Constants.PayTypeEnum.ALI_PAY.getPayType())) {

            AlipayClient alipayClient = PayUtils.alipayClient();
            if (alipayClient == null) {
                throw new RuntimeException("支付宝客户端初始化失败");
            }
            AliPayProperties aliPayProperties = paySetting.getAliPayProperties();
            if (tradeType.equals(TradeTypeEnum.MWEB.name())) {
                if (aliPayProperties.getOpenFacePay()) {
                    // 强制开启当面付
                    facePay(orderNo, amount, goodsName, domain, alipayClient, requestPayInfo);
                    return requestPayInfo;
                }
                AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
                AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
                model.setOutTradeNo(orderNo);
                model.setTotalAmount(amount.toPlainString());
                model.setSubject(goodsName);
//                model.setSellerId(aliPayProperties.getSellerId());
                model.setProductCode("QUICK_WAP_WAY");
                request.setBizModel(model);
                request.setNotifyUrl(domain + aliPayNotify);
                request.setReturnUrl(domain);
                try {
                    AlipayTradeWapPayResponse execute = alipayClient.pageExecute(request);
                    if (!execute.isSuccess()) {
                        throw new RuntimeException("支付宝预下单失败");
                    }
                    requestPayInfo.setParams(execute.getBody());
                } catch (AlipayApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (aliPayProperties.getOpenFacePay()) {
                    // 强制开启当面付
                    facePay(orderNo, amount, goodsName, domain, alipayClient, requestPayInfo);
                    return requestPayInfo;
                }

                AlipayTradePagePayModel model = new AlipayTradePagePayModel();
                model.setOutTradeNo(orderNo);
                model.setTotalAmount(amount.toPlainString());
                model.setSubject(goodsName);
                model.setProductCode("FAST_INSTANT_TRADE_PAY");
                AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
                request.setBizModel(model);
                request.setNotifyUrl(domain + aliPayNotify);
                try {
                    AlipayTradePagePayResponse execute = alipayClient.pageExecute(request);

                    if (!execute.isSuccess()) {
                        throw new RuntimeException("支付宝预下单失败");
                    }

                    requestPayInfo.setParams(execute.getBody());
                    requestPayInfo.setOrderNo(orderNo);
                } catch (AlipayApiException e) {
                    throw new RuntimeException(e);
                }

            }

        } else {
            WxPayApiConfig wxPayApiConfig = PayUtils.wxPayApiConfig();


            UnifiedOrderModel.UnifiedOrderModelBuilder unifiedOrderModelBuilder = UnifiedOrderModel
                    .builder()
                    .appid(wxPayApiConfig.getAppId())
                    .mch_id(wxPayApiConfig.getMchId())
                    .nonce_str(WxPayKit.generateStr())
                    .body(goodsName)
                    .out_trade_no(orderNo)
                    .total_fee(MathUtils.toFen(amount).toString())
                    .spbill_create_ip(clientIP)
                    .notify_url(domain + "/api/callback/wechatPay")
                    .trade_type(tradeType);


            if (tradeType.equals(TradeTypeEnum.MWEB.name())) {

                WechatPayConfig wechatPayConfig = paySetting.getWechatPayConfig();
                Map<String, String> h5Info = new HashMap<>();
                h5Info.put("type", "Wap");
                h5Info.put("wap_url", wechatPayConfig.getH5WapUrl());
                h5Info.put("wap_name", "充值");
                unifiedOrderModelBuilder.scene_info(JSON.toJSONString(MapUtil.builder().put("h5_info", h5Info).build()));
            }

            if (tradeType.equals(TradeTypeEnum.JSAPI.name())) {
                WechatUserInfo wechatUserInfo = wechatUserInfoService.getByUserId(userId);
                if (wechatUserInfo == null) {
                    throw new RuntimeException("请您先微信登录");
                }
                unifiedOrderModelBuilder.openid(wechatUserInfo.getOpenId());
            }
            Map<String, String> params = unifiedOrderModelBuilder
                    .build()
                    .createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
            log.info("请求微信支付请求参数:{}", JSONUtil.toJsonStr(params));
            String xmlResult = WxPayApi.pushOrder(false, params);
            log.info("微信支付响应结果:{}", xmlResult);

            Map<String, String> result = WxPayKit.xmlToMap(xmlResult);

            String return_code = result.get("return_code");
            String return_msg = result.get("return_msg");
            if (!WxPayKit.codeIsOk(return_code)) {
                throw new RuntimeException(return_msg);
            }
            String result_code = result.get("result_code");
            if (!WxPayKit.codeIsOk(result_code)) {
                throw new RuntimeException(return_msg);
            }
            if (tradeType.equals(TradeTypeEnum.NATIVE.name())) {
                requestPayInfo.setQrCode(result.get("code_url"));
            }
            if (tradeType.equals(TradeTypeEnum.JSAPI.name())) {
                String prepayId = result.get("prepay_id");
                Map<String, String> packageParams = WxPayKit.prepayIdCreateSign(prepayId, wxPayApiConfig.getAppId(),
                        wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
                requestPayInfo.setParams(packageParams);
                return requestPayInfo;
            }
            requestPayInfo.setParams(result);


        }
        return requestPayInfo;
    }

    String aliPayNotify = "/api/ali/precreatePayCallback";

    private void facePay(String orderNo, BigDecimal amount, String goodsName, String domain, AlipayClient alipayClient, RequestPayInfo requestPayInfo) {
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(orderNo);
        model.setTotalAmount(amount.toPlainString());
        model.setSubject(goodsName);
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setBizModel(model);
        request.setNotifyUrl(domain + aliPayNotify);
        try {
            AlipayTradePrecreateResponse execute = alipayClient.execute(request);

            if (!execute.isSuccess()) {
                throw new RuntimeException("支付宝预下单失败");
            }

            requestPayInfo.setParams(execute.getBody());
            requestPayInfo.setQrCode(execute.getQrCode());
            requestPayInfo.setOrderNo(orderNo);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }
}
