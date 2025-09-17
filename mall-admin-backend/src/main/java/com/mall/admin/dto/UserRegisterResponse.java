package com.mall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户注册响应DTO
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户注册响应")
public class UserRegisterResponse {
    
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "性别：0未知，1男，2女")
    private Integer gender;
    
    @Schema(description = "用户状态：0禁用，1启用")
    private Integer status;
    
    @Schema(description = "注册时间")
    private LocalDateTime registerTime;
    
    @Schema(description = "注册成功消息")
    private String message;
}