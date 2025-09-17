# 商城后台管理系统 - Spring Boot后端项目规范说明书

## 1. 项目概述

### 1.1 项目简介
本项目是一个基于 Spring Boot 3.x + MyBatis-Plus + MySQL 的商城后台管理系统后端服务，为前端 Vue3 管理系统提供 RESTful API 接口支持。采用三层架构设计，确保代码的可维护性和扩展性。

### 1.2 技术栈
- **Java版本**: JDK 17
- **框架**: Spring Boot 3.2.x
- **数据访问**: MyBatis-Plus 3.5.x（禁用XML配置）
- **数据库**: MySQL 8.0+
- **安全认证**: JWT (jsonwebtoken)
- **参数验证**: Spring Boot Validation
- **API文档**: SpringDoc OpenAPI 3 (Swagger)
- **JSON处理**: Jackson
- **日志框架**: Logback
- **构建工具**: Maven 3.9+

### 1.3 架构特性
- 三层架构：Controller层 + Service层 + Mapper层
- RESTful API 设计规范
- JWT 无状态身份验证
- 统一异常处理机制
- 统一响应格式封装
- 自动API文档生成
- 代码生成器支持

## 2. 项目结构

```
spring-boot-mall-admin/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── mall/
│   │   │           └── admin/
│   │   │               ├── MallAdminApplication.java     # 启动类
│   │   │               ├── config/                      # 配置类
│   │   │               │   ├── CorsConfig.java         # 跨域配置
│   │   │               │   ├── JwtConfig.java          # JWT配置
│   │   │               │   ├── MyBatisPlusConfig.java  # MyBatis-Plus配置
│   │   │               │   ├── SwaggerConfig.java      # Swagger配置
│   │   │               │   └── WebMvcConfig.java       # Web MVC配置
│   │   │               ├── controller/                 # 控制层
│   │   │               │   ├── AuthController.java     # 认证控制器
│   │   │               │   ├── DashboardController.java # 仪表板控制器
│   │   │               │   ├── ProductController.java  # 商品管理控制器
│   │   │               │   ├── CategoryController.java # 分类管理控制器
│   │   │               │   ├── OrderController.java    # 订单管理控制器
│   │   │               │   ├── UserController.java     # 用户管理控制器
│   │   │               │   ├── MarketingController.java # 营销管理控制器
│   │   │               │   ├── ContentController.java  # 内容管理控制器
│   │   │               │   ├── SystemController.java   # 系统管理控制器
│   │   │               │   └── FileController.java     # 文件上传控制器
│   │   │               ├── service/                    # 服务层
│   │   │               │   ├── IAuthService.java       # 认证服务接口
│   │   │               │   ├── IProductService.java    # 商品服务接口
│   │   │               │   ├── ICategoryService.java   # 分类服务接口
│   │   │               │   ├── IOrderService.java      # 订单服务接口
│   │   │               │   ├── IUserService.java       # 用户服务接口
│   │   │               │   ├── IMarketingService.java  # 营销服务接口
│   │   │               │   ├── IContentService.java    # 内容服务接口
│   │   │               │   ├── ISystemService.java     # 系统服务接口
│   │   │               │   ├── IFileService.java       # 文件服务接口
│   │   │               │   └── impl/                   # 服务实现类
│   │   │               │       ├── AuthServiceImpl.java
│   │   │               │       ├── ProductServiceImpl.java
│   │   │               │       ├── CategoryServiceImpl.java
│   │   │               │       ├── OrderServiceImpl.java
│   │   │               │       ├── UserServiceImpl.java
│   │   │               │       ├── MarketingServiceImpl.java
│   │   │               │       ├── ContentServiceImpl.java
│   │   │               │       ├── SystemServiceImpl.java
│   │   │               │       └── FileServiceImpl.java
│   │   │               ├── mapper/                     # 数据访问层
│   │   │               │   ├── UserMapper.java         # 用户Mapper
│   │   │               │   ├── ProductMapper.java      # 商品Mapper
│   │   │               │   ├── CategoryMapper.java     # 分类Mapper
│   │   │               │   ├── OrderMapper.java        # 订单Mapper
│   │   │               │   ├── OrderItemMapper.java    # 订单项Mapper
│   │   │               │   ├── BannerMapper.java       # 轮播图Mapper
│   │   │               │   ├── CouponMapper.java       # 优惠券Mapper
│   │   │               │   ├── ArticleMapper.java      # 文章Mapper
│   │   │               │   ├── HotSearchMapper.java    # 热搜Mapper
│   │   │               │   └── OperationLogMapper.java # 操作日志Mapper
│   │   │               ├── entity/                     # 实体类
│   │   │               │   ├── User.java               # 用户实体
│   │   │               │   ├── Product.java            # 商品实体
│   │   │               │   ├── Category.java           # 分类实体
│   │   │               │   ├── Order.java              # 订单实体
│   │   │               │   ├── OrderItem.java          # 订单项实体
│   │   │               │   ├── Banner.java             # 轮播图实体
│   │   │               │   ├── Coupon.java             # 优惠券实体
│   │   │               │   ├── Article.java            # 文章实体
│   │   │               │   ├── HotSearch.java          # 热搜实体
│   │   │               │   └── OperationLog.java       # 操作日志实体
│   │   │               ├── dto/                        # 数据传输对象
│   │   │               │   ├── request/                # 请求DTO
│   │   │               │   │   ├── LoginRequest.java
│   │   │               │   │   ├── ProductRequest.java
│   │   │               │   │   ├── CategoryRequest.java
│   │   │               │   │   ├── OrderRequest.java
│   │   │               │   │   └── UserRequest.java
│   │   │               │   └── response/               # 响应DTO
│   │   │               │       ├── LoginResponse.java
│   │   │               │       ├── ProductResponse.java
│   │   │               │       ├── CategoryResponse.java
│   │   │               │       ├── OrderResponse.java
│   │   │               │       └── UserResponse.java
│   │   │               ├── vo/                         # 视图对象
│   │   │               │   ├── ProductVO.java          # 商品视图对象
│   │   │               │   ├── OrderVO.java            # 订单视图对象
│   │   │               │   ├── UserVO.java             # 用户视图对象
│   │   │               │   └── DashboardVO.java        # 仪表板视图对象
│   │   │               ├── common/                     # 公共类
│   │   │               │   ├── result/                 # 响应结果
│   │   │               │   │   ├── Result.java         # 统一响应结果
│   │   │               │   │   ├── ResultCode.java     # 响应状态码
│   │   │               │   │   └── PageResult.java     # 分页响应结果
│   │   │               │   ├── exception/              # 异常处理
│   │   │               │   │   ├── GlobalExceptionHandler.java # 全局异常处理器
│   │   │               │   │   ├── BusinessException.java      # 业务异常
│   │   │               │   │   └── AuthException.java          # 认证异常
│   │   │               │   ├── interceptor/            # 拦截器
│   │   │               │   │   ├── JwtInterceptor.java # JWT拦截器
│   │   │               │   │   └── LogInterceptor.java # 日志拦截器
│   │   │               │   ├── annotation/             # 自定义注解
│   │   │               │   │   ├── RequireAuth.java    # 需要认证注解
│   │   │               │   │   └── OperationLog.java   # 操作日志注解
│   │   │               │   └── constant/               # 常量类
│   │   │               │       ├── CommonConstant.java # 通用常量
│   │   │               │       ├── JwtConstant.java    # JWT常量
│   │   │               │       └── RedisConstant.java  # Redis常量
│   │   │               └── util/                       # 工具类
│   │   │                   ├── JwtUtil.java            # JWT工具类
│   │   │                   ├── PasswordUtil.java       # 密码工具类
│   │   │                   ├── FileUtil.java           # 文件工具类
│   │   │                   ├── DateUtil.java           # 日期工具类
│   │   │                   └── ValidationUtil.java     # 验证工具类
│   │   └── resources/
│   │       ├── application.yml                         # 主配置文件
│   │       ├── application-dev.yml                     # 开发环境配置
│   │       ├── application-prod.yml                    # 生产环境配置
│   │       ├── logback-spring.xml                      # 日志配置
│   │       └── static/                                 # 静态资源
│   │           └── uploads/                            # 上传文件目录
│   └── test/
│       └── java/
│           └── com/
│               └── mall/
│                   └── admin/
│                       ├── MallAdminApplicationTests.java # 启动测试
│                       ├── controller/                    # 控制器测试
│                       ├── service/                       # 服务测试
│                       └── mapper/                        # Mapper测试
├── target/                                             # 编译输出目录
├── logs/                                               # 日志文件目录
├── .gitignore                                          # Git忽略文件
├── pom.xml                                             # Maven配置文件
└── README.md                                           # 项目说明文档
```

