package cn.apeto.geniusai.server.controller;

import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.entity.ChatDetailLog;
import cn.apeto.geniusai.server.entity.DallEImageInfo;
import cn.apeto.geniusai.server.entity.MjImageInfo;
import cn.apeto.geniusai.server.service.ChatDetailLogService;
import cn.apeto.geniusai.server.service.DallEImageInfoService;
import cn.apeto.geniusai.server.service.MjImageInfoService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.utils.RegexUtils;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

/**
 * @author apeto
 * @create 2023/6/24 11:34 上午
 */
@Tag(name = "下载")
@Slf4j
@RequestMapping("/api/download")
@RestController
public class DownLoadController {

    @Autowired
    private ChatDetailLogService chatDetailLogService;
    @Autowired
    private MjImageInfoService mjImageInfoService;

    @Operation(description = "下载聊天记录md")
    @GetMapping("/reqSession")
    public ResponseEntity<byte[]> reqSession(@RequestParam("reqId") String reqId) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<ChatDetailLog> chatDetailLogs = chatDetailLogService.selectByUserIdAndReqId(userId, reqId);

        StringBuilder content = new StringBuilder();
        for (ChatDetailLog chatDetailLog : chatDetailLogs) {
            content.append(chatDetailLog.getContent()).append("\n");
        }

        byte[] fileBytes = content.toString().getBytes(); // 将字符串转换为字节数组

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", DateUtil.format(new Date(), DatePattern.PURE_DATETIME_FORMATTER) + "记录.md"); // 设置文件名称及其附件类型

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK); // 返回带有字节数组和头部信息的响应实体对象
    }


    @Operation(summary = "对象代理")
    @GetMapping("/ossProxy")
    public ResponseEntity<byte[]> ossProxy(@RequestParam String url) {
        try {
            URL url1 = new URL(url);
            String path = url1.getPath();
            String name = FileUtil.getName(path);

            URLConnection urlConnection = url1.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            byte[] bytes = IoUtil.readBytes(inputStream, true);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", name);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            if (StrUtil.equalsAny(FileUtil.getSuffix(name), "doc", "docx")) {

                headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            }
            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (Exception e) {
            log.error("下载异常", e);
            return null;
        }

    }

    @Autowired
    private DallEImageInfoService dallEImageInfoService;

    @Operation(summary = "下载图片")
    @GetMapping("/image")
    public void image(@RequestParam("fileId") String fileId, @RequestParam(value = "type", defaultValue = "2") Integer type, HttpServletResponse response) {

        String cosUrl;

        if (type.equals(Constants.LeftMenuTypeEnum.DALLE.getType())) {
            DallEImageInfo dallEImageInfo = dallEImageInfoService.getByFileId(fileId);
            cosUrl = dallEImageInfo.getCosUrl();
        } else {

            MjImageInfo mjImageInfo = mjImageInfoService.getByFileId(fileId);
            cosUrl = mjImageInfo.getCosUrl();
        }

        if (!RegexUtils.isHttpOrHttps(cosUrl)) {
            cosUrl = OSSFactory.build().getFullUrl(cosUrl);
        }
        URLConnection connection;
        if (StrUtil.isBlank(cosUrl)) {
            return;
        }
        URL url;
        try {
            url = new URL(cosUrl);
            connection = url.openConnection();
        } catch (Exception e) {
            log.error("根据URL地址获取图片异常 地址:{}", cosUrl, e);
            return;
        }
        try (InputStream inputStream = connection.getInputStream()) {
            String extension = getExtension(url);

            // 将图片数据写入到响应输出流中
            byte[] buffer = new byte[1024];
            int bytesRead;
            response.setContentType("image/" + extension); // 根据图片类型设置对应的Content-Type
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileId + "." + extension + "\"");
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            // 关闭输入流和输出流
            inputStream.close();
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("下载图片异常", e);
        }
    }

    public static String getExtension(URL url) {
        String path = url.getPath();
        int lastDotPos = path.lastIndexOf('.');

        // 如果找到了'.'，则截取其后的部分作为文件扩展名；否则，文件扩展名为空
        return lastDotPos != -1 ? path.substring(lastDotPos + 1) : "";
    }
}
