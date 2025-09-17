package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.UserRegisterRequest;
import com.mall.admin.dto.UserRegisterResponse;
import com.mall.admin.dto.request.UserRequest;
import com.mall.admin.dto.response.UserResponse;
import com.mall.admin.entity.User;
import com.mall.admin.mapper.UserMapper;
import com.mall.admin.service.IUserService;
import com.mall.admin.util.PasswordUtil;
import com.mall.admin.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    
    private final UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRegisterResponse register(UserRegisterRequest request, String clientIp) {
        log.info("开始用户注册，用户名: {}, 邮箱: {}, IP: {}", request.getNickname(), request.getEmail(), clientIp);
        
        try {
            // 1. 验证注册信息
            validateRegisterRequest(request);
            
            // 2. 检查用户是否已存在
            checkUserExists(request);
            
            // 3. 创建用户实体
            User user = createUserEntity(request, clientIp);
            
            // 4. 保存用户到数据库
            int result = userMapper.insert(user);
            if (result <= 0) {
                log.error("用户注册失败，数据库插入失败，用户名: {}", request.getNickname());
                throw new BusinessException("用户注册失败，请稍后重试");
            }
            
            log.info("用户注册成功，用户ID: {}, 用户名: {}", user.getUserId(), user.getNickname());
            
            // 5. 构建响应
            return buildRegisterResponse(user);
            
        } catch (BusinessException e) {
            log.warn("用户注册业务异常，用户名: {}, 错误: {}", request.getNickname(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户注册系统异常，用户名: {}", request.getNickname(), e);
            throw new BusinessException("用户注册失败，系统异常");
        }
    }
    
    /**
     * 验证注册请求
     */
    private void validateRegisterRequest(UserRegisterRequest request) {
        // 验证密码强度
        if (!PasswordUtil.isValidPassword(request.getPassword())) {
            throw new BusinessException("密码强度不够，密码必须包含字母和数字，长度6-20位");
        }
        
        // 验证确认密码
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        
        // 验证手机号（如果提供）
        if (StringUtils.hasText(request.getPhone())) {
            if (!request.getPhone().matches("^1[3-9]\\d{9}$")) {
                throw new BusinessException("手机号格式不正确");
            }
        }
    }
    
    /**
     * 检查用户是否已存在
     */
    private void checkUserExists(UserRegisterRequest request) {
        // 检查昵称是否存在
        if (existsByNickname(request.getNickname())) {
            throw new BusinessException("昵称已存在");
        }
        
        // 检查邮箱是否存在
        if (existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }
        
        // 检查手机号是否存在（如果提供）
        if (StringUtils.hasText(request.getPhone()) && existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已被注册");
        }
    }
    
    /**
     * 创建用户实体
     */
    private User createUserEntity(UserRegisterRequest request, String clientIp) {
        User user = new User();
        // 根据User实体类的实际字段设置
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getNickname());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender() != null ? request.getGender() : 0);
        user.setStatus(1); // 默认启用
        user.setRegisterTime(LocalDateTime.now());
        
        return user;
    }
    
    /**
     * 构建注册响应
     */
    private UserRegisterResponse buildRegisterResponse(User user) {
        return UserRegisterResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .status(user.getStatus())
                .registerTime(user.getRegisterTime())
                .message("用户注册成功")
                .build();
    }
    
    @Override
    public User findByNickname(String nickname) {
        return userMapper.findByNickname(nickname);
    }
    
    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
    
    @Override
    public User findByPhone(String phone) {
        return userMapper.findByPhone(phone);
    }
    
    @Override
    public boolean existsByNickname(String nickname) {
        return userMapper.existsByNickname(nickname);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }
    
    @Override
    public boolean existsByPhone(String phone) {
        return StringUtils.hasText(phone) && userMapper.existsByPhone(phone);
    }
    
    @Override
    public PageResult<UserResponse> getUserList(Integer page, Integer size, String keyword, Integer status) {
        log.info("获取用户列表，页码: {}, 大小: {}, 关键词: {}, 状态: {}", page, size, keyword, status);
        
        try {
            // 构建分页对象
            Page<User> pageObj = new Page<>(page, size);
            
            // 构建查询条件
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            
            // 关键词搜索（昵称、手机号、邮箱）
            if (StringUtils.hasText(keyword)) {
                queryWrapper.and(wrapper -> wrapper
                    .like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getEmail, keyword)
                );
            }
            
            // 状态筛选
            if (status != null) {
                queryWrapper.eq(User::getStatus, status);
            }
            
            // 按注册时间倒序
            queryWrapper.orderByDesc(User::getRegisterTime);
            
            // 执行分页查询
            IPage<User> userPage = userMapper.selectPage(pageObj, queryWrapper);
            
            // 转换为响应DTO
            List<UserResponse> userResponses = userPage.getRecords().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
            
            return new PageResult<>(
                userResponses,
                userPage.getTotal(),
                userPage.getCurrent(),
                userPage.getSize()
            );
                
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            throw new BusinessException("获取用户列表失败：" + e.getMessage());
        }
    }
    
    @Override
    public UserResponse getUserById(Integer id) {
        log.info("获取用户详情，用户ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        
        try {
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            return convertToUserResponse(user);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取用户详情失败，用户ID: {}", id, e);
            throw new BusinessException("获取用户详情失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createUser(UserRequest request) {
        log.info("创建用户，昵称: {}, 邮箱: {}", request.getNickname(), request.getEmail());
        
        try {
            // 验证用户信息
            validateUserRequest(request, true);
            
            // 检查用户是否已存在
            checkUserExistsForCreate(request);
            
            // 创建用户实体
            User user = convertToUserEntity(request);
            user.setRegisterTime(LocalDateTime.now());
            user.setStatus(request.getStatus() != null ? request.getStatus() : 1); // 默认启用
            
            // 保存到数据库
            int result = userMapper.insert(user);
            if (result <= 0) {
                throw new BusinessException("创建用户失败");
            }
            
            log.info("用户创建成功，用户ID: {}, 昵称: {}", user.getUserId(), user.getNickname());
            return user.getUserId();
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建用户失败，昵称: {}", request.getNickname(), e);
            throw new BusinessException("创建用户失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Integer id, UserRequest request) {
        log.info("更新用户，用户ID: {}, 昵称: {}", id, request.getNickname());
        
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        
        try {
            // 检查用户是否存在
            User existingUser = userMapper.selectById(id);
            if (existingUser == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 验证用户信息
            validateUserRequest(request, false);
            
            // 检查用户信息是否冲突（排除自己）
            checkUserExistsForUpdate(id, request);
            
            // 更新用户信息
            User user = convertToUserEntity(request);
            user.setUserId(id);
            
            int result = userMapper.updateById(user);
            if (result <= 0) {
                throw new BusinessException("更新用户失败");
            }
            
            log.info("用户更新成功，用户ID: {}", id);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户失败，用户ID: {}", id, e);
            throw new BusinessException("更新用户失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Integer id) {
        log.info("删除用户，用户ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        
        try {
            // 检查用户是否存在
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 这里可以添加业务逻辑，比如检查用户是否有未完成的订单等
            
            int result = userMapper.deleteById(id);
            if (result <= 0) {
                throw new BusinessException("删除用户失败");
            }
            
            log.info("用户删除成功，用户ID: {}", id);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除用户失败，用户ID: {}", id, e);
            throw new BusinessException("删除用户失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Integer id, Integer status) {
        log.info("更新用户状态，用户ID: {}, 状态: {}", id, status);
        
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("用户状态值不正确");
        }
        
        try {
            // 检查用户是否存在
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 更新状态
            user.setStatus(status);
            int result = userMapper.updateById(user);
            if (result <= 0) {
                throw new BusinessException("更新用户状态失败");
            }
            
            log.info("用户状态更新成功，用户ID: {}, 新状态: {}", id, status);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户状态失败，用户ID: {}", id, e);
            throw new BusinessException("更新用户状态失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Integer> ids, Integer status) {
        log.info("批量更新用户状态，用户IDs: {}, 状态: {}", ids, status);
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("用户ID列表不能为空");
        }
        
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("用户状态值不正确");
        }
        
        try {
            // 批量更新
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(User::getUserId, ids);
            
            User updateUser = new User();
            updateUser.setStatus(status);
            
            int result = userMapper.update(updateUser, queryWrapper);
            if (result <= 0) {
                throw new BusinessException("批量更新用户状态失败");
            }
            
            log.info("批量更新用户状态成功，更新数量: {}", result);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量更新用户状态失败，用户IDs: {}", ids, e);
            throw new BusinessException("批量更新用户状态失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Integer id, String newPassword) {
        log.info("重置用户密码，用户ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        
        if (!StringUtils.hasText(newPassword)) {
            throw new BusinessException("新密码不能为空");
        }
        
        if (!PasswordUtil.isValidPassword(newPassword)) {
            throw new BusinessException("密码强度不够，密码必须包含字母和数字，长度6-20位");
        }
        
        try {
            // 检查用户是否存在
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 更新密码
            user.setPassword(PasswordUtil.encode(newPassword));
            int result = userMapper.updateById(user);
            if (result <= 0) {
                throw new BusinessException("重置密码失败");
            }
            
            log.info("用户密码重置成功，用户ID: {}", id);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("重置用户密码失败，用户ID: {}", id, e);
            throw new BusinessException("重置用户密码失败：" + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> getUserStatistics() {
        log.info("获取用户统计信息");
        
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总用户数
            Long totalUsers = userMapper.selectCount(null);
            statistics.put("totalUsers", totalUsers);
            
            // 正常用户数
            LambdaQueryWrapper<User> activeWrapper = new LambdaQueryWrapper<>();
            activeWrapper.eq(User::getStatus, 1);
            Long activeUsers = userMapper.selectCount(activeWrapper);
            statistics.put("activeUsers", activeUsers);
            
            // 禁用用户数
            LambdaQueryWrapper<User> disabledWrapper = new LambdaQueryWrapper<>();
            disabledWrapper.eq(User::getStatus, 0);
            Long disabledUsers = userMapper.selectCount(disabledWrapper);
            statistics.put("disabledUsers", disabledUsers);
            
            // 今日新增用户数
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LambdaQueryWrapper<User> todayWrapper = new LambdaQueryWrapper<>();
            todayWrapper.ge(User::getRegisterTime, today);
            Long todayNewUsers = userMapper.selectCount(todayWrapper);
            statistics.put("todayNewUsers", todayNewUsers);
            
            // 本月新增用户数
            LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LambdaQueryWrapper<User> monthWrapper = new LambdaQueryWrapper<>();
            monthWrapper.ge(User::getRegisterTime, thisMonth);
            Long monthNewUsers = userMapper.selectCount(monthWrapper);
            statistics.put("monthNewUsers", monthNewUsers);
            
            log.info("用户统计信息获取成功: {}", statistics);
            return statistics;
            
        } catch (Exception e) {
            log.error("获取用户统计信息失败", e);
            throw new BusinessException("获取用户统计信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 转换User实体为UserResponse
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setOpenid(user.getOpenid());
        response.setUnionid(user.getUnionid());
        response.setNickname(user.getNickname());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setGender(user.getGender());
        response.setGenderName(getGenderName(user.getGender()));
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setBirthday(user.getBirthday());
        response.setRegisterTime(user.getRegisterTime());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setStatus(user.getStatus());
        response.setStatusName(getStatusName(user.getStatus()));
        
        // 这里可以添加统计信息查询，如订单数量、消费金额等
        // 为了性能考虑，可以在需要时单独查询
        response.setTotalOrders(0);
        response.setTotalConsumption(0.0);
        
        return response;
    }
    
    /**
     * 转换UserRequest为User实体
     */
    private User convertToUserEntity(UserRequest request) {
        User user = new User();
        user.setOpenid(request.getOpenid());
        user.setUnionid(request.getUnionid());
        user.setNickname(request.getNickname());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setBirthday(request.getBirthday());
        
        // 如果有密码，进行加密
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(PasswordUtil.encode(request.getPassword()));
        }
        
        return user;
    }
    
    /**
     * 验证用户请求
     */
    private void validateUserRequest(UserRequest request, boolean isCreate) {
        if (!StringUtils.hasText(request.getNickname())) {
            throw new BusinessException("用户昵称不能为空");
        }
        
        if (isCreate && !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        
        if (StringUtils.hasText(request.getPassword()) && !PasswordUtil.isValidPassword(request.getPassword())) {
            throw new BusinessException("密码强度不够，密码必须包含字母和数字，长度6-20位");
        }
        
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }
        
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new BusinessException("邮箱格式不正确");
        }
    }
    
    /**
     * 检查用户是否已存在（创建时）
     */
    private void checkUserExistsForCreate(UserRequest request) {
        if (existsByNickname(request.getNickname())) {
            throw new BusinessException("昵称已存在");
        }
        
        if (StringUtils.hasText(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }
        
        if (StringUtils.hasText(request.getPhone()) && existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已被注册");
        }
    }
    
    /**
     * 检查用户是否已存在（更新时，排除自己）
     */
    private void checkUserExistsForUpdate(Integer userId, UserRequest request) {
        // 检查昵称
        LambdaQueryWrapper<User> nicknameWrapper = new LambdaQueryWrapper<>();
        nicknameWrapper.eq(User::getNickname, request.getNickname())
                      .ne(User::getUserId, userId);
        if (userMapper.selectCount(nicknameWrapper) > 0) {
            throw new BusinessException("昵称已存在");
        }
        
        // 检查邮箱
        if (StringUtils.hasText(request.getEmail())) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, request.getEmail())
                       .ne(User::getUserId, userId);
            if (userMapper.selectCount(emailWrapper) > 0) {
                throw new BusinessException("邮箱已被注册");
            }
        }
        
        // 检查手机号
        if (StringUtils.hasText(request.getPhone())) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, request.getPhone())
                       .ne(User::getUserId, userId);
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BusinessException("手机号已被注册");
            }
        }
    }
    
    /**
     * 获取性别名称
     */
    private String getGenderName(Integer gender) {
        if (gender == null) return "未知";
        switch (gender) {
            case 1: return "男";
            case 2: return "女";
            default: return "未知";
        }
    }
    
    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "禁用";
            case 1: return "正常";
            default: return "未知";
        }
    }
}