package cn.apeto.geniusai.server.config;

import cn.apeto.geniusai.server.utils.StpManagerUtil;
import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.interceptor.SaInterceptor;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.*;


/**
 * @author apeto
 * @create 2023/4/2 10:50 下午
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Bean
    public ApplicationRunner saTokenInit() {
        return args -> {
            SaManager.getConfig();
//            StpLogic managerLogic = StpManagerUtil.stpLogic;
//            StpManagerUtil.setStpLogic(managerLogic);

            StpManagerUtil.getLoginType();


        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 设置通用静态资源的路径映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/","classpath:/static/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 映射首页到base_web的index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        // 映射/admin到dist的index.html
        registry.addViewController("/admin").setViewName("forward:/dist/index.html");
    }


    // 注册 Sa-Token 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }

    // 允许跨域请求
    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        String domain = SpringUtil.getProperty("appServer.domain");
        // 剔除协议头
//        domain = domain.substring(domain.indexOf("//") + 2);
        registry.addMapping("/**") // 为所有请求路径启用跨域
//                .allowedOriginPatterns("http://" + domain, "https://" + domain) // 选择自动适配跨域域名
                .allowedOriginPatterns("*") // 选择自动适配跨域域名
                .allowedMethods("*") // 允许所有请求方法（GET, POST, PUT, DELETE等）
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许携带Cookie
                .exposedHeaders("Authorization", "satoken") // 配置跨域可见的头部（根据实际情况添加，例如"Authorization"）
                .maxAge(3600); // 预检请求的有效期（单位：秒）
    }

    // 解决tomcat url中特殊字符被拦截问题
    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "|{}[]\\"));
        return factory;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置单个文件最大值，支持KB、MB、GB，默认100M
        factory.setMaxFileSize(DataSize.ofMegabytes(30));
        // 设置总上传数据总大小，默认100M，单位字节。
        factory.setMaxRequestSize(DataSize.ofMegabytes(30));
        return factory.createMultipartConfig();
    }

}
