package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类实体类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@TableName("categories")
@Schema(description = "商品分类实体")
public class Category {
    
    @TableId(value = "category_id", type = IdType.AUTO)
    @Schema(description = "分类ID")
    private Integer categoryId;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "分类编码")
    private String categoryCode;
    
    @Schema(description = "父分类ID")
    private Integer parentId;
    
    @Schema(description = "分类层级")
    private Integer categoryLevel;
    
    @Schema(description = "分类图片")
    @TableField("category_image")
    private String categoryImage;
    
    @Schema(description = "分类描述")
    private String description;
    
    @Schema(description = "排序值")
    private Integer sortOrder;
    
    @Schema(description = "是否启用：0禁用，1启用")
    @TableField("is_active")
    private Integer isActive;
    
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    // @TableLogic
    // @Schema(description = "删除标志：0未删除，1已删除")
    // private Integer deleted;
}