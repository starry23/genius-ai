/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package cn.apeto.geniusai.server.service.oss;


import cn.hutool.core.io.IoUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * 腾讯云存储
 */
@Slf4j
public class QcloudCloudStorageService extends CloudStorageService {
    private COSClient client;

    public QcloudCloudStorageService(CloudStorageConfig config) {
        this.config = config;

        //初始化
        init();
    }

    private void init() {
        COSCredentials cred = new BasicCOSCredentials(config.getQcloudSecretId(), config.getQcloudSecretKey());
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(config.getQcloudRegion()));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 设置 socket 读取超时，默认 30s
        clientConfig.setSocketTimeout(30 * 1000);
        // 设置建立连接超时，默认 30s
        clientConfig.setConnectionTimeout(30 * 1000);

        client = new COSClient(cred, clientConfig);
    }

    @Override
    public URL getUrl(String objectName) {
        Date expirationTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        return client.generatePresignedUrl(config.getQcloudBucketName(), objectName, expirationTime);
    }

    @Override
    public UploadResult upload(MultipartFile file) {
        try {
            return upload(file.getInputStream(), file.getOriginalFilename());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public UploadResult upload(InputStream inputStream, String fileName) {
        try {
            String getsuffix = getSuffix(fileName);
            String path = getPath(config.getQcloudPrefix(), getsuffix);
            //腾讯云必需要以"/"开头
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            String qcloudBucketName = config.getQcloudBucketName();
            client.putObject(qcloudBucketName, path, inputStream, null);
            return new UploadResult(config.getQcloudDomain(), path, getFullUrl(path));
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        }finally {
            IoUtil.close(inputStream);

        }
    }

    @Override
    public UploadResult uploadUrl(String urlStr) {
        URL url;
        try {
            url = new URL(urlStr);
            String fileName = getFileName(url);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/4.76");
            InputStream inputStream = urlConnection.getInputStream();
            return upload(inputStream, fileName);
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        }
    }


    public String getFullUrl(String path) {
        return getUrl(path).toString();
    }
}
