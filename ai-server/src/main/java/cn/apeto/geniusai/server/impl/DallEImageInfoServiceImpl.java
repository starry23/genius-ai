package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.apeto.geniusai.server.entity.BaseEntity;
import cn.apeto.geniusai.server.entity.DallEImageInfo;
import cn.apeto.geniusai.server.mapper.DallEImageInfoMapper;
import cn.apeto.geniusai.server.service.DallEImageInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * mj图片信息 服务实现类
 * </p>
 *
 * @author apeto
 * @since 2023-11-15 02:48:01
 */
@Service
public class DallEImageInfoServiceImpl extends ServiceImpl<DallEImageInfoMapper, DallEImageInfo> implements DallEImageInfoService {

    @Override
    public List<DallEImageInfo> selectByUserId(long userId) {

        LambdaQueryWrapper<DallEImageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DallEImageInfo::getUserId, userId);
        queryWrapper.orderByDesc(BaseEntity::getId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public DallEImageInfo getByFileId(String fileId) {
        LambdaQueryWrapper<DallEImageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DallEImageInfo::getFileId, fileId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void removeByFileId(String fileId) {
        LambdaQueryWrapper<DallEImageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DallEImageInfo::getFileId, fileId);
        remove(queryWrapper);
    }
}
