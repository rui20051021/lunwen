# Fresh Logistics Backend

Fresh Logistics 冷链物流智能监测预警系统后端服务

## 技术栈

- **Java**: 17+
- **框架**: Spring Boot 3.2.0
- **安全**: Spring Security + JWT
- **数据库**: MySQL 8.0 + MyBatis Plus
- **缓存**: Redis 6.0+
- **消息队列**: Kafka 2.8+
- **API文档**: Swagger/OpenAPI 3
- **监控**: Spring Boot Actuator + Druid

## 项目结构

```
backend/
├── pom.xml                                 # Maven依赖配置
├── start-backend.bat                       # 启动脚本
├── README.md                              # 项目文档
└── src/main/java/com/freshlogistics/
    ├── FreshLogisticsApplication.java      # 启动类
    ├── entity/                            # 实体类
    │   ├── SysUser.java                   # 用户实体
    │   ├── SysRole.java                   # 角色实体
    │   ├── SysPermission.java             # 权限实体
    │   ├── Product.java                   # 产品实体
    │   ├── Supplier.java                  # 供应商实体
    │   ├── Vehicle.java                   # 车辆实体
    │   ├── Order.java                     # 订单实体
    │   ├── OrderItem.java                 # 订单明细实体
    │   └── AlertRule.java                 # 预警规则实体
    ├── mapper/                            # 数据访问层
    │   ├── SysUserMapper.java             # 用户Mapper
    │   ├── SysRoleMapper.java             # 角色Mapper
    │   ├── ProductMapper.java             # 产品Mapper
    │   ├── OrderMapper.java               # 订单Mapper
    │   └── OrderItemMapper.java           # 订单明细Mapper
    ├── service/                           # 业务逻辑层
    │   ├── SysUserService.java            # 用户服务接口
    │   ├── ProductService.java            # 产品服务接口
    │   ├── OrderService.java              # 订单服务接口
    │   ├── OrderItemService.java          # 订单明细服务接口
    │   ├── AlertRuleService.java          # 预警规则服务接口
    │   ├── WebSocketService.java          # WebSocket推送服务
    │   └── impl/                          # 服务实现类
    │       ├── SysUserServiceImpl.java    # 用户服务实现
    │       ├── ProductServiceImpl.java    # 产品服务实现
    │       ├── OrderServiceImpl.java      # 订单服务实现
    │       └── OrderItemServiceImpl.java  # 订单明细服务实现
    ├── controller/                        # 控制器层
    │   ├── AuthController.java            # 认证控制器
    │   ├── ProductController.java         # 产品控制器
    │   ├── OrderController.java           # 订单控制器
    │   └── AlertController.java           # 预警控制器
    ├── dto/                               # 数据传输对象
    │   ├── LoginRequest.java              # 登录请求DTO
    │   ├── UserCreateRequest.java         # 用户创建DTO
    │   ├── ProductCreateRequest.java      # 产品创建DTO
    │   └── OrderCreateRequest.java        # 订单创建DTO
    ├── vo/                                # 视图对象
    │   ├── LoginResponse.java             # 登录响应VO
    │   └── PageResponse.java              # 分页响应VO
    ├── config/                            # 配置类
    │   ├── MyBatisPlusConfig.java         # MyBatis Plus配置
    │   ├── SecurityConfig.java            # Spring Security配置
    │   ├── RedisConfig.java               # Redis配置
    │   ├── SwaggerConfig.java             # Swagger配置
    │   └── WebSocketConfig.java           # WebSocket配置
    ├── security/                          # 安全组件
    │   ├── JwtAuthenticationFilter.java   # JWT认证过滤器
    │   └── JwtAuthenticationEntryPoint.java # JWT认证入口点
    ├── utils/                             # 工具类
    │   ├── JwtUtils.java                  # JWT工具类
    │   ├── PasswordUtils.java             # 密码工具类
    │   ├── RedisUtils.java                # Redis工具类
    │   └── DateTimeUtils.java             # 日期时间工具类
    ├── common/                            # 公共组件
    │   └── ApiResponse.java               # 统一响应类
    └── exception/                         # 异常处理
        ├── BusinessException.java         # 业务异常类
        └── GlobalExceptionHandler.java    # 全局异常处理器
```

## 核心功能

### 1. 用户权限管理
- **RBAC权限模型**: 用户-角色-权限三层架构
- **JWT认证**: 无状态Token认证
- **多角色支持**: 供应商/物流商/采购商/监管员/管理员
- **权限控制**: 方法级权限控制