## 3. 数据库集成方案

### 3.1 数据库连接配置

```yaml
# application-dev.yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/database_design?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: abc123
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      pool-name: HikariCP
      max-lifetime: 1800000
      connection-timeout: 30000
```

### 3.2 MyBatis-Plus配置

```java
@Configuration
@MapperScan("com.mall.admin.mapper")
public class MyBatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 元对象字段填充控制器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
```

### 3.3 实体类映射示例

```java
@Data
@TableName("products")
@ApiModel(description = "商品实体")
public class Product {
    
    @TableId(value = "product_id", type = IdType.AUTO)
    @ApiModelProperty("商品ID")
    private Integer productId;
    
    @ApiModelProperty("商品名称")
    private String productName;
    
    @ApiModelProperty("商品编码")
    private String productCode;
    
    @ApiModelProperty("分类ID")
    private Integer categoryId;
    
    @ApiModelProperty("品牌")
    private String brand;
    
    @ApiModelProperty("商品描述")
    private String description;
    
    @ApiModelProperty("材质")
    private String material;
    
    @ApiModelProperty("颜色")
    private String color;
    
    @ApiModelProperty("尺寸")
    private String size;
    
    @ApiModelProperty("重量(kg)")
    private BigDecimal weight;
    
    @ApiModelProperty("原价")
    private BigDecimal originalPrice;
    
    @ApiModelProperty("现价")
    private BigDecimal currentPrice;
    
    @ApiModelProperty("成本价")
    private BigDecimal costPrice;
    
    @ApiModelProperty("库存数量")
    private Integer stockQuantity;
    
    @ApiModelProperty("销售数量")
    private Integer salesCount;
    
    @ApiModelProperty("浏览次数")
    private Integer viewCount;
    
    @ApiModelProperty("平均评分")
    private BigDecimal ratingAverage;
    
    @ApiModelProperty("评价数量")
    private Integer ratingCount;
    
    @ApiModelProperty("是否热门：0否，1是")
    private Integer isHot;
    
    @ApiModelProperty("是否新品：0否，1是")
    private Integer isNew;
    
    @ApiModelProperty("是否促销：0否，1是")
    private Integer isPromotion;
    
    @ApiModelProperty("促销价格")
    private BigDecimal promotionPrice;
    
    @ApiModelProperty("促销开始时间")
    private LocalDateTime promotionStartTime;
    
    @ApiModelProperty("促销结束时间")
    private LocalDateTime promotionEndTime;
    
    @ApiModelProperty("状态：0下架，1上架")
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
```

