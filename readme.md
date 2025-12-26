# Fresh Logistics 基于hdoop技术的冷链物流智能监测预警系统

## 项目概述

Fresh Logistics 是基于前后端分离架构的冷链物流智能监测预警系统，通过实时监控和智能预警保障生鲜农产品全程品质安全。本项目采用分层架构设计，支持多角色权限管理，提供完整的冷链物流监控解决方案。

### 核心目标
- 🎯 **实时预警响应**：预警响应延迟≤3秒，支持超时/温控/路径三类预警
- 📊 **数据驱动分析**：时效分析、损耗分析、预警统计三维度数据分析
- 🚀 **降本增效**：货损降低率6%-8%，减少40%人工巡检成本
- 🔒 **多角色协同**：供应商、物流商、采购商、监管员四角色权限隔离
- 🌐 **轨迹可视化**：集成高德地图API，实时展示配送轨迹和车辆位置

### 预期成果
- 预警响应延迟≤3秒
- 数据处理吞吐量≥5万条/秒
- 货损降低率6%-8%
- 支持1000+传感器并发处理

## 功能设计

### (一) 前台功能设计
1. **多角色注册与登录**
   - 供应商、物流商、采购商、监管员四类角色账号注册及登录
   - RBAC权限控制，实现数据隔离

2. **订单全流程管理**
   - 订单创建、状态修改（创建→配送中→签收/异常）
   - 实时跟踪订单配送轨迹（起止点标记、车辆位置更新）

3. **配送详情可视化**
   - 展示承运车辆信息（车牌、司机联系方式）
   - 对比预计/实际抵达时间
   - 实时提示预警事件（超时/温控异常/路径偏离）

4. **数据分析报告**
   - 时效分析：平均配送时长、延迟订单占比统计
   - 损耗分析：温控失效与货损率关联分析
   - 预警统计：三类预警触发频率与处理效率汇总

5. **智能预警处理**
   - 实时接收超时/温控/路径偏离预警消息
   - 在线提交预警处理反馈

### (二) 后台功能设计
1. **管理员登录**
   - 独立后台管理系统登录入口

2. **用户与权限管理**
   - 四类角色账号增删改查
   - 权限分级配置（如供应商仅管理自家订单）

3. **动态预警规则配置**
   | 规则类型 | 可配置参数 |
   |---------|------------|
   | 超时规则 | 按路线设置最大延迟容忍时长 |
   | 温控规则 | 按农产品类型设定温湿度区间 |
   | 路径规则 | 设置电子围栏偏离半径 |

4. **预警事件审计**
   - 查询历史预警记录（触发时间、类型、处理人）
   - 追踪预警处理全流程（响应时长、处理结果）

5. **基础数据管理**
   - 车辆库：绑定冷链设备、监控实时温湿度
   - 司机库：管理驾驶证及冷链资质（有效期预警）
   - 路线库：预设配送路径坐标及里程

### 技术特性
- 🔄 **分层架构**：前端层 + 后端层 + 实时层 + 计算层 + 存储层
- 🔄 **前端现代化**：Vue.js 3 + Element Plus + TypeScript + 高德地图API
- 🔄 **数据可视化**：ECharts图表 + 轨迹可视化看板
- 🔄 **实时通信**：Kafka数据管道 + WebSocket推送预警消息
- 🔄 **安全认证**：JWT Token + Spring Security RBAC权限模型
- 🔄 **数据存储**：MySQL主数据库 + Redis缓存 + HDFS历史数据存储





## 开发环境要求

### 必需环境
1. **Java 17+** - 后端服务运行环境
2. **Maven 3.6+** - 项目构建工具
3. **Node.js 16+** - 前端开发环境
4. **MySQL 8.0+** - 主数据库（主从复制+读写分离）
   - 数据库名：`freshlogistics`
   - 用户名：`root`
   - 密码：`123456`
5. **Redis 6.0+** - 实时缓存（车辆位置响应≤50ms）
6. **Kafka 2.8+** - 数据管道（传感器数据流处理）
7. **HDFS** - 历史数据仓库（传感器时序数据存储）

