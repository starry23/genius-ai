package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.apeto.geniusai.server.entity.ItemPartition;

import cn.apeto.geniusai.server.mapper.ItemPartitionMapper;

import cn.apeto.geniusai.server.service.ItemPartitionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gblhjh
 * @Date: 2023/08/03/9:48
 * @Description:
 */
@Service
public class ItemPartitionServiceImpl extends ServiceImpl<ItemPartitionMapper, ItemPartition> implements ItemPartitionService {
    @Override
    public List<ItemPartition> getByItemId(Long itemId) {
        LambdaQueryWrapper<ItemPartition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItemPartition::getItemId,itemId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public ItemPartition getByItemIdAndPartitionName(Long itemId,String partitionName) {
        LambdaQueryWrapper<ItemPartition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItemPartition::getItemId,itemId);
        wrapper.eq(ItemPartition::getPartitionName,partitionName);
        return baseMapper.selectOne(wrapper);
    }
}
