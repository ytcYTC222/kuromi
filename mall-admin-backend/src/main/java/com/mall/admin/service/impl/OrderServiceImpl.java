package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.entity.Order;
import com.mall.admin.entity.OrderItem;
import com.mall.admin.mapper.OrderMapper;
import com.mall.admin.mapper.OrderItemMapper;
import com.mall.admin.service.IOrderService;
import com.mall.admin.dto.request.OrderRequest;
import com.mall.admin.dto.response.OrderResponse;
import com.mall.admin.dto.response.OrderItemResponse;
import com.mall.admin.dto.response.UserResponse;
import com.mall.admin.dto.response.UserAddressResponse;
import com.mall.admin.dto.response.CouponResponse;
import com.mall.admin.common.PageResult;
import com.mall.admin.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    
    @Override
    public PageResult<OrderResponse> getOrderList(Integer page, Integer size, String keyword, Integer status, Integer userId) {
        log.info("获取订单列表，页码: {}, 大小: {}, 关键词: {}, 状态: {}, 用户ID: {}", page, size, keyword, status, userId);
        
        // 构建查询条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        // 订单号或收货人姓名模糊查询
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> 
                wrapper.like(Order::getOrderNo, keyword)
                       .or()
                       .like(Order::getReceiverName, keyword)
                       .or()
                       .like(Order::getReceiverPhone, keyword)
            );
        }
        
        // 订单状态
        queryWrapper.eq(status != null, Order::getOrderStatus, status);
        
        // 用户ID
        queryWrapper.eq(userId != null, Order::getUserId, userId);
        
        // 排序
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        // 分页查询
        IPage<Order> pageInfo = new Page<>(page != null ? page : 1, size != null ? size : 10);
        IPage<Order> result = orderMapper.selectPage(pageInfo, queryWrapper);
        
        // 转换为响应对象
        List<OrderResponse> responseList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageResult<>(responseList, result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public OrderResponse getOrderById(Integer id) {
        log.info("根据ID获取订单详情，订单ID: {}", id);
        
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        return convertToResponse(order);
    }
    
    @Override
    public OrderResponse getOrderByNumber(String orderNumber) {
        log.info("根据订单号获取订单，订单号: {}", orderNumber);
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderNumber);
        Order order = orderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        return convertToResponse(order);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(OrderRequest request) {
        log.info("创建订单，用户ID: {}", request.getUserId());
        
        // 生成订单号
        String orderNo = generateOrderNumber();
        
        // 创建订单
        Order order = convertToEntity(request);
        order.setOrderNo(orderNo);
        order.setOrderStatus(1); // 待付款
        order.setPaymentStatus(0); // 未支付
        order.setCreateTime(LocalDateTime.now());
        
        int result = orderMapper.insert(order);
        if (result <= 0) {
            throw new BusinessException("订单创建失败");
        }
        
        // 创建订单商品项
        if (request.getOrderItems() != null && !request.getOrderItems().isEmpty()) {
            for (OrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
                OrderItem orderItem = convertToOrderItem(itemRequest, order.getOrderId());
                orderItemMapper.insert(orderItem);
            }
        }
        
        log.info("订单创建成功，订单号: {}", orderNo);
        return orderNo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(Integer id, OrderRequest request) {
        log.info("更新订单，订单ID: {}", id);
        
        // 检查订单是否存在
        Order existingOrder = orderMapper.selectById(id);
        if (existingOrder == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 更新订单信息
        Order order = convertToEntity(request);
        order.setOrderId(id);
        
        int result = orderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException("订单更新失败");
        }
        
        log.info("订单更新成功，订单ID: {}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Integer id, String reason) {
        log.info("取消订单，订单ID: {}, 取消原因: {}", id, reason);
        
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (order.getOrderStatus() != 1) {
            throw new BusinessException("只有待付款订单才能取消");
        }
        
        order.setOrderStatus(6); // 已取消
        order.setCancelTime(LocalDateTime.now());
        order.setRemark(order.getRemark() + " 取消原因: " + reason);
        
        orderMapper.updateById(order);
        
        log.info("订单取消成功，订单ID: {}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Integer id) {
        log.info("确认订单，订单ID: {}", id);
        
        updateOrderStatus(id, 2); // 已付款
        
        Order order = orderMapper.selectById(id);
        order.setPaymentStatus(1); // 已支付
        order.setPaymentTime(LocalDateTime.now());
        
        orderMapper.updateById(order);
        
        log.info("订单确认成功，订单ID: {}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Integer id, String trackingNumber, String shippingCompany) {
        log.info("发货订单，订单ID: {}, 快递单号: {}, 快递公司: {}", id, trackingNumber, shippingCompany);
        
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (order.getOrderStatus() != 2) {
            throw new BusinessException("只有已付款订单才能发货");
        }
        
        order.setOrderStatus(3); // 已发货
        order.setShipTime(LocalDateTime.now());
        // 这里可以添加物流信息字段，如果需要的话
        
        orderMapper.updateById(order);
        
        log.info("订单发货成功，订单ID: {}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Integer id) {
        log.info("完成订单，订单ID: {}", id);
        
        updateOrderStatus(id, 5); // 已完成
        
        Order order = orderMapper.selectById(id);
        order.setFinishTime(LocalDateTime.now());
        
        orderMapper.updateById(order);
        
        log.info("订单完成成功，订单ID: {}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Integer id, Integer status) {
        log.info("更新订单状态，订单ID: {}, 新状态: {}", id, status);
        
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        order.setOrderStatus(status);
        
        int result = orderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException("订单状态更新失败");
        }
        
        log.info("订单状态更新成功，订单ID: {}", id);
    }
    
    @Override
    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        log.info("根据用户ID获取订单列表，用户ID: {}", userId);
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        List<Order> orders = orderMapper.selectList(queryWrapper);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderResponse> getOrdersByStatus(Integer status) {
        log.info("根据订单状态获取订单列表，状态: {}", status);
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderStatus, status);
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        List<Order> orders = orderMapper.selectList(queryWrapper);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getOrderStatistics() {
        log.info("获取订单统计信息");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 总订单数
        Long totalOrders = orderMapper.selectCount(null);
        statistics.put("totalOrders", totalOrders);
        
        // 各状态订单数
        for (int status = 1; status <= 6; status++) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getOrderStatus, status);
            Long count = orderMapper.selectCount(wrapper);
            statistics.put("status" + status + "Count", count);
        }
        
        // 今日订单数
        LambdaQueryWrapper<Order> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(Order::getCreateTime, LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
        Long todayOrders = orderMapper.selectCount(todayWrapper);
        statistics.put("todayOrders", todayOrders);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getSalesStatistics(String startDate, String endDate) {
        log.info("获取销售统计，开始日期: {}, 结束日期: {}", startDate, endDate);
        
        Map<String, Object> statistics = new HashMap<>();
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        // 解析日期
        if (StringUtils.hasText(startDate)) {
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.ge(Order::getCreateTime, start);
        }
        
        if (StringUtils.hasText(endDate)) {
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.le(Order::getCreateTime, end);
        }
        
        // 只统计有效订单（非取消状态）
        queryWrapper.ne(Order::getOrderStatus, 6);
        
        List<Order> orders = orderMapper.selectList(queryWrapper);
        
        // 计算销售金额
        BigDecimal totalSales = orders.stream()
                .map(Order::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        statistics.put("totalSales", totalSales);
        statistics.put("orderCount", orders.size());
        
        return statistics;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Integer> ids, Integer status) {
        log.info("批量更新订单状态，订单ID列表: {}, 新状态: {}", ids, status);
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("订单ID列表不能为空");
        }
        
        for (Integer id : ids) {
            updateOrderStatus(id, status);
        }
        
        log.info("批量更新订单状态成功，更新数量: {}", ids.size());
    }
    
    /**
     * 将Order实体转换为OrderResponse
     */
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        
        // 基础字段映射
        response.setOrderId(order.getOrderId());
        response.setOrderNumber(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setStatus(order.getOrderStatus());
        response.setStatusName(getOrderStatusName(order.getOrderStatus()));
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentStatusName(getPaymentStatusName(order.getPaymentStatus()));
        
        // 支付信息字段（设置默认值）
        response.setPaymentMethod(1); // 默认支付宝
        response.setPaymentMethodName("支付宝");
        response.setPaymentTransactionId(""); // 支付流水号
        
        // 金额字段
        response.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
        response.setShippingFee(order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO);
        response.setDiscountAmount(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
        response.setActualAmount(order.getActualAmount() != null ? order.getActualAmount() : BigDecimal.ZERO);
        
        // 收货信息
        response.setReceiverName(order.getReceiverName());
        response.setReceiverPhone(order.getReceiverPhone());
        response.setReceiverAddress(order.getReceiverAddress());
        
        // 订单备注和操作信息
        response.setRemark(order.getRemark());
        
        // 物流信息（设置默认值）
        response.setShippingCompany(""); // 物流公司
        response.setTrackingNumber(""); // 物流单号
        
        // 优惠券信息（设置默认值）
        response.setCouponId(null);
        
        // 取消和退款信息（设置默认值）
        response.setCancelReason("");
        response.setRefundReason("");
        response.setRefundAmount(BigDecimal.ZERO);
        
        // 地址信息（设置默认值）
        response.setAddressId(null);
        
        // 时间字段映射
        response.setPaymentTime(order.getPaymentTime());
        response.setShippingTime(order.getShipTime());
        response.setDeliveryTime(order.getReceiveTime());
        response.setCancelTime(order.getCancelTime());
        response.setCreateTime(order.getCreateTime());
        
        // 更新时间映射（优先使用完成时间）
        if (order.getFinishTime() != null) {
            response.setUpdateTime(order.getFinishTime());
        } else if (order.getCancelTime() != null) {
            response.setUpdateTime(order.getCancelTime());
        } else if (order.getShipTime() != null) {
            response.setUpdateTime(order.getShipTime());
        } else if (order.getPaymentTime() != null) {
            response.setUpdateTime(order.getPaymentTime());
        } else {
            response.setUpdateTime(order.getCreateTime());
        }
        
        // 获取订单商品项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, order.getOrderId());
        List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
        
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(this::convertToOrderItemResponse)
                .collect(Collectors.toList());
        response.setOrderItems(itemResponses);
        
        return response;
    }
    
    /**
     * 将OrderRequest转换为Order实体
     */
    private Order convertToEntity(OrderRequest request) {
        Order order = new Order();
        BeanUtils.copyProperties(request, order);
        
        // 设置默认值
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(BigDecimal.ZERO);
        }
        if (order.getShippingFee() == null) {
            order.setShippingFee(BigDecimal.ZERO);
        }
        if (order.getDiscountAmount() == null) {
            order.setDiscountAmount(BigDecimal.ZERO);
        }
        if (order.getActualAmount() == null) {
            order.setActualAmount(order.getTotalAmount().add(order.getShippingFee()).subtract(order.getDiscountAmount()));
        }
        
        // 计算订单金额（这里简化处理，实际应该根据商品价格计算）
        // 实际项目中应该查询商品价格进行计算
        
        return order;
    }
    
    /**
     * 将OrderItemRequest转换为OrderItem
     */
    private OrderItem convertToOrderItem(OrderRequest.OrderItemRequest request, Integer orderId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setProductId(request.getProductId());
        orderItem.setQuantity(request.getQuantity());
        orderItem.setCreateTime(LocalDateTime.now());
        
        // 这里应该查询商品信息获取商品名称、价格等
        // 简化处理，实际需要注入ProductService
        
        return orderItem;
    }
    
    /**
     * 将OrderItem转换为OrderItemResponse
     */
    private OrderItemResponse convertToOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        
        // 基础字段映射
        response.setOrderItemId(orderItem.getItemId());
        response.setOrderId(orderItem.getOrderId());
        response.setProductId(orderItem.getProductId());
        response.setProductName(orderItem.getProductName());
        response.setProductImage(orderItem.getProductImage());
        response.setUnitPrice(orderItem.getProductPrice()); // 单价映射
        response.setQuantity(orderItem.getQuantity());
        response.setSubtotal(orderItem.getTotalPrice()); // 小计金额映射
        response.setActualAmount(orderItem.getTotalPrice()); // 实际支付金额
        response.setCreateTime(orderItem.getCreateTime());
        
        // 设置默认值
        response.setDiscountAmount(BigDecimal.ZERO);
        response.setIsReviewed(0);
        
        // 这里可以添加查询商品详情的逻辑
        // ProductResponse product = productService.getProductById(orderItem.getProductId());
        // response.setProduct(product);
        
        return response;
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + ((int)(Math.random() * 1000));
    }
    
    /**
     * 获取订单状态名称
     */
    private String getOrderStatusName(Integer status) {
        if (status == null) return "未知";
        
        switch (status) {
            case 1: return "待付款";
            case 2: return "已付款";
            case 3: return "已发货";
            case 4: return "已收货";
            case 5: return "已完成";
            case 6: return "已取消";
            default: return "未知";
        }
    }
    
    /**
     * 获取支付状态名称
     */
    private String getPaymentStatusName(Integer status) {
        if (status == null) return "未知";
        
        switch (status) {
            case 0: return "未支付";
            case 1: return "已支付";
            case 2: return "已退款";
            default: return "未知";
        }
    }
}