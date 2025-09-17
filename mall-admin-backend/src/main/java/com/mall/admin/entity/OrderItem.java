package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品表实体类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@TableName("order_items")
@Schema(description = "订单商品表实体")
public class OrderItem {
    
    @TableId(value = "item_id", type = IdType.AUTO)
    @Schema(description = "订单商品ID")
    private Integer itemId;
    
    @Schema(description = "订单ID")
    private Integer orderId;
    
    @Schema(description = "商品ID")
    private Integer productId;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "商品图片")
    private String productImage;
    
    @Schema(description = "商品单价")
    @TableField("product_price")
    private BigDecimal productPrice;
    
    @Schema(description = "购买数量")
    private Integer quantity;
    
    @Schema(description = "小计金额")
    @TableField("total_price")
    private BigDecimal totalPrice;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}