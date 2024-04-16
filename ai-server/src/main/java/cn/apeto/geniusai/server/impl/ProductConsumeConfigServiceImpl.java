package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.entity.ProductConsumeConfig;
import cn.apeto.geniusai.server.mapper.ProductConsumeConfigMapper;
import cn.apeto.geniusai.server.service.ProductConsumeConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品类型消耗配置表 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-06-17 06:38:37
 */
@Service
public class ProductConsumeConfigServiceImpl extends ServiceImpl<ProductConsumeConfigMapper, ProductConsumeConfig> implements ProductConsumeConfigService {

    @Override
    public ProductConsumeConfig getByType(Integer productType) {
        LambdaQueryWrapper<ProductConsumeConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductConsumeConfig::getProductType, productType);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ProductConsumeConfig> selectAllByStatus(Integer status) {
        LambdaQueryWrapper<ProductConsumeConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductConsumeConfig::getStatus, status);
        return baseMapper.selectList(queryWrapper);
    }
}
