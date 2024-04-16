package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.server.config.properties.AliPayProperties;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.vo.Gpts;
import cn.apeto.geniusai.server.domain.vo.GptsRootBean;
import cn.apeto.geniusai.server.domain.vo.SaveSettingVO;
import cn.apeto.geniusai.server.utils.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author apeto
 * @create 2023/5/5 14:03
 */
@SaIgnore
@Tag(name = "站点信息")
@Slf4j
@RestController
@RequestMapping("/api/web")
public class WebConfigController {

    public static String gptsListUrl = "https://www.diygpts.cn/gpts/search?page=1&page_size=25&token=87cb6f1122e65b4a10fc03653a68822a&query={}";
    public static String gptsListUrl_base = "https://gpts.ddaiai.com/open/gpts";
    public static String gptsListUrl_base_query = "https://gpts.ddaiai.com/open/gptsapi/search?q={}";


    private static final Map<String, Integer> gptsTagsMap;

    static {
        gptsTagsMap = new HashMap<>();
        gptsTagsMap.put("商业", 17);
        gptsTagsMap.put("开发", 16);
        gptsTagsMap.put("教育", 21);
        gptsTagsMap.put("金融", 39);
        gptsTagsMap.put("医疗", 38);
        gptsTagsMap.put("营销", 18);
        gptsTagsMap.put("生产力", 15);
        gptsTagsMap.put("交易", 50);
        gptsTagsMap.put("游戏", 22);
        gptsTagsMap.put("设计", 20);
        gptsTagsMap.put("写作", 14);
    }

    @GetMapping("/getGpsList")
    @Operation(summary = "获取网站支持的GPT模型")
    public ResponseResult<GptsVo> getGpsList(String query, String tag) {
        GptsVo vo = new GptsVo();

        ChatConfigEntity chatConfig = CommonUtils.getChatConfig();
        ChatConfigEntity.Entity gptsEntity = chatConfig.getGptsEntity();
        String route = gptsEntity.getRoute();
        if ("1".equals(route)) {
            vo.setTag(ListUtil.toList(gptsTagsMap.keySet()));
            getBaisuGptsVo(gptsTagsMap.get(tag), query, vo);
        } else {
            vo.setTag(ListUtil.toList("图像", "论文", "文件", "识别", "PDF", "新闻", "医生", "老师", "Logo"));
            query = (StrUtil.isNotBlank(query) ? query : tag);
            getPltGptsVO(query, vo);
        }
        return ResponseResultGenerator.success(vo);
    }


    private void getPltGptsVO(String query, GptsVo vo) {
        String reqUrl = gptsListUrl_base;

        if (StrUtil.isNotBlank(query)) {

            // 搜索
            reqUrl = StrUtil.format(gptsListUrl_base_query, query);
            String httpRes = HttpUtil.get(reqUrl);

            GptsRootBean gptsRootBean = JSONUtil.toBean(httpRes, new TypeReference<GptsRootBean>() {
            }, true);

            vo.setGpts(gptsRootBean.getData().getList());
        } else {
            // 默认
            String httpRes = HttpUtil.get(reqUrl);
            GptsVo gptsVo = JSONUtil.toBean(httpRes, new TypeReference<GptsVo>() {
            }, true);
            vo.setGpts(gptsVo.getGpts());
            vo.setTag(gptsVo.getTag());
        }
    }

    private void getBaisuGptsVo(Integer tag, String query, GptsVo vo) {
        // 白苏
        List<GptsModel> gptsModels;

        if (tag != null) {
            // TODO
        }
        String res = HttpUtil.get(StrUtil.format(gptsListUrl, query));

        gptsModels = JSONUtil.toBean(res, new TypeReference<List<GptsModel>>() {
        }, true);

        if (CollUtil.isNotEmpty(gptsModels)) {
            ChatConfigEntity.Entity gptsEntity = CommonUtils.getChatConfig().getGptsEntity();
            ArrayList<Gpts> gpts = new ArrayList<>();
            for (GptsModel gptsModel : gptsModels) {
                String gptDesc = gptsModel.getGptDesc();
                String gptGizmoId = gptsModel.getGptGizmoId();
                String gptLogo = gptsModel.getGptLogo();
                String gptName = gptsModel.getGptName();
                Gpts e = new Gpts();
                e.setGid(gptsEntity.getModelPrefix() + gptGizmoId);
                e.setInfo(gptDesc);
                e.setLogo(gptLogo);
                e.setName(gptName);
                gpts.add(e);
            }
            vo.setGpts(gpts);
        }
    }

    @GetMapping("/setting")
    @Operation(summary = "获取网站设置")
    public ResponseResult<SaveSettingVO> setting() {
        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        SaveSettingVO vo = new SaveSettingVO();
        BeanUtil.copyProperties(packageWebConfig, vo);
        vo.setWebVersion(Constants.VERSION);
        PaySetting paySetting = CommonUtils.getPaySetting();

        if (paySetting != null) {
            WechatPayConfig wechatPayConfig = paySetting.getWechatPayConfig();
            if (wechatPayConfig != null) {
                vo.setWechatPayEnable(wechatPayConfig.getEnable());
            }

            AliPayProperties aliPayProperties = paySetting.getAliPayProperties();
            if (aliPayProperties != null) {
                vo.setAliPayEnable(aliPayProperties.getEnable());
                vo.setOpenFacePay(aliPayProperties.getOpenFacePay());
            }

            vo.setClosePayNotice(paySetting.getClosePayNotice());
        }
        return ResponseResultGenerator.success(vo);
    }


    @GetMapping("/checkVersion")
    @Operation(summary = "网站版本")
    public ResponseResult<String> setting(String version) {

        if (StrUtil.isBlank(version)) {
            return ResponseResultGenerator.result(CommonRespCode.VERSION.getCode(), CommonRespCode.VERSION.getMessage(), Constants.VERSION);
        }

        if (VersionComparator.INSTANCE.compare(Constants.VERSION, version) > 0) {
            return ResponseResultGenerator.result(CommonRespCode.VERSION.getCode(), CommonRespCode.VERSION.getMessage(), Constants.VERSION);
        }
        return ResponseResultGenerator.success();
    }

}
