package cn.apeto.geniusai.server.service;

import cn.apeto.geniusai.server.entity.SessionRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会话记录 服务类
 * </p>
 *
 * @author apeto
 * @since 2023-12-23 11:50:55
 */
public interface SessionRecordService extends IService<SessionRecord> {

    SessionRecord getByReqId(String key);
    SessionRecord getUidAndReqId(Long userId, String reqId);

    void delByUserId(Long userId);

    void delByReqId(String reqId);
}
