package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.vo.MemberCardVo;
import cn.apeto.geniusai.server.domain.vo.MemberUserInfo;
import cn.apeto.geniusai.server.domain.vo.RightConfigVo;
import cn.apeto.geniusai.server.entity.ExchangeCardDetail;
import cn.apeto.geniusai.server.entity.MemberCard;
import cn.apeto.geniusai.server.entity.MemberRights;
import cn.apeto.geniusai.server.entity.UserInfo;
import cn.apeto.geniusai.server.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * 会员卡 前端控制器
 * </p>
 *
 * @author warape
 * @since 2023-04-08 06:49:07
 */
@Tag(name = "会员卡相关")
@RestController
@RequestMapping("/api/memberCard")
public class MemberCardController {

    @Autowired
    private MemberCardService memberCardService;
    @Autowired
    private MemberRightsService memberRightsService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;


    @Operation(summary = "会员卡列表")
    @GetMapping("/members")
    public ResponseResult<List<MemberCardVo>> members() {
        List<MemberCard> memberCards = memberCardService.selectByViewTypeAndType();
        List<MemberCardVo> result = memberCards.stream().map(memberCard -> {

            List<MemberRights> memberRights = memberRightsService.getByMemberCode(memberCard.getCardCode());
            if (CollUtil.isNotEmpty(memberRights)) {
                MemberCardVo vo = new MemberCardVo();
                vo.setCardName(memberCard.getCardName());
                vo.setAmount(memberCard.getAmount());
                vo.setCardDay(memberCard.getCardDay());
                vo.setLineAmount(memberCard.getLineAmount());
                vo.setRecommend(memberCard.getRecommend());
                vo.setMemberId(memberCard.getId());
                vo.setRightList(memberRights.stream().map(mr -> {
                    RightConfigVo rightConfigVo = new RightConfigVo();
                    rightConfigVo.setRightsDesc(mr.getRightsDesc());
                    return rightConfigVo;
                }).collect(Collectors.toList()));
                return vo;
            } else {
                return null;
            }

        }).filter(Objects::nonNull).sorted(
                Comparator.comparing(MemberCardVo::getRecommend).reversed().thenComparing(MemberCardVo::getAmount)
        ).collect(Collectors.toList());
        return ResponseResultGenerator.success(result);
    }


    @Operation(summary = "用户会员信息")
    @GetMapping("/memberUserInfo")
    public ResponseResult<MemberUserInfo> memberUserInfo() {

        Long userId = StpUtil.getLoginIdAsLong();

        UserInfo userInfo = userInfoService.getById(userId);
        MemberUserInfo memberUserInfo = new MemberUserInfo();
        Date memberValidTime = userInfo.getMemberValidTime();
        Date now = new Date();
        if (memberValidTime != null && memberValidTime.after(now)) {
            memberUserInfo.setDeadline((DateUtil.betweenDay(memberValidTime, now, true)));
        }

        List<ExchangeCardDetail> exchangeCardDetails = exchangeCardDetailService.selectByUserId(userId);
        exchangeCardDetails.stream().collect(Collectors.groupingBy(ExchangeCardDetail::getMemberCardId, Collectors.counting())).forEach((memberCardId, value) -> {
            MemberCard memberCard = memberCardService.getById(memberCardId);
            if (memberCard != null) {
                List<MemberRights> memberRights = memberRightsService.getByMemberCode(memberCard.getCardCode());
                if (CollUtil.isNotEmpty(memberRights)) {
                    memberUserInfo.setRightConfigVoList(memberRights.stream().map(memberRight -> {
                        RightConfigVo rightConfigVo = new RightConfigVo();
                        rightConfigVo.setRightsDesc(memberRight.getRightsDesc() + " * " + value);
                        return rightConfigVo;
                    }).collect(Collectors.toList()));
                }
            }

        });
        return ResponseResultGenerator.success(memberUserInfo);
    }

    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
