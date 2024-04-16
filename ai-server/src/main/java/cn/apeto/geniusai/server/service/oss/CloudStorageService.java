/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package cn.apeto.geniusai.server.service.oss;


import com.alibaba.fastjson2.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * 云存储(支持七牛、阿里云、腾讯云、又拍云)
 */
public abstract class CloudStorageService {
    /**
     * 云存储配置信息
     */
    CloudStorageConfig config;

    public Integer getType() {
        return config.getType();
    }

    /**
     * 文件路径
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    public String getPath(String prefix, String suffix) {

        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = DateUtils.format(new Date(), "yyyyMMdd") + "/" + uuid;

        if (StringUtils.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }

        return path + suffix;
    }

    public String getSuffix(String originalFilename) {
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException("文件名不能为空");
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    public abstract URL getUrl(String objectName);


    /**
     * 文件上传
     *
     * @return 返回http地址
     */
    public abstract UploadResult upload(MultipartFile file);

    public abstract UploadResult upload(InputStream inputStream, String fileName);

    public abstract UploadResult uploadUrl(String urlStr);


    public abstract String getFullUrl(String path);

    public static String getFileName(URL url) {
        // 获取路径部分
        String path = url.getPath();

        // 使用斜杠分隔路径，获取最后一个部分作为文件名
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
}
