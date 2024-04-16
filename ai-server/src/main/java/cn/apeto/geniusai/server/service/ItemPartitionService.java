package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.entity.ItemPartition;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/03/9:47
 * @Description:
 */
public interface ItemPartitionService extends IService<ItemPartition> {
    /**
     * * 根据项目获取分区
     * @param itemId
     * @return
     */
    List<ItemPartition> getByItemId(Long itemId);

    ItemPartition getByItemIdAndPartitionName(Long itemId,String partitionName);

}
