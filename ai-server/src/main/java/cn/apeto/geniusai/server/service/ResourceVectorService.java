package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.entity.ResourceVector;
import cn.apeto.geniusai.server.entity.TopBarConfig;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/02/16:35
 * @Description:
 */
public interface ResourceVectorService extends IService<ResourceVector> {
    int insertBranch(Long resourceId, List<Long> docIds);

    List<ResourceVector> selectResourceVectorByResourceId(Long resourceId);

    boolean removeResourceVectorInResourceId(List<Long> resourceId);
}
