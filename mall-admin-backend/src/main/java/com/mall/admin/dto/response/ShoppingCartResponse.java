package com.mall.admin.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车响应DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public class ShoppingCartResponse {
    
    /**
     * 购物车ID
     */
    private Integer cartId;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 商品ID
     */
    private Integer productId;
    
    /**
     * 商品信息
     */
    private ProductResponse product;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品图片
     */
    private String productImage;
    
    /**
     * 商品价格
     */
    private BigDecimal productPrice;
    
    /**
     * 商品原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 商品规格
     */
    private String specification;
    
    /**
     * 是否选中 (0-否, 1-是)
     */
    private Integer isSelected;
    
    /**
     * 小计金额
     */
    private BigDecimal subtotal;
    
    /**
     * 商品库存
     */
    private Integer stock;
    
    /**
     * 商品状态 (0-下架, 1-上架, 2-草稿)
     */
    private Integer productStatus;
    
    /**
     * 商品状态名称
     */
    private String productStatusName;
    
    /**
     * 是否有库存
     */
    private Boolean hasStock;
    
    /**
     * 是否可购买
     */
    private Boolean canPurchase;
    
    /**
     * 不可购买原因
     */
    private String unavailableReason;
    
    /**
     * 商品重量（克）
     */
    private Integer weight;
    
    /**
     * 商品体积（立方厘米）
     */
    private Integer volume;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    public Integer getCartId() {
        return cartId;
    }
    
    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }
    
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
    
    public ProductResponse getProduct() {
        return product;
    }
    
    public void setProduct(ProductResponse product) {
        this.product = product;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductImage() {
        return productImage;
    }
    
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
    
    public BigDecimal getProductPrice() {
        return productPrice;
    }
    
    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }
    
    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getSpecification() {
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    public Integer getIsSelected() {
        return isSelected;
    }
    
    public void setIsSelected(Integer isSelected) {
        this.isSelected = isSelected;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public Integer getProductStatus() {
        return productStatus;
    }
    
    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }
    
    public String getProductStatusName() {
        return productStatusName;
    }
    
    public void setProductStatusName(String productStatusName) {
        this.productStatusName = productStatusName;
    }
    
    public Boolean getHasStock() {
        return hasStock;
    }
    
    public void setHasStock(Boolean hasStock) {
        this.hasStock = hasStock;
    }
    
    public Boolean getCanPurchase() {
        return canPurchase;
    }
    
    public void setCanPurchase(Boolean canPurchase) {
        this.canPurchase = canPurchase;
    }
    
    public String getUnavailableReason() {
        return unavailableReason;
    }
    
    public void setUnavailableReason(String unavailableReason) {
        this.unavailableReason = unavailableReason;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    public Integer getVolume() {
        return volume;
    }
    
    public void setVolume(Integer volume) {
        this.volume = volume;
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