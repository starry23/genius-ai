package cn.apeto.geniusai.server.config;


import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author apeto
 * @create 2023/3/27 11:24
 */
@Slf4j
@Configuration
public class BeanConfig {


    @Bean
    public ApplicationRunner memberData() {
        return args -> {
            SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
            StringRedisUtils.set(SystemConstants.RedisKeyEnum.WEB_CONFIG.getKey(), JSONUtil.toJsonStr(packageWebConfig));
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OkHttpClient sparkDeskOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }




}
