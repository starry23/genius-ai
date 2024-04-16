package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.entity.BaseEntity;
import cn.apeto.geniusai.server.entity.ItemResource;
import cn.apeto.geniusai.server.mapper.ItemResourceMapper;
import cn.apeto.geniusai.server.service.ItemResourceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 知识库项目资源表 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
@Service
public class ItemResourceServiceImpl extends ServiceImpl<ItemResourceMapper, ItemResource> implements ItemResourceService {

    @Override
    public List<ItemResource> getByItemId(Long itemId) {
        LambdaQueryWrapper<ItemResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ItemResource::getItemId, itemId);
        queryWrapper.orderByDesc(BaseEntity::getId);
        return list(queryWrapper);
    }

}
