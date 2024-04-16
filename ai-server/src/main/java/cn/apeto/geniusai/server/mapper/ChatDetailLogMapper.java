package cn.apeto.geniusai.server.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.apeto.geniusai.server.domain.vo.SessionRecordVo;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.ChatDetailLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 问答记录表 Mapper 接口
 * </p>
 *
 * @author warape
 * @since 2023-03-29 08:14:15
 */
@Mapper
public interface ChatDetailLogMapper extends BaseMapper<ChatDetailLog> {

  List<SessionRecordVo> selectLastQuestion (@Param("userId") Long userId, @Param("chatRole") String chatRole, @Param("logType") String logType);

  List<TrendVO> trend (@Param("start") DateTime start, @Param("end") Date end);
}
