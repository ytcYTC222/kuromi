package com.mall.admin.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 商品评价请求DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public class ProductReviewRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Integer userId;
    
    @NotNull(message = "商品ID不能为空")
    private Integer productId;
    
    /**
     * 订单ID（可选，用于验证用户是否购买过该商品）
     */
    private Integer orderId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer rating;
    
    /**
     * 评价内容
     */
    @Size(max = 1000, message = "评价内容长度不能超过1000个字符")
    private String content;
    
    /**
     * 评价图片（多张图片用逗号分隔）
     */
    private String images;
    
    /**
     * 是否匿名评价 (0-否, 1-是)
     */
    private Integer isAnonymous;
    
    /**
     * 商品质量评分 (1-5分)
     */
    @Min(value = 1, message = "商品质量评分不能小于1")
    @Max(value = 5, message = "商品质量评分不能大于5")
    private Integer qualityRating;
    
    /**
     * 服务态度评分 (1-5分)
     */
    @Min(value = 1, message = "服务态度评分不能小于1")
    @Max(value = 5, message = "服务态度评分不能大于5")
    private Integer serviceRating;
    
    /**
     * 物流速度评分 (1-5分)
     */
    @Min(value = 1, message = "物流速度评分不能小于1")
    @Max(value = 5, message = "物流速度评分不能大于5")
    private Integer deliveryRating;
    
    /**
     * 评价标签（多个标签用逗号分隔）
     */
    private String tags;
    
    /**
     * 商品规格信息
     */
    private String specification;
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getProductId() {
        return productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    public Integer getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getImages() {
        return images;
    }
    
    public void setImages(String images) {
        this.images = images;
    }
    
    public Integer getIsAnonymous() {
        return isAnonymous;
    }
    
    public void setIsAnonymous(Integer isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    public Integer getQualityRating() {
        return qualityRating;
    }
    
    public void setQualityRating(Integer qualityRating) {
        this.qualityRating = qualityRating;
    }
    
    public Integer getServiceRating() {
        return serviceRating;
    }
    
    public void setServiceRating(Integer serviceRating) {
        this.serviceRating = serviceRating;
    }
    
    public Integer getDeliveryRating() {
        return deliveryRating;
    }
    
    public void setDeliveryRating(Integer deliveryRating) {
        this.deliveryRating = deliveryRating;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getSpecification() {
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
}