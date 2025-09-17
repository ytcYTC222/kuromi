package com.mall.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 促销活动请求DTO
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Data
public class PromotionRequest {

    /**
     * 促销活动名称
     */
    @NotBlank(message = "促销活动名称不能为空")
    @Size(max = 100, message = "促销活动名称长度不能超过100个字符")
    private String promotionName;

    /**
     * 促销类型：1商品促销，2分类促销，3全场促销
     */
    @NotNull(message = "促销类型不能为空")
    @Min(value = 1, message = "促销类型无效")
    @Max(value = 3, message = "促销类型无效")
    private Integer promotionType;

    /**
     * 目标ID（商品ID或分类ID），当promotionType为1或2时必填
     */
    private Integer targetId;

    /**
     * 折扣类型：1百分比折扣，2固定金额减免
     */
    @NotNull(message = "折扣类型不能为空")
    @Min(value = 1, message = "折扣类型无效")
    @Max(value = 2, message = "折扣类型无效")
    private Integer discountType;

    /**
     * 折扣值
     */
    @NotNull(message = "折扣值不能为空")
    @DecimalMin(value = "0.01", message = "折扣值必须大于0")
    private BigDecimal discountValue;

    /**
     * 最低消费金额
     */
    @DecimalMin(value = "0", message = "最低消费金额不能小于0")
    private BigDecimal minAmount = BigDecimal.ZERO;

    /**
     * 最大优惠金额
     */
    @DecimalMin(value = "0.01", message = "最大优惠金额必须大于0")
    private BigDecimal maxDiscount;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 是否启用：0禁用，1启用
     */
    @Min(value = 0, message = "启用状态无效")
    @Max(value = 1, message = "启用状态无效")
    private Integer isActive = 1;

    /**
     * 排序权重
     */
    @Min(value = 0, message = "排序权重不能小于0")
    private Integer sortOrder = 0;

    /**
     * 促销横幅图片
     */
    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    private String bannerImage;

    /**
     * 促销描述
     */
    private String description;
}