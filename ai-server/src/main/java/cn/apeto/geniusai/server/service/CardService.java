package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.domain.dto.CardPageDTO;
import cn.apeto.geniusai.server.entity.Card;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 卡密兑换  服务接口
 * </p>
 *
 * @author hsg
 * @since 2023-07-04 16:28:17
 */
public interface CardService extends IService<Card> {
    IPage<Card> getPage(CardPageDTO cardPageDTO);

    void numberCreate(LocalDateTime dateTime, Integer num, BigDecimal amount);

    void numberInvalidate(Long cardId);

    void redeemCard(long loginIdAsLong,String code);

    List<Card> selectByBatchNo(String batchNo);

}
