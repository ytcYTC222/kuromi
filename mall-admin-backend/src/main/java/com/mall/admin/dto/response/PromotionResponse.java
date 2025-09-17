package com.mall.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 促销活动响应DTO
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Data
public class PromotionResponse {

    /**
     * 促销活动ID
     */
    private Integer promotionId;

    /**
     * 促销活动名称
     */
    private String promotionName;

    /**
     * 促销类型：1商品促销，2分类促销，3全场促销
     */
    private Integer promotionType;

    /**
     * 促销类型名称
     */
    private String promotionTypeName;

    /**
     * 目标ID（商品ID或分类ID）
     */
    private Integer targetId;

    /**
     * 目标名称（商品名称或分类名称）
     */
    private String targetName;

    /**
     * 折扣类型：1百分比折扣，2固定金额减免
     */
    private Integer discountType;

    /**
     * 折扣类型名称
     */
    private String discountTypeName;

    /**
     * 折扣值
     */
    private BigDecimal discountValue;

    /**
     * 最低消费金额
     */
    private BigDecimal minAmount;

    /**
     * 最大优惠金额
     */
    private BigDecimal maxDiscount;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 是否启用：0禁用，1启用
     */
    private Integer isActive;

    /**
     * 启用状态名称
     */
    private String statusName;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 促销横幅图片
     */
    private String bannerImage;

    /**
     * 促销描述
     */
    private String description;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}