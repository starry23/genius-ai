package cn.apeto.geniusai.server.impl;

import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.utils.TikTokensUtil;
import cn.apeto.geniusai.server.domain.ChatEntity;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.domain.SystemConstants.RedisKeyEnum;
import cn.apeto.geniusai.server.domain.Usage;
import cn.apeto.geniusai.server.domain.vo.SessionRecordVo;
import cn.apeto.geniusai.server.domain.vo.TrendVO;
import cn.apeto.geniusai.server.entity.AiRole;
import cn.apeto.geniusai.server.entity.ChatDetailLog;
import cn.apeto.geniusai.server.mapper.ChatDetailLogMapper;
import cn.apeto.geniusai.server.service.ChatDetailLogService;
import cn.apeto.geniusai.server.service.ExchangeCardDetailService;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * <p>
 * 问答记录表 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-03-29 08:14:15
 */
@Slf4j
@Service
public class ChatDetailLogServiceImpl extends ServiceImpl<ChatDetailLogMapper, ChatDetailLog> implements ChatDetailLogService {

    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;
    @Autowired
    private SaTokenConfig saTokenConfig;

    @Override
    public List<ChatDetailLog> selectByUserId(Long userId) {
        LambdaQueryWrapper<ChatDetailLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetailLog::getUserId, userId);
        queryWrapper.orderByDesc(ChatDetailLog::getId);
        queryWrapper.last("limit 5");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<SessionRecordVo> selectLastQuestion(Long userId, String chatRole, String logType) {

        return baseMapper.selectLastQuestion(userId, chatRole, logType);
    }

    @Override
    public List<ChatDetailLog> selectByUserIdAndReqId(Long userId, String reqId) {
        LambdaQueryWrapper<ChatDetailLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetailLog::getUserId, userId);
        queryWrapper.eq(ChatDetailLog::getRequestId, reqId);
        queryWrapper.orderByAsc(ChatDetailLog::getId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<ChatDetailLog> selectByUserIdAndReqIdDesc(Long userId, String reqId) {
        LambdaQueryWrapper<ChatDetailLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetailLog::getUserId, userId);
        queryWrapper.eq(ChatDetailLog::getRequestId, reqId);
        queryWrapper.orderByDesc(ChatDetailLog::getId);
        queryWrapper.last("limit 10");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Long selectCountByUserIdAndReqId(Long userId, String reqId) {
        LambdaQueryWrapper<ChatDetailLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetailLog::getUserId, userId);
        queryWrapper.eq(ChatDetailLog::getRequestId, reqId);
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public void removeByReqId(Long userId, String reqId) {
        LambdaQueryWrapper<ChatDetailLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetailLog::getRequestId, reqId);
        queryWrapper.eq(ChatDetailLog::getUserId, userId);
        remove(queryWrapper);
    }

    //    @Async("chat")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void questionsCompleted(String reqId, Long userId, String prompt, String msg, Usage usage, ChatEntity entity, Integer productType, String logType) {

        // 某个用户的某个对话
        String sessionRole = StringRedisUtils.get(RedisKeyEnum.SESSION_ROLE_SYSTEM.getKey(userId, reqId));

        Long roleId = -1L;
        if (StrUtil.isNotEmpty(sessionRole)) {
            AiRole aiRole = JSONUtil.toBean(sessionRole, AiRole.class);
            roleId = aiRole.getId();
        }

        String redisKey = RedisKeyEnum.QUESTIONS.getKey(userId.toString(), reqId);

        // 问题保存到数据库
        ChatDetailLog askUserEntity = new ChatDetailLog();
        askUserEntity.setUserId(userId);
        askUserEntity.setRequestId(reqId);
        askUserEntity.setChatRole(Message.Role.USER.getName());
        askUserEntity.setContent(prompt);
        askUserEntity.setRoleId(roleId);
        askUserEntity.setLogType(logType);
        askUserEntity.setToken(usage.getPromptTokens());
        askUserEntity.setProductType(productType);
        if (!save(askUserEntity)) {
            log.error("chatDetailLogService save is user fail userId:{} reqId:{}", userId, reqId);
        }

        // 提问放入缓存
        Integer contextSplit = entity.getContextSplit();
        for (String splitStr : StrUtil.split(prompt, contextSplit)) {
            StringRedisUtils.add(redisKey, JSONUtil.toJsonStr(new Message(Message.Role.USER.getName(), splitStr, userId.toString())), askUserEntity.getId());
        }

