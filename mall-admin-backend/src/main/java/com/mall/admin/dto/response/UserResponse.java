package com.mall.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户响应DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@Schema(description = "用户响应DTO")
public class UserResponse {
    
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "微信openid")
    private String openid;
    
    @Schema(description = "微信unionid")
    private String unionid;
    
    @Schema(description = "用户昵称")
    private String nickname;
    
    @Schema(description = "头像URL")
    private String avatarUrl;
    
    @Schema(description = "性别：0未知，1男，2女")
    private Integer gender;
    
    @Schema(description = "性别名称")
    private String genderName;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "生日")
    private LocalDate birthday;
    
    @Schema(description = "注册时间")
    private LocalDateTime registerTime;
    
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;
    
    @Schema(description = "用户状态：0禁用，1正常")
    private Integer status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "用户订单总数")
    private Integer totalOrders;
    
    @Schema(description = "用户累计消费金额")
    private Double totalConsumption;
}