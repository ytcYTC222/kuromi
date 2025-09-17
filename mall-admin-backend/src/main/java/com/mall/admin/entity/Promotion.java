package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 促销活动实体类
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("promotions")
public class Promotion {

    /**
     * 促销活动ID
     */
    @TableId(value = "promotion_id", type = IdType.AUTO)
    private Integer promotionId;

    /**
     * 促销活动名称
     */
    @TableField("promotion_name")
    private String promotionName;

    /**
     * 促销类型：1商品促销，2分类促销，3全场促销
     */
    @TableField("promotion_type")
    private Integer promotionType;

    /**
     * 目标ID（商品ID或分类ID）
     */
    @TableField("target_id")
    private Integer targetId;

    /**
     * 折扣类型：1百分比折扣，2固定金额减免
     */
    @TableField("discount_type")
    private Integer discountType;

    /**
     * 折扣值
     */
    @TableField("discount_value")
    private BigDecimal discountValue;

    /**
     * 最低消费金额
     */
    @TableField("min_amount")
    private BigDecimal minAmount;

    /**
     * 最大优惠金额
     */
    @TableField("max_discount")
    private BigDecimal maxDiscount;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 是否启用：0禁用，1启用
     */
    @TableField("is_active")
    private Integer isActive;

    /**
     * 排序权重
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 促销横幅图片
     */
    @TableField("banner_image")
    private String bannerImage;

    /**
     * 促销描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}