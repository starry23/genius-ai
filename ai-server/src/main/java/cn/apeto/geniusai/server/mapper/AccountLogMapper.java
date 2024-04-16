package cn.apeto.geniusai.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.apeto.geniusai.server.domain.vo.MemberExpConsumeVO;
import cn.apeto.geniusai.server.entity.AccountLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 账户表日志 Mapper 接口
 * </p>
 *
 * @author warape
 * @since 2023-04-02 05:10:18
 */
@Mapper
public interface AccountLogMapper extends BaseMapper<AccountLog> {

    List<MemberExpConsumeVO> selectMemberExpConsume(@Param("userId")Long userId, @Param("startTime") Date startTime, @Param("endTime")Date endTime);


}