### 开发工具推荐
- **IDE**: IntelliJ IDEA / VS Code
- **数据库工具**: Navicat / DBeaver
- **API测试**: Postman / Apifox
- **版本控制**: Git
- **地图服务**: 高德地图API密钥

### 端口规划
| 服务 | 端口 | 说明 |
|------|------|------|
| 前端服务 | 3000 | Vue.js开发服务器 |
| 后端服务 | 8080 | Spring Boot应用 |
| MySQL | 3306 | 数据库服务 |
| Redis | 6379 | 缓存服务 |
| Kafka | 9092 | 消息队列服务 |
| HDFS NameNode | 9870 | HDFS管理界面 |

---

## 系统架构设计

### 分层架构设计
```
┌─────────────────────────────────────────┐
│              用户层                      │
│   供应商 | 物流商 | 采购商 | 监管员      │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│             前端层                       │
│  Vue.js + 高德地图API + 轨迹可视化看板   │
│  多角色自适应界面 + 预警统计大屏        │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            后端层                        │
│  Spring Boot + RBAC权限模型 + MyBatis   │
│  订单状态机 + 动态规则引擎              │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            实时层                        │
│  Kafka接收传感器数据流                   │
│  WebSocket推送预警消息                   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            计算层                        │
│ 实时预警：超时+温控+路径偏离预警         │
│ 离线分析：Spark SQL时效 + MapReduce损耗  │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│           存储层                         │
│MySQL(业务数据) + Redis(实时缓存) + HDFS(历史数据)│
└─────────────────────────────────────────┘
```

### 核心模块设计
1. **动态规则引擎**
   - 超时容忍时长配置（按路线）
   - 温控区间配置（按农产品类型）
   - 偏离半径配置（电子围栏）

2. **订单状态机**
   - 状态流转：创建→配送中→签收/异常
   - 集成高德地图API轨迹跟踪

3. **实时预警系统**
   - 超时预警：基于路线预估时长+实时交通
   - 温控预警：BP神经网络分析传感器数据
   - 路径预警：GPS坐标与预设路线空间匹配

4. **数据分析模块**
   - Spark SQL计算平均配送时长、延迟订单占比
   - MapReduce关联温控失效次数与货损率

## 技术栈选型

### 前端技术栈
- **框架**: Vue.js 3.x + Composition API
- **UI组件**: Element Plus
- **开发语言**: TypeScript
- **状态管理**: Pinia
- **路由管理**: Vue Router 4
- **构建工具**: Vite
- **数据可视化**: ECharts + 预警统计大屏
- **地图服务**: 高德地图API（轨迹可视化）
- **HTTP客户端**: Axios
- **实时通信**: WebSocket（预警消息推送）

### 后端技术栈
- **框架**: Spring Boot 3.x（单体应用）
- **安全框架**: Spring Security + JWT + RBAC权限模型
- **数据访问**: MyBatis Plus
- **数据库**: MySQL 8.0+（主从复制+读写分离）
- **缓存**: Redis 6.0+（车辆位置实时缓存）
- **消息队列**: Kafka 2.8+（传感器数据流处理）
- **大数据存储**: HDFS（传感器历史数据）
- **实时计算**: Spark SQL（时效分析）
- **批处理**: MapReduce（损耗率计算）
- **机器学习**: BP神经网络（温控预警模型）
- **文档**: Swagger/OpenAPI 3

### 数据采集层
- **硬件接入**: 温湿度传感器（±0.5℃精度）→4G/5G传输
- **定位追踪**: 车载GPS设备→坐标回传→电子围栏匹配
- **数据管道**: Kafka接收传感器数据流

### 开发工具链
- **构建工具**: Maven 3.6+
- **版本控制**: Git
- **容器化**: Docker（可选）
- **部署方式**: 单机部署（开发环境）

## 用户角色设计