## 4. 三层架构实现规范

### 4.1 控制层（Controller）规范

#### 4.1.1 职责划分
- 接收HTTP请求并进行参数验证
- 调用Service层处理业务逻辑
- 返回统一格式的响应结果
- 处理异常并返回错误信息

#### 4.1.2 实现规范

```java
@RestController
@RequestMapping("/api/products")
@Api(tags = "商品管理")
@Validated
public class ProductController {
    
    @Autowired
    private IProductService productService;
    
    @GetMapping
    @ApiOperation("获取商品列表")
    public Result<PageResult<ProductVO>> getProductList(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer status) {
        
        PageResult<ProductVO> result = productService.getProductList(page, size, keyword, categoryId, status);
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    @ApiOperation("获取商品详情")
    public Result<ProductVO> getProductById(@PathVariable @Min(1) Integer id) {
        ProductVO product = productService.getProductById(id);
        return Result.success(product);
    }
    
    @PostMapping
    @ApiOperation("创建商品")
    @RequireAuth
    @OperationLog("创建商品")
    public Result<Void> createProduct(@RequestBody @Valid ProductRequest request) {
        productService.createProduct(request);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @ApiOperation("更新商品")
    @RequireAuth
    @OperationLog("更新商品")
    public Result<Void> updateProduct(
            @PathVariable @Min(1) Integer id,
            @RequestBody @Valid ProductRequest request) {
        productService.updateProduct(id, request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除商品")
    @RequireAuth
    @OperationLog("删除商品")
    public Result<Void> deleteProduct(@PathVariable @Min(1) Integer id) {
        productService.deleteProduct(id);
        return Result.success();
    }
}
```

