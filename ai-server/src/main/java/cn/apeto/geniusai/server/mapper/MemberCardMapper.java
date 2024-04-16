package cn.apeto.geniusai.server.mapper;

import cn.hutool.core.date.DateTime;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.MemberCard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 会员卡 Mapper 接口
 * </p>
 *
 * @author warape
 * @since 2023-04-08 06:49:07
 */
@Mapper
public interface MemberCardMapper extends BaseMapper<MemberCard> {

}
