package cn.apeto.geniusai.server.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.InviteLog;
import cn.apeto.geniusai.server.entity.UserInfo;
import cn.apeto.geniusai.server.mapper.InviteLogMapper;
import cn.apeto.geniusai.server.service.AccountService;
import cn.apeto.geniusai.server.service.InviteLogService;
import cn.apeto.geniusai.server.service.UserInfoService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 邀请记录 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-04-06 12:10:07
 */
@Slf4j
@Service
public class InviteLogServiceImpl extends ServiceImpl<InviteLogMapper, InviteLog> implements InviteLogService {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private AccountService accountService;


    @Async("chat")
    @Override
    public void inviteHandler(String inviteCode, Long signUpUserId) {

        try {

            if (StrUtil.isBlank(inviteCode)) {
                return;
            }
            Long inviteUserId = Long.valueOf(CommonUtils.inviteSymmetricCrypto().decryptStr(inviteCode));

            if (inviteUserId.equals(signUpUserId)) {
                return;
            }
            UserInfo userInfo = userInfoService.getById(inviteUserId);
            if (userInfo == null) {
                log.warn("邀请用户不存在");
                return;
            }

            SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
            Integer inviteGiveCurrency = packageWebConfig.getActivitySetting().getInviteGiveCurrency();
            if (inviteGiveCurrency == null || inviteGiveCurrency <= 0) {
                log.warn("邀请有礼权益不存在");
                return;
            }

            // 增加账户余额
            String reqId = IdUtil.fastSimpleUUID();
            Constants.LogDescriptionTypeEnum recharge = Constants.LogDescriptionTypeEnum.INVITE;
            Constants.DirectionTypeEnum directionTypeEnum = Constants.DirectionTypeEnum.IN;
            accountService.commonUpdateAccount(inviteUserId, BigDecimal.valueOf(inviteGiveCurrency), reqId, reqId, recharge, directionTypeEnum, "公众号邀请有礼");
            InviteLog inviteLog = new InviteLog();
            inviteLog.setInviteUserId(inviteUserId);
            inviteLog.setToInviteUserId(signUpUserId);
            inviteLog.setCurrencyCount(inviteGiveCurrency);
            if (!save(inviteLog)) {
                log.error("邀请记录插入失败! inviteCode:{} signUpUserId:{} mpGiveCurrency:{}", inviteCode, signUpUserId, inviteGiveCurrency);
            }
            userInfo.setInviteCount(userInfo.getInviteCount() + 1);
            userInfoService.updateById(userInfo);
        } catch (Exception e) {
            log.error("邀请有礼插入异常 inviteCode:{} signUpUserId:{}", inviteCode, signUpUserId, e);
        }

    }

    @Override
    public long getCountByInviteUserId(Long userId) {
        LambdaQueryWrapper<InviteLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InviteLog::getInviteUserId, userId);
        Long aLong = baseMapper.selectCount(queryWrapper);
        if (aLong == null) {
            return 0;
        }
        return aLong;
    }

    @Override
    public InviteLog getByToInviteUserId(Long userId) {
        LambdaQueryWrapper<InviteLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InviteLog::getToInviteUserId, userId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<TrendVO<Integer>> inviteTrend(Integer day) {

        Date end = new Date();
        DateTime start = DateUtil.offsetDay(end, -day);
        if (day == 0) {
            start = DateUtil.beginOfDay(end);
            end = DateUtil.endOfDay(end);
        }
        return baseMapper.trend(start, end);
    }

}