### 4.2 服务层（Service）规范

#### 4.2.1 职责划分
- 实现具体的业务逻辑
- 处理数据转换和业务规则验证
- 调用Mapper层进行数据操作
- 处理事务管理

#### 4.2.2 接口定义

```java
public interface IProductService {
    
    /**
     * 获取商品列表
     */
    PageResult<ProductVO> getProductList(Integer page, Integer size, String keyword, Integer categoryId, Integer status);
    
    /**
     * 根据ID获取商品详情
     */
    ProductVO getProductById(Integer id);
    
    /**
     * 创建商品
     */
    void createProduct(ProductRequest request);
    
    /**
     * 更新商品
     */
    void updateProduct(Integer id, ProductRequest request);
    
    /**
     * 删除商品
     */
    void deleteProduct(Integer id);
    
    /**
     * 批量更新商品状态
     */
    void batchUpdateStatus(List<Integer> ids, Integer status);
}
```

#### 4.2.3 实现规范

```java
@Service
@Transactional(rollbackFor = Exception.class)
public class ProductServiceImpl implements IProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    @Transactional(readOnly = true)
    public PageResult<ProductVO> getProductList(Integer page, Integer size, String keyword, Integer categoryId, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(keyword), Product::getProductName, keyword)
                   .eq(categoryId != null, Product::getCategoryId, categoryId)
                   .eq(status != null, Product::getStatus, status)
                   .orderByDesc(Product::getCreateTime);
        
        // 分页查询
        Page<Product> productPage = new Page<>(page, size);
        Page<Product> result = productMapper.selectPage(productPage, queryWrapper);
        
        // 转换为VO
        List<ProductVO> productVOList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(productVOList, result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductVO getProductById(Integer id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "商品不存在");
        }
        return convertToVO(product);
    }
    
    @Override
    public void createProduct(ProductRequest request) {
        // 验证分类是否存在
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "商品分类不存在");
        }
        
        // 验证商品编码是否重复
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getProductCode, request.getProductCode());
        Long count = productMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "商品编码已存在");
        }
        
        // 转换并保存
        Product product = convertToEntity(request);
        productMapper.insert(product);
    }
    
    @Override
    public void updateProduct(Integer id, ProductRequest request) {
        // 验证商品是否存在
        Product existProduct = productMapper.selectById(id);
        if (existProduct == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "商品不存在");
        }
        
        // 验证商品编码是否重复（排除自己）
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getProductCode, request.getProductCode())
                   .ne(Product::getProductId, id);
        Long count = productMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "商品编码已存在");
        }
        
        // 更新商品信息
        Product product = convertToEntity(request);
        product.setProductId(id);
        productMapper.updateById(product);
    }
    
    @Override
    public void deleteProduct(Integer id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "商品不存在");
        }
        productMapper.deleteById(id);
    }
    
    /**
     * 实体转换为VO
     */
    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        // 可以在这里添加额外的业务逻辑，如获取分类名称等
        return vo;
    }
    
    /**
     * 请求DTO转换为实体
     */
    private Product convertToEntity(ProductRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        return product;
    }
}
```

### 4.3 数据访问层（Mapper）规范

#### 4.3.1 职责划分
- 定义数据库操作接口
- 继承MyBatis-Plus的BaseMapper
- 提供自定义查询方法
- 禁用XML配置，使用注解方式

#### 4.3.2 实现规范

