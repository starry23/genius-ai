package cn.apeto.geniusai.server.controller.admin;

import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.domain.Constants.LoginTypeEnum;
import cn.apeto.geniusai.server.domain.dto.*;
import cn.apeto.geniusai.server.entity.*;
import cn.apeto.geniusai.server.service.*;
import cn.apeto.geniusai.server.service.oss.CloudStorageService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.MathUtils;
import cn.apeto.geniusai.server.utils.StpManagerUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/4/29 11:39 上午
 */
@Tag(name = "后台管理系统(待拆分)")
@Slf4j
@RestController
@RequestMapping("/api/manager")
@SaCheckLogin(type = StpManagerUtil.TYPE)
public class ManagerController {

    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private ChatDetailLogService chatDetailLogService;
    @Autowired
    private MemberCardService memberCardService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private WechatUserInfoService wechatUserInfoService;
    @Autowired
    private MemberRightsService memberRightsService;
    @Autowired
    private AdvertiseConfigService advertiseConfigService;
    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;
    @Autowired
    private TopBarConfigService topBarConfigService;
    @Autowired
    private AiRoleService aiRoleService;
    @Autowired
    private ProductConsumeConfigService productConsumeConfigService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CurrencyConfigService currencyConfigService;
    @Autowired
    private FeedbackService feedbackService;

    @Value("${ai-manager-config.name}")
    private String name;
    @Value("${ai-manager-config.password}")
    private String password;

    @SaIgnore
    @GetMapping("/login")
    @Operation(summary = "登录")
    public ResponseResult<?> login(@RequestParam("name") String name, @RequestParam("password") String password) {
        if (StrUtil.equals(this.name, name) && StrUtil.equals(this.password, password)) {
            StpManagerUtil.login(name, TimeUnit.HOURS.toSeconds(3));
            return ResponseResultGenerator.success();
        }
        return ResponseResultGenerator.result(CommonRespCode.PASSWORD_ERROR);
    }


    @GetMapping("/logout")
    @Operation(summary = "登出")
    public ResponseResult<?> logout() {
        StpManagerUtil.logout(name);

        return ResponseResultGenerator.success();
    }

