package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.entity.TopBarConfig;
import cn.apeto.geniusai.server.service.TopBarConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 上拦配置表 前端控制器
 * </p>
 *
 * @author warape
 * @since 2023-05-05 03:57:39
 */
@Tag(name = "上拦配置")
@RestController
@RequestMapping("/api/topBarConfig")
public class TopBarConfigController {

  @Autowired
  private TopBarConfigService topBarConfigService;

  @SaIgnore
  @Operation(summary = "上拦列表")
  @GetMapping("/list")
  public ResponseResult<List<TopBarConfig>> list () {
    List<TopBarConfig> list = topBarConfigService.list();
    return ResponseResultGenerator.success(list);
  }

}
