package cn.apeto.geniusai.server.mapper;

import cn.apeto.geniusai.server.entity.SessionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 会话记录 Mapper 接口
 * </p>
 *
 * @author apeto
 * @since 2023-12-23 11:50:55
 */
@Mapper
public interface SessionRecordMapper extends BaseMapper<SessionRecord> {

}
