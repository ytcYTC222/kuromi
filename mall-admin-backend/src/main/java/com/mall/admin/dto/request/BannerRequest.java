package com.mall.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;

/**
 * 轮播图请求DTO
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Data
public class BannerRequest {
    
    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 图片URL
     */
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    /**
     * 链接类型：1商品，2分类，3文章，4外链
     */
    @NotNull(message = "链接类型不能为空")
    @Min(value = 1, message = "链接类型值必须在1-4之间")
    @Max(value = 4, message = "链接类型值必须在1-4之间")
    private Integer linkType;

    /**
     * 链接值
     */
    private String linkValue;

    /**
     * 排序
     */
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sortOrder = 0;

    /**
     * 是否启用：0禁用，1启用
     */
    private Integer isActive = 1;

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
}