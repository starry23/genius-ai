/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package cn.apeto.geniusai.server.service.oss;

import cn.hutool.core.io.IoUtil;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 七牛云存储
 */
public class QiniuCloudStorageService extends CloudStorageService {
    private UploadManager uploadManager;
    private String token;

    public QiniuCloudStorageService(CloudStorageConfig config) {
        this.config = config;

        //初始化
        init();
    }

    private void init() {
        uploadManager = new UploadManager(new Configuration(Zone.autoZone()));
        token = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey()).
                uploadToken(config.getQiniuBucketName());
    }

    @Override
    public URL getUrl(String objectName) {
        return null;
    }

    @Override
    public UploadResult upload(MultipartFile file) {
        String path = getPath(config.getQiniuPrefix(), getSuffix(file.getOriginalFilename()));
        try {
            Response res = uploadManager.put(file.getBytes(), path, token);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res);
            }
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，请核对七牛配置信息", e);
        }
        return new UploadResult(config.getQiniuDomain(), path);
    }

    @Override
    public UploadResult upload(InputStream inputStream, String fileName) {
        try {
            Response res = uploadManager.put(inputStream, fileName, token, null, null);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res);
            }
            return new UploadResult(config.getQiniuDomain(), fileName);
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        } finally {
            IoUtil.close(inputStream);
        }
    }

    @Override
    public UploadResult uploadUrl(String urlStr) {

        try {
            URL url = new URL(urlStr);
            String path = getPath(config.getQiniuPrefix(), getSuffix(getFileName(url)));
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/4.76");
            upload(urlConnection.getInputStream(), path);
            return new UploadResult(config.getQiniuDomain(), path);
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        }

    }

    public String getFullUrl(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        if (path.startsWith("https://") || path.startsWith("http://")) {
            return path;
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return config.getQiniuDomain() + path;
    }
}
