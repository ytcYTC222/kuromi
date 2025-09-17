package com.mall.admin.dto.request;

import com.mall.admin.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品查询DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品查询参数")
public class ProductQueryDTO extends PageQuery {
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "商品编码")
    private String productCode;
    
    @Schema(description = "分类ID")
    private Integer categoryId;
    
    @Schema(description = "商品状态 (0-下架, 1-上架, 2-草稿)")
    private Integer status;
    
    @Schema(description = "是否热销 (0-否, 1-是)")
    private Integer isHot;
    
    @Schema(description = "是否新品 (0-否, 1-是)")
    private Integer isNew;
    
    @Schema(description = "最低价格")
    private BigDecimal minPrice;
    
    @Schema(description = "最高价格")
    private BigDecimal maxPrice;
    
    @Schema(description = "商品品牌")
    private String brand;
    
    @Schema(description = "商品标签")
    private String tags;
    
    @Schema(description = "关键词搜索")
    private String keyword;
    
    @Schema(description = "创建时间开始")
    private String createTimeStart;
    
    @Schema(description = "创建时间结束")
    private String createTimeEnd;
    
    @Schema(description = "库存预警（查询库存小于等于此值的商品）")
    private Integer stockWarning;
}