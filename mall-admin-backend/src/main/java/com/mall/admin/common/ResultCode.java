package com.mall.admin.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错�?    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允"),
    CONFLICT(409, "资源冲突"),
    VALIDATION_ERROR(422, "参数校验失败"),
    
    // 服务器错误?    ERROR(500, "系统内部错误"),
    ERROR(500,"系统内部错误"),

    SERVICE_UNAVAILABLE(503, "服务不可用"),
    
    // 业务错误�?    USER_NOT_FOUND(1001, "用户不存�?),
    USER_DISABLED(1002, "用户已被禁用"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_EXPIRED(1004, "令牌已过期"),
    TOKEN_INVALID(1005, "令牌无效"),
    
    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(2002, "商品库存不足"),
    
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    
    CATEGORY_NOT_FOUND(4001, "分类不存在"),
    CATEGORY_HAS_CHILDREN(4002, "分类下存在子分类"),
    CATEGORY_HAS_PRODUCTS(4003, "分类下存在商品");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 状态消�?     */
    private final String message;
}
