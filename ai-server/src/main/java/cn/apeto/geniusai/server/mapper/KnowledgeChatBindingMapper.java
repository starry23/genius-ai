package cn.apeto.geniusai.server.mapper;

import cn.apeto.geniusai.server.entity.KnowledgeChatBinding;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 知识库&聊天记录绑定 Mapper 接口
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
@Mapper
public interface KnowledgeChatBindingMapper extends BaseMapper<KnowledgeChatBinding> {

}
