package com.mall.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章响应DTO
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Data
public class ArticleResponse {
    
    /**
     * 文章ID
     */
    private Integer articleId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String excerpt;

    /**
     * 文章内容
     */
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
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 状态：0草稿，1发布
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

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

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
}