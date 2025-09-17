package com.mall.admin.dto;

import lombok.Data;

@Data
public class CategoryStatsDTO {
    private String categoryName;
    private Long orderCount;
    private Double percentage;
}