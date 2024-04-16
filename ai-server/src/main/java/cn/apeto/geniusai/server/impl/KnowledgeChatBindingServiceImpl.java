package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.apeto.geniusai.server.entity.KnowledgeChatBinding;
import cn.apeto.geniusai.server.mapper.KnowledgeChatBindingMapper;
import cn.apeto.geniusai.server.service.KnowledgeChatBindingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 知识库&聊天记录绑定 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
@Service
public class KnowledgeChatBindingServiceImpl extends ServiceImpl<KnowledgeChatBindingMapper, KnowledgeChatBinding> implements KnowledgeChatBindingService {

    @Override
    public KnowledgeChatBinding getByKnowledgeId(Long knowledgeItemId) {
        LambdaQueryWrapper<KnowledgeChatBinding> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgeChatBinding::getKnowledgeId, knowledgeItemId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public KnowledgeChatBinding getByReqId(String reqId) {
        LambdaQueryWrapper<KnowledgeChatBinding> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgeChatBinding::getChatLogReq, reqId);
        return baseMapper.selectOne(queryWrapper);
    }
}
