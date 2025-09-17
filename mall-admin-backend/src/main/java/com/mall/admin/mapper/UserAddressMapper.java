package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户地址数据访问层接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
    
    /**
     * 根据用户ID获取地址列表
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} ORDER BY is_default DESC, create_time DESC")
    List<UserAddress> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 获取用户默认地址
     *
     * @param userId 用户ID
     * @return 默认地址
     */
    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} AND is_default = 1")
    UserAddress selectDefaultByUserId(@Param("userId") Integer userId);
    
    /**
     * 清除用户所有默认地址
     *
     * @param userId 用户ID
     * @return 更新行数
     */
    @Update("UPDATE user_addresses SET is_default = 0, update_time = NOW() WHERE user_id = #{userId}")
    int clearDefaultByUserId(@Param("userId") Integer userId);
    
    /**
     * 设置默认地址
     *
     * @param addressId 地址ID
     * @param userId 用户ID
     * @return 更新行数
     */
    @Update("UPDATE user_addresses SET is_default = 1, update_time = NOW() WHERE address_id = #{addressId} AND user_id = #{userId}")
    int setDefaultAddress(@Param("addressId") Integer addressId, @Param("userId") Integer userId);
    
    /**
     * 统计用户地址数量
     *
     * @param userId 用户ID
     * @return 地址数量
     */
    @Select("SELECT COUNT(*) FROM user_addresses WHERE user_id = #{userId}")
    Long countByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据收货人姓名模糊查询地址
     *
     * @param userId 用户ID
     * @param receiverName 收货人姓名
     * @return 地址列表
     */
    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} AND receiver_name LIKE CONCAT('%', #{receiverName}, '%') ORDER BY create_time DESC")
    List<UserAddress> searchByReceiverName(@Param("userId") Integer userId, @Param("receiverName") String receiverName);
    
    /**
     * 根据地址关键词模糊查询
     *
     * @param userId 用户ID
     * @param keyword 关键词
     * @return 地址列表
     */
    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} AND (province LIKE CONCAT('%', #{keyword}, '%') OR city LIKE CONCAT('%', #{keyword}, '%') OR district LIKE CONCAT('%', #{keyword}, '%') OR detail_address LIKE CONCAT('%', #{keyword}, '%')) ORDER BY create_time DESC")
    List<UserAddress> searchByKeyword(@Param("userId") Integer userId, @Param("keyword") String keyword);
}