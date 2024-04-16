package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.Constants.DirectionTypeEnum;
import cn.apeto.geniusai.server.domain.Constants.LogDescriptionTypeEnum;
import cn.apeto.geniusai.server.entity.Account;
import cn.apeto.geniusai.server.entity.AccountLog;
import cn.apeto.geniusai.server.mapper.AccountMapper;
import cn.apeto.geniusai.server.service.AccountLogService;
import cn.apeto.geniusai.server.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 账户表 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-04-02 05:10:17
 */
@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private AccountLogService logService;

    @Override
    public Account getByUserIdAndType(Long userId, Integer accountType) {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUserId, userId);
        queryWrapper.eq(Account::getAccountType, accountType);
        Account one = getOne(queryWrapper);
        if (one == null) {
            one = new Account();
            one.setUserId(userId);
            one.setAccountBalance(BigDecimal.ZERO);
            one.setAccountType(accountType);
            save(one);
        }

        return one;
    }

    @Override
    public List<Account> getByUserId(Long userId) {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUserId, userId);
        return list(queryWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account commonUpdateAccount(Long userId, BigDecimal changeAmount, String reqId, String outsideCode, LogDescriptionTypeEnum logDescriptionTypeEnum, DirectionTypeEnum directionTypeEnum, String operatorName) {
        return commonUpdateAccount(userId, Constants.AccountTypeEnum.TOKEN_BALANCE.getType(), changeAmount, reqId, outsideCode, logDescriptionTypeEnum, directionTypeEnum, operatorName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account commonUpdateAccount(Long userId, Integer accountType, BigDecimal changeAmount, String reqId, String outsideCode, LogDescriptionTypeEnum logDescriptionTypeEnum, DirectionTypeEnum directionTypeEnum, String operatorName) {

        Account account = getByUserIdAndType(userId, accountType);
        if (account == null) {
            account = new Account();
            account.setUserId(userId);
            account.setAccountType(accountType);
            account.setAccountBalance(BigDecimal.ZERO);

            if (!save(account)) {
                throw new RuntimeException("账户信息保存失败");
            }
        }
        BigDecimal realAmount = DirectionTypeEnum.IN.equals(directionTypeEnum) ? changeAmount : changeAmount.negate();
        if (baseMapper.commonUpdateAmount(account.getId(), realAmount, operatorName) < 1) {
            throw new RuntimeException("修改金额失败");
        }

        AccountLog accountLog = new AccountLog();
        accountLog.setUserId(userId);
        accountLog.setAccountId(account.getId());
        accountLog.setAmount(changeAmount);
        accountLog.setRequestId(reqId);
        accountLog.setOutsideCode(outsideCode);
        accountLog.setBalanceAmount(account.getAccountBalance().add(realAmount));
        accountLog.setLogDescription(logDescriptionTypeEnum.getDesc());
        accountLog.setLogDescriptionType(logDescriptionTypeEnum.getType());
        accountLog.setDirectionType(directionTypeEnum.getType());
        if (!logService.save(accountLog)) {
            throw new RuntimeException("账户信息日志保存失败");
        }

        return account;
    }

    @Override
    public void fullRefund(Long userId, String orderNo, LogDescriptionTypeEnum logDescriptionTypeEnum, String operatorName) {
        AccountLog acclog = logService.getByOrderNoAndType("re" + orderNo, logDescriptionTypeEnum.getType());
        if (acclog != null) {
            log.warn("{}已退", orderNo);
            return;
        }
        AccountLog accountLog = logService.getByReqId(orderNo, DirectionTypeEnum.OUT.getType());
        BigDecimal amount = accountLog.getAmount();
        commonUpdateAccount(userId, amount, orderNo, "re" + orderNo, logDescriptionTypeEnum, DirectionTypeEnum.IN, operatorName);
    }
}
