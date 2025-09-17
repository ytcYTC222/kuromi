package com.mall.admin.service;

import com.mall.admin.dto.request.LoginRequest;
import com.mall.admin.dto.UserRegisterRequest;
import com.mall.admin.dto.request.ChangePasswordRequest;
import com.mall.admin.dto.response.LoginResponse;
import com.mall.admin.dto.response.UserResponse;

/**
 * 认证服务接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public interface IAuthService {
    
    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户信息
     */
    UserResponse register(UserRegisterRequest request);
    
    /**
     * 用户登出
     *
     * @param token 用户token
     */
    void logout(String token);
    
    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return 新的登录响应
     */
    LoginResponse refreshToken(String refreshToken);
    
    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param request 修改密码请求
     */
    void changePassword(Integer userId, ChangePasswordRequest request);
    
    /**
     * 重置密码
     *
     * @param email 邮箱
     * @param newPassword 新密码
     */
    void resetPassword(String email, String newPassword);
    
    /**
     * 发送验证码
     *
     * @param email 邮箱
     * @param type 验证码类型（注册、重置密码等）
     */
    void sendVerificationCode(String email, String type);
    
    /**
     * 验证验证码
     *
     * @param email 邮箱
     * @param code 验证码
     * @param type 验证码类型
     * @return 验证结果
     */
    boolean verifyCode(String email, String code, String type);
    
    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回true，否则返回false
     */
    boolean checkUsernameExists(String username);
    
    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 存在返回true，否则返回false
     */
    boolean checkEmailExists(String email);
    
    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 存在返回true，否则返回false
     */
    boolean checkPhoneExists(String phone);
    
    /**
     * 根据token获取用户信息
     *
     * @param token 用户token
     * @return 用户信息
     */
    UserResponse getUserByToken(String token);
    
    /**
     * 验证token有效性
     *
     * @param token 用户token
     * @return 有效返回true，否则返回false
     */
    boolean validateToken(String token);
    
    /**
     * 激活用户账户
     *
     * @param email 邮箱
     * @param activationCode 激活码
     * @return 激活结果
     */
    boolean activateAccount(String email, String activationCode);
    
    /**
     * 发送激活邮件
     *
     * @param email 邮箱
     */
    void sendActivationEmail(String email);
    
    /**
     * 锁定用户账户
     *
     * @param userId 用户ID
     * @param reason 锁定原因
     */
    void lockAccount(Integer userId, String reason);
    
    /**
     * 解锁用户账户
     *
     * @param userId 用户ID
     */
    void unlockAccount(Integer userId);
    
    /**
     * 检查账户是否被锁定
     *
     * @param userId 用户ID
     * @return 锁定返回true，否则返回false
     */
    boolean isAccountLocked(Integer userId);
    
    /**
     * 记录登录失败次数
     *
     * @param username 用户名
     */
    void recordLoginFailure(String username);
    
    /**
     * 清除登录失败记录
     *
     * @param username 用户名
     */
    void clearLoginFailures(String username);
    
    /**
     * 获取登录失败次数
     *
     * @param username 用户名
     * @return 失败次数
     */
    int getLoginFailureCount(String username);
}