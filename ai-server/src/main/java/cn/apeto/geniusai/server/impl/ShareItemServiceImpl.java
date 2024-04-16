package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.entity.ShareItem;
import cn.apeto.geniusai.server.entity.TopBarConfig;
import cn.apeto.geniusai.server.mapper.ShareItemMapper;
import cn.apeto.geniusai.server.mapper.TopBarConfigMapper;
import cn.apeto.geniusai.server.service.ShareItemService;
import cn.apeto.geniusai.server.service.TopBarConfigService;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/05/11:20
 * @Description:
 */
@Service
public class ShareItemServiceImpl extends ServiceImpl<ShareItemMapper, ShareItem> implements ShareItemService {
    @Override
    public ShareItem getByUidAndItemId(Long userId, Long itemId) {
        LambdaQueryWrapper<ShareItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareItem::getUserId,userId);
        wrapper.eq(ShareItem::getItemId,itemId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public ShareItem getByUuid(String uuid) {
        LambdaQueryWrapper<ShareItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareItem::getUuid,uuid);
        return baseMapper.selectOne(wrapper);
    }
}
