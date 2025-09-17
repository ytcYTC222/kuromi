package com.mall.admin.controller;

import com.mall.admin.common.ApiResponse;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.CouponRequest;
import com.mall.admin.dto.response.CouponResponse;
import com.mall.admin.service.ICouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 优惠卷管理控制器
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Slf4j
@Tag(name = "优惠卷管理", description = "优惠卷相关接口")
@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    
    @Autowired
    private ICouponService couponService;
    
    /**
     * 分页查询优惠卷列表
     */
    @Operation(summary = "分页查询优惠卷列表", description = "支持关键词搜索、类型筛选、状态筛选")
    @GetMapping
    public ApiResponse<PageResult<CouponResponse>> getCouponList(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "优惠卷类型：1满减，2折扣，3免邮") @RequestParam(required = false) Integer couponType,
            @Parameter(description = "状态：0禁用，1启用") @RequestParam(required = false) Integer status) {
        
        try {
            PageResult<CouponResponse> result = couponService.getCouponList(page, size, keyword, couponType, status);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("查询优惠卷列表失败", e);
            return ApiResponse.error("查询优惠卷列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询优惠卷详情
     */
    @Operation(summary = "查询优惠卷详情", description = "根据优惠卷ID查询详细信息")
    @GetMapping("/{id}")
    public ApiResponse<CouponResponse> getCouponById(
            @Parameter(description = "优惠卷ID") @PathVariable Integer id) {
        
        try {
            CouponResponse result = couponService.getCouponById(id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("查询优惠卷详情失败，ID: {}", id, e);
            return ApiResponse.error("查询优惠卷详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建优惠卷
     */
    @Operation(summary = "创建优惠卷", description = "创建新的优惠卷")
    @PostMapping
    public ApiResponse<CouponResponse> createCoupon(
            @Parameter(description = "优惠卷信息") @Valid @RequestBody CouponRequest request) {
        
        try {
            CouponResponse result = couponService.createCoupon(request);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("创建优惠卷失败", e);
            return ApiResponse.error("创建优惠卷失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新优惠卷
     */
    @Operation(summary = "更新优惠卷", description = "更新优惠卷信息")
    @PutMapping("/{id}")
    public ApiResponse<CouponResponse> updateCoupon(
            @Parameter(description = "优惠卷ID") @PathVariable Integer id,
            @Parameter(description = "优惠卷信息") @Valid @RequestBody CouponRequest request) {
        
        try {
            CouponResponse result = couponService.updateCoupon(id, request);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("更新优惠卷失败，ID: {}", id, e);
            return ApiResponse.error("更新优惠卷失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除优惠卷
     */
    @Operation(summary = "删除优惠卷", description = "删除指定优惠卷")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCoupon(
            @Parameter(description = "优惠卷ID") @PathVariable Integer id) {
        
        try {
            couponService.deleteCoupon(id);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("删除优惠卷失败，ID: {}", id, e);
            return ApiResponse.error("删除优惠卷失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除优惠卷
     */
    @Operation(summary = "批量删除优惠卷", description = "批量删除多个优惠卷")
    @DeleteMapping("/batch")
    public ApiResponse<Void> deleteCoupons(
            @Parameter(description = "优惠卷ID列表") @RequestBody List<Integer> couponIds) {
        
        try {
            couponService.deleteCoupons(couponIds);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量删除优惠卷失败", e);
            return ApiResponse.error("批量删除优惠卷失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新优惠卷状态
     */
    @Operation(summary = "更新优惠卷状态", description = "启用或禁用优惠卷")
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateCouponStatus(
            @Parameter(description = "优惠卷ID") @PathVariable Integer id,
            @Parameter(description = "状态信息") @RequestBody Map<String, Integer> request) {
        
        try {
            Integer status = request.get("status");
            couponService.updateCouponStatus(id, status);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("更新优惠卷状态失败，ID: {}", id, e);
            return ApiResponse.error("更新优惠卷状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量更新优惠卷状态
     */
    @Operation(summary = "批量更新优惠卷状态", description = "批量启用或禁用优惠卷")
    @PatchMapping("/batch/status")
    public ApiResponse<Void> updateCouponStatus(
            @Parameter(description = "批量状态更新请求") @RequestBody Map<String, Object> request) {
        
        try {
            @SuppressWarnings("unchecked")
            List<Integer> couponIds = (List<Integer>) request.get("couponIds");
            Integer status = (Integer) request.get("status");
            
            couponService.updateCouponStatus(couponIds, status);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量更新优惠卷状态失败", e);
            return ApiResponse.error("批量更新优惠卷状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有启用的优惠卷
     */
    @Operation(summary = "获取启用优惠卷", description = "获取所有启用状态的优惠卷")
    @GetMapping("/active")
    public ApiResponse<List<CouponResponse>> getActiveCoupons() {
        
        try {
            List<CouponResponse> result = couponService.getActiveCoupons();
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取启用优惠卷失败", e);
            return ApiResponse.error("获取启用优惠卷失败：" + e.getMessage());
        }
    }
}