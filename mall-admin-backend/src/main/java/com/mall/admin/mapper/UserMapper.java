package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问层接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 获取用户总数
     */
    @Select("SELECT COUNT(*) FROM users")
    Long selectTotalUsers();
    
    /**
     * 根据昵称查找用户
     */
    @Select("SELECT * FROM users WHERE nickname = #{nickname}")
    User findByNickname(String nickname);
    
    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);
    
    /**
     * 根据手机号查找用户
     */
    @Select("SELECT * FROM users WHERE phone = #{phone}")
    User findByPhone(String phone);
    
    /**
     * 检查昵称是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE nickname = #{nickname}")
    boolean existsByNickname(String nickname);
    
    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email}")
    boolean existsByEmail(String email);
    
    /**
     * 检查手机号是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE phone = #{phone}")
    boolean existsByPhone(String phone);
}