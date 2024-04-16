package cn.apeto.geniusai.server.impl;

import cn.apeto.geniusai.server.entity.UserConsumerLog;
import cn.apeto.geniusai.server.mapper.UserConsumerLogMapper;
import cn.apeto.geniusai.server.service.UserConsumerLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户消耗日志 服务实现类
 * </p>
 *
 * @author apeto
 * @since 2023-08-30 03:37:25
 */
@Service
public class UserConsumerLogServiceImpl extends ServiceImpl<UserConsumerLogMapper, UserConsumerLog> implements UserConsumerLogService {

}