### 四角色权限规划
| 角色 | 核心需求 | 主要权限 | 功能模块 |
|------|----------|----------|----------|
| 供应商（supplier） | 实时监控订单异常事件（如温控失效） | 订单管理、产品管理 | 订单创建、状态跟踪、预警接收 |
| 物流商（logistics） | 降低货损率（目标6%-8%） | 运输管理、车辆调度 | 车辆管理、司机调度、轨迹跟踪 |
| 采购商（purchaser） | 获取预计/实际抵达时间对比及路径轨迹 | 订单查看、收货确认 | 订单查询、配送跟踪、收货管理 |
| 监管员（regulator） | 全链路审计与高频预警事件分析 | 监管查看、报表导出、审计 | 监管仪表板、预警审计、数据分析 |
| 管理员（admin） | 系统配置和用户管理 | 系统全部权限 | 用户管理、规则配置、系统维护 |

### 权限控制模型
```
用户(User) ←→ 用户角色(UserRole) ←→ 角色(Role)
                                      ↓
                              角色权限(RolePermission)
                                      ↓
                              权限(Permission) ←→ 资源(Resource)
```



## 数据库设计

### 核心数据表
- **用户管理**: sys_user, sys_role, sys_permission
- **供应商模块**: suppliers, products, supplier_products
- **物流模块**: vehicles, drivers, transports, transport_routes
- **订单模块**: orders, order_items, logistics_orders
- **监控模块**: sensor_data, alert_records, temperature_logs
- **监管模块**: compliance_checks, regulator_reports
- **大数据存储**: HDFS存储历史数据、传感器数据归档

### 数据库配置
```yaml
# application-mysql.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/freshlogistics?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 开发指南

### 项目初始化
```bash
# 1. 创建项目目录结构
mkdir fresh-logistics
cd fresh-logistics

# 2. 初始化后端项目
mkdir backend
cd backend
# 创建Spring Boot单体应用

# 3. 初始化前端项目
cd ..
npm create vue@latest frontend
cd frontend
npm install

# 4. 本机环境配置
# 安装MySQL 8.0+
# 安装Redis 6.0+
# 安装Kafka 2.8+
# 安装HDFS（可选，用于历史数据存储）
# 配置Java 17+环境变量
# 安装Maven 3.6+
# 安装Node.js 16+
# 申请高德地图API密钥
```

### 数据库初始化
```sql
-- 创建数据库
CREATE DATABASE freshlogistics CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用root用户连接（用户名：root，密码：123456）
-- 创建核心数据表
-- 用户管理: sys_user, sys_role, sys_permission
-- 订单模块: orders, order_items
-- 车辆模块: vehicles, drivers
-- 预警模块: alert_rules, alert_records
-- 传感器数据: sensor_data, temperature_logs
```

### 开发环境启动
```bash
# 1. 启动数据库服务
net start mysql80
net start redis

# 2. 启动Kafka服务（用于传感器数据流）
# Windows环境下启动Kafka
kafka-server-start.bat config/server.properties

# 3. 启动后端服务（单体应用）
cd backend
mvn spring-boot:run

