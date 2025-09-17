package com.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.UserRegisterRequest;
import com.mall.admin.dto.UserRegisterResponse;
import com.mall.admin.dto.request.UserRequest;
import com.mall.admin.dto.response.UserResponse;
import com.mall.admin.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public interface IUserService extends IService<User> {
    
    /**
     * 获取用户列表（分页）
     *
     * @param page     页码
     * @param size     每页大小
     * @param keyword  搜索关键词（用户名、手机号、邮箱）
     * @param status   用户状态
     * @return 分页结果
     */
    PageResult<UserResponse> getUserList(Integer page, Integer size, String keyword, Integer status);
    
    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    UserResponse getUserById(Integer id);
    
    /**
     * 创建用户
     *
     * @param request 用户请求
     * @return 用户ID
     */
    Integer createUser(UserRequest request);
    
    /**
     * 更新用户
     *
     * @param id      用户ID
     * @param request 用户请求
     */
    void updateUser(Integer id, UserRequest request);
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Integer id);
    
    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态（0禁用，1启用）
     */
    void updateUserStatus(Integer id, Integer status);
    
    /**
     * 批量更新用户状态
     *
     * @param ids    用户ID列表
     * @param status 状态（0禁用，1启用）
     */
    void batchUpdateStatus(List<Integer> ids, Integer status);
    
    /**
     * 重置用户密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Integer id, String newPassword);
    
    /**
     * 获取用户统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getUserStatistics();
    
    /**
     * 用户注册
     *
     * @param request 注册请求
     * @param clientIp 客户端IP
     * @return 注册响应
     */
    UserRegisterResponse register(UserRegisterRequest request, String clientIp);
    
    /**
     * 根据昵称查询用户
     *
     * @param nickname 昵称
     * @return 用户信息
     */
    User findByNickname(String nickname);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User findByEmail(String email);
    
    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(String phone);
    
    /**
     * 检查昵称是否存在
     *
     * @param nickname 昵称
     * @return 存在返回true，否则返回false
     */
    boolean existsByNickname(String nickname);
    
    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 存在返回true，否则返回false
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 存在返回true，否则返回false
     */
    boolean existsByPhone(String phone);
}