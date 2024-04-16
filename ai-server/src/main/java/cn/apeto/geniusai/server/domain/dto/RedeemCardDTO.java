package cn.apeto.geniusai.server.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @author apeto
 * @create 2023/7/8 11:35 上午
 */
@Data
public class RedeemCardDTO {

    @NotBlank(message = "兑换码不能为空")
    private String code;
}
