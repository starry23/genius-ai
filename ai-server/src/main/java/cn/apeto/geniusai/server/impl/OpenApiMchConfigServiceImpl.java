package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.apeto.geniusai.server.entity.OpenApiMchConfig;
import cn.apeto.geniusai.server.mapper.OpenApiMchConfigMapper;
import cn.apeto.geniusai.server.service.OpenApiMchConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 开放api商户配置 服务实现类
 * </p>
 *
 * @author apeto
 * @since 2023-12-30 03:13:30
 */
@Service
public class OpenApiMchConfigServiceImpl extends ServiceImpl<OpenApiMchConfigMapper, OpenApiMchConfig> implements OpenApiMchConfigService {

    @Override
    public OpenApiMchConfig getByMchId(String mchId) {

        LambdaQueryWrapper<OpenApiMchConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OpenApiMchConfig::getMchId,mchId);
        return getOne(queryWrapper);
    }
}