### 2. 产品管理
- **产品信息**: 基本信息、分类、规格
- **冷链要求**: 温湿度范围、保质期
- **状态管理**: 启用/禁用状态控制
- **批量操作**: 批量导入、删除、状态更新

### 3. 订单管理
- **全流程**: 创建→确认→运输→签收→完成
- **状态机**: 严格的状态转换控制
- **明细管理**: 产品明细、批次追踪
- **权限隔离**: 按角色限制订单访问

### 4. 预警系统
- **动态规则**: JSON格式的灵活规则配置
- **多类型预警**: 超时/温控/路径偏离
- **实时推送**: WebSocket消息推送
- **处理流程**: 预警生成→处理→反馈

### 5. 实时通信
- **WebSocket**: 实时消息推送
- **多频道**: 预警/位置/订单/通知
- **用户定向**: 支持点对点消息推送

## API接口

### 认证接口
- `POST /auth/login` - 用户登录
- `POST /auth/register` - 用户注册
- `POST /auth/logout` - 用户登出
- `GET /auth/me` - 获取当前用户信息

### 产品接口
- `GET /products` - 分页查询产品
- `POST /products` - 创建产品
- `PUT /products/{id}` - 更新产品
- `DELETE /products/{id}` - 删除产品
- `GET /products/type/{type}` - 按类型查询
- `GET /products/temperature-control` - 查询温控产品

### 订单接口
- `GET /orders` - 分页查询订单
- `POST /orders` - 创建订单
- `GET /orders/{id}` - 获取订单详情
- `POST /orders/{id}/confirm` - 确认订单
- `POST /orders/{id}/ship` - 订单发货
- `POST /orders/{id}/deliver` - 订单签收
- `POST /orders/{id}/cancel` - 取消订单

### 预警接口
- `GET /alerts/rules` - 分页查询预警规则
- `POST /alerts/rules` - 创建预警规则
- `PUT /alerts/rules/{id}` - 更新预警规则
- `POST /alerts/check/temperature` - 检查温度预警
- `POST /alerts/check/timeout` - 检查超时预警

## 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/freshlogistics
    username: root
    password: 123456
```

### Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

### JWT配置
```yaml
fresh-logistics:
  jwt:
    secret: FreshLogistics2025SecretKey
    expiration: 604800 # 7天
    header: Authorization
    token-prefix: Bearer 
```

## 快速启动

### 方式一：使用启动脚本（推荐）
```bash
# Windows
start-backend.bat

# Linux/Mac
./start-backend.sh
```

### 方式二：Maven命令
```bash
# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

### 方式三：JAR包运行
```bash
# 打包
mvn clean package

# 运行
java -jar target/fresh-logistics-backend-1.0.0.jar
```

## 环境要求

- **Java**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+ (数据库: freshlogistics)
- **Redis**: 6.0+ (可选，用于缓存)
- **Kafka**: 2.8+ (可选，用于消息队列)

## 默认账户

- **用户名**: admin
- **密码**: admin123
- **角色**: 系统管理员

## 访问地址

启动成功后，可以访问以下地址：

- **API文档**: http://localhost:8080/api/swagger-ui.html
- **健康检查**: http://localhost:8080/api/actuator/health
- **Druid监控**: http://localhost:8080/api/druid (admin/admin123)

## 开发指南

### 1. 添加新接口
1. 在对应的Controller中添加接口方法
2. 使用`@Operation`注解添加接口文档
3. 使用`@PreAuthorize`注解添加权限控制

### 2. 添加新实体
1. 在entity包中创建实体类
2. 使用MyBatis Plus注解
3. 在mapper包中创建对应的Mapper接口

### 3. 业务逻辑开发
1. 在service包中定义服务接口
2. 在service.impl包中实现业务逻辑
3. 使用`@Transactional`注解保证事务一致性

### 4. 异常处理
- 业务异常使用`BusinessException`
- 参数验证使用`@Valid`注解
- 全局异常由`GlobalExceptionHandler`统一处理

## 注意事项

1. **字符编码**: 全程使用UTF-8编码
2. **事务管理**: 重要操作必须加事务注解
3. **权限控制**: 接口必须添加权限注解
4. **日志记录**: 重要操作添加日志记录
5. **异常处理**: 统一使用ApiResponse返回

## 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

## 部署

### 开发环境
使用`application-dev.yml`配置文件，执行`start-backend.bat`即可。

### 生产环境
1. 修改`application-prod.yml`配置
2. 执行`mvn clean package`打包
3. 使用`java -jar`运行JAR包

---

**Fresh Logistics Backend** - 为冷链物流提供可靠的后端支撑！ 🚀
