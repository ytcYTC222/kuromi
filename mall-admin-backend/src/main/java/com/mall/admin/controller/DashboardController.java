package com.mall.admin.controller;

import com.mall.admin.common.Result;
import com.mall.admin.dto.DashboardOverviewDTO;
import com.mall.admin.dto.SalesTrendDTO;
import com.mall.admin.dto.CategoryStatsDTO;
import com.mall.admin.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private IDashboardService dashboardService;

    /**
     * 获取仪表板概览数据
     */
    @GetMapping("/overview")
    public Result<DashboardOverviewDTO> getOverview() {
        DashboardOverviewDTO overview = dashboardService.getOverview();
        return Result.success(overview);
    }

    /**
     * 获取销售趋势数据
     */
    @GetMapping("/sales-trend")
    public Result<List<SalesTrendDTO>> getSalesTrend(
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        List<SalesTrendDTO> trendList = dashboardService.getSalesTrend(days);
        return Result.success(trendList);
    }

    /**
     * 获取分类统计数据
     */
    @GetMapping("/category-stats")
    public Result<List<CategoryStatsDTO>> getCategoryStats() {
        List<CategoryStatsDTO> statsList = dashboardService.getCategoryStats();
        return Result.success(statsList);
    }
}