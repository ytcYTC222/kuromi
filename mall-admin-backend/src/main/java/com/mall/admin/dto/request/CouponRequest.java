package com.mall.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠卷请求DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@Schema(description = "优惠卷请求DTO")
public class CouponRequest {
    
    @NotBlank(message = "优惠券名称不能为空")
    @Size(max = 100, message = "优惠券名称不能超过100个字符")
    @Schema(description = "优惠券名称")
    private String couponName;
    
    @NotNull(message = "优惠券类型不能为空")
    @Min(value = 1, message = "优惠券类型无效")
    @Max(value = 3, message = "优惠券类型无效")
    @Schema(description = "类型：1满减，2折扣，3免邮")
    private Integer couponType;
    
    @NotNull(message = "优惠值不能为空")
    @DecimalMin(value = "0.01", message = "优惠值必须大于0")
    @Schema(description = "优惠值")
    private BigDecimal discountValue;
    
    @DecimalMin(value = "0.00", message = "最低消费金额不能小于0")
    @Schema(description = "最低消费金额")
    private BigDecimal minAmount;
    
    @DecimalMin(value = "0.01", message = "最大优惠金额必须大于0")
    @Schema(description = "最大优惠金额")
    private BigDecimal maxDiscount;
    
    @NotNull(message = "发放总数不能为空")
    @Min(value = 1, message = "发放总数必须大于0")
    @Schema(description = "发放总数")
    private Integer totalQuantity;
    
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;
}