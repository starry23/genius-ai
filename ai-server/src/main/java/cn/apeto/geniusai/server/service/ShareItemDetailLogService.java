package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.domain.ChatConfigEntity;
import cn.apeto.geniusai.server.domain.Usage;
import cn.apeto.geniusai.server.entity.ShareItemDetailLog;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/05/18:34
 * @Description:
 */
public interface ShareItemDetailLogService extends IService<ShareItemDetailLog> {
    void questionsCompleted(Long shareId, String reqId, Long userId, String prompt, String question, Usage usage, Integer type);
}