        // 回到保存到数据库
        ChatDetailLog answerEntity = new ChatDetailLog();
        answerEntity.setUserId(userId);
        answerEntity.setRequestId(reqId);
        answerEntity.setChatRole(Message.Role.ASSISTANT.getName());
        answerEntity.setContent(msg);
        answerEntity.setLogType(logType);
        answerEntity.setToken(usage.getCompletionTokens());
        answerEntity.setProductType(productType);
        answerEntity.setRoleId(roleId);
        // mysql保存对话
        if (!save(answerEntity)) {
            log.error("chatDetailLogService save assistant is fail userId:{} reqId:{}", userId, reqId);
        }
        // 回答放入缓存
        for (String splitStr : StrUtil.split(msg, contextSplit)) {
            StringRedisUtils.add(redisKey, JSONUtil.toJsonStr(new Message(Message.Role.ASSISTANT.getName(), splitStr, userId.toString())), answerEntity.getId());
        }

        // 消费
        exchangeCardDetailService.consume(userId, productType, reqId, usage.getTotalTokens());

        // 设置过期时间 86400s
        StringRedisUtils.expireSeconds(redisKey, saTokenConfig.getTimeout() / 2);
    }

    @Override
    public List<MyMessage> getMessage(Long userId, String reqId, String prompt, ChatEntity entity, Integer productType) {

        // key
        String redisKey = RedisKeyEnum.QUESTIONS.getKey(userId.toString(), reqId);

        // 上下文限制
        int limit = entity.getContextLimit();
        String model = entity.getModel();
        Integer contextSplit = entity.getContextSplit();
        int totalLimit = limit * contextSplit;
        Constants.ProductTypeEnum productTypeEnum = Constants.ProductTypeEnum.getByEnum(productType);
        Constants.ServiceEnum serviceEnum = productTypeEnum.getServiceEnum();
        List<MyMessage> messages = new ArrayList<>();

        // 大文本不返回
        Set<String> strings = StringRedisUtils.reverseRangeByScore(redisKey, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, limit);

        int current = 0;
        boolean checkMessage = serviceEnum.equals(Constants.ServiceEnum.GPT) || serviceEnum.equals(Constants.ServiceEnum.SPARK_DESK);
        if (CollectionUtil.isNotEmpty(strings)) {
            for (String str : strings) {
                if (checkMessage) {
                    current += TikTokensUtil.tokens(model, str);
                    if (current + current > totalLimit) {
                        break;
                    }
                }
                messages.add(0, JSONUtil.toBean(str, MyMessage.class));
            }
        } else {
            List<ChatDetailLog> chatDetailLogs = selectByUserIdAndReqIdDesc(userId, reqId);
            for (ChatDetailLog chatDetailLog : chatDetailLogs) {
                String content = chatDetailLog.getContent();
                String chatRole = chatDetailLog.getChatRole();
                if (checkMessage) {
                    // gpt
                    for (String splitContent : StrUtil.split(content, contextSplit)) {
                        current += TikTokensUtil.tokens(model, content);
                        if (current + current > totalLimit) {
                            break;
                        }
                        MyMessage message = new MyMessage(chatRole, splitContent, userId.toString());
                        if (messages.size() >= limit) {
                            break;
                        }
                        messages.add(0, message);
                        StringRedisUtils.add(redisKey, JSONUtil.toJsonStr(message), chatDetailLog.getId());

                    }
                }
            }
        }
        messages.add(new MyMessage(Message.Role.USER.getName(), prompt));
        String sessionRole = StringRedisUtils.get(RedisKeyEnum.SESSION_ROLE_SYSTEM.getKey(userId, reqId));
        if (StrUtil.isNotEmpty(sessionRole) && serviceEnum.equals(Constants.ServiceEnum.GPT)) {
            AiRole aiRole = JSONUtil.toBean(sessionRole, AiRole.class);
            messages.add(0, new MyMessage(Message.Role.SYSTEM.getName(), aiRole.getPrompt()));
        }
        return messages;
    }

    @Override
    public List<TrendVO> trend(Integer day) {
        Date end = new Date();
        DateTime start = DateUtil.offsetDay(end, -day);
        if (day == 0) {
            start = DateUtil.beginOfDay(end);
            end = DateUtil.endOfDay(end);
        }
        return baseMapper.trend(start, end);
    }

    @Override
    public void removeByUserId(Long userId) {
        LambdaQueryWrapper<ChatDetailLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetailLog::getUserId, userId);
        remove(queryWrapper);
    }
}