# 4. 启动前端服务
cd frontend
npm run dev
```

### 访问地址
- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **HDFS管理界面**: http://localhost:9870（可选）

## 开发路线图

### 第一阶段：基础架构搭建（2周）
- [ ] 项目脚手架搭建（前后端分离）
- [ ] 数据库设计和初始化
- [ ] RBAC权限模型实现
- [ ] 四角色用户管理
- [ ] 前端基础框架搭建

### 第二阶段：动态规则引擎（3周）
- [ ] 超时规则配置（按路线设置延迟容忍时长）
- [ ] 温控规则配置（按农产品类型设定温湿度区间）
- [ ] 路径规则配置（电子围栏偏离半径）
- [ ] 规则引擎核心逻辑实现
- [ ] 预警触发机制

### 第三阶段：订单状态机与轨迹跟踪（2周）
- [ ] 订单状态机（创建→配送中→签收/异常）
- [ ] 高德地图API集成
- [ ] 车辆实时位置跟踪
- [ ] 配送轨迹可视化
- [ ] GPS坐标与预设路线匹配

### 第四阶段：实时预警系统
- [ ] Kafka传感器数据流接入
- [ ] WebSocket实时消息推送
- [ ] 三类预警实现：
  - [ ] 超时预警（路线预估+实时交通）
  - [ ] 温控预警（BP神经网络模型）
  - [ ] 路径预警（空间匹配算法）
- [ ] 预警处理反馈机制

### 第五阶段：数据分析与可视化
- [ ] 时效分析：Spark SQL计算配送时长统计
- [ ] 损耗分析：MapReduce关联温控失效与货损率
- [ ] 预警统计：三维度图表展示
- [ ] 轨迹可视化看板
- [ ] 监管员审计视图

### 第六阶段：系统集成与测试
- [ ] 性能测试（预警响应≤3秒，处理≥5万条/秒）
- [ ] 1000+传感器并发压测
- [ ] 货损降低率验证
- [ ] 部署优化

## 核心功能模块

### 1. 用户权限管理
- 用户注册、登录、注销
- 角色权限分配
- 菜单权限控制
- 操作日志记录

### 2. 供应商管理
- 供应商信息维护
- 产品目录管理
- 供应商评级
- 合作协议管理

### 3. 物流运输管理
- 车辆档案管理
- 司机信息管理
- 运输任务调度
- 运输轨迹跟踪

### 4. 订单管理
- 订单创建和处理
- 订单状态跟踪
- 物流配送安排
- 收货确认流程

### 5. 监控预警
- 实时温度监控
- 异常情况预警
- 报警信息处理
- 历史数据查询

### 6. 数据分析
- 运输效率统计
- 成本分析报告
- 质量损耗分析
- 业务趋势预测
- 大数据挖掘分析
- Hadoop分布式计算

### 7. 监管合规
- 监管员专属仪表板
- 合规检查记录
- 监管报告生成
- 审计追踪功能

## 项目结构规划

```
fresh-logistics/
├── README.md                # 项目说明文档
├── .gitignore              # Git忽略文件
├── start-services.bat      # Windows服务启动脚本
│
├── frontend/               # 前端项目（Vue.js 3）
│   ├── public/            # 静态资源
│   ├── src/
│   │   ├── api/          # API接口定义
│   │   ├── components/   # 公共组件
│   │   ├── views/        # 页面组件
│   │   │   ├── supplier/   # 供应商界面
│   │   │   ├── logistics/  # 物流商界面
│   │   │   ├── purchaser/  # 采购商界面
│   │   │   ├── regulator/  # 监管员界面
│   │   │   └── admin/      # 管理员界面
│   │   ├── router/       # 路由配置
│   │   ├── store/        # 状态管理（Pinia）
│   │   ├── utils/        # 工具函数
│   │   ├── styles/       # 样式文件
│   │   └── maps/         # 高德地图组件
│   ├── package.json
│   └── vite.config.ts
│
├── backend/               # 后端单体应用（Spring Boot）
│   ├── pom.xml           # Maven配置文件
│   └── src/main/java/com/freshlogistics/
│       ├── FreshLogisticsApplication.java  # 启动类
│       ├── controller/     # 控制器层
│       │   ├── AuthController.java         # 认证控制器
│       │   ├── OrderController.java        # 订单管理
│       │   ├── VehicleController.java      # 车辆管理
│       │   ├── AlertController.java        # 预警管理
│       │   └── AnalysisController.java     # 数据分析
│       ├── service/        # 业务逻辑层
│       │   ├── UserService.java            # 用户服务
│       │   ├── OrderService.java           # 订单服务
│       │   ├── AlertRuleService.java       # 规则引擎服务
│       │   ├── TrackingService.java        # 轨迹跟踪服务
│       │   └── AnalysisService.java        # 数据分析服务
│       ├── mapper/         # 数据访问层（MyBatis）
│       ├── entity/         # 实体类
│       ├── dto/            # 数据传输对象
│       ├── config/         # 配置类
│       │   ├── SecurityConfig.java         # 安全配置
│       │   ├── KafkaConfig.java           # Kafka配置
│       │   └── WebSocketConfig.java       # WebSocket配置
│       └── utils/          # 工具类
│           ├── MapUtils.java              # 地图工具
│           ├── AlertUtils.java            # 预警工具
│           └── AnalysisUtils.java         # 分析工具
│   └── src/main/resources/
│       ├── application.yml # 配置文件
│       ├── mapper/         # MyBatis映射文件
│       └── static/         # 静态资源
│
├── database/              # 数据库相关
│   ├── init/             # 初始化脚本
│   │   ├── schema.sql    # 表结构
│   │   └── data.sql      # 初始数据
│   └── docs/             # 数据库设计文档
│
├── scripts/               # 脚本文件
│   ├── start-dev.bat     # 开发环境启动
│   └── deploy.sh         # 部署脚本
│
└── docs/                  # 项目文档
    ├── api/              # API文档
    ├── design/           # 设计文档
    └── user-manual/      # 用户手册
