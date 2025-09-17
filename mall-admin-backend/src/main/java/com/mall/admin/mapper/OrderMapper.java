package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 获取订单总数
     */
    @Select("SELECT COUNT(*) FROM orders")
    Long selectTotalOrders();
    
    /**
     * 获取今日订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE DATE(create_time) = CURDATE()")
    Long selectTodayOrders();
    
    /**
     * 获取总销售额
     */
    @Select("SELECT COALESCE(SUM(actual_amount), 0) FROM orders WHERE order_status = 5")
    BigDecimal selectTotalSales();
    
    /**
     * 获取今日销售额
     */
    @Select("SELECT COALESCE(SUM(actual_amount), 0) FROM orders WHERE order_status = 5 AND DATE(create_time) = CURDATE()")
    BigDecimal selectTodaySales();
}