```java
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 根据分类ID统计商品数量
     */
    @Select("SELECT COUNT(*) FROM products WHERE category_id = #{categoryId} AND status = 1")
    Long countByCategoryId(@Param("categoryId") Integer categoryId);
    
    /**
     * 获取热门商品列表
     */
    @Select("SELECT * FROM products WHERE is_hot = 1 AND status = 1 ORDER BY sales_count DESC LIMIT #{limit}")
    List<Product> selectHotProducts(@Param("limit") Integer limit);
    
    /**
     * 更新商品库存
     */
    @Update("UPDATE products SET stock_quantity = stock_quantity - #{quantity} WHERE product_id = #{productId} AND stock_quantity >= #{quantity}")
    int updateStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);
    
    /**
     * 获取商品销售统计
     */
    @Select("SELECT p.product_name, SUM(oi.quantity) as total_sales " +
            "FROM products p " +
            "LEFT JOIN order_items oi ON p.product_id = oi.product_id " +
            "LEFT JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE o.order_status >= 2 " +
            "GROUP BY p.product_id, p.product_name " +
            "ORDER BY total_sales DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectSalesStatistics(@Param("limit") Integer limit);
}
```

## 5. JWT认证实现方案

### 5.1 JWT工具类

```java
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * 生成JWT Token
     */
    public String generateToken(Integer userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return createToken(claims);
    }
    
    /**
     * 创建Token
     */
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    /**
     * 从Token中获取用户ID
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (Integer) claims.get("userId") : null;
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (String) claims.get("username") : null;
    }
    
    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 从Token中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 判断Token是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
```

### 5.2 JWT拦截器

```java
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查是否需要认证
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireAuth requireAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);
        if (requireAuth == null) {
            requireAuth = handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
        }
        
        if (requireAuth == null) {
            return true;
        }
        
        // 获取Token
        String token = getTokenFromRequest(request);
        if (StringUtils.isEmpty(token)) {
            throw new AuthException("Token不能为空");
        }
        
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            throw new AuthException("Token无效或已过期");
        }
        
        // 将用户信息存储到请求中
        Integer userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        
        return true;
    }
    
    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### 5.3 认证控制器

```java
@RestController
@RequestMapping("/api/auth")
@Api(tags = "认证管理")
public class AuthController {
    
    @Autowired
    private IAuthService authService;
    
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }
    
    @PostMapping("/logout")
    @ApiOperation("用户登出")
    @RequireAuth
    public Result<Void> logout(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        authService.logout(userId);
        return Result.success();
    }
    
    @GetMapping("/profile")
    @ApiOperation("获取用户信息")
    @RequireAuth
    public Result<UserVO> getProfile(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        UserVO userVO = authService.getUserProfile(userId);
        return Result.success(userVO);
    }
}
```

## 6. 统一响应格式

### 6.1 响应结果类

```java
@Data
@ApiModel(description = "统一响应结果")
public class Result<T> {
    
    @ApiModelProperty("响应码")
    private Integer code;
    
    @ApiModelProperty("响应消息")
    private String message;
    
    @ApiModelProperty("响应数据")
    private T data;
    
    @ApiModelProperty("时间戳")
    private Long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }
    
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }
    
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
```

### 6.2 响应状态码

```java
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "权限不足"),
    DATA_NOT_FOUND(404, "数据不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    
    // 业务相关错误码
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    TOKEN_EXPIRED(1004, "Token已过期"),
    TOKEN_INVALID(1005, "Token无效"),
    
    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(2002, "商品库存不足"),
    CATEGORY_NOT_FOUND(2003, "分类不存在"),
    
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    ORDER_CANNOT_CANCEL(3003, "订单无法取消");
    
    private final Integer code;
    private final String message;
}
```

### 6.3 分页响应结果

```java
@Data
@ApiModel(description = "分页响应结果")
public class PageResult<T> {
    
    @ApiModelProperty("数据列表")
    private List<T> records;
    
    @ApiModelProperty("总记录数")
    private Long total;
    
    @ApiModelProperty("当前页码")
    private Long current;
    
    @ApiModelProperty("每页大小")
    private Long size;
    
    @ApiModelProperty("总页数")
    private Long pages;
    
    public PageResult() {}
    
    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
    }
}
```

## 7. 异常处理机制

### 7.1 自定义异常类

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    
    private Integer code;
    private String message;
    
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }
}

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthException extends RuntimeException {
    
    private Integer code;
    private String message;
    
    public AuthException(String message) {
        super(message);
        this.code = ResultCode.UNAUTHORIZED.getCode();
        this.message = message;
    }
    
    public AuthException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
```

