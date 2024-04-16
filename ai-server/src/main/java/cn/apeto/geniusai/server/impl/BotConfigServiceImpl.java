package cn.apeto.geniusai.server.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.dto.BotPageReq;
import cn.apeto.geniusai.server.entity.BotConfig;
import cn.apeto.geniusai.server.mapper.BotConfigMapper;
import cn.apeto.geniusai.server.service.BotConfigService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 机器人配置 服务实现类
 * </p>
 *
 * @author apeto
 * @since 2024-01-25 06:00:08
 */
@Service
public class BotConfigServiceImpl extends ServiceImpl<BotConfigMapper, BotConfig> implements BotConfigService {

    @Override
    public BotConfig getByAppIdAndToken(String appId, String token) {

        return baseMapper.selectOne(new LambdaQueryWrapper<BotConfig>().eq(BotConfig::getAppId, appId).eq(BotConfig::getToken, token));
    }

    @Override
    public BotConfig getUserAndId(Long id, long userId) {

        return baseMapper.selectOne(new LambdaQueryWrapper<BotConfig>().eq(BotConfig::getId, id).eq(BotConfig::getUserId, userId));
    }

    @Override
    public BotConfig getByAppId(String appId) {

        return baseMapper.selectOne(new LambdaQueryWrapper<BotConfig>().eq(BotConfig::getAppId, appId));
    }

    @Override
    public Page<BotConfig> pageList(Long userId, BotPageReq botPageReq) {
        Page<BotConfig> page = new Page<>(botPageReq.getCurrent(), botPageReq.getSize());
        String botName = botPageReq.getBotName();
        LambdaQueryWrapper<BotConfig> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(userId != null, BotConfig::getUserId, userId);
        queryWrapper.like(StrUtil.isNotBlank(botName),BotConfig::getBotName, botName);
        return baseMapper.selectPage(page, queryWrapper);
    }
}
