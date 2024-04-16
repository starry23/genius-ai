package cn.apeto.geniusai.server.service;

import cn.apeto.geniusai.server.domain.vo.ItemVo;
import cn.apeto.geniusai.server.entity.KnowledgeItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 知识库项目表 服务类
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
public interface KnowledgeItemService extends IService<KnowledgeItem> {

    String createItem(Long userId, String itemName, String itemDesc);

    List<ItemVo> getAllItemVo(Long userId);

    List<KnowledgeItem>  getItemByUid(Long userId);
}