### 7.2 全局异常处理器

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 认证异常处理
     */
    @ExceptionHandler(AuthException.class)
    public Result<Void> handleAuthException(AuthException e) {
        log.warn("认证异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 参数验证异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();
        
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append(fieldError.getField())
                       .append(": ")
                       .append(fieldError.getDefaultMessage())
                       .append("; ");
        }
        
        log.warn("参数验证异常：{}", errorMessage.toString());
        return Result.error(ResultCode.PARAM_ERROR.getCode(), errorMessage.toString());
    }
    
    /**
     * 约束违反异常处理
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        StringBuilder errorMessage = new StringBuilder();
        
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errorMessage.append(violation.getPropertyPath())
                       .append(": ")
                       .append(violation.getMessage())
                       .append("; ");
        }
        
        log.warn("约束违反异常：{}", errorMessage.toString());
        return Result.error(ResultCode.PARAM_ERROR.getCode(), errorMessage.toString());
    }
    
    /**
     * SQL异常处理
     */
    @ExceptionHandler(SQLException.class)
    public Result<Void> handleSQLException(SQLException e) {
        log.error("SQL异常：{}", e.getMessage(), e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "数据库操作异常");
    }
    
    /**
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR);
    }
}
```

## 8. 与前端Vue3项目对接

### 8.1 跨域配置

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的域名
        config.addAllowedOriginPattern("*");
        
        // 允许的请求头
        config.addAllowedHeader("*");
        
        // 允许的请求方法
        config.addAllowedMethod("*");
        
        // 允许携带凭证
        config.setAllowCredentials(true);
        
        // 预检请求的有效期
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
```

### 8.2 API接口对应关系

根据前端Vue3项目的路由结构，后端需要提供以下API接口：

| 前端路由 | 后端API | 说明 |
|---------|---------|------|
| `/dashboard` | `GET /api/dashboard/stats` | 仪表板统计数据 |
| `/products` | `GET /api/products` | 商品列表 |
| `/products/add` | `POST /api/products` | 添加商品 |
| `/products/edit/:id` | `PUT /api/products/{id}` | 编辑商品 |
| `/categories` | `GET /api/categories` | 分类列表 |
| `/orders` | `GET /api/orders` | 订单列表 |
| `/orders/:id` | `GET /api/orders/{id}` | 订单详情 |
| `/users` | `GET /api/users` | 用户列表 |
| `/marketing/coupons` | `GET /api/marketing/coupons` | 优惠券列表 |
| `/content/banners` | `GET /api/content/banners` | 轮播图列表 |
| `/system/settings` | `GET /api/system/settings` | 系统设置 |

### 8.3 请求响应示例

#### 登录接口
```json
// 请求
POST /api/auth/login
{
  "username": "admin",
  "password": "123456"
}

// 响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "nickname": "管理员",
      "avatar": "https://example.com/avatar.jpg"
    }
  },
  "timestamp": 1640995200000
}
```

#### 商品列表接口
```json
// 请求
GET /api/products?page=1&size=10&keyword=沙发&categoryId=1

// 响应
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "productId": 1,
        "productName": "现代简约沙发",
        "productCode": "SF001",
        "categoryId": 1,
        "categoryName": "客厅",
        "currentPrice": 2999.00,
        "stockQuantity": 50,
        "status": 1,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10,
    "pages": 10
  },
  "timestamp": 1640995200000
}
```

## 9. API文档生成

### 9.1 Swagger配置

```java
@Configuration
@EnableOpenApi
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("商城后台管理系统API")
                        .version("1.0.0")
                        .description("商城后台管理系统的RESTful API文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
}
```

### 9.2 API文档注解使用

```java
@RestController
@RequestMapping("/api/products")
@Tag(name = "商品管理", description = "商品相关的API接口")
public class ProductController {
    
    @GetMapping
    @Operation(summary = "获取商品列表", description = "分页查询商品列表，支持关键词搜索和分类筛选")
    @Parameters({
        @Parameter(name = "page", description = "页码", example = "1"),
        @Parameter(name = "size", description = "每页大小", example = "10"),
        @Parameter(name = "keyword", description = "搜索关键词", example = "沙发"),
        @Parameter(name = "categoryId", description = "分类ID", example = "1")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "操作成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<PageResult<ProductVO>> getProductList(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId) {
        // 实现逻辑
    }
}
```

## 10. 代码规范

### 10.1 命名规范

#### 包命名
- 全部小写，使用点号分隔
- 示例：`com.mall.admin.controller`

#### 类命名
- 使用帕斯卡命名法（PascalCase）
- 控制器以Controller结尾：`ProductController`
- 服务接口以I开头：`IProductService`
- 服务实现以Impl结尾：`ProductServiceImpl`
- 实体类使用名词：`Product`
- DTO类以Request/Response结尾：`ProductRequest`

#### 方法命名
- 使用驼峰命名法（camelCase）
- 查询方法以get/find/select开头：`getProductById`
- 新增方法以create/add/insert开头：`createProduct`
- 更新方法以update/modify开头：`updateProduct`
- 删除方法以delete/remove开头：`deleteProduct`

#### 变量命名
- 使用驼峰命名法（camelCase）
- 常量使用全大写，下划线分隔：`MAX_PAGE_SIZE`
- 布尔变量以is/has/can开头：`isActive`

### 10.2 注释规范

#### 类注释
```java
/**
 * 商品管理控制器
 * 
 * @author 开发者姓名
 * @since 2024-01-01
 */
