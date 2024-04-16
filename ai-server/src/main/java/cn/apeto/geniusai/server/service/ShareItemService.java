package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.entity.ShareItem;
import cn.apeto.geniusai.server.entity.TopBarConfig;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/05/11:20
 * @Description:
 */
public interface ShareItemService extends IService<ShareItem> {
    ShareItem getByUidAndItemId(Long userId, Long itemId);

    ShareItem getByUuid(String uuid);
}
