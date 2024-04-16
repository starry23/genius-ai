package cn.apeto.geniusai.server.controller.knowledge;

import cn.apeto.geniusai.server.annotations.DistributedLock;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.domain.dto.CreateItemDTO;
import cn.apeto.geniusai.server.domain.dto.MenusDetail;
import cn.apeto.geniusai.server.domain.dto.MenusSettingDTO;
import cn.apeto.geniusai.server.domain.dto.WelcomeSettingDTO;
import cn.apeto.geniusai.server.domain.vo.ItemPartitionVo;
import cn.apeto.geniusai.server.domain.vo.ItemResourceVO;
import cn.apeto.geniusai.server.domain.vo.ItemsVO;
import cn.apeto.geniusai.server.domain.vo.WelcomeAndMenusVO;
import cn.apeto.geniusai.server.entity.*;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.handler.FileHandler;
import cn.apeto.geniusai.server.service.*;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author apeto
 * @create 2023/7/31 14:23
 */
@Tag(name = "知识库相关")
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private ItemPartitionService itemPartitionService;
    @Autowired
    private FileHandler fileHandler;
    @Autowired
    private KnowledgeItemService knowledgeItemService;
    @Autowired
    private KnowledgeChatBindingService knowledgeChatBindingService;
    @Autowired
    private ItemResourceService itemResourceService;
    @Autowired
    private ShareItemService shareItemService;

    @Operation(summary = "项目列表")
    @GetMapping("/items")
    public ResponseResult<List<ItemsVO>> items() {
        long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<KnowledgeItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgeItem::getUserId, userId);
        queryWrapper.orderByDesc(BaseEntity::getId);
        List<ItemsVO> result = knowledgeItemService.list(queryWrapper).stream().map(knowledgeItem -> {
            ItemsVO vo = new ItemsVO();
            vo.setItemName(knowledgeItem.getItemName());
            vo.setItemDesc(knowledgeItem.getItemDesc());
            vo.setItemId(knowledgeItem.getId());
            KnowledgeChatBinding knowledgeChatBinding = knowledgeChatBindingService.getByKnowledgeId(knowledgeItem.getId());
            vo.setReqId(knowledgeChatBinding.getChatLogReq());
            return vo;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(result);
    }

    @Operation(summary = "创建项目")
    @PutMapping("/createItem")
    @DistributedLock(prefix = SystemConstants.RedisKeyEnum.ITEM_UPLOAD_LOCK, waitFor = 0, isReqUserId = true)
    public ResponseResult<String> createItem(@RequestBody CreateItemDTO createItemDTO) {
        String req = knowledgeItemService.createItem(StpUtil.getLoginIdAsLong(), createItemDTO.getItemName(), createItemDTO.getItemDesc());
        return ResponseResultGenerator.success(req);
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/item/{id}")
    public ResponseResult<String> item(@PathVariable("id") Long id) {
        knowledgeItemService.removeById(id);
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "基本设置")
    @PostMapping("/baseSetting")
    public ResponseResult<?> baseSetting(@RequestBody KnowledgeItem knowledgeItem) {
        knowledgeItemService.updateById(knowledgeItem);
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "欢迎词设置")
    @PostMapping("/welcomeSetting")
    public ResponseResult<?> welcomeSetting(@RequestBody WelcomeSettingDTO welcomeSettingDTO) {
        Long itemId = welcomeSettingDTO.getItemId();
        StringRedisUtils.set(SystemConstants.RedisKeyEnum.KNOWLEDGE_WELCOME_SETTING.getKey(itemId), JSONUtil.toJsonStr(welcomeSettingDTO));
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "查询欢迎词")
    @GetMapping("/welcomeSetting/{itemId}")
    public ResponseResult<WelcomeSettingDTO> getWelcomeSetting(@PathVariable Long itemId) {
        String welcomeJson = StringRedisUtils.get(SystemConstants.RedisKeyEnum.KNOWLEDGE_WELCOME_SETTING.getKey(itemId));
        WelcomeSettingDTO welcomeSettingDTO = new WelcomeSettingDTO();

        if(StrUtil.isNotBlank(welcomeJson)){
            welcomeSettingDTO = JSONUtil.toBean(welcomeJson,WelcomeSettingDTO.class);
        }
        return ResponseResultGenerator.success(welcomeSettingDTO);
    }


    @Operation(summary = "菜单设置")
    @PostMapping("/menusSetting")
    public ResponseResult<?> menusSetting(@RequestBody MenusSettingDTO menusSettingDTO) {
        Long itemId = menusSettingDTO.getItemId();
        StringRedisUtils.set(SystemConstants.RedisKeyEnum.KNOWLEDGE_MENUS_SETTING.getKey(itemId), JSONUtil.toJsonStr(menusSettingDTO.getMenusDetails()));
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "查询菜单设置")
    @PostMapping("/menusSetting/{itemId}")
    public ResponseResult<MenusSettingDTO> getMenusSetting(@PathVariable Long itemId) {
        String menusJson = StringRedisUtils.get(SystemConstants.RedisKeyEnum.KNOWLEDGE_MENUS_SETTING.getKey(itemId));
        MenusSettingDTO menusSettingDTO = new MenusSettingDTO();
        if(StrUtil.isNotBlank(menusJson)){
            List<MenusDetail> list = JSONUtil.parseArray(menusJson).toList(MenusDetail.class);
            menusSettingDTO.setMenusDetails(list);
            menusSettingDTO.setItemId(itemId);

        }
        return ResponseResultGenerator.success(menusSettingDTO);
    }


    @SaIgnore
    @Operation(summary = "获取欢迎设置和菜单设置")
    @GetMapping("/welcomeAndMenus/{uuid}")
    public ResponseResult<WelcomeAndMenusVO> welcomeAndMenus(@PathVariable String uuid){

        WelcomeAndMenusVO vo = new WelcomeAndMenusVO();

        ShareItem shareItem = shareItemService.getByUuid(uuid);

        Long itemId = shareItem.getItemId();

        String menusJson = StringRedisUtils.get(SystemConstants.RedisKeyEnum.KNOWLEDGE_MENUS_SETTING.getKey(itemId));


        if(StrUtil.isNotBlank(menusJson)){
            vo.setMenusDetails(JSONUtil.parseArray(menusJson).toList(MenusDetail.class));
        }

        String welcomeJson = StringRedisUtils.get(SystemConstants.RedisKeyEnum.KNOWLEDGE_WELCOME_SETTING.getKey(itemId));
        if(StrUtil.isNotBlank(welcomeJson)){
            WelcomeSettingDTO welcomeSettingDTO = JSONUtil.toBean(welcomeJson, WelcomeSettingDTO.class);

            vo.setWelcome(welcomeSettingDTO.getWelcome());
            vo.setFastPrompts(welcomeSettingDTO.getFastPrompts());
        }

        return ResponseResultGenerator.success(vo);
    }


    @Operation(summary = "资源列表")
    @GetMapping("/resources/{itemId}")
    public ResponseResult<List<ItemResourceVO>> resources(@PathVariable Long itemId) {
        List<ItemResource> itemResources = itemResourceService.getByItemId(itemId);
        List<ItemResourceVO> result = itemResources.stream().map(itemResource -> {
            ItemResourceVO vo = new ItemResourceVO();
            vo.setItemResourceId(itemResource.getId());
            vo.setItemId(itemResource.getItemId());
            vo.setOriginalName(itemResource.getOriginalName());
            vo.setFileName(itemResource.getFileName());
            vo.setSummaryDesc(itemResource.getSummaryDesc());
            vo.setQ1(itemResource.getQ1());
            vo.setQ2(itemResource.getQ2());
            String filePath = itemResource.getFilePath();
            String fullUrl = OSSFactory.build().getFullUrl(filePath);
            vo.setFileFullUrl(fullUrl);
            return vo;

        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(result);
    }

    @Operation(summary = "上传资源")
    @PostMapping("/updateResources")
    @DistributedLock(prefix = SystemConstants.RedisKeyEnum.ITEM_UPLOAD_LOCK, key = "#itemId", waitFor = 0, isReqUserId = true)
    public ResponseResult<String> updateResources(MultipartFile file, @NotBlank Long itemId, Long partitionId) {
        // 使用默认分区
        if (Objects.isNull(partitionId)) {
            partitionId = -1L;
        }
        long userId = StpUtil.getLoginIdAsLong();
        String url = fileHandler.updateFile(file, userId, itemId, partitionId);

        return ResponseResultGenerator.success(url);
    }

    @Operation(summary = "删除资源")
    @DeleteMapping("/deleteResources")
    @DistributedLock(prefix = SystemConstants.RedisKeyEnum.DEL_RESOURCES_LOCK, waitFor = 0, isReqUserId = true)
    public ResponseResult<?> delResources(@RequestBody List<Long> ids) {
        // 删除时候考虑删除分区
        fileHandler.deleteResource(ids, StpUtil.getLoginIdAsLong());
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "获取分区列表")
    @GetMapping("/partitions/{itemId}")
    public ResponseResult<?> partitions(@PathVariable Long itemId) {
        if (Objects.isNull(itemId)) {
            throw new ServiceException(CommonRespCode.VALID_SERVICE_ILLEGAL_ARGUMENT);
        }

        List<ItemPartition> itemPartitions = itemPartitionService.getByItemId(itemId);

        List<ItemPartitionVo> itemPartitionVos = itemPartitions.stream().map(itemPartition -> {
            ItemPartitionVo itemPartitionVo = new ItemPartitionVo();
            itemPartitionVo.setPartitionId(itemPartition.getId());
            itemPartitionVo.setPartitionName(itemPartition.getPartitionName());
            return itemPartitionVo;
        }).collect(Collectors.toList());

        return ResponseResultGenerator.success(itemPartitionVos);
    }

}