@RestController
public class ProductController {
    // 类实现
}
```

#### 方法注释
```java
/**
 * 根据ID获取商品详情
 * 
 * @param id 商品ID
 * @return 商品详情
 * @throws BusinessException 当商品不存在时抛出
 */
public ProductVO getProductById(Integer id) {
    // 方法实现
}
```

### 10.3 代码格式规范

- 使用4个空格缩进，不使用Tab
- 每行代码长度不超过120个字符
- 左大括号不换行，右大括号单独一行
- if/for/while语句必须使用大括号
- 方法之间空一行
- 导入语句按字母顺序排列

## 11. 配置文件

### 11.1 主配置文件（application.yml）

```yaml
spring:
  profiles:
    active: dev
  application:
    name: mall-admin
  
  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB
  
  # Jackson配置
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
    default-property-inclusion: non_null

# MyBatis-Plus配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# JWT配置
jwt:
  secret: mySecretKey
  expiration: 86400

# 文件上传配置
file:
  upload:
    path: /uploads/
    max-size: 10485760

# 日志配置
logging:
  level:
    com.mall.admin.mapper: debug
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/mall-admin.log

# Swagger配置
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### 11.2 开发环境配置（application-dev.yml）

```yaml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/database_design?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      pool-name: HikariCP
      max-lifetime: 1800000
      connection-timeout: 30000

logging:
  level:
    root: info
    com.mall.admin: debug
```

### 11.3 生产环境配置（application-prod.yml）

```yaml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://your-prod-host:3306/database_design?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      auto-commit: true
      idle-timeout: 30000
      pool-name: HikariCP
      max-lifetime: 1800000
      connection-timeout: 30000

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400

logging:
  level:
    root: warn
    com.mall.admin: info
  file:
    name: /var/log/mall-admin/application.log
```

## 12. Maven配置（pom.xml）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.mall</groupId>
    <artifactId>mall-admin</artifactId>
    <version>1.0.0</version>
    <name>mall-admin</name>
    <description>商城后台管理系统</description>
    
    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.4</mybatis-plus.version>
        <jwt.version>0.11.5</jwt.version>
        <springdoc.version>2.2.0</springdoc.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jwt.version}</version>
        </dependency>
        
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        
        <!-- SpringDoc OpenAPI -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 13. 部署说明

### 13.1 开发环境部署

1. **环境要求**
   - JDK 17+
   - Maven 3.9+
   - MySQL 8.0+
   - IDE（推荐IntelliJ IDEA）

2. **部署步骤**
   ```bash
   # 1. 克隆项目
   git clone <repository-url>
   cd mall-admin
   
   # 2. 创建数据库
   mysql -u root -p < database_design.sql
   
   # 3. 修改配置文件
   # 编辑 src/main/resources/application-dev.yml
   # 配置数据库连接信息
   
   # 4. 编译项目
   mvn clean compile
   
   # 5. 运行项目
   mvn spring-boot:run
   ```

3. **访问地址**
   - API接口：http://localhost:8080/api
   - Swagger文档：http://localhost:8080/swagger-ui.html

