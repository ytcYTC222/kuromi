package com.mall.admin.service;

import com.mall.admin.entity.UserAddress;
import com.mall.admin.dto.request.UserAddressRequest;
import com.mall.admin.dto.response.UserAddressResponse;
import com.mall.admin.common.PageResult;

import java.util.List;

/**
 * 用户地址服务接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public interface IUserAddressService {
    
    /**
     * 获取用户地址列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID
     * @return 地址列表
     */
    PageResult<UserAddressResponse> getAddressList(Integer page, Integer size, Integer userId);
    
    /**
     * 根据ID获取地址详情
     *
     * @param id 地址ID
     * @return 地址详情
     */
    UserAddressResponse getAddressById(Integer id);
    
    /**
     * 创建用户地址
     *
     * @param request 地址创建请求
     */
    void createAddress(UserAddressRequest request);
    
    /**
     * 更新用户地址
     *
     * @param id 地址ID
     * @param request 地址更新请求
     */
    void updateAddress(Integer id, UserAddressRequest request);
    
    /**
     * 删除用户地址
     *
     * @param id 地址ID
     */
    void deleteAddress(Integer id);
    
    /**
     * 根据用户ID获取地址列表
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    List<UserAddressResponse> getAddressByUserId(Integer userId);
    
    /**
     * 获取用户默认地址
     *
     * @param userId 用户ID
     * @return 默认地址
     */
    UserAddressResponse getDefaultAddressByUserId(Integer userId);
    
    /**
     * 设置默认地址
     *
     * @param userId 用户ID
     * @param addressId 地址ID
     */
    void setDefaultAddress(Integer userId, Integer addressId);
    
    /**
     * 清除用户所有默认地址
     *
     * @param userId 用户ID
     */
    void clearDefaultByUserId(Integer userId);
    
    /**
     * 统计用户地址数量
     *
     * @param userId 用户ID
     * @return 地址数量
     */
    Long countByUserId(Integer userId);
    
    /**
     * 根据收货人姓名搜索地址
     *
     * @param userId 用户ID
     * @param receiverName 收货人姓名
     * @return 地址列表
     */
    List<UserAddressResponse> searchByReceiverName(Integer userId, String receiverName);
    
    /**
     * 根据关键词搜索地址
     *
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 地址列表
     */
    List<UserAddressResponse> searchByKeyword(Integer userId, String keyword);
    
    /**
     * 批量删除地址
     *
     * @param ids 地址ID列表
     */
    void batchDeleteAddresses(List<Integer> ids);
    
    /**
     * 检查地址是否属于指定用户
     *
     * @param addressId 地址ID
     * @param userId 用户ID
     * @return 属于返回true，否则返回false
     */
    boolean checkAddressOwnership(Integer addressId, Integer userId);
    
    /**
     * 根据省市区获取地址列表
     *
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @return 地址列表
     */
    List<UserAddressResponse> getAddressByRegion(String province, String city, String district);
    
    /**
     * 验证地址信息完整性
     *
     * @param request 地址请求
     * @return 验证结果
     */
    boolean validateAddress(UserAddressRequest request);
    
    /**
     * 获取用户最近使用的地址
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近使用的地址列表
     */
    List<UserAddressResponse> getRecentUsedAddresses(Integer userId, Integer limit);
}