package com.mall.admin.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品评价响应DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public class ProductReviewResponse {
    
    /**
     * 评价ID
     */
    private Integer reviewId;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户信息
     */
    private UserResponse user;
    
    /**
     * 商品ID
     */
    private Integer productId;
    
    /**
     * 商品信息
     */
    private ProductResponse product;
    
    /**
     * 订单ID
     */
    private Integer orderId;
    
    /**
     * 订单项ID
     */
    private Integer orderItemId;
    
    /**
     * 评分 (1-5星)
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评价图片列表
     */
    private List<String> images;
    
    /**
     * 评价标签（如：质量好、物流快等）
     */
    private List<String> tags;
    
    /**
     * 是否匿名评价 (0-否, 1-是)
     */
    private Integer isAnonymous;
    
    /**
     * 评价状态 (0-待审核, 1-已通过, 2-已拒绝)
     */
    private Integer status;
    
    /**
     * 评价状态名称
     */
    private String statusName;
    
    /**
     * 审核意见
     */
    private String auditComment;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 商家回复
     */
    private String merchantReply;
    
    /**
     * 商家回复时间
     */
    private LocalDateTime merchantReplyTime;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 是否置顶 (0-否, 1-是)
     */
    private Integer isTop;
    
    /**
     * 是否精选 (0-否, 1-是)
     */
    private Integer isFeatured;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    public Integer getReviewId() {
        return reviewId;
    }
    
    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public UserResponse getUser() {
        return user;
    }
    
    public void setUser(UserResponse user) {
        this.user = user;
    }
    
    public Integer getProductId() {
        return productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    public ProductResponse getProduct() {
        return product;
    }
    
    public void setProduct(ProductResponse product) {
        this.product = product;
    }
    
    public Integer getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
    public Integer getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
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
    
    public List<String> getImages() {
        return images;
    }
    
    public void setImages(List<String> images) {
        this.images = images;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public Integer getIsAnonymous() {
        return isAnonymous;
    }
    
    public void setIsAnonymous(Integer isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getStatusName() {
        return statusName;
    }
    
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    
    public String getAuditComment() {
        return auditComment;
    }
    
    public void setAuditComment(String auditComment) {
        this.auditComment = auditComment;
    }
    
    public LocalDateTime getAuditTime() {
        return auditTime;
    }
    
    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }
    
    public String getMerchantReply() {
        return merchantReply;
    }
    
    public void setMerchantReply(String merchantReply) {
        this.merchantReply = merchantReply;
    }
    
    public LocalDateTime getMerchantReplyTime() {
        return merchantReplyTime;
    }
    
    public void setMerchantReplyTime(LocalDateTime merchantReplyTime) {
        this.merchantReplyTime = merchantReplyTime;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getIsTop() {
        return isTop;
    }
    
    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }
    
    public Integer getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Integer isFeatured) {
        this.isFeatured = isFeatured;
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
}