### 13.2 生产环境部署

1. **打包应用**
   ```bash
   mvn clean package -Pprod
   ```

2. **Docker部署**
   ```dockerfile
   FROM openjdk:17-jre-slim
   
   WORKDIR /app
   
   COPY target/mall-admin-1.0.0.jar app.jar
   
   EXPOSE 8080
   
   ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
   ```

3. **环境变量配置**
   ```bash
   export DB_USERNAME=your_username
   export DB_PASSWORD=your_password
   export JWT_SECRET=your_jwt_secret
   ```

## 14. 测试规范

### 14.1 单元测试

```java
@SpringBootTest
@Transactional
class ProductServiceTest {
    
    @Autowired
    private IProductService productService;
    
    @Test
    void testCreateProduct() {
        ProductRequest request = new ProductRequest();
        request.setProductName("测试商品");
        request.setProductCode("TEST001");
        request.setCategoryId(1);
        request.setCurrentPrice(new BigDecimal("99.99"));
        
        assertDoesNotThrow(() -> productService.createProduct(request));
    }
    
    @Test
    void testGetProductById() {
        ProductVO product = productService.getProductById(1);
        assertNotNull(product);
        assertEquals(1, product.getProductId());
    }
}
```

### 14.2 集成测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testGetProductList() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/products?page=1&size=10", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

## 15. 监控和日志

### 15.1 日志配置

```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="dev">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
    
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logs/mall-admin.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>logs/mall-admin.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                <maxFileSize>100MB</maxFileSize>
                <maxHistory>30</maxHistory>
                <totalSizeCap>3GB</totalSizeCap>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        
        <root level="INFO">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>
</configuration>
```

### 15.2 操作日志记录

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    String value() default "";
    String module() default "";
}

@Aspect
@Component
@Slf4j
public class OperationLogAspect {
    
    @Autowired
    private OperationLogMapper operationLogMapper;
    
    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            
            // 记录操作日志
            saveOperationLog(joinPoint, operationLog, true, null, endTime - startTime);
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            
            // 记录异常日志
            saveOperationLog(joinPoint, operationLog, false, e.getMessage(), endTime - startTime);
            
            throw e;
        }
    }
    
    private void saveOperationLog(ProceedingJoinPoint joinPoint, OperationLog operationLog, 
                                 boolean success, String errorMsg, long duration) {
        // 实现日志保存逻辑
    }
}
```

## 16. 安全规范

### 16.1 密码加密

```java
@Component
public class PasswordUtil {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

### 16.2 SQL注入防护

- 使用MyBatis-Plus的LambdaQueryWrapper
- 禁止字符串拼接SQL
- 对用户输入进行验证和过滤

### 16.3 XSS防护

```java
@Component
public class XssFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        XssHttpServletRequestWrapper wrappedRequest = new XssHttpServletRequestWrapper(
            (HttpServletRequest) request);
        
        chain.doFilter(wrappedRequest, response);
    }
}
```

## 17. 性能优化

### 17.1 数据库优化

- 合理使用索引
- 避免N+1查询问题
- 使用分页查询
- 配置数据库连接池

### 17.2 缓存策略

```java
@Service
public class ProductServiceImpl implements IProductService {
    
    @Cacheable(value = "products", key = "#id")
    public ProductVO getProductById(Integer id) {
        // 查询逻辑
    }
    
    @CacheEvict(value = "products", key = "#id")
    public void updateProduct(Integer id, ProductRequest request) {
        // 更新逻辑
    }
}
```

## 18. 总结

本规范说明书详细描述了基于Spring Boot框架的商城后台管理系统后端项目的技术架构、开发规范和实现方案。主要特点包括：

1. **技术栈现代化**：采用Spring Boot 3.x + JDK 17 + MyBatis-Plus的现代化技术栈
2. **架构清晰**：严格遵循三层架构设计，职责分离明确
3. **安全可靠**：集成JWT认证、密码加密、XSS防护等安全机制
4. **开发高效**：提供统一的响应格式、异常处理、API文档生成
5. **易于维护**：规范的代码结构、完善的注释和测试用例
6. **部署便捷**：支持多环境配置、Docker容器化部署

开发团队应严格按照本规范进行开发，确保项目的质量和可维护性。