package com.mall.admin.controller;

import com.mall.admin.common.ApiResponse;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.PromotionRequest;
import com.mall.admin.dto.response.PromotionResponse;
import com.mall.admin.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 促销活动管理控制器
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Slf4j
@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Validated
@Tag(name = "促销活动管理", description = "促销活动管理相关接口")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    @Operation(summary = "获取促销活动列表", description = "分页获取促销活动列表，支持按名称、类型、状态筛选")
    public ApiResponse<PageResult<PromotionResponse>> getPromotionList(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "促销活动名称，支持模糊查询") @RequestParam(required = false) String promotionName,
            @Parameter(description = "促销类型：1商品促销，2分类促销，3全场促销") @RequestParam(required = false) Integer promotionType,
            @Parameter(description = "是否启用：0禁用，1启用") @RequestParam(required = false) Integer isActive) {
        
        log.info("获取促销活动列表请求，page: {}, size: {}, promotionName: {}, promotionType: {}, isActive: {}", 
                page, size, promotionName, promotionType, isActive);

        PageResult<PromotionResponse> result = promotionService.getPromotionList(page, size, promotionName, promotionType, isActive);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取促销活动详情", description = "根据促销活动ID获取详细信息")
    public ApiResponse<PromotionResponse> getPromotionById(
            @Parameter(description = "促销活动ID") @PathVariable Integer id) {
        
        log.info("获取促销活动详情请求，promotionId: {}", id);

        PromotionResponse result = promotionService.getPromotionById(id);
        return ApiResponse.success(result);
    }

    @PostMapping
    @Operation(summary = "创建促销活动", description = "创建新的促销活动")
    public ApiResponse<Integer> createPromotion(@Valid @RequestBody PromotionRequest request) {
        log.info("创建促销活动请求，request: {}", request);

        Integer promotionId = promotionService.createPromotion(request);
        return ApiResponse.success(promotionId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新促销活动", description = "更新指定促销活动的信息")
    public ApiResponse<Void> updatePromotion(
            @Parameter(description = "促销活动ID") @PathVariable Integer id,
            @Valid @RequestBody PromotionRequest request) {
        
        log.info("更新促销活动请求，promotionId: {}, request: {}", id, request);

        promotionService.updatePromotion(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除促销活动", description = "删除指定的促销活动")
    public ApiResponse<Void> deletePromotion(
            @Parameter(description = "促销活动ID") @PathVariable Integer id) {
        
        log.info("删除促销活动请求，promotionId: {}", id);

        promotionService.deletePromotion(id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新促销活动状态", description = "更新促销活动的启用/禁用状态")
    public ApiResponse<Void> updatePromotionStatus(
            @Parameter(description = "促销活动ID") @PathVariable Integer id,
            @RequestBody Map<String, Integer> request) {
        
        Integer isActive = request.get("isActive");
        log.info("更新促销活动状态请求，promotionId: {}, isActive: {}", id, isActive);

        promotionService.updatePromotionStatus(id, isActive);
        return ApiResponse.success();
    }
}