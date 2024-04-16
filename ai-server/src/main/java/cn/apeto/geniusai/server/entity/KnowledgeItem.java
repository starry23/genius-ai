package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.apeto.geniusai.server.entity.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 知识库项目表
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
@Getter
@Setter
@TableName("knowledge_item")
public class KnowledgeItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 项目名称
     */
    @TableField("item_name")
    private String itemName;

    /**
     * 项目描述
     */
    @TableField("item_desc")
    private String itemDesc;


}
