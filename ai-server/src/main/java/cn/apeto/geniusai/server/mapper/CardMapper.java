package cn.apeto.geniusai.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.apeto.geniusai.server.entity.Card;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 卡密兑换 Mapper 接口
 * </p>
 *
 * @author hsg
 * @since 2023-07-04 16:28:17
 */
@Mapper
public interface CardMapper extends BaseMapper<Card> {
}
