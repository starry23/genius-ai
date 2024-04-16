package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.vo.UploadFileVO;
import cn.apeto.geniusai.server.entity.FileInfo;
import cn.apeto.geniusai.server.service.FileInfoService;
import cn.apeto.geniusai.server.service.oss.CloudStorageService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.service.oss.UploadResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author apeto
 * @create 2023/7/1 3:43 下午
 */
@Tag(name = "上传")
@Slf4j
@RequestMapping("/api/upload")
@RestController
public class UploadController {

    @Autowired
    private FileInfoService fileInfoService;

    @RequestMapping("/file")
    public ResponseResult<UploadFileVO> file(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "1") Integer viewType) {


        try {

            long userId=StpUtil.getLoginId(-1);
            String fileName = file.getOriginalFilename();
            if (StrUtil.isBlank(fileName)) {
                return ResponseResultGenerator.error("文件名不能为空");
            }
            // 获取文件扩展名
            CloudStorageService cloudStorageService = OSSFactory.build();
            UploadResult uploadResult = cloudStorageService.upload(file);
            log.info("上传文件结果:{} url:{}", JSONUtil.toJsonStr(uploadResult), uploadResult.getFullUrl());
            UploadFileVO vo = new UploadFileVO();
            String path = uploadResult.getPath();
            vo.setPath(path);
            vo.setFullUrl(uploadResult.getFullUrl());


            FileInfo entity = new FileInfo();
            entity.setUserId(userId);
            entity.setOriginalFileName(fileName);
            entity.setPath(path);
            entity.setViewType(viewType);
            entity.setType(cloudStorageService.getType());
            fileInfoService.save(entity);
            vo.setFileId(entity.getId());
            return ResponseResultGenerator.success(vo);
        } catch (Exception e) {
            return ResponseResultGenerator.error(e.getMessage());
        }
    }
}
