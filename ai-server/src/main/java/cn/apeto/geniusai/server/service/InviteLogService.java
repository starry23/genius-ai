package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.InviteLog;

import java.util.List;

/**
 * <p>
 * 邀请记录 服务类
 * </p>
 *
 * @author warape
 * @since 2023-04-06 12:10:07
 */
public interface InviteLogService extends IService<InviteLog> {

  void inviteHandler (String inviteCode, Long signUpUserId);

  long getCountByInviteUserId(Long userId);

  InviteLog getByToInviteUserId(Long userId);

    List<TrendVO<Integer>> inviteTrend(Integer day);
}
