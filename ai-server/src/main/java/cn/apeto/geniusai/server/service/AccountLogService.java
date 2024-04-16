package cn.apeto.geniusai.server.service;

import cn.apeto.geniusai.server.domain.vo.MemberExpConsumeVO;
import cn.apeto.geniusai.server.entity.AccountLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 账户表日志 服务类
 * </p>
 *
 * @author warape
 * @since 2023-04-02 05:10:18
 */
public interface AccountLogService extends IService<AccountLog> {


    List<AccountLog> getByReqId(String reqId);

    AccountLog getAccountLog(String reqId, String cardCode);

    AccountLog getByOrderNoAndType(String orderNo, Integer type);

    AccountLog getByOrderNo(String orderNo,Integer directionType);
    AccountLog getByReqId(String reqId,Integer directionType);

    List<MemberExpConsumeVO> selectMemberExpConsume(Long userId, Date startTime, Date endTime);
}
