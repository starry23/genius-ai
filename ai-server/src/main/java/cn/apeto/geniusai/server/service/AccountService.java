package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.domain.Constants.DirectionTypeEnum;
import cn.apeto.geniusai.server.domain.Constants.LogDescriptionTypeEnum;
import cn.apeto.geniusai.server.entity.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 账户表 服务类
 * </p>
 *
 * @author warape
 * @since 2023-04-02 05:10:17
 */
public interface AccountService extends IService<Account> {

    Account getByUserIdAndType(Long userId, Integer accountType);

    List<Account> getByUserId(Long userId);

    Account commonUpdateAccount(Long userId, BigDecimal changeAmount, String reqId, String outsideCode, LogDescriptionTypeEnum logDescriptionTypeEnum,
                                DirectionTypeEnum directionTypeEnum,
                                String operatorName);

    Account commonUpdateAccount(Long userId, Integer accountType, BigDecimal changeAmount, String reqId, String outsideCode, LogDescriptionTypeEnum logDescriptionTypeEnum, DirectionTypeEnum directionTypeEnum, String operatorName);

    void fullRefund(Long userId, String reqId, LogDescriptionTypeEnum logDescriptionTypeEnum, String operatorName);
}
