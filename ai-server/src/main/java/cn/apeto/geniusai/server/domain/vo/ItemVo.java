package cn.apeto.geniusai.server.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import cn.apeto.geniusai.server.entity.ItemResource;
import cn.apeto.geniusai.server.entity.KnowledgeItem;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/02/12:12
 * @Description:
 */
@Data
public class ItemVo {

    private Long userId;
    /**
     * 知识库项目ID
     */
    private Long knowledgeId;

    /**
     * 文件名称
     */
    private String itemName;

    private String itemDesc;

    private List<ItemResource> knowledgeItemList;
}
