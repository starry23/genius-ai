package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.vo.AccountBalanceVO;
import cn.apeto.geniusai.server.entity.Account;
import cn.apeto.geniusai.server.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/6/19 14:21
 */
@Tag(name = "账户相关")
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "获取代币账户余额")
    @GetMapping("/balance")
    public ResponseResult<AccountBalanceVO> balance() {
        long loginIdAsLong = StpUtil.getLoginIdAsLong();
        List<Account> accountList = accountService.getByUserId(loginIdAsLong);
        Map<Integer, BigDecimal> accountMap = accountList.stream().collect(Collectors.toMap(Account::getAccountType, Account::getAccountBalance));
        BigDecimal rmb = Optional.ofNullable(accountMap.get(Constants.AccountTypeEnum.RMB_BALANCE.getType())).orElse(BigDecimal.ZERO);
        BigDecimal token = Optional.ofNullable(accountMap.get(Constants.AccountTypeEnum.TOKEN_BALANCE.getType())).orElse(BigDecimal.ZERO);
        AccountBalanceVO accountBalanceVO = new AccountBalanceVO();
        accountBalanceVO.setTokenBalance(token);
        accountBalanceVO.setRmbBalance(rmb);
        return ResponseResultGenerator.success(accountBalanceVO);
    }
}
