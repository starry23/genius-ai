package cn.apeto.geniusai.server.service;

import cn.apeto.geniusai.server.entity.ItemResource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 知识库项目资源表 服务类
 * </p>
 *
 * @author warape
 * @since 2023-07-31 04:31:00
 */
public interface ItemResourceService extends IService<ItemResource> {

    List<ItemResource> getByItemId(Long itemId);



}
