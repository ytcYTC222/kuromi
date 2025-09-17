package com.mall.admin.service;

import com.mall.admin.entity.Order;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.request.OrderRequest;
import com.mall.admin.dto.response.OrderResponse;

import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public interface IOrderService {
    
    /**
     * 获取订单列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param status 订单状态
     * @param userId 用户ID
     * @return 订单列表
     */
    PageResult<OrderResponse> getOrderList(Integer page, Integer size, String keyword, Integer status, Integer userId);
    
    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    OrderResponse getOrderById(Integer id);
    
    /**
     * 根据订单号获取订单
     *
     * @param orderNumber 订单号
     * @return 订单信息
     */
    OrderResponse getOrderByNumber(String orderNumber);
    
    /**
     * 创建订单
     *
     * @param request 订单创建请求
     * @return 订单号
     */
    String createOrder(OrderRequest request);
    
    /**
     * 更新订单
     *
     * @param id 订单ID
     * @param request 订单更新请求
     */
    void updateOrder(Integer id, OrderRequest request);
    
    /**
     * 取消订单
     *
     * @param id 订单ID
     * @param reason 取消原因
     */
    void cancelOrder(Integer id, String reason);
    
    /**
     * 确认订单
     *
     * @param id 订单ID
     */
    void confirmOrder(Integer id);
    
    /**
     * 发货
     *
     * @param id 订单ID
     * @param trackingNumber 快递单号
     * @param shippingCompany 快递公司
     */
    void shipOrder(Integer id, String trackingNumber, String shippingCompany);
    
    /**
     * 完成订单
     *
     * @param id 订单ID
     */
    void completeOrder(Integer id);
    
    /**
     * 更新订单状态
     *
     * @param id 订单ID
     * @param status 新状态
     */
    void updateOrderStatus(Integer id, Integer status);
    
    /**
     * 根据用户ID获取订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderResponse> getOrdersByUserId(Integer userId);
    
    /**
     * 根据订单状态获取订单列表
     *
     * @param status 订单状态
     * @return 订单列表
     */
    List<OrderResponse> getOrdersByStatus(Integer status);
    
    /**
     * 获取订单统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getOrderStatistics();
    
    /**
     * 获取销售统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 销售统计
     */
    Map<String, Object> getSalesStatistics(String startDate, String endDate);
    
    /**
     * 批量更新订单状态
     *
     * @param ids 订单ID列表
     * @param status 新状态
     */
    void batchUpdateStatus(List<Integer> ids, Integer status);
}