    @GetMapping("/userInfoList")
    @Operation(summary = "用户列表")
    public ResponseResult<Page<UserInfo>> userInfoList(UserInfoListDTO userInfoListDTO) {

        String email = userInfoListDTO.getEmail();
        String phone = userInfoListDTO.getPhone();
        Long userId = userInfoListDTO.getUserId();
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(email), UserInfo::getEmail, email);
        queryWrapper.eq(StrUtil.isNotBlank(phone), UserInfo::getPhone, phone);
        queryWrapper.eq(userId != null, UserInfo::getId, userId);
        queryWrapper.orderByDesc(BaseEntity::getId);
        Page<UserInfo> page = userInfoService.page(new Page<>(userInfoListDTO.getCurrent(), userInfoListDTO.getSize()), queryWrapper);
        page.getRecords().forEach(userInfo -> {
            WechatUserInfo wechatUserInfo = wechatUserInfoService.getByUserId(userInfo.getId());
            if (wechatUserInfo != null) {
                userInfo.setHeadImgUrl(wechatUserInfo.getHeadImgUrl());
                userInfo.setNickName(wechatUserInfo.getNickName());
                userInfo.setOpenId(wechatUserInfo.getOpenId());
            }
        });
        return ResponseResultGenerator.success(page);
    }

    @Operation(summary = "手动修改拉新数量(返佣等级)")
    @PostMapping("/userCommissionUpdate")
    public ResponseResult<?> userCommissionUpdate(@RequestBody UserCommissionUpdateDTO userCommissionUpdateDTO) {

        UserInfo userInfoServiceById = userInfoService.getById(userCommissionUpdateDTO.getUserId());
        userInfoServiceById.setInviteCount(userCommissionUpdateDTO.getNum());
        userInfoService.updateById(userInfoServiceById);
        return ResponseResultGenerator.success();
    }


    @PostMapping("/depositMember")
    @Operation(summary = "手动充值会员卡")
    public ResponseResult<?> depositMember(@RequestBody DepositMemberDTO depositMemberDTO) {
        exchangeCardDetailService.exchange(depositMemberDTO.getUserId(), memberCardService.getByCardCode(depositMemberDTO.getMemberCode()), "管理员", true);
        return ResponseResultGenerator.success();
    }

    @PostMapping("/depositCurrency")
    @Operation(summary = "手动充值")
    public ResponseResult<?> depositCurrency(@RequestBody @Validated DepositCurrencyDTO depositCurrencyDTO) {
        Long userId = depositCurrencyDTO.getUserId();
        Integer count = depositCurrencyDTO.getCount();
        // 增加账户余额
        String reqId = IdUtil.fastSimpleUUID();
        Constants.LogDescriptionTypeEnum recharge = Constants.LogDescriptionTypeEnum.MANAGER_DEPOSIT;
        Constants.DirectionTypeEnum directionTypeEnum = Constants.DirectionTypeEnum.IN;
        Integer accountType = depositCurrencyDTO.getAccountType();
        accountService.commonUpdateAccount(userId, accountType, BigDecimal.valueOf(count), reqId, reqId, recharge, directionTypeEnum, "管理员");
        return ResponseResultGenerator.success();
    }


    @Operation(summary = "返佣手动提现")
    @PostMapping("/commissionWithdraw")
    public ResponseResult<?> commissionWithdraw(@RequestBody CommissionWithdrawDTO commissionWithdrawDTO) {

        Long userId = commissionWithdrawDTO.getUserId();
        BigDecimal amount = commissionWithdrawDTO.getAmount();
        Integer accountType = Constants.AccountTypeEnum.RMB_BALANCE.getType();
        Account account = accountService.getByUserIdAndType(userId, accountType);
        if (account.getAccountBalance().compareTo(commissionWithdrawDTO.getAmount()) < 0) {
            return ResponseResultGenerator.result(Constants.ResponseEnum.ACCOUNT_BALANCE_NOT_ENOUGH);
        }
        // 返佣手动提现
        String reqId = IdUtil.fastSimpleUUID();
        Constants.LogDescriptionTypeEnum recharge = Constants.LogDescriptionTypeEnum.REBATE_WITHDRAW;
        Constants.DirectionTypeEnum directionTypeEnum = Constants.DirectionTypeEnum.OUT;
        accountService.commonUpdateAccount(userId, accountType, amount, reqId, reqId, recharge, directionTypeEnum, "管理员");
        return ResponseResultGenerator.success();
    }

    @GetMapping("/memberExchange")
    @Operation(summary = "会员兑换列表")
    public ResponseResult<Page<ExchangeCardDetail>> memberExchange(MemberExchangeDTO memberExchangeDTO) {
        LambdaQueryWrapper<ExchangeCardDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(memberExchangeDTO.getUserId() != null, ExchangeCardDetail::getUserId, memberExchangeDTO.getUserId());
        Page<ExchangeCardDetail> page = exchangeCardDetailService.page(new Page<>(memberExchangeDTO.getCurrent(), memberExchangeDTO.getSize()), queryWrapper);
        return ResponseResultGenerator.success(page);
    }


    @GetMapping("/orders")
    @Operation(summary = "支付订单列表")
    public ResponseResult<Page<PaymentInfo>> orders(ManagerOrdersDTO managerOrdersDto) {
        Long userId = managerOrdersDto.getUserId();
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, PaymentInfo::getUserId, userId);
        queryWrapper.orderByDesc(BaseEntity::getId);
        Page<PaymentInfo> paymentInfoPage = paymentInfoService.page(new Page<>(managerOrdersDto.getCurrent(), managerOrdersDto.getSize()), queryWrapper);
        return ResponseResultGenerator.success(paymentInfoPage);
    }

    @DeleteMapping("/delete/useless/orders")
    @Operation(summary = "一键删除无用订单")
    public ResponseResult<String> deleteUselessOrders() {
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getPayState, "3").or().eq(PaymentInfo::getPayState, "2");
        paymentInfoService.deleteByConditions(queryWrapper);
        return ResponseResultGenerator.success("OK");
    }


    @GetMapping("/qLogs")
    @Operation(summary = "提问日志列表", description = "条件可根据reqId userId进行搜素")
    public ResponseResult<Page<ChatDetailLog>> qLogs(QLogsDTO qLogsDTO) {
        LambdaQueryWrapper<ChatDetailLog> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = qLogsDTO.getUserId();
        String reqId = qLogsDTO.getReqId();
        queryWrapper.eq(userId != null, ChatDetailLog::getUserId, userId);
        queryWrapper.eq(StrUtil.isNotBlank(reqId), ChatDetailLog::getRequestId, reqId);
        queryWrapper.orderByDesc(BaseEntity::getId);
        queryWrapper.in(BaseEntity::getYn, 1, 0);
        Page<ChatDetailLog> page = chatDetailLogService.page(new Page<>(qLogsDTO.getCurrent(), qLogsDTO.getSize()), queryWrapper);

        return ResponseResultGenerator.success(page);
    }

    @GetMapping("/memberList")
    @Operation(summary = "会员卡列表")
    public ResponseResult<List<MemberCard>> memberList() {
        List<MemberCard> list = memberCardService.list();
        return ResponseResultGenerator.success(list);
    }

    @GetMapping("/memberRightsList")
    @Operation(summary = "会员权益列表")
    public ResponseResult<List<MemberRights>> memberRightsList() {
        List<MemberRights> list = memberRightsService.list();
        return ResponseResultGenerator.success(list);
    }

    @DeleteMapping("/memberRights/{id}")
    @Operation(summary = "删除权益")
    public ResponseResult<?> memberRightsDel(@PathVariable Long id) {
        memberRightsService.removeById(id);
        return ResponseResultGenerator.success();
    }

    @GetMapping("/currencyList")
    @Operation(summary = "代币商品列表")
    public ResponseResult<List<CurrencyConfig>> currencyList() {
        List<CurrencyConfig> list = currencyConfigService.list();
        return ResponseResultGenerator.success(list);
    }

    @DeleteMapping("/currency/{id}")
    @Operation(summary = "删除代币商品")
    public ResponseResult<?> currencyDel(@PathVariable Long id) {
        currencyConfigService.removeById(id);
        return ResponseResultGenerator.success();
    }

    @PostMapping("/currencySaveOrUpdate")
    @Operation(summary = "修改或增加代币商品")
    public ResponseResult<?> currencySaveOrUpdate(@RequestBody CurrencyConfig currencyConfig) {
        return currencyConfigService.saveOrUpdate(currencyConfig) ? ResponseResultGenerator.success() : ResponseResultGenerator.error();
    }


    @PostMapping("/memberConfigSaveOrUpdate")
    @Operation(summary = "修改或增加会员卡")
    public ResponseResult<?> memberConfigSaveOrUpdate(@RequestBody MemberCard memberCard) {
        String cardCode = memberCard.getCardCode();
        MemberCard byCardCode = memberCardService.getByCardCode(cardCode);
        if (memberCard.getId() == null) {
            if (byCardCode != null) {
                return ResponseResultGenerator.error("该会员卡已存在");
            }
        } else {
            if (byCardCode != null && !byCardCode.getId().equals(memberCard.getId())) {
                return ResponseResultGenerator.error("该会员卡已存在");
            }
        }
        return memberCardService.saveOrUpdate(memberCard) ? ResponseResultGenerator.success() : ResponseResultGenerator.error();
    }

    @DeleteMapping("/memberConfig/{id}")
    @Operation(summary = "删除会员")
    public ResponseResult<?> memberConfigDel(@PathVariable Long id) {
        return memberCardService.removeById(id) ? ResponseResultGenerator.success() : ResponseResultGenerator.error();
    }

    @PostMapping("/memberRightsConfigSaveOrUpdate")
    @Operation(summary = "修改或增加会员权益")
    public ResponseResult<?> memberRightsConfigSaveOrUpdate(@RequestBody MemberRights memberRights) {

        String memberCode = memberRights.getMemberCode();
        List<MemberRights> memberRightsList = memberRightsService.getByMemberCode(memberCode);
        Integer rightsType = memberRights.getRightsType();
        if (memberRightsList.stream().anyMatch(rs -> rs.getRightsType().equals(rightsType) && !rs.getId().equals(memberRights.getId()))) {
            return ResponseResultGenerator.error("该会员卡已存在该权益类型 请勿重复创建~");
        }
        String rightsDesc = memberRights.getRightsDesc();
        String realDesc = rightsDesc;
        if (rightsType.equals(Constants.MemberRightsTypeEnum.CURRENCY.getType())) {
            SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
            realDesc = StrUtil.format(rightsDesc, memberRights.getCount() + "个" + packageWebConfig.getCurrencyName());
        }

        if (rightsType.equals(Constants.MemberRightsTypeEnum.BUY_PACKAGE.getType())
                || rightsType.equals(Constants.MemberRightsTypeEnum.CONSUME_DISCOUNT.getType())
        ) {
            realDesc = StrUtil.format(rightsDesc, MathUtils.discountHalfUp2(memberRights.getDiscount()) + "折");
        }
        memberRights.setRightsDesc(realDesc);
        memberRightsService.saveOrUpdate(memberRights);
        return ResponseResultGenerator.success();
    }

    @GetMapping("/productConsumeConfigs")
    @Operation(summary = "代币消费配置列表")
    public ResponseResult<List<ProductConsumeConfig>> productConsumeConfigs() {
        return ResponseResultGenerator.success(productConsumeConfigService.list());
    }

    @PostMapping("/productConsumeConfigSaveOrUpdate")
    @Operation(summary = "修改或增加商品消费配置")
    public ResponseResult<?> productConsumeConfigSaveOrUpdate(@RequestBody ProductConsumeConfig productConsumeConfig) {

        ProductConsumeConfig byType = productConsumeConfigService.getByType(productConsumeConfig.getProductType());
        if (productConsumeConfig.getId() != null) {
            if (byType != null && !byType.getId().equals(productConsumeConfig.getId())) {
                return ResponseResultGenerator.error("该商品类型已存在");
            }
        } else {
            if (byType != null) {
                return ResponseResultGenerator.error("该商品类型已存在");
            }
        }
        productConsumeConfigService.saveOrUpdate(productConsumeConfig);
        return ResponseResultGenerator.success();
    }

    @DeleteMapping("/productConsumeConfig/{id}")
    @Operation(summary = "删除商品消费配置")
    public ResponseResult<?> productConsumeConfigDel(@PathVariable Long id) {
        productConsumeConfigService.removeById(id);
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "商品消费上下线")
    @PostMapping("/productConsumeConfig/onOff")
    public ResponseResult<?> onOff(@RequestBody ProductOnOffDTO productOnOffDTO) {
        ProductConsumeConfig entity = new ProductConsumeConfig();
        entity.setStatus(productOnOffDTO.getStatus());
        entity.setId(productOnOffDTO.getId());
        productConsumeConfigService.updateById(entity);
        return ResponseResultGenerator.success();
    }

    @DeleteMapping("/advertiseConfig/{id}")
    @Operation(summary = "删除活动")
    public ResponseResult<?> advertiseConfig(@PathVariable Long id) {
        return advertiseConfigService.removeById(id) ? ResponseResultGenerator.success() : ResponseResultGenerator.error();
    }

    @PostMapping("/advertiseConfigSaveOrUpdate")
    @Operation(summary = "修改或增加广告配置")
    public ResponseResult<?> advertiseConfigSaveOrUpdate(@RequestBody AdvertiseConfig advertiseConfig) {

        advertiseConfigService.saveOrUpdate(advertiseConfig);
        return ResponseResultGenerator.success();
    }


    @Operation(summary = "上拦列表")
    @GetMapping("/topBarConfig")
    public ResponseResult<List<TopBarConfig>> topBarConfig() {
        List<TopBarConfig> list = topBarConfigService.list();
        return ResponseResultGenerator.success(list);
    }


    @Operation(summary = "上拦按钮配置")
    @PostMapping("/topBarConfigSaveOrUpdate")
    public ResponseResult<?> topBarConfigSaveOrUpdate(@RequestBody TopBarConfig topBarConfig) {
        topBarConfigService.saveOrUpdate(topBarConfig);
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "删除上拦按钮配置")
    @DeleteMapping("/topBarConfig/{id}")
    public ResponseResult<?> topBarConfig(@PathVariable Long id) {
        return topBarConfigService.removeById(id) ? ResponseResultGenerator.success() : ResponseResultGenerator.error();
    }


    @Operation(summary = "AI角色列表(分页)")
    @GetMapping("/aiRoles")
    public ResponseResult<IPage<AiRole>> aiRoles(AiRoleDTO aiRoleDTO) {
        LambdaQueryWrapper<AiRole> queryWrapper = new LambdaQueryWrapper<>();
        Integer roleType = aiRoleDTO.getRoleType();
        String roleName = aiRoleDTO.getRoleName();
        queryWrapper.eq(roleType != null, AiRole::getRoleType, roleType);
        queryWrapper.like(StrUtil.isNotBlank(roleName), AiRole::getRoleName, roleName);
        queryWrapper.orderByDesc(AiRole::getUpdateBy);
        Page<AiRole> page = aiRoleService.page(new Page<>(aiRoleDTO.getCurrent(), aiRoleDTO.getSize()), queryWrapper);
        IPage<AiRole> convert = page.convert(aiRole -> {
            try {
                CloudStorageService build = OSSFactory.build();
                aiRole.setFullUrl(build.getFullUrl(aiRole.getImageUrl()));
            } catch (Exception e) {
                log.error("获取图片地址失败", e);
            }

            return aiRole;
        });
        return ResponseResultGenerator.success(convert);
    }


    @Operation(summary = "AI角色修改及添加")
    @PostMapping("/saveOrUpdateAiRole")
    public ResponseResult<?> saveOrUpdateAiRole(@RequestBody AiRole aiRole) {
        aiRoleService.saveOrUpdate(aiRole);
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "根据ID删除AI角色")
    @DeleteMapping("/aiRole/{id}")
    public ResponseResult<?> aiRole(@PathVariable Long id) {
        return aiRoleService.removeById(id) ? ResponseResultGenerator.success() : ResponseResultGenerator.error();
    }

    @Operation(summary = "登录类型列表")
    @GetMapping("/loginType")
    public ResponseResult<List<Dict<Integer>>> loginType() {
        List<Dict<Integer>> result = Arrays.stream(LoginTypeEnum.values())
                .map(loginTypeEnum -> new Dict<>(loginTypeEnum.getType(), loginTypeEnum.getDesc())).collect(Collectors.toList());
        return ResponseResultGenerator.success(result);
    }


//
//  @Operation(summary = "查询余额")
//  @PostMapping("/chat")
//  public ResponseResult<?> saveOrUpdateChatConfig (String key) {
//    openAiStreamClient.subscription()
//    StringRedisUtils.set(RedisKeyEnum.WEB_CONFIG.getKey(), JSONUtil.toJsonStr(chatConfig));
//    return ResponseResultGenerator.success();
//  }

    @Operation(summary = "GPT模型列表")
    @GetMapping("/chatModel")
    public ResponseResult<List<Dict<String>>> chatModel() {
        List<Dict<String>> collect = Arrays.stream(ChatCompletion.Model.values()).map(model -> {
            Dict<String> dict = new Dict<>();
            dict.setKey(model.name());
            dict.setValue(model.getName());
            return dict;
        }).collect(Collectors.toList());

        return ResponseResultGenerator.success(collect);
    }


    @Operation(summary = "反馈信息查询")
    @GetMapping("/getFeedback")
    public ResponseResult<Page<Feedback>> getFeedback() {
        Page<Feedback> page = feedbackService.page(new Page<>(1, 10), new LambdaQueryWrapper<Feedback>().orderByDesc(BaseEntity::getId));
        return ResponseResultGenerator.success(page);
    }

}
