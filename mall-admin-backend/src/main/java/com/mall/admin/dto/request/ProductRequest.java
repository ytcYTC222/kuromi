package com.mall.admin.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 商品请求DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public class ProductRequest {
    
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200个字符")
    private String productName;
    
    @NotBlank(message = "商品编码不能为空")
    @Size(max = 50, message = "商品编码长度不能超过50个字符")
    private String productCode;
    
    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;
    
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    private BigDecimal currentPrice;
    
    /**
     * 原价
     */
    @DecimalMin(value = "0.01", message = "原价必须大于0")
    private BigDecimal originalPrice;
    
    /**
     * 成本价
     */
    @DecimalMin(value = "0.01", message = "成本价必须大于0")
    private BigDecimal costPrice;
    
    /**
     * 促销价
     */
    @DecimalMin(value = "0.01", message = "促销价必须大于0")
    private BigDecimal promotionPrice;
    
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能小于0")
    private Integer stockQuantity;
    
    /**
     * 商品描述
     */
    @Size(max = 1000, message = "商品描述长度不能超过1000个字符")
    private String description;
    
    /**
     * 商品详情
     */
    private String detail;
    
    /**
     * 商品图片URL
     */
    private String imageUrl;
    
    /**
     * 商品图片列表（多张图片用逗号分隔）
     */
    private String images;
    
    /**
     * 商品重量（克）
     */
    private Integer weight;
    
    /**
     * 商品尺寸
     */
    private String dimensions;
    
    /**
     * 商品品牌
     */
    private String brand;
    
    /**
     * 商品型号
     */
    private String model;
    
    /**
     * 商品颜色
     */
    private String color;
    
    /**
     * 商品尺码
     */
    private String size;
    
    /**
     * 商品材质
     */
    private String material;
    
    /**
     * 商品标签
     */
    private String tags;
    

    
    /**
     * 是否热销 (0-否, 1-是)
     */
    private Integer isHot;
    
    /**
     * 是否新品 (0-否, 1-是)
     */
    private Integer isNew;
    
    /**
     * 商品状态 (0-下架, 1-上架)
     */
    private Integer status;
    
    /**
     * 排序值
     */
    private Integer sortOrder;
    
    /**
     * SEO关键词
     */
    private String seoKeywords;
    
    /**
     * SEO描述
     */
    private String seoDescription;
    
    // Getters and Setters
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public BigDecimal getCostPrice() {
        return costPrice;
    }
    
    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }
    
    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }
    
    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDetail() {
        return detail;
    }
    
    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getImages() {
        return images;
    }
    
    public void setImages(String images) {
        this.images = images;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    public String getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    

    
    public Integer getIsHot() {
        return isHot;
    }
    
    public void setIsHot(Integer isHot) {
        this.isHot = isHot;
    }
    
    public Integer getIsNew() {
        return isNew;
    }
    
    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getSeoKeywords() {
        return seoKeywords;
    }
    
    public void setSeoKeywords(String seoKeywords) {
        this.seoKeywords = seoKeywords;
    }
    
    public String getSeoDescription() {
        return seoDescription;
    }
    
    public void setSeoDescription(String seoDescription) {
        this.seoDescription = seoDescription;
    }
}