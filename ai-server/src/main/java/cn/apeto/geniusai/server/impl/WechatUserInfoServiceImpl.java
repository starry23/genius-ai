package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.apeto.geniusai.server.entity.WechatUserInfo;
import cn.apeto.geniusai.server.mapper.WechatUserInfoMapper;
import cn.apeto.geniusai.server.service.WechatUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-04-01 10:17:34
 */
@Service
public class WechatUserInfoServiceImpl extends ServiceImpl<WechatUserInfoMapper, WechatUserInfo> implements WechatUserInfoService {

  @Override
  public WechatUserInfo getByOpenId (String openid) {
    return getOne(new LambdaQueryWrapper<WechatUserInfo>().eq(WechatUserInfo::getOpenId, openid));
  }

  @Override
  public WechatUserInfo getByUserId (Long userId) {
    LambdaQueryWrapper<WechatUserInfo> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(WechatUserInfo::getUserId, userId);
    return getOne(queryWrapper);
  }
}
