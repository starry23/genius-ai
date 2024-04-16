package cn.apeto.geniusai.server.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.core.mj.MJService;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.MjConstants;
import cn.apeto.geniusai.server.domain.MjHookInfo;
import cn.apeto.geniusai.server.domain.StateDTO;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.BaseEntity;
import cn.apeto.geniusai.server.entity.MjImageInfo;
import cn.apeto.geniusai.server.mapper.MjImageInfoMapper;
import cn.apeto.geniusai.server.service.AccountService;
import cn.apeto.geniusai.server.service.MjImageInfoService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.service.oss.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * mj图片信息 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-06-10 04:01:04
 */
@Slf4j
@Service
public class MjImageInfoServiceImpl extends ServiceImpl<MjImageInfoMapper, MjImageInfo> implements MjImageInfoService {

    @Autowired
    private AccountService accountService;

    @Override
    public MjImageInfo getByFileId(String fileId) {
        LambdaQueryWrapper<MjImageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MjImageInfo::getFileId, fileId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public MjImageInfo getByFileIdAndFileAction(String mjId, String action) {
        LambdaQueryWrapper<MjImageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MjImageInfo::getFileId, mjId);
        queryWrapper.eq(MjImageInfo::getFileAction, action);
        return baseMapper.selectOne(queryWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveHook(MjHookInfo mjHookInfo) {
        String mjId = mjHookInfo.getId();
        if (StrUtil.isBlank(mjId)) {
            log.error("hook mjId is null");
            return;
        }
        MjImageInfo mjImageInfo;
        String state = mjHookInfo.getState();
        if(StrUtil.isNotBlank(state)) {
            mjImageInfo =  getByMjId(mjId);
        }else {
            StateDTO stateDTO = JSONUtil.toBean(state, StateDTO.class);
            String fileId = stateDTO.getFileId();
            mjImageInfo = getByFileId(fileId);
        }

        if(mjImageInfo == null){
            log.warn("回调没有找到数据");
            return;
        }

        String fileId = mjImageInfo.getFileId();
        String action = mjHookInfo.getAction();

        String serviceType = mjImageInfo.getServiceType();
        SaveSettingDTO.AiImageSetting aiImageSetting = MJService.MJ_MAP.get(serviceType).getConfig();

        String imageUrl = mjHookInfo.getImageUrl();

        String cosUrl = "";
        if (StrUtil.isNotBlank(imageUrl)) {
            try {
                imageUrl = CharSequenceUtil.replaceFirst(imageUrl, MjConstants.DISCORD_CDN_URL, aiImageSetting.getDiscordCdn());
                UploadResult uploadResult = OSSFactory.build().uploadUrl(imageUrl);
                cosUrl = uploadResult.getPath();
            } catch (Exception e) {
                log.error("MJ回调上传OSS异常", e);
            }
        }

        if (MjConstants.TaskStatus.FAILURE.name().equals(mjHookInfo.getStatus())) {
            Constants.LogDescriptionTypeEnum mjRefund = Constants.LogDescriptionTypeEnum.MJ_REFUND;
            accountService.fullRefund(mjImageInfo.getUserId(), fileId, mjRefund, "systemCallback");
        }

        mjImageInfo.setFileAction(action);
        mjImageInfo.setMjId(mjId);
        mjImageInfo.setCosUrl(cosUrl);
        mjImageInfo.setFileStatus(mjHookInfo.getStatus());
        mjImageInfo.setProgress(mjHookInfo.getProgress());
        saveOrUpdate(mjImageInfo);
    }

    private MjImageInfo getByMjId(String mjId) {

        return baseMapper.selectOne(new LambdaQueryWrapper<MjImageInfo>().eq(MjImageInfo::getMjId,mjId));
    }

    @Override
    public List<MjImageInfo> selectByUserId(Long userId) {
        LambdaQueryWrapper<MjImageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MjImageInfo::getUserId, userId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Page<MjImageInfo> selectByUserIdPage(Page<MjImageInfo> page, Long userId) {
        LambdaQueryWrapper<MjImageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MjImageInfo::getUserId, userId).orderByDesc(BaseEntity::getId);
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<TrendVO<Integer>> mjTrend(Integer day, String fileStatus) {
        Date end = new Date();
        DateTime start = DateUtil.offsetDay(end, -day);
        if (day == 0) {
            start = DateUtil.beginOfDay(end);
            end = DateUtil.endOfDay(end);
        }
        return baseMapper.trend(start, end, fileStatus);

    }
}
