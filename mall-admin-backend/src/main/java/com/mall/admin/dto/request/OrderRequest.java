package com.mall.admin.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单请求DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public class OrderRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Integer userId;
    
    @NotNull(message = "收货地址ID不能为空")
    private Integer addressId;
    
    /**
     * 订单商品列表
     */
    @NotNull(message = "订单商品列表不能为空")
    private List<OrderItemRequest> orderItems;
    
    /**
     * 优惠券ID
     */
    private Integer couponId;
    
    /**
     * 订单备注
     */
    @Size(max = 500, message = "订单备注长度不能超过500个字符")
    private String remark;
    
    /**
     * 配送方式 (1-普通配送, 2-快速配送)
     */
    private Integer deliveryType;
    
    /**
     * 支付方式 (1-微信支付, 2-支付宝, 3-银行卡)
     */
    private Integer paymentMethod;
    
    /**
     * 发票类型 (0-不开发票, 1-个人发票, 2-企业发票)
     */
    private Integer invoiceType;
    
    /**
     * 发票抬头
     */
    private String invoiceTitle;
    
    /**
     * 发票内容
     */
    private String invoiceContent;
    
    /**
     * 预期送达时间
     */
    private String expectedDeliveryTime;
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
    
    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }
    
    public Integer getCouponId() {
        return couponId;
    }
    
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public Integer getDeliveryType() {
        return deliveryType;
    }
    
    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }
    
    public Integer getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public Integer getInvoiceType() {
        return invoiceType;
    }
    
    public void setInvoiceType(Integer invoiceType) {
        this.invoiceType = invoiceType;
    }
    
    public String getInvoiceTitle() {
        return invoiceTitle;
    }
    
    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }
    
    public String getInvoiceContent() {
        return invoiceContent;
    }
    
    public void setInvoiceContent(String invoiceContent) {
        this.invoiceContent = invoiceContent;
    }
    
    public String getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }
    
    public void setExpectedDeliveryTime(String expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }
    
    /**
     * 订单商品项请求DTO
     */
    public static class OrderItemRequest {
        
        @NotNull(message = "商品ID不能为空")
        private Integer productId;
        
        @NotNull(message = "商品数量不能为空")
        @DecimalMin(value = "1", message = "商品数量必须大于0")
        private Integer quantity;
        
        /**
         * 商品规格（如颜色、尺寸等）
         */
        private String specification;
        
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
    }
}