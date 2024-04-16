package cn.apeto.geniusai.server.mapper;

import cn.hutool.core.date.DateTime;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.InviteLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 邀请记录 Mapper 接口
 * </p>
 *
 * @author warape
 * @since 2023-04-06 12:10:07
 */
@Mapper
public interface InviteLogMapper extends BaseMapper<InviteLog> {

    List<TrendVO<Integer>> trend(@Param("start") DateTime start, @Param("end") Date end);
}
