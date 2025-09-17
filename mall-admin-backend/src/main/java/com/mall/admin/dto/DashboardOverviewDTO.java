package com.mall.admin.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardOverviewDTO {
    private Long totalUsers;
    private Long totalOrders;
    private Long totalProducts;
    private BigDecimal totalSales;
    private Long todayOrders;
    private BigDecimal todaySales;
    private List<SalesTrendDTO> salesTrend;
    private List<CategoryStatsDTO> categoryStats;
}