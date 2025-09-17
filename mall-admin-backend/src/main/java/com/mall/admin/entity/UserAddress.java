package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户地址实体类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@TableName("user_addresses")
@Schema(description = "用户地址实体")
public class UserAddress {
    
    @TableId(value = "address_id", type = IdType.AUTO)
    @Schema(description = "地址ID")
    private Integer addressId;
    
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "收货人姓名")
    private String receiverName;
    
    @Schema(description = "收货人电话")
    private String receiverPhone;
    
    @Schema(description = "省份")
    private String province;
    
    @Schema(description = "城市")
    private String city;
    
    @Schema(description = "区县")
    private String district;
    
    @Schema(description = "详细地址")
    private String detailAddress;
    
    @Schema(description = "邮政编码")
    private String postalCode;
    
    @Schema(description = "是否默认地址：0否，1是")
    private Integer isDefault;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}