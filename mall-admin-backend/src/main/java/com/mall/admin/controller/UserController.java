package com.mall.admin.controller;

import com.mall.admin.common.ApiResponse;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.UserRegisterRequest;
import com.mall.admin.dto.UserRegisterResponse;
import com.mall.admin.dto.request.UserRequest;
import com.mall.admin.dto.response.UserResponse;
import com.mall.admin.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关的API接口")
public class UserController {

    private final IUserService userService;

    /**
     * 获取用户列表（分页）
     */
    @Operation(summary = "获取用户列表", description = "分页查询用户列表")
    @GetMapping
    public ApiResponse<PageResult<UserResponse>> getUserList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") 
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词（用户名、手机号、邮箱）")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "用户状态：0禁用，1正常")
            @RequestParam(required = false) Integer status) {
        
        log.info("获取用户列表，页码: {}, 大小: {}, 关键词: {}, 状态: {}", page, size, keyword, status);
        
        PageResult<UserResponse> result = userService.getUserList(page, size, keyword, status);
        return ApiResponse.success(result);
    }
    
    /**
     * 根据ID获取用户详情
     */
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Integer id) {
        
        log.info("获取用户详情，用户ID: {}", id);
        
        UserResponse user = userService.getUserById(id);
        return ApiResponse.success(user);
    }
    
    /**
     * 创建用户
     */
    @Operation(summary = "创建用户", description = "创建新用户")
    @PostMapping
    public ApiResponse<Integer> createUser(
            @Parameter(description = "用户信息", required = true)
            @Valid @RequestBody UserRequest request) {
        
        log.info("创建用户，昵称: {}", request.getNickname());
        
        Integer userId = userService.createUser(request);
        
        log.info("用户创建成功，用户ID: {}", userId);
        return ApiResponse.success(userId);
    }
    
    /**
     * 更新用户
     */
    @Operation(summary = "更新用户", description = "更新用户信息")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Integer id,
            @Parameter(description = "用户信息", required = true)
            @Valid @RequestBody UserRequest request) {
        
        log.info("更新用户，用户ID: {}", id);
        
        userService.updateUser(id, request);
        
        log.info("用户更新成功，用户ID: {}", id);
        return ApiResponse.success();
    }
    
    /**
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "删除指定用户")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Integer id) {
        
        log.info("删除用户，用户ID: {}", id);
        
        userService.deleteUser(id);
        
        log.info("用户删除成功，用户ID: {}", id);
        return ApiResponse.success();
    }
    
    /**
     * 更新用户状态
     */
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateUserStatus(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Integer id,
            @Parameter(description = "状态信息", required = true)
            @RequestBody Map<String, Integer> request) {
        
        Integer status = request.get("status");
        log.info("更新用户状态，用户ID: {}, 新状态: {}", id, status);
        
        userService.updateUserStatus(id, status);
        
        log.info("用户状态更新成功，用户ID: {}", id);
        return ApiResponse.success();
    }
    
    /**
     * 批量更新用户状态
     */
    @Operation(summary = "批量更新用户状态", description = "批量启用或禁用用户")
    @PatchMapping("/batch/status")
    public ApiResponse<Void> batchUpdateStatus(
            @Parameter(description = "批量状态更新请求", required = true)
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        Integer status = (Integer) request.get("status");
        
        log.info("批量更新用户状态，用户IDs: {}, 新状态: {}", ids, status);
        
        userService.batchUpdateStatus(ids, status);
        
        log.info("批量更新用户状态成功，更新数量: {}", ids.size());
        return ApiResponse.success();
    }
    
    /**
     * 重置用户密码
     */
    @Operation(summary = "重置用户密码", description = "重置指定用户的密码")
    @PatchMapping("/{id}/password")
    public ApiResponse<Void> resetPassword(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Integer id,
            @Parameter(description = "密码信息", required = true)
            @RequestBody Map<String, String> request) {
        
        String newPassword = request.get("password");
        log.info("重置用户密码，用户ID: {}", id);
        
        userService.resetPassword(id, newPassword);
        
        log.info("用户密码重置成功，用户ID: {}", id);
        return ApiResponse.success();
    }
    
    /**
     * 获取用户统计信息
     */
    @Operation(summary = "获取用户统计", description = "获取用户统计信息")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getUserStatistics() {
        
        log.info("获取用户统计信息");
        
        Map<String, Object> statistics = userService.getUserStatistics();
        return ApiResponse.success(statistics);
    }

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
     * 检查昵称是否存在
     */
    @Operation(summary = "检查昵称", description = "检查昵称是否已被使用")
    @GetMapping("/check-nickname/{nickname}")
    public ApiResponse<Boolean> checkNicknameExists(
            @Parameter(description = "昵称", required = true)
            @PathVariable String nickname) {
        
        log.info("检查昵称是否存在: {}", nickname);
        
        boolean exists = userService.existsByNickname(nickname);
        return ApiResponse.success(exists);
    }
    
    /**
     * 检查邮箱是否存在
     */
    @Operation(summary = "检查邮箱", description = "检查邮箱是否已被注册")
    @GetMapping("/check-email/{email}")
    public ApiResponse<Boolean> checkEmailExists(
            @Parameter(description = "邮箱", required = true)
            @PathVariable String email) {
        
        log.info("检查邮箱是否存在: {}", email);
        
        boolean exists = userService.existsByEmail(email);
        return ApiResponse.success(exists);
    }
    
    /**
     * 检查手机号是否存在
     */
    @Operation(summary = "检查手机号", description = "检查手机号是否已被注册")
    @GetMapping("/check-phone/{phone}")
    public ApiResponse<Boolean> checkPhoneExists(
            @Parameter(description = "手机号", required = true)
            @PathVariable String phone) {
        
        log.info("检查手机号是否存在: {}", phone);
        
        boolean exists = userService.existsByPhone(phone);
        return ApiResponse.success(exists);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}