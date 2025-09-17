package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车实体类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@TableName("shopping_cart")
@Schema(description = "购物车实体")
public class ShoppingCart {
    
    @TableId(value = "cart_id", type = IdType.AUTO)
    @Schema(description = "购物车ID")
    private Integer cartId;
    
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "商品ID")
    private Integer productId;
    
    @Schema(description = "数量")
    private Integer quantity;
    
    @Schema(description = "是否选中：0否，1是")
    private Integer selected;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}