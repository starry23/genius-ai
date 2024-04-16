package cn.apeto.geniusai.server.controller;

import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.dto.TokenAvoidSignDTO;
import cn.apeto.geniusai.server.entity.OpenApiMchConfig;
import cn.apeto.geniusai.server.entity.UserInfo;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.service.OpenApiMchConfigService;
import cn.apeto.geniusai.server.service.UserInfoService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * <p>
 * 开放api商户配置 前端控制器
 * </p>
 *
 * @author apeto
 * @since 2023-12-30 03:13:30
 */
@Slf4j
@Controller
@RequestMapping("/api/open-api")
public class OpenApiMchConfigController {

    @Autowired
    private OpenApiMchConfigService openApiMchConfigService;

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/sign-in/{mchId}")
    public String avoidSignIn(String token, @PathVariable("mchId") String mchId, HttpServletResponse response) {

        log.info("接收到的商户号为:{} 密文为:{} ", mchId, token);
        OpenApiMchConfig openApiMchConfig = openApiMchConfigService.getByMchId(mchId);

        if (openApiMchConfig == null) {
            throw new ServiceException(500, "商户不存在");
        }

        Integer menuCode = openApiMchConfig.getMenuCode();
        RSA rsa = SecureUtil.rsa(openApiMchConfig.getPrivateKey(), openApiMchConfig.getPublicKey());
        byte[] decrypt = rsa.decrypt(token, KeyType.PrivateKey);
        String result = Base64.decodeStr(Base64.encode(decrypt));
        log.info("avoidSignIn解密后的参数为:{}", result);

        TokenAvoidSignDTO avoidSignDTO = JSONUtil.toBean(result, TokenAvoidSignDTO.class);
        String accountType = avoidSignDTO.getAccountType();
        String accountNum = avoidSignDTO.getAccountNum();
        String password = avoidSignDTO.getPassword();
        Long timestamp = avoidSignDTO.getTimestamp();


        Integer userType = Constants.OpenApiLoginTypeEnum.getByType(accountType);
        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        String domain = packageWebConfig.getDomain();
        Constants.LeftMenuTypeEnum leftMenuTypeEnum = Constants.LeftMenuTypeEnum.getByEnum(menuCode);
        if (leftMenuTypeEnum == null) {
            leftMenuTypeEnum = Constants.LeftMenuTypeEnum.GPTS;
        }
        String urlSuffix = leftMenuTypeEnum.getUrlsuffix();
        String loginUrl = "redirect:" + domain + "/#login";
        String url = "redirect:" + domain + urlSuffix;


        if (StrUtil.hasBlank(accountType, accountNum, password, timestamp + "")) {
            // 校验
            log.warn("avoidSignIn传参异常");
            return loginUrl;
        }

        if (System.currentTimeMillis() - timestamp > 10 * 1000) {
            log.warn("请求超时timestamp");
            return loginUrl;
        }

        UserInfo userInfo;

        if (Constants.OpenApiLoginTypeEnum.PHONE.getName().equals(accountType)) {
            // 手机号
            userInfo = userInfoService.getByPhone(accountNum);
        } else if (Constants.OpenApiLoginTypeEnum.EMAIL.getName().equals(accountType)) {
            // 邮箱
            userInfo = userInfoService.getByEmail(accountNum);
        } else {
            return loginUrl;
        }
        if (userInfo == null) {
            userInfo = userInfoService.createUser(userType, accountNum, password);
        }

        Long userId = userInfo.getId();
        String tokenValue = StpUtil.getTokenValueByLoginId(userId);
        if (StrUtil.isBlank(tokenValue)) {
            // 重新登录
            StpUtil.login(userId);
            tokenValue = StpUtil.getTokenValue();
        }
        StpLogic stpLogic = StpUtil.getStpLogic();
        SaTokenConfig configOrGlobal = stpLogic.getConfigOrGlobal();
        Cookie cookie1 = new Cookie("tokenName", configOrGlobal.getTokenName());
        cookie1.setPath("/");
        Cookie cookie2 = new Cookie("tokenValue", tokenValue);
        cookie2.setPath("/");
        response.addCookie(cookie1);
        response.addCookie(cookie2);

        return url;
    }

}
