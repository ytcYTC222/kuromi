package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@TableName("orders")
@Schema(description = "订单主表实体")
public class Order {
    
    @TableId(value = "order_id", type = IdType.AUTO)
    @Schema(description = "订单ID")
    private Integer orderId;
    
    @Schema(description = "订单号")
    @TableField("order_no")
    private String orderNo;
    
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "订单状态：1待付款，2已付款，3已发货，4已收货，5已完成，6已取消")
    @TableField("order_status")
    private Integer orderStatus;
    
    @Schema(description = "支付状态：0未支付，1已支付，2已退款")
    private Integer paymentStatus;
    
    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;
    
    @Schema(description = "优惠金额")
    @TableField("discount_amount")
    private BigDecimal discountAmount;
    
    @Schema(description = "运费")
    @TableField("shipping_fee")
    private BigDecimal shippingFee;
    
    @Schema(description = "实付金额")
    private BigDecimal actualAmount;
    
    @Schema(description = "收货人姓名")
    private String receiverName;
    
    @Schema(description = "收货人电话")
    private String receiverPhone;
    
    @Schema(description = "收货地址")
    private String receiverAddress;
    
    @Schema(description = "订单备注")
    private String remark;
    
    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;
    
    @Schema(description = "发货时间")
    @TableField("ship_time")
    private LocalDateTime shipTime;
    
    @Schema(description = "收货时间")
    @TableField("receive_time")
    private LocalDateTime receiveTime;
    
    @Schema(description = "完成时间")
    @TableField("finish_time")
    private LocalDateTime finishTime;
    
    @Schema(description = "取消时间")
    @TableField("cancel_time")
    private LocalDateTime cancelTime;
    
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    

}