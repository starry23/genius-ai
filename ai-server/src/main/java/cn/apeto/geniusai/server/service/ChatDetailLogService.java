package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.domain.ChatEntity;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.domain.Usage;
import cn.apeto.geniusai.server.domain.vo.SessionRecordVo;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.ChatDetailLog;

import java.util.List;

/**
 * <p>
 * 问答记录表 服务类
 * </p>
 *
 * @author warape
 * @since 2023-03-29 08:14:15
 */
public interface ChatDetailLogService extends IService<ChatDetailLog> {

    List<ChatDetailLog> selectByUserId(Long userId);


    List<SessionRecordVo> selectLastQuestion(Long userId, String chatRole,String logType);

    List<ChatDetailLog> selectByUserIdAndReqId(Long userId, String reqId);

    List<ChatDetailLog> selectByUserIdAndReqIdDesc(Long userId, String reqId);

    Long selectCountByUserIdAndReqId(Long userId, String reqId);

    void removeByReqId(Long userId, String reqId);

    void questionsCompleted(String reqId, Long userId, String prompt, String msg, Usage usage, ChatEntity entity, Integer productType,String logType);

    List<MyMessage> getMessage(Long userId, String reqId, String prompt, ChatEntity entity,Integer productType);

    List<TrendVO> trend(Integer day);

    void removeByUserId(Long userId);

}
