package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.Usage;
import cn.apeto.geniusai.server.entity.ShareItemDetailLog;
import cn.apeto.geniusai.server.mapper.ShareItemDetailLogMapper;
import cn.apeto.geniusai.server.service.ExchangeCardDetailService;
import cn.apeto.geniusai.server.service.ShareItemDetailLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/05/18:35
 * @Description:
 */
@Service
@Slf4j
public class ShareItemDetailLogServiceImpl extends ServiceImpl<ShareItemDetailLogMapper, ShareItemDetailLog> implements ShareItemDetailLogService {

    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void questionsCompleted(Long shareId, String reqId, Long userId, String prompt, String answer, Usage usage, Integer type) {

        //  1 记录聊天
        ShareItemDetailLog shareItemDetailLog = new ShareItemDetailLog();
        shareItemDetailLog.setShareId(shareId);
        shareItemDetailLog.setQuestion(prompt);
        shareItemDetailLog.setAnswer(answer);
        shareItemDetailLog.setToken(usage.getTotalTokens());
        shareItemDetailLog.setReqId(reqId);
        shareItemDetailLog.setProductType(Constants.ProductTypeEnum.CHAT_KNOWLEDGE.getType());
        shareItemDetailLog.setLogDesc(Constants.LogDescriptionTypeEnum.SHARE_KNOWLEDGE.getDesc());
        if (!save(shareItemDetailLog)){
            log.error("ShareItemDetailLogService save is detail fail shareId:{} reqId:{}",userId, reqId);
        }

        //  2 代币消耗
        exchangeCardDetailService.consume(userId, type, reqId, usage.getTotalTokens());

    }
}
