package com.mall.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置�? * 用于配置JWT相关参数
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT密钥
     */
    private String secret = "mySecretKey123456789012345678901234567890";

    /**
     * JWT过期时间（秒�?     */
    private Long expiration = 86400L; // 24小时

    /**
     * JWT请求头名�?     */
    private String header = "Authorization";

    /**
     * JWT令牌前缀
     */
    private String prefix = "Bearer ";

    /**
     * 刷新令牌过期时间（秒）
     */
    private Long refreshExpiration = 604800L; // 7天
    // Getter和Setter方法
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getRefreshExpiration() {
        return refreshExpiration;
    }

    public void setRefreshExpiration(Long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}
