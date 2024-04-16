package cn.apeto.geniusai.server.impl;

import cn.apeto.geniusai.server.entity.ChatGptConfig;
import cn.apeto.geniusai.server.mapper.ChatGptConfigMapper;
import cn.apeto.geniusai.server.service.ChatGptConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-03-29 08:14:15
 */
@Service
public class ChatGptConfigServiceImpl extends ServiceImpl<ChatGptConfigMapper, ChatGptConfig> implements ChatGptConfigService {

}
