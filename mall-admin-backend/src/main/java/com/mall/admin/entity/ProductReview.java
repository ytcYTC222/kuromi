package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品评价实体类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@TableName("product_reviews")
@Schema(description = "商品评价实体")
public class ProductReview {
    
    @TableId(value = "review_id", type = IdType.AUTO)
    @Schema(description = "评价ID")
    private Integer reviewId;
    
    @Schema(description = "商品ID")
    private Integer productId;
    
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "订单ID")
    private Integer orderId;
    
    @Schema(description = "订单商品ID")
    private Integer orderItemId;
    
    @Schema(description = "评分：1-5星")
    private Integer rating;
    
    @Schema(description = "评价内容")
    private String reviewContent;
    
    @Schema(description = "评价图片，多张图片用逗号分隔")
    private String reviewImages;
    
    @Schema(description = "是否匿名：0否，1是")
    private Integer isAnonymous;
    
    @Schema(description = "审核状态：0待审核，1已通过，2已拒绝")
    private Integer auditStatus;
    
    @Schema(description = "审核备注")
    private String auditRemark;
    
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    
    @Schema(description = "商家回复")
    private String merchantReply;
    
    @Schema(description = "商家回复时间")
    private LocalDateTime merchantReplyTime;
    
    @Schema(description = "点赞数")
    private Integer likeCount;
    
    @Schema(description = "是否置顶：0否，1是")
    private Integer isTop;
    
    @Schema(description = "备注")
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @TableLogic
    @Schema(description = "删除标志：0未删除，1已删除")
    private Integer deleted;
}