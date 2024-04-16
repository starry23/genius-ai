package cn.apeto.geniusai.server.job;

import cn.apeto.geniusai.server.core.mj.MJService;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.MjConstants;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.dto.TaskResponse;
import cn.apeto.geniusai.server.entity.MjImageInfo;
import cn.apeto.geniusai.server.service.AccountService;
import cn.apeto.geniusai.server.service.MjImageInfoService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.service.oss.UploadResult;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author apeto
 * @create 2023/6/12 19:39
 */
@Slf4j
@Component
public class MjJob {

    @Autowired
    private MjImageInfoService mjImageInfoService;
    @Autowired
    private AccountService accountService;

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void expireTask() {
        String key = SystemConstants.RedisKeyEnum.EXPIRE_TASK.getKey();
        if (!RedissonUtils.tryLockNotWaitBoolean(key)) {
            return;
        }

        try {
            List<MjImageInfo> list = mjImageInfoService.lambdaQuery()
                    .in(MjImageInfo::getFileStatus, MjConstants.TaskStatus.IN_PROGRESS
                            , MjConstants.TaskStatus.NOT_START, MjConstants.TaskStatus.SUBMITTED)
                    .le(MjImageInfo::getCreateTime, LocalDateTime.now().minusMinutes(15))
                    .ge(MjImageInfo::getCreateTime, LocalDateTime.now().minusMinutes(30))
                    .list();
            log.warn("置为失败返还资产任务: 当前有{}个任务超时", list.size());
            list.parallelStream().forEach(mjImageInfo -> {
                mjImageInfo.setFileStatus(MjConstants.TaskStatus.FAILURE.name());
                if (mjImageInfoService.updateById(mjImageInfo)) {
                    falureRefund(mjImageInfo);
                }
            });
        } catch (Exception e) {
            log.error("mj过期任务异常", e);
        } finally {
            RedissonUtils.unlock(key);
        }

    }

    @Scheduled(cron = "0 0 12,23 * * ?")
    public void expireTask30() {
        String key = SystemConstants.RedisKeyEnum.EXPIRE_TASK30.getKey();
        if (!RedissonUtils.tryLockNotWaitBoolean(key)) {
            return;
        }

        try {
            List<MjImageInfo> list = mjImageInfoService.lambdaQuery()
                    .eq(MjImageInfo::getFileStatus, MjConstants.TaskStatus.SUCCESS)
                    .in(MjImageInfo::getFileAction, MjConstants.ActionEnum.IMAGINE, MjConstants.ActionEnum.VARIATION)
                    .le(MjImageInfo::getCreateTime, LocalDateTime.now().minusDays(30))
                    .ge(MjImageInfo::getCreateTime, LocalDateTime.now().minusMinutes(31))
                    .list();
            log.warn("置为过期: 当前有{}个任务已超过30天", list.size());
            list.parallelStream().forEach(mjImageInfo -> {
                mjImageInfo.setFileStatus(MjConstants.TaskStatus.EXPIRE.name());
                mjImageInfoService.updateById(mjImageInfo);
            });
        } catch (Exception e) {
            log.error("mj过期30天任务异常", e);
        } finally {
            RedissonUtils.unlock(key);
        }

    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void task() {

        List<MjImageInfo> list = mjImageInfoService.lambdaQuery()
                .in(MjImageInfo::getFileStatus, MjConstants.TaskStatus.IN_PROGRESS
                        , MjConstants.TaskStatus.NOT_START, MjConstants.TaskStatus.SUBMITTED)
                .le(MjImageInfo::getCreateTime, LocalDateTime.now())
                .ge(MjImageInfo::getCreateTime, LocalDateTime.now().minusMinutes(5))
                .list();
        SaveSettingDTO.AiImageSetting aiImageSetting = CommonUtils.getPackageWebConfig().getAiImageSetting();
        if (aiImageSetting == null) {
            return;
        }
        for (MjImageInfo mjImageInfo : list) {
            String fileId = mjImageInfo.getFileId();
            String mjId = mjImageInfo.getMjId();
            String serviceType = mjImageInfo.getServiceType();
            String key = SystemConstants.RedisKeyEnum.MJ_STATUS_JOB.getKey(fileId);
            if (!RedissonUtils.tryLockNotWaitBoolean(key)) {
                continue;
            }
            try {

                log.info("请求mj fetch参数为:{}", mjId);
                MJService mjService = MJService.MJ_MAP.get(serviceType);

                TaskResponse taskResponse = mjService.fetch(mjId);
                if (taskResponse == null) {
                    log.error("mj task 返回信息TaskResponse为空!!! fileId:{}", fileId);
                    continue;
                }
                String status = taskResponse.getStatus();
                if (MjConstants.TaskStatus.FAILURE.name().equals(status)) {
                    falureRefund(mjImageInfo);
                }
                String cosImageUrl = taskResponse.getImageUrl();
                try {
                    UploadResult uploadResult = OSSFactory.build().uploadUrl(taskResponse.getCosImageUrl());
                    cosImageUrl = uploadResult.getPath();
                } catch (Exception ignored) {
                }

                mjImageInfo.setFailReason(taskResponse.getFailReason());
                mjImageInfo.setFileStatus(status);
                mjImageInfo.setCosUrl(cosImageUrl);
                mjImageInfo.setProgress(taskResponse.getProgress());
                mjImageInfo.setFileAction(taskResponse.getAction());
                mjImageInfoService.updateById(mjImageInfo);
            } catch (Exception e) {
                log.error("mj task 执行异常 fileId:{}", fileId, e);
            } finally {
                RedissonUtils.unlock(key);
            }
        }
    }

    private void falureRefund(MjImageInfo mjImageInfo) {
        String fileId = mjImageInfo.getFileId();
        Constants.LogDescriptionTypeEnum mjRefund = Constants.LogDescriptionTypeEnum.MJ_CONSUMPTION;
        accountService.fullRefund(mjImageInfo.getUserId(), fileId, mjRefund, "systemCallback");
    }
}
