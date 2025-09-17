package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品图片实体类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@TableName("product_images")
public class ProductImage {
    
    /**
     * 图片ID
     */
    @TableId(type = IdType.AUTO)
    private Integer imageId;
    
    /**
     * 商品ID
     */
    private Integer productId;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 是否为主图 (0-否, 1-是)
     */
    private Integer isMain;
    
    /**
     * 排序值
     */
    private Integer sortOrder;
    
    /**
     * 图片描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}