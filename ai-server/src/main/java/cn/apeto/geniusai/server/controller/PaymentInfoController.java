package cn.apeto.geniusai.server.controller;

import cn.apeto.geniusai.server.annotations.DistributedLock;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.domain.SystemConstants.RedisKeyEnum;
import cn.apeto.geniusai.server.domain.dto.PayMemberDTO;
import cn.apeto.geniusai.server.entity.*;
import cn.apeto.geniusai.server.handler.PayHandler;
import cn.apeto.geniusai.server.service.CurrencyConfigService;
import cn.apeto.geniusai.server.service.ExchangeCardDetailService;
import cn.apeto.geniusai.server.service.MemberCardService;
import cn.apeto.geniusai.server.service.PaymentInfoService;
import cn.apeto.geniusai.server.utils.MathUtils;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author warape
 * @since 2023-03-29 08:14:15
 */
@Slf4j
@Tag(name = "支付相关")
@RestController
@RequestMapping("/api/paymentInfo")
public class PaymentInfoController {

    @Autowired
    private MemberCardService memberCardService;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private CurrencyConfigService currencyConfigService;
    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;
    @Autowired
    private PayHandler payHandler;


    @Operation(description = "查询购买结果")
    @GetMapping("/paymentQuery")
    public ResponseResult<?> paymentQuery(@RequestParam("orderNo") String orderNo) {
        StpUtil.checkLogin();
        PaymentInfo paymentInfo = paymentInfoService.getByOrderNo(orderNo);
        if (paymentInfo == null) {
            return ResponseResultGenerator.error();
        }

        return ResponseResultGenerator.success(paymentInfo.getPayState());
    }

    @Operation(description = "购买")
    @PostMapping("/buy")
    @DistributedLock(prefix = RedisKeyEnum.BUY_MEMBER_LOCK, key = "#payMemberDTO.getProductId()", waitFor = 0, isReqUserId = true)
    public ResponseResult<RequestPayInfo> buy(@RequestBody @Validated PayMemberDTO payMemberDTO, HttpServletRequest request) {
        String clientIP = request.getRemoteAddr();

        long userId = StpUtil.getLoginIdAsLong();
        String orderNo = IdUtil.fastSimpleUUID();

        BigDecimal amount;
        String goodsCode;
        String goodsName;
        if (payMemberDTO.getType().equals(Constants.GoodsTypeEnum.MEMBER.getType())) {

            MemberCard memberCard = memberCardService.getById(payMemberDTO.getProductId());
            amount = memberCard.getAmount();
            goodsName = memberCard.getCardName() + "会员";
            goodsCode = memberCard.getCardCode();
        } else if (payMemberDTO.getType().equals(Constants.GoodsTypeEnum.PACKAGE.getType())) {

            CurrencyConfig currencyConfig = currencyConfigService.getById(payMemberDTO.getProductId());
            amount = currencyConfig.getCurrencyAmount();
            MemberRights memberRights = exchangeCardDetailService.getUserFirsMemberRights(userId, Constants.MemberRightsTypeEnum.BUY_PACKAGE);
            if (memberRights != null && memberRights.getDiscount() != null) {
                amount = MathUtils.multiplyDiscountHalfUp2(amount, memberRights.getDiscount());
                log.info("享受折扣  折扣后金额:{} memberRights:{} ", amount, JSONUtil.toJsonStr(memberRights));

            }
            goodsCode = currencyConfig.getId().toString();
            goodsName = currencyConfig.getCurrencyCount() + "流量包";
        } else {
            return ResponseResultGenerator.result(CommonRespCode.VALID_SERVICE_ILLEGAL_ARGUMENT);
        }
        PayRequest payRequest = new PayRequest();
        payRequest.setPayType(payMemberDTO.getPayType());
        payRequest.setType(payMemberDTO.getType());
        payRequest.setAmount(amount);
        payRequest.setGoodsName(goodsName);
        payRequest.setGoodsCode(goodsCode);
        payRequest.setClientIP(clientIP);
        payRequest.setUserId(userId);
        payRequest.setOrderNo(orderNo);
        payRequest.setTradeType(payMemberDTO.getTradeType());
        return ResponseResultGenerator.success(payHandler.doPay(payRequest));

    }


}
