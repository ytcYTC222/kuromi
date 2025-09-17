package com.mall.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠卷响应DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@Schema(description = "优惠卷响应DTO")
public class CouponResponse {
    
    @Schema(description = "优惠券ID")
    private Integer couponId;
    
    @Schema(description = "优惠券名称")
    private String couponName;
    
    @Schema(description = "类型：1满减，2折扣，3免邮")
    private Integer couponType;
    
    @Schema(description = "类型名称")
    private String couponTypeName;
    
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
    
    @Schema(description = "剩余数量")
    private Integer remainingQuantity;
    
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "优惠券状态：0未开始，1进行中，2已结束，3已禁用")
    private Integer couponStatus;
    
    @Schema(description = "优惠券状态名称")
    private String couponStatusName;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}