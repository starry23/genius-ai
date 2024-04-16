package cn.apeto.geniusai.server.service;

import cn.apeto.geniusai.server.entity.KnowledgeChatBinding;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 知识库&聊天记录绑定 服务类
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
public interface KnowledgeChatBindingService extends IService<KnowledgeChatBinding> {

    KnowledgeChatBinding getByKnowledgeId(Long knowledgeItemId);


    KnowledgeChatBinding getByReqId(String reqId);
}
