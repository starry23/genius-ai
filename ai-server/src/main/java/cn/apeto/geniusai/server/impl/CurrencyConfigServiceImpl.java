package cn.apeto.geniusai.server.impl;

import cn.apeto.geniusai.server.entity.CurrencyConfig;
import cn.apeto.geniusai.server.mapper.CurrencyConfigMapper;
import cn.apeto.geniusai.server.service.CurrencyConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 币配置 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-06-16 05:47:30
 */
@Service
public class CurrencyConfigServiceImpl extends ServiceImpl<CurrencyConfigMapper, CurrencyConfig> implements CurrencyConfigService {

}
