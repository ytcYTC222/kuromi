package com.mall.admin.common.interceptor;

import com.mall.admin.common.annotation.RequireAuth;
import com.mall.admin.common.exception.AuthException;
import com.mall.admin.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * JWT拦截器
 * 实现JWT令牌验证和权限校验
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        
        // 检查方法或类上是否有RequireAuth注解
        RequireAuth requireAuth = method.getAnnotation(RequireAuth.class);
        if (requireAuth == null) {
            requireAuth = handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
        }
        
        // 如果没有RequireAuth注解，直接放行
        if (requireAuth == null) {
            return true;
        }

        // 获取请求头中的JWT令牌
        String authHeader = request.getHeader(jwtUtil.getHeader());
        String token = jwtUtil.extractToken(authHeader);
        
        // 检查令牌是否存在
        if (token == null) {
            throw new AuthException(401, "缺少认证令牌");
        }
        
        // 验证令牌有效性
        if (!jwtUtil.validateToken(token)) {
            throw new AuthException(401, "认证令牌无效或已过期");
        }
        
        // 检查令牌是否过期
        if (jwtUtil.isTokenExpired(token)) {
            throw new AuthException(401, "认证令牌已过期");
        }
        
        // 获取用户信息
        Integer userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String userRole = jwtUtil.getRoleFromToken(token);
        
        // 将用户信息存储到请求属性中，供后续使用
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("userRole", userRole);
        
        // 权限校验
        if (!checkPermission(requireAuth, userRole)) {
            throw new AuthException(403, "权限不足");
        }
        
        return true;
    }
    
    /**
     * 检查权限
     * @param requireAuth 权限注解
     * @param userRole 用户角色
     * @return 是否有权限
     */
    private boolean checkPermission(RequireAuth requireAuth, String userRole) {
        // 如果需要管理员权限
        if (requireAuth.admin()) {
            return "ADMIN".equals(userRole);
        }
        
        // 如果指定了特定角色
        String requiredRole = requireAuth.role();
        if (!requiredRole.isEmpty()) {
            return jwtUtil.hasPermission(userRole, requiredRole);
        }
        
        // 如果没有特定要求，只要有有效令牌即可
        return true;
    }
}