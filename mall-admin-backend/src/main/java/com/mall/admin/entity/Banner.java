package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 轮播图实体类
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("banners")
public class Banner {

    /**
     * 轮播图ID
     */
    @TableId(value = "banner_id", type = IdType.AUTO)
    private Integer bannerId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 链接类型：1商品，2分类，3文章，4外链
     */
    @TableField("link_type")
    private Integer linkType;

    /**
     * 链接值
     */
    @TableField("link_value")
    private String linkValue;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 是否启用：0禁用，1启用
     */
    @TableField("is_active")
    private Integer isActive;

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
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}