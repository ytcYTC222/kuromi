package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@TableName("users")
@Schema(description = "用户实体")
public class User {
    
    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "微信openid")
    private String openid;
    
    @Schema(description = "微信unionid")
    private String unionid;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "头像URL")
    @TableField("avatar_url")
    private String avatarUrl;
    
    @Schema(description = "性别：0未知，1男，2女")
    private Integer gender;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "密码")
    private String password;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "生日")
    private java.time.LocalDate birthday;
    
    @Schema(description = "注册时间")
    @TableField("register_time")
    private LocalDateTime registerTime;
    
    @Schema(description = "最后登录时间")
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    
    @Schema(description = "用户状态：0禁用，1正常")
    private Integer status;
    

}