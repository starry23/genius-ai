package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.dto.BindingBotDTO;
import cn.apeto.geniusai.server.domain.dto.BotPageReq;
import cn.apeto.geniusai.server.domain.dto.OnlineBotDTO;
import cn.apeto.geniusai.server.domain.vo.BindingBotVO;
import cn.apeto.geniusai.server.entity.BotConfig;
import cn.apeto.geniusai.server.service.BotConfigService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "微信智能对话相关")
@RestController
@RequestMapping("/api/wechat/bot")
public class BotConfigController {


    @Autowired
    private BotConfigService botConfigService;

    @Operation(summary = "机器人绑定及修改")
    @PostMapping("/bot/saveOrUpdate")
    public ResponseResult<?> botSaveOrUpdate(@RequestBody @Validated BindingBotDTO bindingBotDTO) {
        long userId = StpUtil.getLoginIdAsLong();

        String appId = bindingBotDTO.getAppId();
        String token = bindingBotDTO.getToken();
        Long id = bindingBotDTO.getId();
        BotConfig botConfig = new BotConfig();


        if (id != null) {
            // 修改
            botConfig = botConfigService.getById(id);
        } else {
            // 新增
            BotConfig checkBotConfig = botConfigService.getByAppIdAndToken(appId, token);
            if (checkBotConfig != null) {
                return ResponseResultGenerator.error("机器人已绑定");
            }
            botConfig.setUserId(userId);
            botConfig.setAppId(appId);
            botConfig.setAesKey(bindingBotDTO.getAesKey());
            botConfig.setToken(token);
        }

        botConfig.setBotDesc(bindingBotDTO.getBotDesc());
        botConfig.setBotName(bindingBotDTO.getBotName());
        botConfig.setKefuName(bindingBotDTO.getKefuName());
        botConfig.setKnowledgeId(bindingBotDTO.getKnowledgeId());
        botConfig.setKefuAvatar(bindingBotDTO.getKefuAvatar());
        botConfig.setProductType(bindingBotDTO.getProductType());
        botConfig.setState(bindingBotDTO.getState());
        String h5BotUrl = "https://chatbot.weixin.qq.com/webapp/{}?robotName={}";
        botConfig.setH5Url(StrUtil.format(h5BotUrl, token, bindingBotDTO.getKefuName()));
        botConfigService.saveOrUpdate(botConfig);
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "机器人上删除")
    @DeleteMapping("/bot/{id}")
    public ResponseResult<?> deleteBot(@PathVariable Long id) {
        botConfigService.removeById(id);
        return ResponseResultGenerator.success();
    }


    @Operation(summary = "机器人上下线")
    @PostMapping("/bot/online")
    public ResponseResult<?> onlineBot(@RequestBody OnlineBotDTO onlineBotDTO) {

        long userId = StpUtil.getLoginIdAsLong();
        BotConfig botConfig = botConfigService.getById(onlineBotDTO.getId());
        if (botConfig.getUserId() != userId) {
            return ResponseResultGenerator.error("机器人不属于当前用户");
        }
        Integer state = onlineBotDTO.getState();
        botConfig.setState(state);
        botConfigService.updateById(botConfig);
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "获取机器人信息")
    @GetMapping("/bot/info/{id}")
    public ResponseResult<BindingBotVO> getH5Url(@PathVariable("id") Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        BotConfig botConfig = botConfigService.getUserAndId(id, userId);
        BindingBotVO vo = new BindingBotVO();
        vo.setKefuAvatar(OSSFactory.build().getFullUrl(botConfig.getKefuAvatar()));
        BeanUtil.copyProperties(botConfig, vo);
        return ResponseResultGenerator.success(vo);
    }

    @Operation(summary = "分页查询")
    @GetMapping("/bot/page")
    public ResponseResult<IPage<BindingBotVO>> page(BotPageReq reqPage) {
        long userId = StpUtil.getLoginIdAsLong();
        IPage<BindingBotVO> convert = botConfigService.pageList(userId, reqPage)
                .convert(botConfig -> {
                    BindingBotVO vo = new BindingBotVO();
                    BeanUtil.copyProperties(botConfig, vo);
                    vo.setKefuAvatarView(OSSFactory.build().getFullUrl(botConfig.getKefuAvatar()));
                    return vo;
                });

        return ResponseResultGenerator.success(convert);
    }
}
