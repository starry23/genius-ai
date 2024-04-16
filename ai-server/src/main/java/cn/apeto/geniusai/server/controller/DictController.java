package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.domain.Constants.DirectionTypeEnum;
import cn.apeto.geniusai.server.domain.Constants.LogDescriptionTypeEnum;
import cn.apeto.geniusai.server.domain.Constants.MemberRightsTypeEnum;
import cn.apeto.geniusai.server.domain.Constants.ProductTypeEnum;
import cn.apeto.geniusai.server.entity.ProductConsumeConfig;
import cn.apeto.geniusai.server.service.ProductConsumeConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/6/13 18:56
 */
@Tag(name = "字典相关")
@SaIgnore
@Slf4j
@RestController
@RequestMapping("/api/dict")
public class DictController {

    @Autowired
    private ProductConsumeConfigService productConsumeConfigService;

    @Operation(summary = "权益类型")
    @GetMapping("/rightsType")
    public ResponseResult<List<Dict<Integer>>> memberCardType() {
        List<Dict<Integer>> collect = Arrays.stream(MemberRightsTypeEnum.values()).map(memberRightsTypeEnum -> {
            Dict<Integer> dict = new Dict<>();
            dict.setKey(memberRightsTypeEnum.getType());
            dict.setValue(memberRightsTypeEnum.getDesc());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }

    @Operation(summary = "产品类型服务端使用")
    @GetMapping("/productType")
    public ResponseResult<List<Dict<Integer>>> productType() {
        List<Dict<Integer>> collect = Arrays.stream(ProductTypeEnum.values()).map(productTypeEnum -> {
            Dict<Integer> dict = new Dict<>();
            dict.setKey(productTypeEnum.getType());
            dict.setValue(productTypeEnum.getName());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }

    @Operation(summary = "日志操作类型")
    @GetMapping("/logDescriptionType")
    public ResponseResult<List<Dict<Integer>>> logDescriptionType() {
        List<Dict<Integer>> collect = Arrays.stream(LogDescriptionTypeEnum.values()).map(logTypeEnum -> {
            Dict<Integer> dict = new Dict<>();
            dict.setKey(logTypeEnum.getType());
            dict.setValue(logTypeEnum.getDesc());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }

    @Operation(summary = "方向类型")
    @GetMapping("/directionType")
    public ResponseResult<List<Dict<Integer>>> directionType() {
        List<Dict<Integer>> collect = Arrays.stream(DirectionTypeEnum.values()).map(descriptionTypeEnum -> {
            Dict<Integer> dict = new Dict<>();
            dict.setKey(descriptionTypeEnum.getType());
            dict.setValue(descriptionTypeEnum.getDesc());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }

    @Operation(summary = "产品消耗类型")
    @GetMapping("/productConsumedType")
    public ResponseResult<List<Dict<Integer>>> productConsumedType() {

        List<ProductConsumeConfig> list = productConsumeConfigService.selectAllByStatus(Constants.ProductConsumeStatusEnum.UP.getStatus());
        List<Dict<Integer>> collect = list.stream().map(productConsumeConfig -> {
            Integer productType = productConsumeConfig.getProductType();
            ProductTypeEnum productTypeEnum = ProductTypeEnum.getByEnum(productType);
            Dict<Integer> dict = new Dict<>();
            dict.setKey(productTypeEnum.getType());
            dict.setValue(productTypeEnum.getName());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }

    @Operation(summary = "左侧菜单")
    @GetMapping("/leftMenuType")
    public ResponseResult<List<Dict<Integer>>> leftMenuType() {
        List<Dict<Integer>> collect = Arrays.stream(Constants.LeftMenuTypeEnum.values()).map(descriptionTypeEnum -> {
            Dict<Integer> dict = new Dict<>();
            dict.setKey(descriptionTypeEnum.getType());
            dict.setValue(descriptionTypeEnum.getDesc());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }

    @Operation(summary = "mj文件操作类型")
    @GetMapping("/fileAction")
    public ResponseResult<List<Dict<String>>> fileAction() {
        List<Dict<String>> collect = Arrays.stream(MjConstants.ActionEnum.values()).map(descriptionTypeEnum -> {
            Dict<String> dict = new Dict<>();
            dict.setKey(descriptionTypeEnum.name());
            dict.setValue(descriptionTypeEnum.name());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }

    @Operation(summary = "mj文件状态")
    @GetMapping("/fileStatus")
    public ResponseResult<List<Dict<String>>> fileStatus() {
        List<Dict<String>> collect = Arrays.stream(MjConstants.TaskStatus.values()).map(descriptionTypeEnum -> {
            Dict<String> dict = new Dict<>();
            dict.setKey(descriptionTypeEnum.name());
            dict.setValue(descriptionTypeEnum.name());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }


    @Operation(summary = "角色类型")
    @GetMapping("/roleType")
    public ResponseResult<List<Dict<Integer>>> roleType() {
        List<Dict<Integer>> collect = Arrays.stream(Constants.RoleTypeEnum.values()).map(roleTypeEnum -> {
            Dict<Integer> dict = new Dict<>();
            dict.setKey(roleTypeEnum.getType());
            dict.setValue(roleTypeEnum.getDesc());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }

    @Operation(summary = "账户类型")
    @GetMapping("/accountType")
    public ResponseResult<List<Dict<Integer>>> accountType() {
        List<Dict<Integer>> collect = Arrays.stream(Constants.AccountTypeEnum.values()).map(roleTypeEnum -> {
            Dict<Integer> dict = new Dict<>();
            dict.setKey(roleTypeEnum.getType());
            dict.setValue(roleTypeEnum.getDesc());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }

    @Operation(summary = "wxbot产品类型")
    @GetMapping("/botProductType")
    public ResponseResult<List<Dict<Integer>>> botProductType() {

        List<Dict<Integer>> collect = Arrays.stream(Constants.WxBotProductTypeEnum.values()).map(roleTypeEnum -> {
            Dict<Integer> dict = new Dict<>();
            dict.setKey(roleTypeEnum.getType().getType());
            dict.setValue(roleTypeEnum.getType().getName());
            return dict;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }
}
