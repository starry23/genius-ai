package cn.apeto.geniusai.server.service;

import cn.apeto.geniusai.server.entity.OpenApiMchConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 开放api商户配置 服务类
 * </p>
 *
 * @author apeto
 * @since 2023-12-30 03:13:30
 */
public interface OpenApiMchConfigService extends IService<OpenApiMchConfig> {

    OpenApiMchConfig getByMchId(String mchId);
}
