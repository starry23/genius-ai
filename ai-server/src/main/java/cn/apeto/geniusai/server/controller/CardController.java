package cn.apeto.geniusai.server.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.dto.RedeemCardDTO;
import cn.apeto.geniusai.server.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hsg
 * @since 2023-07-04 16:28:17
 */
@Validated
@Tag(name = "卡密兑换")
@RestController
@RequestMapping("/api/card")
public class CardController {

    @Autowired
    private CardService cardService;

    /**
     * 4. C端兑换(输入参数: 兑换码)
     */
    @Operation(summary = "C端兑换卡密")
    @PostMapping("/redeem")
    public ResponseResult<?> numberRedeem(@RequestBody RedeemCardDTO redeemCardDTO) {
        cardService.redeemCard(StpUtil.getLoginIdAsLong(),redeemCardDTO.getCode());
        return ResponseResultGenerator.success();
    }


}
