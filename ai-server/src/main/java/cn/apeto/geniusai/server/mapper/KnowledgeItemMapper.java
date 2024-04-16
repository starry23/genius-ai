package cn.apeto.geniusai.server.mapper;

import cn.apeto.geniusai.server.domain.vo.ItemVo;
import cn.apeto.geniusai.server.entity.KnowledgeItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

/**
 * <p>
 * 知识库项目表 Mapper 接口
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
@Mapper
public interface KnowledgeItemMapper extends BaseMapper<KnowledgeItem> {
    List<ItemVo> getItemVos(@Param("uid") Long uid);
}
