package cn.apeto.geniusai.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.apeto.geniusai.server.entity.AccountLog;
import cn.apeto.geniusai.server.entity.ResourceVector;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/02/16:25
 * @Description:
 */
@Mapper
public interface ResourceVectorMapper extends BaseMapper<ResourceVector> {

    // 批量插入
    int insertBatchVector(@Param("resourceId") Long resourceId, @Param("docIds") List<Long> docIds);
}
