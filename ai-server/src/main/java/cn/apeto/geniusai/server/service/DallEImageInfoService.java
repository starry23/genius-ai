package cn.apeto.geniusai.server.service;

import cn.apeto.geniusai.server.entity.DallEImageInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * mj图片信息 服务类
 * </p>
 *
 * @author apeto
 * @since 2023-11-15 02:48:01
 */
public interface DallEImageInfoService extends IService<DallEImageInfo> {

    List<DallEImageInfo> selectByUserId(long userId);

    DallEImageInfo getByFileId(String fileId);

    void removeByFileId(String fileId);
}
