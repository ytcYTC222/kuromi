package com.mall.admin.controller;

import com.mall.admin.common.ApiResponse;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.OrderRequest;
import com.mall.admin.dto.response.OrderResponse;
import com.mall.admin.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "订单相关的API接口")
public class OrderController {
    
    private final IOrderService orderService;
    
    /**
     * 获取订单列表
     */
    @Operation(summary = "获取订单列表", description = "分页查询订单列表，支持多条件筛选")
    @GetMapping
    public ApiResponse<PageResult<OrderResponse>> getOrderList(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "用户ID") @RequestParam(required = false) Integer userId) {
        try {
            PageResult<OrderResponse> result = orderService.getOrderList(page, size, keyword, status, userId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return ApiResponse.error("获取订单列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取订单详情
     */
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(
            @Parameter(description = "订单ID", required = true) @PathVariable Integer id) {
        try {
            OrderResponse order = orderService.getOrderById(id);
            return ApiResponse.success(order);
        } catch (Exception e) {
            log.error("获取订单详情失败，订单ID: {}", id, e);
            return ApiResponse.error("获取订单详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据订单号获取订单
     */
    @Operation(summary = "根据订单号获取订单", description = "根据订单号获取订单信息")
    @GetMapping("/number/{orderNumber}")
    public ApiResponse<OrderResponse> getOrderByNumber(
            @Parameter(description = "订单号", required = true) @PathVariable String orderNumber) {
        try {
            OrderResponse order = orderService.getOrderByNumber(orderNumber);
            return ApiResponse.success(order);
        } catch (Exception e) {
            log.error("根据订单号获取订单失败，订单号: {}", orderNumber, e);
            return ApiResponse.error("获取订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建订单
     */
    @Operation(summary = "创建订单", description = "创建新的订单")
    @PostMapping
    public ApiResponse<String> createOrder(
            @Parameter(description = "订单创建请求", required = true) @Valid @RequestBody OrderRequest request) {
        try {
            String orderNumber = orderService.createOrder(request);
            return ApiResponse.success(orderNumber, "订单创建成功");
        } catch (Exception e) {
            log.error("创建订单失败，用户ID: {}", request.getUserId(), e);
            return ApiResponse.error("创建订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新订单
     */
    @Operation(summary = "更新订单", description = "根据订单ID更新订单信息")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Integer id,
            @Parameter(description = "订单更新请求", required = true) @Valid @RequestBody OrderRequest request) {
        try {
            orderService.updateOrder(id, request);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("更新订单失败，订单ID: {}", id, e);
            return ApiResponse.error("更新订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消订单
     */
    @Operation(summary = "取消订单", description = "取消指定订单")
    @PatchMapping("/{id}/cancel")
    public ApiResponse<Void> cancelOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Integer id,
            @Parameter(description = "取消原因") @RequestBody Map<String, String> request) {
        try {
            String reason = request.getOrDefault("reason", "用户取消");
            orderService.cancelOrder(id, reason);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("取消订单失败，订单ID: {}", id, e);
            return ApiResponse.error("取消订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 确认订单
     */
    @Operation(summary = "确认订单", description = "确认订单支付")
    @PatchMapping("/{id}/confirm")
    public ApiResponse<Void> confirmOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Integer id) {
        try {
            orderService.confirmOrder(id);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("确认订单失败，订单ID: {}", id, e);
            return ApiResponse.error("确认订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 发货
     */
    @Operation(summary = "订单发货", description = "处理订单发货")
    @PatchMapping("/{id}/ship")
    public ApiResponse<Void> shipOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Integer id,
            @Parameter(description = "发货信息", required = true) @RequestBody Map<String, String> request) {
        try {
            String trackingNumber = request.get("trackingNumber");
            String shippingCompany = request.get("shippingCompany");
            orderService.shipOrder(id, trackingNumber, shippingCompany);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("订单发货失败，订单ID: {}", id, e);
            return ApiResponse.error("订单发货失败：" + e.getMessage());
        }
    }
    
    /**
     * 完成订单
     */
    @Operation(summary = "完成订单", description = "标记订单为已完成")
    @PatchMapping("/{id}/complete")
    public ApiResponse<Void> completeOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Integer id) {
        try {
            orderService.completeOrder(id);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("完成订单失败，订单ID: {}", id, e);
            return ApiResponse.error("完成订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新订单状态
     */
    @Operation(summary = "更新订单状态", description = "更新订单状态")
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateOrderStatus(
            @Parameter(description = "订单ID", required = true) @PathVariable Integer id,
            @Parameter(description = "状态更新请求", required = true) @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            if (status == null) {
                return ApiResponse.error("状态参数不能为空");
            }
            orderService.updateOrderStatus(id, status);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("更新订单状态失败，订单ID: {}", id, e);
            return ApiResponse.error("更新订单状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID获取订单列表
     */
    @Operation(summary = "获取用户订单", description = "根据用户ID获取订单列表")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderResponse>> getOrdersByUserId(
            @Parameter(description = "用户ID", required = true) @PathVariable Integer userId) {
        try {
            List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            log.error("获取用户订单失败，用户ID: {}", userId, e);
            return ApiResponse.error("获取用户订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据状态获取订单列表
     */
    @Operation(summary = "根据状态获取订单", description = "根据订单状态获取订单列表")
    @GetMapping("/status/{status}")
    public ApiResponse<List<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "订单状态", required = true) @PathVariable Integer status) {
        try {
            List<OrderResponse> orders = orderService.getOrdersByStatus(status);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            log.error("根据状态获取订单失败，状态: {}", status, e);
            return ApiResponse.error("获取订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取订单统计信息
     */
    @Operation(summary = "获取订单统计", description = "获取订单统计信息")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getOrderStatistics() {
        try {
            Map<String, Object> statistics = orderService.getOrderStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            log.error("获取订单统计失败", e);
            return ApiResponse.error("获取订单统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取销售统计
     */
    @Operation(summary = "获取销售统计", description = "获取指定时间段的销售统计")
    @GetMapping("/sales-statistics")
    public ApiResponse<Map<String, Object>> getSalesStatistics(
            @Parameter(description = "开始日期", example = "2024-01-01") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期", example = "2024-12-31") @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> statistics = orderService.getSalesStatistics(startDate, endDate);
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            log.error("获取销售统计失败", e);
            return ApiResponse.error("获取销售统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量更新订单状态
     */
    @Operation(summary = "批量更新订单状态", description = "批量更新多个订单的状态")
    @PatchMapping("/batch/status")
    public ApiResponse<Void> batchUpdateStatus(
            @Parameter(description = "批量更新请求", required = true) @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) request.get("ids");
            Integer status = (Integer) request.get("status");
            
            if (ids == null || ids.isEmpty()) {
                return ApiResponse.error("订单ID列表不能为空");
            }
            if (status == null) {
                return ApiResponse.error("状态不能为空");
            }
            
            orderService.batchUpdateStatus(ids, status);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量更新订单状态失败", e);
            return ApiResponse.error("批量更新订单状态失败：" + e.getMessage());
        }
    }
}