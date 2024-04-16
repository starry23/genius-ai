package cn.apeto.geniusai.server.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.ExchangeCardDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 兑换卡详情 Mapper 接口
 * </p>
 *
 * @author warape
 * @since 2023-04-05 04:51:24
 */
@Mapper
public interface ExchangeCardDetailMapper extends BaseMapper<ExchangeCardDetail> {

//    List<TrendVO<Integer>> exchangeCardTrend(@Param("start") DateTime start, @Param("end") Date end);
}
