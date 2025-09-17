package com.mall.admin.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要认证注解
 * 用于标记需要JWT认证的接口方
 * 可以指定需要的角色权限和是否必须是管理员
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {

    /**
     * 需要的角色权限
     * 默认为空，表示只需要登录即可访问
     */
    String role() default "";

    /**
     * 是否必须是管理员
     * 默认为false
     */
    boolean admin() default false;
}
