package cn.apeto.geniusai.server.service.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author apeto
 * @create 2023/8/28 16:41
 */
@Slf4j
public class LocalStorageService extends CloudStorageService {

    public LocalStorageService(CloudStorageConfig config) {
        this.config = config;
    }

    @Override
    public URL getUrl(String objectName) {
        return null;
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
            String localRootPath = config.getLocalRootPath();

            if(StrUtil.isBlank(localRootPath)){
                SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
                URL url = new URL(packageWebConfig.getDomain());
                localRootPath = "/www/wwwroot/"+ url.getHost()+"/base_web/";
            }

            String path = getPath(config.getLocalPrefix(), getsuffix);
            File file = FileUtil.writeFromStream(inputStream, localRootPath+path);
            return new UploadResult(config.getLocalDomain(), path, config.getLocalDomain() + path);
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败，请检查配置信息", e);
        } finally {
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

    @Override
    public String getFullUrl(String path) {
        return config.getLocalDomain() + path;
    }
}
