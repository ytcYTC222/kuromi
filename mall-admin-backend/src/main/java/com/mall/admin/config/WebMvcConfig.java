package com.mall.admin.config;

import com.mall.admin.common.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置�? * 配置拦截器、跨域等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 注册拦截�?     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // 拦截所有请求路径
                .excludePathPatterns(
                        "/api/auth/login",           // 登录接口
                        "/api/auth/register",        // 注册接口
                        "/api/auth/refresh",         // 刷新令牌接口
                        "/swagger-ui/**",            // Swagger UI
                        "/v3/api-docs/**",           // API文档
                        "/swagger-resources/**",     // Swagger资源
                        "/webjars/**",               // Web资源
                        "/favicon.ico",              // 网站图标
                        "/error",                    // 错误页面
                        "/actuator/**",              // 监控端点
                        "/static/**",                // 静态资源
                        "/uploads/**"                // 上传文件
                );
    }
}
