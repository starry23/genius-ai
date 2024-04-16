package cn.apeto.geniusai.server.controller;

import cn.apeto.geniusai.server.config.properties.WxMpProperties;
import cn.apeto.geniusai.server.domain.Constants.ResponseEnum;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.SystemConstants.RedisKeyEnum;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.vo.AuthorizationVO;
import cn.apeto.geniusai.server.domain.vo.TokenInfoVo;
import cn.apeto.geniusai.server.entity.UserInfo;
import cn.apeto.geniusai.server.service.UserInfoService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

/**
 * @author apeto
 * @create 2023/3/28 11:23
 */
@Slf4j
@Tag(name = "微信相关")
@Controller
@RequestMapping("/api/wechat")
public class WechatController {

    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private WxMpProperties wxMpProperties;


    @Operation(summary = "公众号登录")
    @ResponseBody
    @GetMapping("/authorizationUrl")
    public ResponseResult<AuthorizationVO> authorizationUrl(String inviteCode) {
        String sceneStr = IdUtil.fastSimpleUUID();

        try {
            if (StrUtil.isNotBlank(inviteCode)) {
                sceneStr = sceneStr + "@" + inviteCode;
            }
            WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService()
                    .qrCodeCreateTmpTicket(sceneStr, Integer.parseInt(TimeUnit.MINUTES.toSeconds(10) + ""));
            AuthorizationVO vo = new AuthorizationVO();

            String ticket = wxMpQrCodeTicket.getTicket();
            vo.setSceneStr(ticket);
            vo.setUrl(wxMpQrCodeTicket.getUrl());
            StringRedisUtils.setForTimeMIN(RedisKeyEnum.WECHAT_QR_LOGIN_CODE.getKey(ticket), Boolean.FALSE.toString(), 10);

            return ResponseResultGenerator.success(vo);
        } catch (Exception e) {
            log.error("getAuthorizationUrl 生成二维码失败", e);

        }
        return ResponseResultGenerator.error();
    }

    @Operation(summary = "查询登录状态返回token")
    @ResponseBody
    @GetMapping("/loginState")
    public ResponseResult<String> loginState(@RequestParam("sceneStr") String sceneStr) {
        String uid = StringRedisUtils.get(RedisKeyEnum.WECHAT_QR_LOGIN_CODE.getKey(sceneStr));
        if (StrUtil.isBlank(uid)) {
            return ResponseResultGenerator.result(ResponseEnum.QR_INVALID);
        }

        if (uid.equals(Boolean.FALSE.toString())) {
            return ResponseResultGenerator.result(ResponseEnum.WAITING_FOLLOW);
        }

        UserInfo userInfo = userInfoService.getById(Long.parseLong(uid));
        String token = StpUtil.createLoginSession(userInfo.getId());
        return ResponseResultGenerator.success(token);
    }

    @Operation(summary = "获取授权URL")
    @ResponseBody
    @GetMapping("/getAuthUrl")
    public ResponseResult<AuthorizationVO> getAuthUrl(String inviteCode) {
        AuthorizationVO vo = new AuthorizationVO();
        String redirectUri = wxMpProperties.getRedirectUri();
        String authorizationUrl = wxMpService.getOAuth2Service()
                .buildAuthorizationUrl(redirectUri + "/api/wechat/wechatLogin?inviteCode=" + inviteCode, WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
        vo.setUrl(authorizationUrl);
        return ResponseResultGenerator.success(vo);
    }

    @Operation(summary = "微信注册登录")
    @GetMapping("/wechatLogin")
    public String wechatLogin(String code, String inviteCode, HttpServletResponse response) throws WxErrorException {
        log.info("wechatLogin code:{} inviteCode:{}", code, inviteCode);
        WxOAuth2AccessToken wxOAuth2AccessToken = wxMpService.getOAuth2Service().getAccessToken(code);

        WxOAuth2UserInfo wxMpUser = wxMpService.getOAuth2Service().getUserInfo(wxOAuth2AccessToken, null);
        Long userId = userInfoService.getOrCreateWechatUser(wxMpUser);
        StpUtil.login(userId);
        SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
        TokenInfoVo tokenInfo = new TokenInfoVo();
        tokenInfo.setTokenName(saTokenInfo.getTokenName());
        tokenInfo.setTokenValue(saTokenInfo.getTokenValue());

        StpLogic stpLogic = StpUtil.getStpLogic();
        SaTokenConfig configOrGlobal = stpLogic.getConfigOrGlobal();
        Cookie cookie1 = new Cookie("tokenName", configOrGlobal.getTokenName());
        cookie1.setPath("/");
        cookie1.setMaxAge(Integer.parseInt(configOrGlobal.getTimeout() + ""));
        Cookie cookie2 = new Cookie("tokenValue", saTokenInfo.getTokenValue());
        cookie2.setPath("/");
        cookie2.setMaxAge(Integer.parseInt(configOrGlobal.getTimeout() + ""));
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        String domain = packageWebConfig.getDomain();
        return "redirect:" + domain + "/#/ai/aiAssistant";
    }



}
