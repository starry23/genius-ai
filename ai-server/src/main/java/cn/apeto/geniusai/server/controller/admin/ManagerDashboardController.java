package cn.apeto.geniusai.server.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.service.*;
import cn.apeto.geniusai.server.utils.StpManagerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author apeto
 * @create 2023/7/8 12:29 上午
 */
@Tag(name = "后台管理系统兑换码相关")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/manager")
@SaCheckLogin(type = StpManagerUtil.TYPE)
public class ManagerDashboardController {

    private PaymentInfoService paymentInfoService;
    private ChatDetailLogService chatDetailLogService;
    private UserInfoService userInfoService;
    private ExchangeCardDetailService exchangeCardDetailService;
    private MjImageInfoService mjImageInfoService;
    private InviteLogService inviteLogService;

    @Operation(summary = "注册用户趋势图")
    @GetMapping("/registerUserTrend")
    public ResponseResult<List<TrendVO>> registerUserTrend(Integer day) {

        if (day == null) {
            day = 7;
        }

        List<TrendVO> list = userInfoService.trend(day);
        return ResponseResultGenerator.success(list);
    }

    @Operation(summary = "提问趋势图")
    @GetMapping("/qATrend")
    public ResponseResult<List<TrendVO>> qATrend(Integer day) {

        if (day == null) {
            day = 7;
        }

        List<TrendVO> list = chatDetailLogService.trend(day);
        return ResponseResultGenerator.success(list);
    }


    @Operation(summary = "支付数量趋势图")
    @GetMapping("/payTrend")
    public ResponseResult<List<TrendVO<Integer>>> payTrend(Integer day, Integer payState) {

        if (day == null) {
            day = 7;
        }

        List<TrendVO<Integer>> list = paymentInfoService.trend(day, payState);
        return ResponseResultGenerator.success(list);
    }

    @Operation(summary = "支付金额趋势图")
    @GetMapping("/payAmountTrend")
    public ResponseResult<List<TrendVO<BigDecimal>>> payAmountTrend(Integer day) {

        if (day == null) {
            day = 7;
        }

        List<TrendVO<BigDecimal>> list = paymentInfoService.payAmountTrend(day);
        return ResponseResultGenerator.success(list);
    }

    @Operation(summary = "购买会员卡趋势图")
    @GetMapping("/exchangeCardTrend")
    public ResponseResult<List<TrendVO<Integer>>> exchangeCardTrend(Integer day) {

        if (day == null) {
            day = 7;
        }

        List<TrendVO<Integer>> list = exchangeCardDetailService.exchangeCardTrend(day);
        return ResponseResultGenerator.success(list);
    }

    @Operation(summary = "mj趋势图")
    @GetMapping("/mjTrend")
    public ResponseResult<List<TrendVO<Integer>>> mjTrend(Integer day, String fileStatus) {

        if (day == null) {
            day = 7;
        }

        List<TrendVO<Integer>> list = mjImageInfoService.mjTrend(day, fileStatus);
        return ResponseResultGenerator.success(list);
    }


    @Operation(summary = "邀请趋势图")
    @GetMapping("/inviteTrend")
    public ResponseResult<List<TrendVO<Integer>>> inviteTrend(Integer day) {

        if (day == null) {
            day = 7;
        }

        List<TrendVO<Integer>> list = inviteLogService.inviteTrend(day);
        return ResponseResultGenerator.success(list);
    }
}
