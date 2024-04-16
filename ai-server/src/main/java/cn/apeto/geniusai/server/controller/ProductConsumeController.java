package cn.apeto.geniusai.server.controller;


import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.entity.ProductConsumeConfig;
import cn.apeto.geniusai.server.service.ProductConsumeConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author apeto
 * @create 2023/6/19 16:50
 */
@Tag(name = "产品消耗类型配置相关")
@RestController
@RequestMapping("/api/productConsumedTypeConfig")
public class ProductConsumeController {

    @Autowired
    private ProductConsumeConfigService productConsumeConfigService;

    @GetMapping("/infos")
    public ResponseResult<Map<Integer,Long>> info() {
        List<ProductConsumeConfig> list = productConsumeConfigService.selectAllByStatus(Constants.ProductConsumeStatusEnum.UP.getStatus());

        Map<Integer, Long> map = list.stream()
                .collect(Collectors.toMap(ProductConsumeConfig::getProductType, ProductConsumeConfig::getConsumeCurrency));

        return ResponseResultGenerator.success(map);
    }

}