```

## 开发规范

### 代码规范
#### 后端规范
- 遵循阿里巴巴Java开发手册
- 使用Spring Boot官方推荐的项目结构
- 统一异常处理和返回格式
- 完善的单元测试覆盖

#### 前端规范
- 遵循Vue.js 3官方风格指南
- 使用TypeScript进行类型约束
- 组件命名采用PascalCase
- 统一的API调用和错误处理

### Git提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

### 分支管理
- `main`: 主分支，用于生产环境
- `develop`: 开发分支，用于集成测试
- `feature/*`: 功能分支
- `hotfix/*`: 紧急修复分支

## 测试策略

### 测试分层
- **单元测试**: JUnit 5 + Mockito
- **集成测试**: Spring Boot Test
- **接口测试**: Postman + Newman
- **前端测试**: Vitest + Vue Test Utils
- **端到端测试**: Cypress

### 质量保证
- 代码覆盖率要求: >80%
- 静态代码分析: SonarQube
- 安全扫描: OWASP ZAP
- 性能测试: JMeter

---

## 部署方案

### 开发环境（推荐）
- **架构**: 前后端分离单体应用
- **部署方式**: 本机开发环境
- **服务启动**: 
  - 后端：Spring Boot单体应用（端口8080）
  - 前端：Vue.js开发服务器（端口3000）
- **数据库**: 本机MySQL + Redis + Kafka
- **优势**: 简单易用，适合毕业设计开发

### 测试环境
- **部署方式**: 单机部署
- **配置管理**: application-test.yml环境配置
- **服务监控**: Spring Boot Actuator健康检查
- **性能测试**: 支持1000+传感器并发测试

### 生产环境（可选）
- **部署方式**: Docker容器化部署
- **负载均衡**: Nginx反向代理
- **监控告警**: 预警响应延迟监控
- **扩展性**: 支持水平扩展

### 技术可行性验证
| 测试项 | 目标值 | 验证方法 |
|--------|--------|----------|
| 预警响应延迟 | ≤3秒 | 模拟温控异常事件压测（1000+传感器并发） |
| 数据处理吞吐量 | ≥5万条/秒 | Hadoop集群资源监控（CPU/内存/网络） |
| 货损降低率 | 6%-8% | 对比上线前后冷链失效订单占比 |

---

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

---

**Fresh Logistics** - 让冷链物流更智能、更安全、更高效！

## 项目状态

🚀 **项目状态**: 毕业论文开发中  
📅 **最后更新**: 2025-01-27  
👥 **开发团队**: 个人毕业设计  
🎯 **当前阶段**: 架构简化完成，准备开发实现

### 简化说明
本项目已根据开题报告要求进行架构简化：
- ✅ **保持功能完整**: 前台5大功能 + 后台5大功能全部保留
- ✅ **简化技术架构**: 从微服务改为单体分层架构，降低复杂度
- ✅ **优化部署方案**: 支持本机开发环境，便于毕业设计实现
- ✅ **明确性能目标**: 预警响应≤3秒，数据处理≥5万条/秒，货损降低6%-8%

### 核心亮点
- 🎯 **实时预警系统**: 超时/温控/路径三类预警，响应延迟≤3秒
- 📊 **动态规则引擎**: 支持按路线、农产品类型、电子围栏的灵活配置
- 🗺️ **轨迹可视化**: 集成高德地图API，实时展示配送轨迹
- 🔐 **多角色权限**: RBAC模型支持供应商、物流商、采购商、监管员四角色
- 📈 **数据分析**: 时效、损耗、预警三维度统计分析