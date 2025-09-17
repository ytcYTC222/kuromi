package com.mall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 用户注册请求DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@Schema(description = "用户注册请求")
public class UserRegisterRequest {
    

    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Schema(description = "密码", example = "password123")
    private String password;
    
    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码", example = "password123")
    private String confirmPassword;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "john@example.com")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 30, message = "昵称长度必须在2-30位之间")
    @Schema(description = "昵称", example = "小张")
    private String nickname;
    
    @Min(value = 0, message = "性别值不正确")
    @Max(value = 2, message = "性别值不正确")
    @Schema(description = "性别：0未知，1男，2女", example = "1")
    private Integer gender;
    

}