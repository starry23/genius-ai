package cn.apeto.geniusai.server.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.apeto.geniusai.server.domain.AccountsDTO;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.dto.AccountLogsDTO;
import cn.apeto.geniusai.server.entity.Account;
import cn.apeto.geniusai.server.entity.AccountLog;
import cn.apeto.geniusai.server.entity.BaseEntity;
import cn.apeto.geniusai.server.service.AccountLogService;
import cn.apeto.geniusai.server.service.AccountService;
import cn.apeto.geniusai.server.utils.StpManagerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apeto
 * @create 2023/7/8 5:06 下午
 */
@Tag(name = "后台管理系统[账户]相关")
@Slf4j
@RestController
@RequestMapping("/api/manager")
@AllArgsConstructor
@SaCheckLogin(type = StpManagerUtil.TYPE)
public class ManagerAccountController {

    private AccountService accountService;
    private AccountLogService accountLogService;

    @GetMapping("/accounts")
    @Operation(summary = "账户列表")
    public ResponseResult<Page<Account>> accounts(AccountsDTO accountsDTO) {
        Long userId = accountsDTO.getUserId();
        Integer accountType = accountsDTO.getAccountType();
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, Account::getUserId, userId);
        queryWrapper.eq(accountType != null, Account::getAccountType, accountType);
        queryWrapper.orderByDesc(BaseEntity::getId);
        Page<Account> paymentInfoPage = accountService.page(new Page<>(accountsDTO.getCurrent(), accountsDTO.getSize()), queryWrapper);
        return ResponseResultGenerator.success(paymentInfoPage);
    }

    @GetMapping("/accountLogs")
    @Operation(summary = "账户流水列表")
    public ResponseResult<Page<AccountLog>> accountLogs(AccountLogsDTO accountLogsDTO) {
        Long userId = accountLogsDTO.getUserId();
        String outsideCode = accountLogsDTO.getOutsideCode();
        Integer directionType = accountLogsDTO.getDirectionType();
        Integer logDescriptionType = accountLogsDTO.getLogDescriptionType();
        Long accountId = accountLogsDTO.getAccountId();
        LambdaQueryWrapper<AccountLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, AccountLog::getUserId, userId);
        queryWrapper.eq(directionType != null, AccountLog::getDirectionType, directionType);
        queryWrapper.eq(logDescriptionType != null, AccountLog::getLogDescriptionType, logDescriptionType);
        queryWrapper.eq(accountId != null, AccountLog::getAccountId, accountId);
        queryWrapper.eq(StrUtil.isNotBlank(outsideCode), AccountLog::getOutsideCode, outsideCode);
        queryWrapper.orderByDesc(BaseEntity::getId);
        Page<AccountLog> paymentInfoPage = accountLogService.page(new Page<>(accountLogsDTO.getCurrent(), accountLogsDTO.getSize()), queryWrapper);
        return ResponseResultGenerator.success(paymentInfoPage);
    }
}
