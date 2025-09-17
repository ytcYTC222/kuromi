package com.mall.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图响应DTO
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Data
public class BannerResponse {
    
    /**
     * 轮播图ID
     */
    private Integer bannerId;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 链接类型：1商品，2分类，3文章，4外链
     */
    private Integer linkType;

    /**
     * 链接类型名称
     */
    private String linkTypeName;

    /**
     * 链接值
     */
    private String linkValue;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否启用：0禁用，1启用
     */
    private Integer isActive;

    /**
     * 启用状态名称
     */
    private String statusName;

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
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}