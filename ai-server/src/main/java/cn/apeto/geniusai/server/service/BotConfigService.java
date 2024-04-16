package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.domain.dto.BotPageReq;
import cn.apeto.geniusai.server.entity.BotConfig;

/**
 * <p>
 * 机器人配置 服务类
 * </p>
 *
 * @author apeto
 * @since 2024-01-25 06:00:08
 */
public interface BotConfigService extends IService<BotConfig> {

    BotConfig getByAppIdAndToken(String appId, String token);

    BotConfig getUserAndId(Long id, long userId);

    BotConfig getByAppId(String appId);

    Page<BotConfig> pageList(Long userId, BotPageReq botPageReq);

}
