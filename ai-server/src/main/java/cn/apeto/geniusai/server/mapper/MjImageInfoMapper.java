package cn.apeto.geniusai.server.mapper;

import cn.hutool.core.date.DateTime;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.MjImageInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * mj图片信息 Mapper 接口
 * </p>
 *
 * @author warape
 * @since 2023-06-10 04:01:04
 */
@Mapper
public interface MjImageInfoMapper extends BaseMapper<MjImageInfo> {

    List<TrendVO<Integer>> trend(@Param("start") DateTime start, @Param("end")Date end, @Param("fileStatus")String fileStatus);
}
