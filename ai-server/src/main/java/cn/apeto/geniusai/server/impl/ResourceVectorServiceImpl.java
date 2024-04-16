package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.entity.ResourceVector;
import cn.apeto.geniusai.server.mapper.ResourceVectorMapper;
import cn.apeto.geniusai.server.service.ResourceVectorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/02/16:36
 * @Description:
 */
@Service
public class ResourceVectorServiceImpl extends ServiceImpl<ResourceVectorMapper, ResourceVector> implements ResourceVectorService {

    @Override
    public int insertBranch(Long resourceId, List<Long> docIds) {
        return baseMapper.insertBatchVector(resourceId,docIds);
    }

    @Override
    public List<ResourceVector> selectResourceVectorByResourceId(Long resourceId) {
        LambdaQueryWrapper<ResourceVector> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ResourceVector::getResourceId,resourceId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public boolean removeResourceVectorInResourceId(List<Long> resourceId) {
        LambdaQueryWrapper<ResourceVector> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ResourceVector::getResourceId,resourceId);
        return remove(wrapper);
    }
}
