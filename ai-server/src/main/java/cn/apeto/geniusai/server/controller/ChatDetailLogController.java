package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.sdk.entity.chat.Message.Role;
import cn.apeto.geniusai.server.annotations.DistributedLock;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.SystemConstants.RedisKeyEnum;
import cn.apeto.geniusai.server.domain.dto.CreateSessionDTO;
import cn.apeto.geniusai.server.domain.dto.SessionUploadFileDTO;
import cn.apeto.geniusai.server.domain.dto.UpdateSessionDTO;
import cn.apeto.geniusai.server.domain.vo.SessionRecordVo;
import cn.apeto.geniusai.server.entity.*;
import cn.apeto.geniusai.server.service.AiRoleService;
import cn.apeto.geniusai.server.service.ChatDetailLogService;
import cn.apeto.geniusai.server.service.FileInfoService;
import cn.apeto.geniusai.server.service.SessionRecordService;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 问答记录表 前端控制器
 * </p>
 *
 * @author warape
 * @since 2023-03-29 08:14:15
 */
@Tag(name = "会话记录相关")
@RestController
@RequestMapping("/api/session")
public class ChatDetailLogController {

    @Autowired
    private ChatDetailLogService chatDetailLogService;
    @Autowired
    private AiRoleService aiRoleService;
    @Autowired
    private SessionRecordService sessionRecordService;


    @Operation(summary = "创建会话")
    @PostMapping("/createSession")
    @DistributedLock(prefix = RedisKeyEnum.QUESTIONS_LOCK, key = "#roleId", waitFor = 0, isReqUserId = true)
    public ResponseResult<String> createSession(@RequestBody CreateSessionDTO createSessionDTO) {
        Long roleId = createSessionDTO.getRoleId();
        Long userId = StpUtil.getLoginIdAsLong();
        Integer productType = createSessionDTO.getProductType();
        String sessionName = createSessionDTO.getSessionName();
        String sessionDesc = createSessionDTO.getSessionDesc();
        String logType = createSessionDTO.getLogType();
        String model = createSessionDTO.getModel();

        String requestId = IdUtil.fastSimpleUUID();

        if (roleId != -1) {
            AiRole aiRole = aiRoleService.getById(roleId);
            if (aiRole != null) {
                sessionName = aiRole.getRoleName();
                sessionDesc = aiRole.getRoleDesc();
                String prompt = aiRole.getPrompt();
                ChatDetailLog entity = new ChatDetailLog();
                entity.setChatRole(Role.SYSTEM.getName());
                entity.setContent(prompt);
                entity.setRequestId(requestId);
                entity.setUserId(StpUtil.getLoginIdAsLong());
                entity.setProductType(productType);
                entity.setRoleId(roleId);
                chatDetailLogService.save(entity);
                StringRedisUtils.set(RedisKeyEnum.SESSION_ROLE_SYSTEM.getKey(userId, requestId), JSONUtil.toJsonStr(aiRole));
            }
        }
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setUserId(userId);
        sessionRecord.setRequestId(requestId);
        sessionRecord.setSessionName(sessionName);
        sessionRecord.setSessionDesc(sessionDesc);
        sessionRecord.setModel(model);
        sessionRecord.setLogType(logType);
        sessionRecord.setProductType(productType);
        sessionRecord.setRoleId(roleId);
        sessionRecordService.save(sessionRecord);

        return ResponseResultGenerator.success(requestId);
    }

    @Operation(summary = "修改会话")
    @PostMapping("/updateSession")
    public ResponseResult<?> updateSession(@RequestBody UpdateSessionDTO updateSessionDTO) {

        Long userId = StpUtil.getLoginIdAsLong();
        String reqId = updateSessionDTO.getReqId();
        String name = updateSessionDTO.getName();
        SessionRecord sessionRecord = sessionRecordService.getUidAndReqId(userId, reqId);
        sessionRecord.setSessionName(name);
        sessionRecordService.updateById(sessionRecord);

        return ResponseResultGenerator.success();

    }

    @Operation(summary = "会话记录(侧栏)")
    @GetMapping("/sessionRecordSidebar")
    public ResponseResult<List<SessionRecordVo>> sessionRecordSidebar() {
        Long userId = StpUtil.getLoginIdAsLong();

        List<SessionRecord> list = sessionRecordService.lambdaQuery()
                .eq(SessionRecord::getUserId, userId)
                .in(SessionRecord::getProductType, Constants.ProductTypeEnum.getSessionViewList())
                .orderByDesc(BaseEntity::getId)
                .list();

        List<SessionRecordVo> vo = list.stream().map(this::buildSessionRecordVo).collect(Collectors.toList());
        return ResponseResultGenerator.success(vo);
    }

    @Autowired
    private FileInfoService fileInfoService;

