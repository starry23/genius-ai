package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.entity.CurrencyConfig;
import cn.apeto.geniusai.server.service.CurrencyConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 币配置 前端控制器
 * </p>
 *
 * @author warape
 * @since 2023-06-16 05:47:30
 */
@Tag(name = "币配置相关")
@RestController
@RequestMapping("/api/currencyConfig")
public class CurrencyConfigController {

    @Autowired
    private CurrencyConfigService currencyConfigService;

    @Operation(summary = "币列表")
    @SaIgnore
    @GetMapping("/list")
    public ResponseResult<List<CurrencyConfig>> list() {
        List<CurrencyConfig> list = currencyConfigService.list();
        List<CurrencyConfig> collect = list.stream()
                .sorted(Comparator.comparing(CurrencyConfig::getRecommend).reversed().thenComparing(CurrencyConfig::getCurrencyAmount))
                .collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }

}
