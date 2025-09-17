package com.mall.admin.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
public class OrderResponse {
    
    /**
     * 订单ID
     */
    private Integer orderId;
    
    /**
     * 订单号
     */
    private String orderNumber;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户信息
     */
    private UserResponse user;
    
    /**
     * 收货地址ID
     */
    private Integer addressId;
    
    /**
     * 收货地址信息
     */
    private UserAddressResponse address;
    
    /**
     * 订单状态 (1-待付款, 2-待发货, 3-待收货, 4-已完成, 5-已取消, 6-已退款)
     */
    private Integer status;
    
    /**
     * 订单状态名称
     */
    private String statusName;
    
    /**
     * 支付状态 (0-未支付, 1-已支付, 2-支付失败, 3-已退款)
     */
    private Integer paymentStatus;
    
    /**
     * 支付状态名称
     */
    private String paymentStatusName;
    
    /**
     * 支付方式 (1-支付宝, 2-微信, 3-银行卡, 4-余额)
     */
    private Integer paymentMethod;
    
    /**
     * 支付方式名称
     */
    private String paymentMethodName;
    
    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 支付流水号
     */
    private String paymentTransactionId;
    
    /**
     * 商品总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 运费
     */
    private BigDecimal shippingFee;
    
    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 实付金额
     */
    private BigDecimal actualAmount;
    
    /**
     * 优惠券ID
     */
    private Integer couponId;
    
    /**
     * 优惠券信息
     */
    private CouponResponse coupon;
    
    /**
     * 物流公司
     */
    private String shippingCompany;
    
    /**
     * 物流单号
     */
    private String trackingNumber;
    
    /**
     * 发货时间
     */
    private LocalDateTime shippingTime;
    
    /**
     * 收货时间
     */
    private LocalDateTime deliveryTime;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人电话
     */
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;
    
    /**
     * 退款原因
     */
    private String refundReason;
    
    /**
     * 退款时间
     */
    private LocalDateTime refundTime;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 订单项列表
     */
    private List<OrderItemResponse> orderItems;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    public Integer getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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
    
    public Integer getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
    
    public UserAddressResponse getAddress() {
        return address;
    }
    
    public void setAddress(UserAddressResponse address) {
        this.address = address;
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
    
    public Integer getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getPaymentStatusName() {
        return paymentStatusName;
    }
    
    public void setPaymentStatusName(String paymentStatusName) {
        this.paymentStatusName = paymentStatusName;
    }
    
    public Integer getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentMethodName() {
        return paymentMethodName;
    }
    
    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }
    
    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
    
    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }
    
    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }
    
    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getShippingFee() {
        return shippingFee;
    }
    
    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getActualAmount() {
        return actualAmount;
    }
    
    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }
    
    public Integer getCouponId() {
        return couponId;
    }
    
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }
    
    public CouponResponse getCoupon() {
        return coupon;
    }
    
    public void setCoupon(CouponResponse coupon) {
        this.coupon = coupon;
    }
    
    public String getShippingCompany() {
        return shippingCompany;
    }
    
    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    
    public LocalDateTime getShippingTime() {
        return shippingTime;
    }
    
    public void setShippingTime(LocalDateTime shippingTime) {
        this.shippingTime = shippingTime;
    }
    
    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }
    
    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getCancelReason() {
        return cancelReason;
    }
    
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
    
    public LocalDateTime getCancelTime() {
        return cancelTime;
    }
    
    public void setCancelTime(LocalDateTime cancelTime) {
        this.cancelTime = cancelTime;
    }
    
    public String getRefundReason() {
        return refundReason;
    }
    
    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }
    
    public LocalDateTime getRefundTime() {
        return refundTime;
    }
    
    public void setRefundTime(LocalDateTime refundTime) {
        this.refundTime = refundTime;
    }
    
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public List<OrderItemResponse> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItemResponse> orderItems) {
        this.orderItems = orderItems;
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
    
    public String getReceiverName() {
        return receiverName;
    }
    
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    
    public String getReceiverPhone() {
        return receiverPhone;
    }
    
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }
    
    public String getReceiverAddress() {
        return receiverAddress;
    }
    
    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
}