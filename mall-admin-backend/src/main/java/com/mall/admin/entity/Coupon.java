package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠卷实体类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@TableName("coupons")
@Schema(description = "优惠卷实体")
public class Coupon {
    
    @TableId(value = "coupon_id", type = IdType.AUTO)
    @Schema(description = "优惠券ID")
    private Integer couponId;
    
    @Schema(description = "优惠券名称")
    private String couponName;
    
    @Schema(description = "类型：1满减，2折扣，3免邮")
    private Integer couponType;
    
    @Schema(description = "优惠值")
    private BigDecimal discountValue;
    
    @Schema(description = "最低消费金额")
    private BigDecimal minAmount;
    
    @Schema(description = "最大优惠金额")
    private BigDecimal maxDiscount;
    
    @Schema(description = "发放总数")
    private Integer totalQuantity;
    
    @Schema(description = "已使用数量")
    private Integer usedQuantity;
    
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;
    
    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}