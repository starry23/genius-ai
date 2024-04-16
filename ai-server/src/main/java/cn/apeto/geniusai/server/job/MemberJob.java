package cn.apeto.geniusai.server.job;

import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.entity.ExchangeCardDetail;
import cn.apeto.geniusai.server.service.AccountLogService;
import cn.apeto.geniusai.server.service.AccountService;
import cn.apeto.geniusai.server.service.ExchangeCardDetailService;
import cn.apeto.geniusai.server.utils.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author apeto
 * @create 2023/4/9 5:41 下午
 */
@Slf4j
@Component
public class MemberJob {

    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;

    @Scheduled(cron = "5 * * * * ? ")
    public void expireMember() {

        String key = SystemConstants.RedisKeyEnum.EXPIRE_MEMBER.getKey();
        if (!RedissonUtils.tryLockNotWaitBoolean(key)) {
            return;
        }

        try {
            List<ExchangeCardDetail> exchangeCardDetails = exchangeCardDetailService.selectExpire();
            for (ExchangeCardDetail exchangeCardDetail : exchangeCardDetails) {
                try {
                    exchangeCardDetail.setExchangeState(Constants.ExchangeCardStateEnum.EXPIRES.getState());
                } catch (Exception e) {
                    log.error("过期会员清理异常", e);
                }

            }
            exchangeCardDetailService.updateBatchById(exchangeCardDetails);
        } finally {
            RedissonUtils.unlock(key);

        }


    }

}
