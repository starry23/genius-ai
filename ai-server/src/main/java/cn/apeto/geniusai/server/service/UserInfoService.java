package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.UserInfo;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author warape
 * @since 2023-03-29 08:14:15
 */
public interface UserInfoService extends IService<UserInfo> {

  Long getOrCreateWechatUser (WxOAuth2UserInfo wxMpUser);


  UserInfo createWechatUserByOpenId (String openId);

  UserInfo getByPhone (String phone);

  UserInfo getByEmail (String email);

  Long doSmsSignUp (String accountNum, String password, Integer type);

  @Transactional(rollbackFor = Exception.class)
  UserInfo createUser (Integer type, String accountNum, String password);

  List<TrendVO> trend (Integer day);

  boolean isMember(Long userId);
}
