package com.mall.admin.controller;

import com.mall.admin.common.ApiResponse;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.BannerRequest;
import com.mall.admin.dto.response.BannerResponse;
import com.mall.admin.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 轮播图管理控制器
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Slf4j
@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * 获取轮播图列表
     */
    @GetMapping
    public ApiResponse<PageResult<BannerResponse>> getBannerList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer linkType,
            @RequestParam(required = false) Integer isActive) {
        
        log.info("获取轮播图列表接口调用，参数：page={}, size={}, title={}, linkType={}, isActive={}", 
                page, size, title, linkType, isActive);
        
        PageResult<BannerResponse> result = bannerService.getBannerList(page, size, title, linkType, isActive);
        return ApiResponse.success(result);
    }

    /**
     * 根据ID获取轮播图详情
     */
    @GetMapping("/{id}")
    public ApiResponse<BannerResponse> getBannerById(@PathVariable Integer id) {
        log.info("获取轮播图详情接口调用，bannerId: {}", id);
        
        BannerResponse result = bannerService.getBannerById(id);
        return ApiResponse.success(result);
    }

    /**
     * 创建轮播图
     */
    @PostMapping
    public ApiResponse<Integer> createBanner(@Validated @RequestBody BannerRequest request) {
        log.info("创建轮播图接口调用，参数：{}", request);
        
        Integer bannerId = bannerService.createBanner(request);
        return ApiResponse.success(bannerId);
    }

    /**
     * 更新轮播图
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> updateBanner(@PathVariable Integer id, 
                                         @Validated @RequestBody BannerRequest request) {
        log.info("更新轮播图接口调用，bannerId: {}, 参数：{}", id, request);
        
        bannerService.updateBanner(id, request);
        return ApiResponse.success();
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBanner(@PathVariable Integer id) {
        log.info("删除轮播图接口调用，bannerId: {}", id);
        
        bannerService.deleteBanner(id);
        return ApiResponse.success();
    }

    /**
     * 更新轮播图状态
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateBannerStatus(@PathVariable Integer id,
                                               @RequestBody Map<String, Integer> request) {
        Integer isActive = request.get("isActive");
        log.info("更新轮播图状态接口调用，bannerId: {}, isActive: {}", id, isActive);
        
        bannerService.updateBannerStatus(id, isActive);
        return ApiResponse.success();
    }
}