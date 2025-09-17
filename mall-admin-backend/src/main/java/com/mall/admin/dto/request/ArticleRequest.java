package com.mall.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 文章请求DTO
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Data
public class ArticleRequest {
    
    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    private String title;

    /**
     * 文章摘要
     */
    private String excerpt;

    /**
     * 文章内容
     */
    @NotBlank(message = "文章内容不能为空")
    private String content;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 文章分类
     */
    private String category;

    /**
     * 标签（多个用逗号分隔）
     */
    private String tags;

    /**
     * 作者
     */
    private String author;

    /**
     * 状态：0草稿，1发布
     */
    private Integer status = 0;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
}