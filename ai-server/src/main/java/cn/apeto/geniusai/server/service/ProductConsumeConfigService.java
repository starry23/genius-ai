package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.entity.ProductConsumeConfig;

import java.util.List;

/**
 * <p>
 * 产品类型消耗配置表 服务类
 * </p>
 *
 * @author warape
 * @since 2023-06-17 06:38:37
 */
public interface ProductConsumeConfigService extends IService<ProductConsumeConfig> {

    ProductConsumeConfig getByType(Integer productType);

    List<ProductConsumeConfig> selectAllByStatus(Integer status);

}
