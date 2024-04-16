package cn.apeto.geniusai.server.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.vo.ItemVo;
import cn.apeto.geniusai.server.entity.ItemPartition;
import cn.apeto.geniusai.server.entity.KnowledgeChatBinding;
import cn.apeto.geniusai.server.entity.KnowledgeItem;
import cn.apeto.geniusai.server.mapper.KnowledgeItemMapper;
import cn.apeto.geniusai.server.service.ItemPartitionService;
import cn.apeto.geniusai.server.service.KnowledgeChatBindingService;
import cn.apeto.geniusai.server.service.KnowledgeItemService;
import cn.apeto.geniusai.server.utils.MilvusClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 知识库项目表 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
@Service
public class KnowledgeItemServiceImpl extends ServiceImpl<KnowledgeItemMapper, KnowledgeItem> implements KnowledgeItemService {



    @Autowired
    private KnowledgeChatBindingService knowledgeChatBindingService;

    @Autowired
    private KnowledgeItemMapper knowledgeItemMapper;

    @Autowired
    private ItemPartitionService itemPartitionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createItem(Long userId, String itemName, String itemDesc) {
        String requestId = IdUtil.fastSimpleUUID();
        KnowledgeItem knowledgeItem = new KnowledgeItem();
        knowledgeItem.setUserId(userId);
        knowledgeItem.setItemName(itemName);
        knowledgeItem.setItemDesc(itemDesc);
        save(knowledgeItem);

        KnowledgeChatBinding knowledgeChatBinding = new KnowledgeChatBinding();
        knowledgeChatBinding.setUserId(userId);
        knowledgeChatBinding.setKnowledgeId(knowledgeItem.getId());
        knowledgeChatBinding.setChatLogReq(requestId);
        knowledgeChatBindingService.save(knowledgeChatBinding);

        // TODO 创建默认分区
        ItemPartition itemPartition = new ItemPartition();
        itemPartition.setItemId(knowledgeItem.getId());
        itemPartition.setPartitionDesc("默认分区");
        itemPartition.setPartitionName(MilvusClientUtil.DEFAULT_PARTITION_NAME);
        itemPartition.setUserId(userId);
        itemPartition.setPartitionCode(MilvusClientUtil.DEFAULT_PARTITION_NAME);
        itemPartitionService.save(itemPartition);

        return requestId;
    }

    @Override
    public List<ItemVo> getAllItemVo(Long userId) {
        return knowledgeItemMapper.getItemVos(userId);
    }

    @Override
    public List<KnowledgeItem> getItemByUid(Long userId) {
        LambdaQueryWrapper<KnowledgeItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgeItem::getUserId, userId);
        return baseMapper.selectList(queryWrapper);
    }
}
