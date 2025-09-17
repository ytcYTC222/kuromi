package com.mall.admin.service.impl;

import com.mall.admin.dto.DashboardOverviewDTO;
import com.mall.admin.dto.SalesTrendDTO;
import com.mall.admin.dto.CategoryStatsDTO;
import com.mall.admin.mapper.*;
import com.mall.admin.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public DashboardOverviewDTO getOverview() {
        DashboardOverviewDTO overview = new DashboardOverviewDTO();
        
        // 获取统计数据
        overview.setTotalUsers(userMapper.selectTotalUsers());
        overview.setTotalOrders(orderMapper.selectTotalOrders());
        overview.setTotalProducts(productMapper.selectTotalProducts());
        overview.setTotalSales(orderMapper.selectTotalSales());
        overview.setTodayOrders(orderMapper.selectTodayOrders());
        overview.setTodaySales(orderMapper.selectTodaySales());
        
        // 获取销售趋势数据（默认7天）
        overview.setSalesTrend(getSalesTrend(7));
        
        // 获取分类统计数据
        overview.setCategoryStats(getCategoryStats());
        
        return overview;
    }

    @Override
    public List<SalesTrendDTO> getSalesTrend(Integer days) {
        List<SalesTrendDTO> trendList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 生成模拟数据
        Random random = new Random();
        for (int i = days - 1; i >= 0; i--) {
            SalesTrendDTO trend = new SalesTrendDTO();
            LocalDate date = LocalDate.now().minusDays(i);
            trend.setDate(date.format(formatter));
            // 模拟销售额数据
            BigDecimal amount = new BigDecimal(5000 + random.nextInt(10000));
            trend.setAmount(amount);
            trendList.add(trend);
        }
        
        return trendList;
    }

    @Override
    public List<CategoryStatsDTO> getCategoryStats() {
        List<CategoryStatsDTO> statsList = new ArrayList<>();
        
        // 模拟分类统计数据
        String[] categoryNames = {"客厅", "卧室", "餐厅", "书房", "儿童房"};
        Random random = new Random();
        
        for (String categoryName : categoryNames) {
            CategoryStatsDTO stats = new CategoryStatsDTO();
            stats.setCategoryName(categoryName);
            stats.setOrderCount((long) (100 + random.nextInt(200)));
            stats.setPercentage(20.0 + random.nextDouble() * 30);
            statsList.add(stats);
        }
        
        return statsList;
    }
}