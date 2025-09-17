package com.mall.admin.service;

import com.mall.admin.dto.DashboardOverviewDTO;
import com.mall.admin.dto.SalesTrendDTO;
import com.mall.admin.dto.CategoryStatsDTO;
import java.util.List;

public interface IDashboardService {
    /**
     * 获取仪表板概览数据
     */
    DashboardOverviewDTO getOverview();

    /**
     * 获取销售趋势数据
     */
    List<SalesTrendDTO> getSalesTrend(Integer days);

    /**
     * 获取分类统计数据
     */
    List<CategoryStatsDTO> getCategoryStats();
}