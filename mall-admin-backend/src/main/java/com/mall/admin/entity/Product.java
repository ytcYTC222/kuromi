package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@TableName("products")
@Schema(description = "商品实体")
public class Product {
    
    @TableId(value = "product_id", type = IdType.AUTO)
    @Schema(description = "商品ID")
    private Integer productId;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "商品编码")
    private String productCode;
    
    @Schema(description = "分类ID")
    private Integer categoryId;
    
    @Schema(description = "品牌")
    private String brand;
    
    @Schema(description = "商品描述")
    private String description;
    
    @Schema(description = "材质")
    private String material;
    
    @Schema(description = "颜色")
    private String color;
    
    @Schema(description = "尺寸")
    private String size;
    
    @Schema(description = "重量(kg)")
    private BigDecimal weight;
    
    @Schema(description = "原价")
    private BigDecimal originalPrice;
    
    @Schema(description = "现价")
    private BigDecimal currentPrice;
    
    @Schema(description = "成本价")
    private BigDecimal costPrice;
    
    @Schema(description = "促销价")
    private BigDecimal promotionPrice;
    
    @Schema(description = "库存数量")
    private Integer stockQuantity;
    
    @Schema(description = "销售数量")
    private Integer salesCount;
    
    @Schema(description = "浏览次数")
    private Integer viewCount;
    
    @Schema(description = "平均评分")
    private BigDecimal ratingAverage;
    
    @Schema(description = "评价数量")
    private Integer ratingCount;
    
    @Schema(description = "是否热门：0否，1是")
    private Integer isHot;
    
    @Schema(description = "是否新品：0否，1是")
    private Integer isNew;
    
    @Schema(description = "是否促销：0否，1是")
    private Integer isPromotion;
    
    @Schema(description = "促销开始时间")
    private LocalDateTime promotionStartTime;
    
    @Schema(description = "促销结束时间")
    private LocalDateTime promotionEndTime;
    
    @Schema(description = "商品状态：0下架，1上架")
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}