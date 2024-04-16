package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.apeto.geniusai.server.domain.MjHookInfo;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.MjImageInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * mj图片信息 服务类
 * </p>
 *
 * @author warape
 * @since 2023-06-10 04:01:04
 */
public interface MjImageInfoService extends IService<MjImageInfo> {

    MjImageInfo getByFileId(String fileId);

    MjImageInfo getByFileIdAndFileAction(String mjId, String action);


    void saveHook(MjHookInfo mjHookInfo);

    List<MjImageInfo> selectByUserId(Long userId);

    Page<MjImageInfo> selectByUserIdPage(Page<MjImageInfo> page, Long userId);

    List<TrendVO<Integer>> mjTrend(Integer day, String fileStatus);

}
