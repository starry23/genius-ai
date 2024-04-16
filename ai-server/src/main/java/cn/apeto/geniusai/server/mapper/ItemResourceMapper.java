package cn.apeto.geniusai.server.mapper;

import cn.apeto.geniusai.server.entity.ItemResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 知识库项目资源表 Mapper 接口
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
@Mapper
public interface ItemResourceMapper extends BaseMapper<ItemResource> {
    @Select("SELECT * FROM item_resource WHERE user_id = #{uid} AND knowledge_id = #{knowledgeId} AND yn = '1'")
    ItemResource getItemResourceByUidAndKnowledgeId(@Param("uid") Long uid,
                                                    @Param("knowledgeId") Long knowledgeId);
}
