package com.example.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")           // 允许跨域的路径
                .allowedOrigins("http://localhost:5173")  // 允许的前端地址（Vue 默认端口）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")  // 允许的方法
                .allowedHeaders("*")              // 允许的请求头
                .allowCredentials(true)           // 是否允许携带 Cookie
                .maxAge(3600);                    // 预检请求缓存时间（秒）
    }
}