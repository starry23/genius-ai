package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.apeto.geniusai.server.entity.SessionRecord;
import cn.apeto.geniusai.server.mapper.SessionRecordMapper;
import cn.apeto.geniusai.server.service.SessionRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会话记录 服务实现类
 * </p>
 *
 * @author apeto
 * @since 2023-12-23 11:50:55
 */
@Service
public class SessionRecordServiceImpl extends ServiceImpl<SessionRecordMapper, SessionRecord> implements SessionRecordService {

    @Override
    public SessionRecord getByReqId(String reqId) {
        LambdaQueryWrapper<SessionRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SessionRecord::getRequestId,reqId);
        return getOne(queryWrapper);
    }

    @Override
    public SessionRecord getUidAndReqId(Long userId, String reqId) {
        LambdaQueryWrapper<SessionRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SessionRecord::getRequestId,reqId);
        queryWrapper.eq(SessionRecord::getUserId,userId);
        return getOne(queryWrapper);
    }

    @Override
    public void delByUserId(Long userId) {
        remove(new LambdaQueryWrapper<SessionRecord>().eq(SessionRecord::getUserId,userId));
    }

    @Override
    public void delByReqId(String reqId) {
        remove(new LambdaQueryWrapper<SessionRecord>().eq(SessionRecord::getRequestId,reqId));
    }
}
