package com.mall.admin.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 购物车请求DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public class ShoppingCartRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Integer userId;
    
    @NotNull(message = "商品ID不能为空")
    private Integer productId;
    
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    private Integer quantity;
    
    /**
     * 商品规格（如颜色、尺寸等）
     */
    private String specification;
    
    /**
     * 是否选中 (0-否, 1-是)
     */
    private Integer isSelected;
    
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
}