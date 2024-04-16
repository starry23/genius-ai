package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.vo.AiRoleVO;
import cn.apeto.geniusai.server.entity.AiRole;
import cn.apeto.geniusai.server.service.AiRoleService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/5/30 13:50
 */
@Slf4j
@Tag(name = "ai角色相关")
@RestController
@SaIgnore
@RequestMapping("/api/ai-role")
public class AiRoleController {

    @Autowired
    private AiRoleService aiRoleService;


    @Operation(summary = "角色列表")
    @GetMapping("/roles")
    public ResponseResult<List<AiRoleVO>> roles(Integer roleType, String roleName) {
        LambdaQueryWrapper<AiRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(roleType != null, AiRole::getRoleType, roleType);
        queryWrapper.like(StrUtil.isNotBlank(roleName), AiRole::getRoleName, roleName);
        List<AiRole> list = aiRoleService.list(queryWrapper);
        List<AiRoleVO> collect = list.stream()
                .map(aiRole -> {
                    String fullUrl = "";
                    String imageUrl = aiRole.getImageUrl();
                    if (StrUtil.isNotBlank(imageUrl)) {
                        try {
                            fullUrl = OSSFactory.build().getFullUrl(imageUrl);
                        } catch (Exception e) {
                            log.error("获取图片地址失败", e);
                        }
                    }

                    return new AiRoleVO(aiRole.getId(), aiRole.getRoleName(), aiRole.getRoleDesc(), aiRole.getUpdateTime(), fullUrl);
                })
                .sorted(Comparator.comparing(AiRoleVO::getUpdateTime).reversed())
                .collect(Collectors.toList());
        return ResponseResultGenerator.success(collect);
    }
}
