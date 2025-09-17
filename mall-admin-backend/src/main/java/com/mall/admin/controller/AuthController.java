package com.mall.admin.controller;

import com.mall.admin.common.annotation.RequireAuth;
import com.mall.admin.common.ApiResponse;
import com.mall.admin.dto.UserRegisterRequest;
import com.mall.admin.dto.UserRegisterResponse;
import com.mall.admin.entity.User;
import com.mall.admin.service.IUserService;
import com.mall.admin.util.JwtUtil;
import com.mall.admin.util.PasswordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 演示JWT功能的使用
 */
@Slf4j
@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private IUserService userService;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "用户注册接口")
    @PostMapping("/register")
    public ApiResponse<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request, HttpServletRequest httpRequest) {
        try {
            // 获取客户端IP
            String clientIp = getClientIp(httpRequest);
            
            // 执行注册
            UserRegisterResponse response = userService.register(request, clientIp);
            
            log.info("用户注册成功，昵称: {}, 用户ID: {}", response.getNickname(), response.getUserId());
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("用户注册失败，昵称: {}, 错误: {}", request.getNickname(), e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     * 验证用户昵称和密码
     */
    @Operation(summary = "用户登录", description = "用户登录获取JWT令牌")
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            // 根据昵称查询用户
            User user = userService.findByNickname(request.getNickname());
            
            if (user == null) {
                return ApiResponse.error("昵称或密码错误");
            }
            
            // 检查用户状态
            if (user.getStatus() == 0) {
                return ApiResponse.error("账户已被禁用");
            }
            
            // 验证密码
            if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
                return ApiResponse.error("昵称或密码错误");
            }
            
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userService.updateById(user);
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUserId(), user.getNickname(), "USER");
            
            // 构建用户信息对象
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("email", user.getEmail());
            userInfo.put("avatarUrl", user.getAvatarUrl());
            userInfo.put("phone", user.getPhone());
            userInfo.put("gender", user.getGender());
            userInfo.put("status", user.getStatus());
            userInfo.put("role", "USER");
            userInfo.put("permissions", new String[]{"user:read", "user:update"});
            
            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", userInfo);
            
            return ApiResponse.success(data);
            
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage(), e);
            return ApiResponse.error("登录失败，请稍后重试");
        }
    }

    /**
     * 获取当前用户信息
     * 需要JWT认证
     */
    @Operation(summary = "获取用户信息", description = "获取当前登录用户信息")
    @RequireAuth
    @GetMapping("/userinfo")
    public ApiResponse<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        // 从请求中获取用户信息（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        String nickname = (String) request.getAttribute("nickname");  // 使用nickname字段
        String role = (String) request.getAttribute("role");
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", userId);
        userInfo.put("nickname", nickname);  // 使用nickname字段
        userInfo.put("role", role);
        
        return ApiResponse.success(userInfo);
    }

    /**
     * 管理员专用接口
     * 需要管理员权限
     */
    @Operation(summary = "管理员接口", description = "仅管理员可访问的接口")
    @RequireAuth(role = "ADMIN")
    @GetMapping("/admin/test")
    public ApiResponse<String> adminTest() {
        return ApiResponse.success("这是管理员专用功能");
    }

    /**
     * 普通用户接口
     * 只需要登录即可访问
     */
    @Operation(summary = "用户接口", description = "登录用户可访问的接口")
    @RequireAuth
    @GetMapping("/user/test")
    public ApiResponse<String> userTest() {
        return ApiResponse.success("这是普通用户功能");
    }

    /**
     * 公开接口
     * 无需认证
     */
    @Operation(summary = "公开接口", description = "无需认证的公开接口")
    @GetMapping("/public/test")
    public ApiResponse<String> publicTest() {
        return ApiResponse.success("这是公开接口，无需认证");
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 登录请求对象
     */
    public static class LoginRequest {
        private String nickname;  // 对应User实体的nickname字段
        private String password;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}