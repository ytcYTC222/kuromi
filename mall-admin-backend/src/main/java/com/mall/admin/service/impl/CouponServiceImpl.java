package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.CouponRequest;
import com.mall.admin.dto.response.CouponResponse;
import com.mall.admin.entity.Coupon;
import com.mall.admin.mapper.CouponMapper;
import com.mall.admin.service.ICouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 优惠卷服务实现类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {
    
    // 优惠卷类型映射
    private static final Map<Integer, String> COUPON_TYPE_MAP = new HashMap<>();
    
    // 状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    
    // 优惠卷状态映射
    private static final Map<Integer, String> COUPON_STATUS_MAP = new HashMap<>();
    
    static {
        COUPON_TYPE_MAP.put(1, "满减");
        COUPON_TYPE_MAP.put(2, "折扣");
        COUPON_TYPE_MAP.put(3, "免邮");
        
        STATUS_MAP.put(0, "禁用");
        STATUS_MAP.put(1, "启用");
        
        COUPON_STATUS_MAP.put(0, "未开始");
        COUPON_STATUS_MAP.put(1, "进行中");
        COUPON_STATUS_MAP.put(2, "已结束");
        COUPON_STATUS_MAP.put(3, "已禁用");
    }
    
    @Override
    public PageResult<CouponResponse> getCouponList(Integer page, Integer size, String keyword, Integer couponType, Integer status) {
        log.info("查询优惠卷列表，页码: {}, 每页大小: {}, 关键词: {}, 类型: {}, 状态: {}", page, size, keyword, couponType, status);
        
        Page<Coupon> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Coupon> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Coupon::getCouponName, keyword);
        }
        if (couponType != null) {
            queryWrapper.eq(Coupon::getCouponType, couponType);
        }
        if (status != null) {
            queryWrapper.eq(Coupon::getStatus, status);
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Coupon::getCreateTime);
        
        Page<Coupon> couponPage = this.page(pageObj, queryWrapper);
        
        // 转换为响应DTO
        List<CouponResponse> couponResponses = couponPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageResult<>(couponResponses, couponPage.getTotal(), couponPage.getCurrent(), couponPage.getSize());
    }
    
    @Override
    public CouponResponse getCouponById(Integer couponId) {
        log.info("查询优惠卷详情，ID: {}", couponId);
        
        if (couponId == null || couponId <= 0) {
            throw new IllegalArgumentException("优惠卷ID不能为空");
        }
        
        Coupon coupon = this.getById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠卷不存在");
        }
        
        return convertToResponse(coupon);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CouponResponse createCoupon(CouponRequest request) {
        log.info("创建优惠卷，名称: {}", request.getCouponName());
        
        // 参数验证
        validateCouponRequest(request);
        
        // 检查时间合理性
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
        
        // 检查优惠卷名称是否重复
        LambdaQueryWrapper<Coupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Coupon::getCouponName, request.getCouponName());
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("优惠卷名称已存在");
        }
        
        // 创建优惠卷实体
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(request, coupon);
        coupon.setUsedQuantity(0);
        coupon.setCreateTime(LocalDateTime.now());
        
        // 保存到数据库
        this.save(coupon);
        
        log.info("优惠卷创建成功，ID: {}", coupon.getCouponId());
        return convertToResponse(coupon);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CouponResponse updateCoupon(Integer couponId, CouponRequest request) {
        log.info("更新优惠卷，ID: {}", couponId);
        
        if (couponId == null || couponId <= 0) {
            throw new IllegalArgumentException("优惠卷ID不能为空");
        }
        
        // 参数验证
        validateCouponRequest(request);
        
        // 检查时间合理性
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
        
        // 检查优惠卷是否存在
        Coupon existingCoupon = this.getById(couponId);
        if (existingCoupon == null) {
            throw new RuntimeException("优惠卷不存在");
        }
        
        // 检查优惠卷名称是否重复（排除自己）
        LambdaQueryWrapper<Coupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Coupon::getCouponName, request.getCouponName())
                .ne(Coupon::getCouponId, couponId);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("优惠卷名称已存在");
        }
        
        // 更新优惠卷信息
        BeanUtils.copyProperties(request, existingCoupon);
        this.updateById(existingCoupon);
        
        log.info("优惠卷更新成功，ID: {}", couponId);
        return convertToResponse(existingCoupon);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCoupon(Integer couponId) {
        log.info("删除优惠卷，ID: {}", couponId);
        
        if (couponId == null || couponId <= 0) {
            throw new IllegalArgumentException("优惠卷ID不能为空");
        }
        
        // 检查优惠卷是否存在
        Coupon coupon = this.getById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠卷不存在");
        }
        
        // 检查优惠卷是否已被使用
        if (coupon.getUsedQuantity() > 0) {
            throw new RuntimeException("该优惠卷已有用户使用，无法删除");
        }
        
        this.removeById(couponId);
        log.info("优惠卷删除成功，ID: {}", couponId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCoupons(List<Integer> couponIds) {
        log.info("批量删除优惠卷，数量: {}", couponIds.size());
        
        if (couponIds == null || couponIds.isEmpty()) {
            throw new IllegalArgumentException("优惠卷ID列表不能为空");
        }
        
        // 检查所有优惠卷是否都可以删除
        for (Integer couponId : couponIds) {
            Coupon coupon = this.getById(couponId);
            if (coupon == null) {
                throw new RuntimeException("优惠卷不存在，ID: " + couponId);
            }
            if (coupon.getUsedQuantity() > 0) {
                throw new RuntimeException("优惠卷已有用户使用，无法删除，名称: " + coupon.getCouponName());
            }
        }
        
        this.removeByIds(couponIds);
        log.info("批量删除优惠卷成功，数量: {}", couponIds.size());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCouponStatus(Integer couponId, Integer status) {
        log.info("更新优惠卷状态，ID: {}, 状态: {}", couponId, status);
        
        if (couponId == null || couponId <= 0) {
            throw new IllegalArgumentException("优惠卷ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("状态值无效");
        }
        
        // 检查优惠卷是否存在
        Coupon coupon = this.getById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠卷不存在");
        }
        
        coupon.setStatus(status);
        this.updateById(coupon);
        
        log.info("优惠卷状态更新成功，ID: {}", couponId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCouponStatus(List<Integer> couponIds, Integer status) {
        log.info("批量更新优惠卷状态，数量: {}, 状态: {}", couponIds.size(), status);
        
        if (couponIds == null || couponIds.isEmpty()) {
            throw new IllegalArgumentException("优惠卷ID列表不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("状态值无效");
        }
        
        for (Integer couponId : couponIds) {
            updateCouponStatus(couponId, status);
        }
        
        log.info("批量更新优惠卷状态成功，数量: {}", couponIds.size());
    }
    
    @Override
    public List<CouponResponse> getActiveCoupons() {
        log.info("查询所有启用的优惠卷");
        
        LambdaQueryWrapper<Coupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Coupon::getStatus, 1)
                .orderByDesc(Coupon::getCreateTime);
        
        List<Coupon> coupons = this.list(queryWrapper);
        return coupons.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为响应DTO
     */
    private CouponResponse convertToResponse(Coupon coupon) {
        CouponResponse response = new CouponResponse();
        BeanUtils.copyProperties(coupon, response);
        
        // 设置类型名称
        response.setCouponTypeName(COUPON_TYPE_MAP.get(coupon.getCouponType()));
        
        // 设置状态名称
        response.setStatusName(STATUS_MAP.get(coupon.getStatus()));
        
        // 计算剩余数量
        response.setRemainingQuantity(coupon.getTotalQuantity() - coupon.getUsedQuantity());
        
        // 计算优惠卷状态
        LocalDateTime now = LocalDateTime.now();
        Integer couponStatus;
        if (coupon.getStatus() == 0) {
            couponStatus = 3; // 已禁用
        } else if (now.isBefore(coupon.getStartTime())) {
            couponStatus = 0; // 未开始
        } else if (now.isAfter(coupon.getEndTime())) {
            couponStatus = 2; // 已结束
        } else {
            couponStatus = 1; // 进行中
        }
        response.setCouponStatus(couponStatus);
        response.setCouponStatusName(COUPON_STATUS_MAP.get(couponStatus));
        
        return response;
    }
    
    /**
     * 验证优惠卷请求参数
     */
    private void validateCouponRequest(CouponRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        
        // 根据优惠卷类型验证特定参数
        if (request.getCouponType() == 2) { // 折扣优惠卷
            if (request.getDiscountValue().compareTo(new java.math.BigDecimal("1")) >= 0) {
                throw new IllegalArgumentException("折扣优惠卷的优惠值必须小于1");
            }
        }
        
        if (request.getCouponType() == 1 && request.getMinAmount() != null) { // 满减优惠卷
            if (request.getDiscountValue().compareTo(request.getMinAmount()) >= 0) {
                throw new IllegalArgumentException("满减优惠卷的优惠值不能大于等于最低消费金额");
            }
        }
    }
}