    private SessionRecordVo buildSessionRecordVo(SessionRecord sessionRecord) {
        SessionRecordVo sessionRecordVo = new SessionRecordVo();
        sessionRecordVo.setReqId(sessionRecord.getRequestId());
        sessionRecordVo.setRoleId(sessionRecord.getRoleId());
        sessionRecordVo.setCreateTime(sessionRecord.getCreateTime());
        sessionRecordVo.setProductType(sessionRecord.getProductType());
        sessionRecordVo.setSessionName(sessionRecord.getSessionName());
        sessionRecordVo.setSessionDesc(sessionRecord.getSessionDesc());
        Long fileId = sessionRecord.getFileId();
        if (fileId > 0) {
            FileInfo fileInfo = fileInfoService.getById(fileId);
            sessionRecordVo.setFileId(fileId);
            sessionRecordVo.setFileName(fileInfo.getOriginalFileName());
        }
        sessionRecordVo.setModel(sessionRecord.getModel());
        return sessionRecordVo;
    }


    @Operation(summary = "根据ReqId获取会话")
    @GetMapping("/sessionRecordSidebar/info/{reqId}")
    public ResponseResult<SessionRecordVo> sessionRecordSidebarByReqId(@PathVariable("reqId") String reqId) {
        Long userId = StpUtil.getLoginIdAsLong();
        SessionRecord sessionRecord = sessionRecordService.getUidAndReqId(userId, reqId);
        return ResponseResultGenerator.success(buildSessionRecordVo(sessionRecord));
    }

    @Operation(summary = "会话记录详情")
    @GetMapping("/sessionDetail")
    public ResponseResult<List<SessionRecordVo>> sessionDetail(@RequestParam("reqId") String reqId) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<ChatDetailLog> chatDetailLogs = chatDetailLogService.selectByUserIdAndReqId(userId, reqId);
        List<SessionRecordVo> result = chatDetailLogs.stream().map(chatDetailLog -> {
            SessionRecordVo sessionRecordVo = new SessionRecordVo();
            sessionRecordVo.setReqId(reqId);
            sessionRecordVo.setContent(chatDetailLog.getContent());
            sessionRecordVo.setChatRole(chatDetailLog.getChatRole());
            sessionRecordVo.setRoleId(chatDetailLog.getRoleId());
            sessionRecordVo.setProductType(chatDetailLog.getProductType());
            sessionRecordVo.setCreateTime(chatDetailLog.getCreateTime());
            return sessionRecordVo;
        }).collect(Collectors.toList());

        return ResponseResultGenerator.success(result);
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/removeSession")
    public ResponseResult<String> removeSession(@RequestParam("reqId") String reqId) {
        Long userId = StpUtil.getLoginIdAsLong();
        String redisKey = RedisKeyEnum.QUESTIONS.getKey(userId.toString(), reqId);
        StringRedisUtils.delete(redisKey);
        sessionRecordService.delByReqId(reqId);
        StringRedisUtils.delete(RedisKeyEnum.SESSION_ROLE_SYSTEM.getKey(userId, reqId));
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "全部删除")
    @DeleteMapping("/removeAllSession")
    public ResponseResult<String> removeAllSession() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<ChatDetailLog> list = chatDetailLogService.lambdaQuery()
                .select(BaseEntity::getId, ChatDetailLog::getRequestId, ChatDetailLog::getUserId)
                .eq(ChatDetailLog::getUserId, userId)
                .list();
        list.parallelStream()
                .forEach(chatDetailLog -> {
                    StringRedisUtils.delete(RedisKeyEnum.QUESTIONS.getKey(userId.toString(), chatDetailLog.getRequestId()));
                    StringRedisUtils.delete(RedisKeyEnum.SESSION_ROLE_SYSTEM.getKey(userId, chatDetailLog.getRequestId()));
                });
        sessionRecordService.delByUserId(userId);

        return ResponseResultGenerator.success();
    }

    @Operation(summary = "清空附件")
    @DeleteMapping("/sessionRecordSidebar/empty/file/{reqId}/{fileIds}")
    public ResponseResult<String> emptyFile(@PathVariable("reqId") String reqId, @PathVariable("fileIds") String fileIds) {
        if (StrUtil.isBlank(fileIds)) {
            return ResponseResultGenerator.success();
        }
        Long userId = StpUtil.getLoginIdAsLong();
        SessionRecord sessionRecord = sessionRecordService.getUidAndReqId(userId, reqId);
        sessionRecord.setFileId(-1L);
        sessionRecordService.updateById(sessionRecord);
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "绑定附件")
    @PostMapping("/sessionRecordSidebar/binding/file")
    public ResponseResult<String> uploadFile(@RequestBody SessionUploadFileDTO sessionUploadFileDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        String reqId = sessionUploadFileDTO.getReqId();
        Long fileId = sessionUploadFileDTO.getFileId();
        SessionRecord sessionRecord = sessionRecordService.getUidAndReqId(userId, reqId);
        sessionRecord.setFileId(fileId);
        sessionRecordService.updateById(sessionRecord);
        return ResponseResultGenerator.success();
    }


}
