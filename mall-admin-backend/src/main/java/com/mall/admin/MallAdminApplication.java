package com.mall.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商城后台管理系统启动类
 *
 * @author Mall Admin Team
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.mall.admin.mapper")
public class MallAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallAdminApplication.class, args);
        System.out.println("\n" +
                "商城后台管理系统启动成功！\n" +
                "接口文档地址: http://localhost:8080/api/swagger-ui.html\n");
    }
}
