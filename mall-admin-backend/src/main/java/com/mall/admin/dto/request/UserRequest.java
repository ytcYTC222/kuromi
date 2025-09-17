package com.mall.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * 用户请求DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@Schema(description = "用户请求DTO")
public class UserRequest {
    
    @Schema(description = "微信openid")
    private String openid;
    
    @Schema(description = "微信unionid")
    private String unionid;
    
    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 50, message = "用户昵称不能超过50个字符")
    @Schema(description = "用户昵称")
    private String nickname;
    
    @Size(max = 500, message = "头像URL不能超过500个字符")
    @Schema(description = "头像URL")
    private String avatarUrl;
    
    @Min(value = 0, message = "性别值无效")
    @Max(value = 2, message = "性别值无效")
    @Schema(description = "性别：0未知，1男，2女")
    private Integer gender;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;
    
    @Size(min = 6, max = 255, message = "密码长度必须在6-255位之间")
    @Schema(description = "密码")
    private String password;
    
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "生日")
    private LocalDate birthday;
    
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    @Schema(description = "用户状态：0禁用，1正常")
    private Integer status;
}