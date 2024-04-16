package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.vo.MemberExpConsumeVO;
import cn.apeto.geniusai.server.entity.AccountLog;
import cn.apeto.geniusai.server.mapper.AccountLogMapper;
import cn.apeto.geniusai.server.service.AccountLogService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 账户表日志 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-04-02 05:10:18
 */
@Service
public class AccountLogServiceImpl extends ServiceImpl<AccountLogMapper, AccountLog> implements AccountLogService {

    @Override
    public List<AccountLog> getByReqId(String reqId) {
        LambdaQueryWrapper<AccountLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountLog::getRequestId, reqId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public AccountLog getAccountLog(String reqId, String cardCode) {
        LambdaQueryWrapper<AccountLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountLog::getRequestId, reqId);
        queryWrapper.eq(AccountLog::getOutsideCode, cardCode);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public AccountLog getByOrderNoAndType(String orderNo, Integer type) {

        LambdaQueryWrapper<AccountLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountLog::getOutsideCode, orderNo);
        queryWrapper.eq(AccountLog::getLogDescriptionType, type);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public AccountLog getByOrderNo(String orderNo, Integer directionType) {
        LambdaQueryWrapper<AccountLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountLog::getOutsideCode, orderNo);
        queryWrapper.eq(AccountLog::getDirectionType, directionType);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public AccountLog getByReqId(String reqId, Integer directionType) {
        LambdaQueryWrapper<AccountLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountLog::getRequestId, reqId);
        queryWrapper.eq(AccountLog::getDirectionType, directionType);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<MemberExpConsumeVO> selectMemberExpConsume(Long userId, Date startTime, Date endTime) {
        return baseMapper.selectMemberExpConsume(userId, startTime, endTime);

    }
}
