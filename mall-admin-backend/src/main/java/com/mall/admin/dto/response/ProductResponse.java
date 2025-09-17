package com.mall.admin.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品响应DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public class ProductResponse {
    
    /**
     * 商品ID
     */
    private Integer productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品编码
     */
    private String productCode;
    
    /**
     * 分类ID
     */
    private Integer categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 现价
     */
    private BigDecimal currentPrice;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 成本价
     */
    private BigDecimal costPrice;
    
    /**
     * 库存数量
     */
    private Integer stockQuantity;
    
    /**
     * 销量
     */
    private Integer salesCount;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 商品详情
     */
    private String detail;
    
    /**
     * 商品图片URL列表
     */
    private List<String> images;
    
    /**
     * 主图URL
     */
    private String mainImage;
    
    /**
     * 商品状态 (0-下架, 1-上架, 2-草稿)
     */
    private Integer status;
    

    
    /**
     * 是否热销 (0-否, 1-是)
     */
    private Integer isHot;
    
    /**
     * 是否新品 (0-否, 1-是)
     */
    private Integer isNew;
    
    /**
     * 排序值
     */
    private Integer sortOrder;
    
    /**
     * 商品重量（kg）
     */
    private Integer  weight;
    
    /**
     * 商品体积（立方厘米）
     */
    private Integer volume;
    
    /**
     * 品牌
     */
    private String brand;
    
    /**
     * 材质
     */
    private String material;
    
    /**
     * 颜色
     */
    private String color;
    
    /**
     * 尺寸
     */
    private String size;
    
    /**
     * 促销价
     */
    private BigDecimal promotionPrice;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 是否促销 (0-否, 1-是)
     */
    private Integer isPromotion;
    
    /**
     * 促销开始时间
     */
    private LocalDateTime promotionStartTime;
    
    /**
     * 促销结束时间
     */
    private LocalDateTime promotionEndTime;
    
    /**
     * 商品规格
     */
    private String specification;
    
    /**
     * 商品标签
     */
    private List<String> tags;
    
    /**
     * SEO关键词
     */
    private String seoKeywords;
    
    /**
     * SEO描述
     */
    private String seoDescription;
    
    /**
     * 评分
     */
    private Double rating;
    
    /**
     * 评价数量
     */
    private Integer reviewCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    public Integer getProductId() {
        return productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
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
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public Integer getSalesCount() {
        return salesCount;
    }
    
    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
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
    
    public List<String> getImages() {
        return images;
    }
    
    public void setImages(List<String> images) {
        this.images = images;
    }
    
    public String getMainImage() {
        return mainImage;
    }
    
    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
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
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Integer  getWeight() {
        return weight;
    }
    
    public void setWeight(Integer  weight) {
        this.weight = weight;
    }
    
    public Integer getVolume() {
        return volume;
    }
    
    public void setVolume(Integer volume) {
        this.volume = volume;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getSpecification() {
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
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
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public Integer getReviewCount() {
        return reviewCount;
    }
    
    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
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
    
    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }
    
    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getIsPromotion() {
        return isPromotion;
    }
    
    public void setIsPromotion(Integer isPromotion) {
        this.isPromotion = isPromotion;
    }
    
    public LocalDateTime getPromotionStartTime() {
        return promotionStartTime;
    }
    
    public void setPromotionStartTime(LocalDateTime promotionStartTime) {
        this.promotionStartTime = promotionStartTime;
    }
    
    public LocalDateTime getPromotionEndTime() {
        return promotionEndTime;
    }
    
    public void setPromotionEndTime(LocalDateTime promotionEndTime) {
        this.promotionEndTime = promotionEndTime;
    }
}