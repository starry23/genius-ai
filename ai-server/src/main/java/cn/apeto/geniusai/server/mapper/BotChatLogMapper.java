package cn.apeto.geniusai.server.mapper;

import cn.apeto.geniusai.server.entity.BotChatLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 微信机器人聊天日志 Mapper 接口
 * </p>
 *
 * @author apeto
 * @since 2024-01-29 10:32:26
 */
@Mapper
public interface BotChatLogMapper extends BaseMapper<BotChatLog> {

}
