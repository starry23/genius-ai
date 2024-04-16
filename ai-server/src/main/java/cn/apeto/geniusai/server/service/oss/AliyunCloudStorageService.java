package cn.apeto.geniusai.server.service.oss;

import cn.hutool.core.io.IoUtil;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * 阿里云存储
 */
@Slf4j
public class AliyunCloudStorageService extends CloudStorageService {
    private OSSClient client;

    public AliyunCloudStorageService(CloudStorageConfig config) {
        this.config = config;
        //初始化
        init();
    }

    private void init() {
        client = new OSSClient(config.getAliyunEndPoint(), config.getAliyunAccessKeyId(),
                config.getAliyunAccessKeySecret());
    }

    public String getFullUrl(String objectName) {
        return getUrl(objectName).toString();
    }

    @Override
    public URL getUrl(String objectName) {

        // 去掉objectName前面的"/"
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(config.getAliyunBucketName(), objectName);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);
        Date expiration = new Date(new Date().getTime() + 3600 * 1000L);
        generatePresignedUrlRequest.setExpiration(expiration);
        URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
        return url;
    }


    @Override
    public UploadResult upload(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("上传文件失败，originalFilename为空");
            }

            String path = getPath(config.getAliyunPrefix(), getSuffix(originalFilename));
            client.putObject(config.getAliyunBucketName(), path, inputStream);
            URL url = getUrl(path);
            return new UploadResult(url.getHost(), url.getPath(), url.toString());
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        }

    }

    @Override
    public UploadResult upload(InputStream inputStream, String fileName) {
        try {
            String path = getPath(config.getAliyunPrefix(), getSuffix(fileName));
            client.putObject(config.getAliyunBucketName(), path, inputStream);
            URL resUrl = getUrl(path);
            return new UploadResult(resUrl.getHost(), resUrl.getPath(), resUrl.toString());
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
            String fileName = getFileName(url);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/4.76");
            return upload(urlConnection.getInputStream(), fileName);
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        }


    }
}
