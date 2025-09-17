package com.mall.admin.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 * Spring Security配置类
 * 配置密码编码器和安全过滤链
 * 当前已禁用，因为移除了Spring Security依赖
 */
// @Configuration
// @EnableWebSecurity
public class SecurityConfig {
    
    /**
     * 密码编码器Bean
     * 使用BCrypt算法进行密码加密
     */
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
    
    /**
     * 安全过滤链配置
     * 配置URL权限和JWT认证
     */
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(AbstractHttpConfigurer::disable)
    //         .authorizeHttpRequests(auth -> auth
    //             .anyRequest().permitAll()
    //         );
    //     return http.build();
    // }
}
