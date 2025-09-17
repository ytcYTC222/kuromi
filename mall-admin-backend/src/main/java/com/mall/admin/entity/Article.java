package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文章实体类
 * 
 * @author Admin
 * @since 2024-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("blog_articles")
public class Article {

    /**
     * 文章ID
     */
    @TableId(value = "article_id", type = IdType.AUTO)
    private Integer articleId;

    /**
     * 文章标题
     */
    @TableField("title")
    private String title;

    /**
     * 文章摘要
     */
    @TableField("excerpt")
    private String excerpt;

    /**
     * 文章内容
     */
    @TableField("content")
    private String content;

    /**
     * 封面图片
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 文章分类
     */
    @TableField("category")
    private String category;

    /**
     * 标签（多个用逗号分隔）
     */
    @TableField("tags")
    private String tags;

    /**
     * 作者
     */
    @TableField("author")
    private String author;

    /**
     * 浏览次数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 点赞次数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 状态：0草稿，1发布
     */
    @TableField("status")
    private Integer status;

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

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;
}