package cn.apeto.geniusai.server.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.Constants.LoginTypeEnum;
import cn.apeto.geniusai.server.domain.Constants.ResponseEnum;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.UserInfo;
import cn.apeto.geniusai.server.entity.WechatUserInfo;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.mapper.UserInfoMapper;
import cn.apeto.geniusai.server.service.AccountService;
import cn.apeto.geniusai.server.service.UserInfoService;
import cn.apeto.geniusai.server.service.WechatUserInfoService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-03-29 08:14:15
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private WechatUserInfoService wechatUserInfoService;
    @Autowired
    private AccountService accountService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long getOrCreateWechatUser(WxOAuth2UserInfo wxMpUser) {

        WechatUserInfo wechatUserInfo = wechatUserInfoService.getByOpenId(wxMpUser.getOpenid());

        if (wechatUserInfo != null) {
            return wechatUserInfo.getUserId();
        }

        UserInfo userInfo = createUser(LoginTypeEnum.MP.getType(), null, null);
        wechatUserInfo = new WechatUserInfo();
        wechatUserInfo.setUserId(userInfo.getId());
        wechatUserInfo.setOpenId(wxMpUser.getOpenid());
        wechatUserInfo.setUnionId(wxMpUser.getUnionId());
        wechatUserInfo.setNickName(wxMpUser.getNickname());
        wechatUserInfo.setCity(wxMpUser.getCity());
        wechatUserInfo.setProvince(wxMpUser.getProvince());
        wechatUserInfo.setCountry(wxMpUser.getCountry());
        wechatUserInfo.setHeadImgUrl(wxMpUser.getHeadImgUrl());
        wechatUserInfo.setSex(wxMpUser.getSex());
        wechatUserInfo.setSnapshotUser(wxMpUser.getSnapshotUser());
        if (!wechatUserInfoService.save(wechatUserInfo)) {
            throw new RuntimeException("保存WechatUserInfo失败");
        }
        return userInfo.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfo createWechatUserByOpenId(String openId) {

        UserInfo userInfo = createUser(LoginTypeEnum.MP.getType(), null, null);
        WechatUserInfo wechatUserInfo = new WechatUserInfo();
        wechatUserInfo.setUserId(userInfo.getId());
        wechatUserInfo.setOpenId(openId);
        wechatUserInfo.setNickName("尊贵会员");
        if (!wechatUserInfoService.save(wechatUserInfo)) {
            throw new RuntimeException("保存WechatUserInfo失败");
        }

        return userInfo;
    }


    @Override
    public UserInfo getByPhone(String phone) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone, phone);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public UserInfo getByEmail(String email) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getEmail, email);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long doSmsSignUp(String accountNum, String password, Integer type) {
        UserInfo userInfo = createUser(type, accountNum, password);
        return userInfo.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfo createUser(Integer type, String accountNum, String password) {

        UserInfo entity = new UserInfo();

        UserInfo userInfo;
        if (type.equals(LoginTypeEnum.PHONE.getType())) {
            userInfo = getByPhone(accountNum);
            entity.setPhone(accountNum);
        } else if (type.equals(LoginTypeEnum.EMAIL.getType())) {
            userInfo = getByEmail(accountNum);
            entity.setEmail(accountNum);
        } else if (type.equals(LoginTypeEnum.MP.getType())) {
            userInfo = null;
        } else {
            throw new ServiceException(CommonRespCode.VALID_SERVICE_ILLEGAL_ARGUMENT);
        }

        if (userInfo != null) {
            throw new ServiceException(ResponseEnum.USER_EXIST);
        }
        if (StrUtil.isNotBlank(password)) {
            String md5SaltPass = CommonUtils.md5Salt(password);
            entity.setUserPassword(md5SaltPass);
        }
        entity.setRegisterType(type);
        if (!save(entity)) {
            throw new RuntimeException("保存UserInfo失败");
        }

        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        SaveSettingDTO.ActivitySetting activitySetting = packageWebConfig.getActivitySetting();
        Integer registerGiveCurrency = activitySetting.getRegisterGiveCurrency();
        if (registerGiveCurrency != null && registerGiveCurrency > 0) {
            String reqId = IdUtil.fastSimpleUUID();
            Constants.LogDescriptionTypeEnum recharge = Constants.LogDescriptionTypeEnum.REGISTER;
            Constants.DirectionTypeEnum directionTypeEnum = Constants.DirectionTypeEnum.IN;
            accountService.commonUpdateAccount(entity.getId(), BigDecimal.valueOf(registerGiveCurrency), reqId, reqId, recharge, directionTypeEnum, "注册有礼物");
        } else {
            log.warn("没有注册有礼会员卡");
        }
        return entity;
    }

    @Override
    public List<TrendVO> trend(Integer day) {
        Date end = new Date();
        DateTime start = DateUtil.offsetDay(end, -day);
        if (day == 0) {
            start = DateUtil.beginOfDay(end);
            end = DateUtil.endOfDay(end);
        }
        return baseMapper.trend(start, end);
    }

    @Override
    public boolean isMember(Long userId) {
        UserInfo userInfo = getById(userId);
        Date memberValidTime = userInfo.getMemberValidTime();
        return memberValidTime != null && memberValidTime.getTime() > new Date().getTime();
    }
}
