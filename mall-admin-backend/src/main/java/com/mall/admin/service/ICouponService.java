package com.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.CouponRequest;
import com.mall.admin.dto.response.CouponResponse;
import com.mall.admin.entity.Coupon;

import java.util.List;

/**
 * 优惠卷服务接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public interface ICouponService extends IService<Coupon> {
    
    /**
     * 分页查询优惠卷列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param couponType 优惠卷类型
     * @param status 状态
     * @return 分页结果
     */
    PageResult<CouponResponse> getCouponList(Integer page, Integer size, String keyword, Integer couponType, Integer status);
    
    /**
     * 根据ID获取优惠卷详情
     *
     * @param couponId 优惠卷ID
     * @return 优惠卷详情
     */
    CouponResponse getCouponById(Integer couponId);
    
    /**
     * 创建优惠卷
     *
     * @param request 优惠卷信息
     * @return 优惠卷详情
     */
    CouponResponse createCoupon(CouponRequest request);
    
    /**
     * 更新优惠卷
     *
     * @param couponId 优惠卷ID
     * @param request 优惠卷信息
     * @return 优惠卷详情
     */
    CouponResponse updateCoupon(Integer couponId, CouponRequest request);
    
    /**
     * 删除优惠卷
     *
     * @param couponId 优惠卷ID
     */
    void deleteCoupon(Integer couponId);
    
    /**
     * 批量删除优惠卷
     *
     * @param couponIds 优惠卷ID列表
     */
    void deleteCoupons(List<Integer> couponIds);
    
    /**
     * 更新优惠卷状态
     *
     * @param couponId 优惠卷ID
     * @param status 状态
     */
    void updateCouponStatus(Integer couponId, Integer status);
    
    /**
     * 批量更新优惠卷状态
     *
     * @param couponIds 优惠卷ID列表
     * @param status 状态
     */
    void updateCouponStatus(List<Integer> couponIds, Integer status);
    
    /**
     * 获取所有启用的优惠卷
     *
     * @return 优惠卷列表
     */
    List<CouponResponse> getActiveCoupons();
}