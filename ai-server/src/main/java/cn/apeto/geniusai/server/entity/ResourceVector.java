package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/02/16:22
 * @Description:
 */
@Schema(description = "产品类型消耗配置表")
@Data
@TableName("resource_vector")
public class ResourceVector  extends BaseEntity{

    private static final long serialVersionUID = 1L;

    @Schema(description = "资源ID")
    @TableField("resource_id")
    private long resourceId;

    @Schema(description = "向量文本ID")
    @TableField("doc_id")
    private String docId;
}
