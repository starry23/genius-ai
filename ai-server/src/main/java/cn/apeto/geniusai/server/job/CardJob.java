package cn.apeto.geniusai.server.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.entity.Card;
import cn.apeto.geniusai.server.service.CardService;
import cn.apeto.geniusai.server.utils.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author apeto
 * @create 2023/7/6 15:08
 */
@Slf4j
@Component
public class CardJob {

    @Autowired
    private CardService cardService;

    @Scheduled(cron = "0 0 11,22 * * ?")
    public void expireTask() {

        String key = SystemConstants.RedisKeyEnum.CARD_EXPIRE_MEMBER.getKey();
        if (!RedissonUtils.tryLockNotWaitBoolean(key)) {
            return;
        }
        try {
            DateTime date = DateUtil.date();
            List<Card> list = cardService.lambdaQuery()
                    .le(Card::getExpirationDate, date)
                    .eq(Card::getStatus, Constants.CardStatusEnum.WAIT.getStatus())
                    .list();
            for (Card card : list) {
                try {
                    card.setStatus(Constants.CardStatusEnum.EXPIRE.getStatus());
                    cardService.updateById(card);
                } catch (Exception e) {
                    log.error("兑换码过期任务异常", e);
                }
            }
        } finally {
            RedissonUtils.unlock(key);
        }

    }
}
