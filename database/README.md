# Fresh Logistics 数据库设计文档

## 概述

本文档详细描述了Fresh Logistics冷链物流智能监测预警系统的数据库设计方案。数据库采用MySQL 8.0+，使用utf8mb4字符集，确保全程数据的完整存储与正确显示。

## 数据库基本信息

- **数据库名称**: `freshlogistics`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_unicode_ci`
- **存储引擎**: `InnoDB`
- **版本要求**: `MySQL 8.0+`

## 架构设计原则

### 1. 设计原则
- **规范性**: 遵循数据库设计规范，采用标准命名约定
- **一致性**: 统一的字段类型、命名规则和约束条件
- **扩展性**: 支持业务发展和功能扩展需求
- **性能优化**: 合理的索引设计和查询优化
- **安全性**: 完善的权限控制和数据保护机制

### 2. 命名规范
- **表名**: 小写字母，单词间用下划线分隔（如：`sys_user`）
- **字段名**: 小写字母，单词间用下划线分隔（如：`user_name`）
- **索引名**: `idx_` + 字段名（如：`idx_username`）
- **唯一约束**: `uk_` + 字段名（如：`uk_user_role`）
- **外键**: `fk_` + 表名_字段名（如：`fk_user_role_user_id`）

### 3. 数据类型规范
- **主键**: `BIGINT AUTO_INCREMENT`
- **字符串**: `VARCHAR(长度)` 或 `TEXT`
- **时间**: `TIMESTAMP` 或 `DATETIME`
- **金额**: `DECIMAL(12,2)`
- **坐标**: `DECIMAL(10,7)` （GPS精度要求）
- **状态**: `ENUM` 或 `TINYINT`
- **JSON数据**: `JSON` 类型（MySQL 5.7+）

## 数据表结构

### 1. 用户权限管理模块

#### 1.1 用户表 (sys_user)
管理系统中的所有用户信息，支持四种角色类型。

```sql
-- 核心字段
id              BIGINT          用户ID（主键）
username        VARCHAR(50)     用户名（唯一）
password        VARCHAR(255)    密码（加密存储）
real_name       VARCHAR(100)    真实姓名
user_type       ENUM            用户类型（supplier/logistics/purchaser/regulator/admin）
company_id      BIGINT          关联公司ID
status          TINYINT         状态（0=禁用，1=启用）
```

#### 1.2 角色表 (sys_role)
定义系统中的角色信息。

```sql
-- 核心字段
id              BIGINT          角色ID（主键）
role_code       VARCHAR(50)     角色编码（唯一）
role_name       VARCHAR(100)    角色名称
description     TEXT            角色描述
status          TINYINT         状态
```

#### 1.3 权限表 (sys_permission)
定义系统中的权限信息，支持菜单、按钮、API等资源类型。

```sql
-- 核心字段
id                 BIGINT          权限ID（主键）
permission_code    VARCHAR(100)    权限编码（唯一）
permission_name    VARCHAR(100)    权限名称
resource_type      ENUM            资源类型（menu/button/api/data）
resource_path      VARCHAR(255)    资源路径
parent_id          BIGINT          父权限ID
```

#### 1.4 关联表
- `sys_user_role`: 用户角色关联表
- `sys_role_permission`: 角色权限关联表

### 2. 供应商管理模块

#### 2.1 供应商表 (suppliers)
管理供应商基本信息和资质等级。

```sql
-- 核心字段
id                    BIGINT          供应商ID（主键）
supplier_code         VARCHAR(50)     供应商编码（唯一）
supplier_name         VARCHAR(200)    供应商名称
qualification_level   ENUM            资质等级（A/B/C/D）
cooperation_status    ENUM            合作状态
credit_rating         DECIMAL(3,2)    信用评级
```

#### 2.2 产品表 (products)
管理产品信息，包含冷链要求。

```sql
-- 核心字段
id              BIGINT          产品ID（主键）
product_code    VARCHAR(50)     产品编码（唯一）
product_name    VARCHAR(200)    产品名称
product_type    ENUM            产品类型（fruit/vegetable/meat/seafood/dairy/other）
min_temp        DECIMAL(4,1)    最低温度（℃）
max_temp        DECIMAL(4,1)    最高温度（℃）
min_humidity    DECIMAL(5,2)    最低湿度（%）
max_humidity    DECIMAL(5,2)    最高湿度（%）
shelf_life      INT             保质期（天）
```

#### 2.3 关联表
- `supplier_products`: 供应商产品关联表
- `product_categories`: 产品分类表

### 3. 物流运输管理模块

#### 3.1 车辆表 (vehicles)
管理冷链运输车辆信息和设备。

```sql
-- 核心字段
id                      BIGINT          车辆ID（主键）
vehicle_code            VARCHAR(50)     车辆编码（唯一）
license_plate           VARCHAR(20)     车牌号（唯一）
vehicle_type            ENUM            车辆类型
load_capacity           DECIMAL(8,2)    载重量（吨）
volume_capacity         DECIMAL(8,2)    容积（立方米）
gps_device_id          VARCHAR(100)    GPS设备ID
temperature_sensor_id   VARCHAR(100)    温度传感器ID
vehicle_status          ENUM            车辆状态
```

#### 3.2 司机表 (drivers)
管理司机信息和资质证件。

```sql
-- 核心字段
id                      BIGINT          司机ID（主键）
driver_code             VARCHAR(50)     司机编码（唯一）
name                    VARCHAR(100)    姓名
id_card                 VARCHAR(18)     身份证号（唯一）
driving_license         VARCHAR(50)     驾驶证号
license_expiry          DATE            驾驶证有效期
cold_chain_certificate  VARCHAR(100)    冷链从业资格证号
certificate_expiry      DATE            资格证有效期
driver_status           ENUM            司机状态
```

#### 3.3 运输路线表 (transport_routes)
管理预设运输路线和电子围栏。

```sql
-- 核心字段
id                      BIGINT          路线ID（主键）
route_code              VARCHAR(50)     路线编码（唯一）
route_name              VARCHAR(200)    路线名称
start_longitude         DECIMAL(10,7)   起点经度
start_latitude          DECIMAL(10,7)   起点纬度
end_longitude           DECIMAL(10,7)   终点经度
end_latitude            DECIMAL(10,7)   终点纬度
route_points            TEXT            路径坐标点集合（JSON）
estimated_distance      DECIMAL(8,2)    预估距离（公里）
max_delay_tolerance     INT             最大延迟容忍时长（分钟）
fence_radius            INT             电子围栏偏离半径（米）
```

#### 3.4 运输任务表 (transports)
管理具体的运输任务执行。

```sql
-- 核心字段
id                      BIGINT          运输任务ID（主键）
transport_code          VARCHAR(50)     运输单号（唯一）
order_id                BIGINT          关联订单ID
vehicle_id              BIGINT          车辆ID
driver_id               BIGINT          司机ID
planned_start_time      DATETIME        计划出发时间
actual_start_time       DATETIME        实际出发时间
planned_arrival_time    DATETIME        计划到达时间
actual_arrival_time     DATETIME        实际到达时间
current_longitude       DECIMAL(10,7)   当前经度
current_latitude        DECIMAL(10,7)   当前纬度
transport_status        ENUM            运输状态
```

### 4. 订单管理模块

#### 4.1 订单表 (orders)
管理订单主信息。

```sql
-- 核心字段
id                      BIGINT          订单ID（主键）
order_code              VARCHAR(50)     订单编号（唯一）
supplier_id             BIGINT          供应商ID
purchaser_id            BIGINT          采购商ID
order_status            ENUM            订单状态（created/confirmed/in_transit/delivered/completed/cancelled/exception）
total_amount            DECIMAL(12,2)   订单总金额
pickup_address          TEXT            取货地址
delivery_address        TEXT            送货地址
required_delivery_time  DATETIME        要求送达时间
temperature_requirement VARCHAR(50)     温度要求
```

#### 4.2 订单明细表 (order_items)
管理订单的产品明细。

```sql
-- 核心字段
id              BIGINT          订单明细ID（主键）
order_id        BIGINT          订单ID
product_id      BIGINT          产品ID
quantity        DECIMAL(10,3)   数量
unit_price      DECIMAL(10,2)   单价
total_price     DECIMAL(12,2)   小计金额
product_batch   VARCHAR(100)    产品批次号
production_date DATE            生产日期
expiry_date     DATE            到期日期
```

#### 4.3 物流订单表 (logistics_orders)
管理物流配送信息。

```sql
-- 核心字段
id                  BIGINT          物流订单ID（主键）
logistics_code      VARCHAR(50)     物流单号（唯一）
order_id            BIGINT          关联订单ID
transport_id        BIGINT          运输任务ID
logistics_status    ENUM            物流状态
pickup_time         DATETIME        取货时间
delivery_time       DATETIME        送达时间
signature_person    VARCHAR(100)    签收人
signature_time      DATETIME        签收时间
```

### 5. 监控预警模块

#### 5.1 预警规则表 (alert_rules)
管理动态预警规则配置。

```sql
-- 核心字段
id                  BIGINT          规则ID（主键）
rule_code           VARCHAR(50)     规则编码（唯一）
rule_name           VARCHAR(200)    规则名称
rule_type           ENUM            规则类型（timeout/temperature/humidity/route_deviation）
rule_category       ENUM            规则分类（product_type/route/vehicle/global）
rule_condition      JSON            规则条件（JSON格式）
threshold_value     DECIMAL(10,3)   阈值
comparison_operator ENUM            比较运算符
alert_level         ENUM            预警级别（info/warning/error/critical）
```

#### 5.2 预警记录表 (alert_records)
记录预警事件和处理过程。

```sql
-- 核心字段
id              BIGINT          预警记录ID（主键）
alert_code      VARCHAR(50)     预警编号（唯一）
rule_id         BIGINT          触发规则ID
alert_type      ENUM            预警类型
alert_level     ENUM            预警级别
related_type    ENUM            关联对象类型（order/transport/vehicle/sensor）
related_id      BIGINT          关联对象ID
alert_message   TEXT            预警消息
trigger_value   DECIMAL(10,3)   触发值
alert_status    ENUM            处理状态（pending/processing/processed/ignored/false_alarm）
processor_id    BIGINT          处理人ID
process_time    DATETIME        处理时间
```

#### 5.3 传感器数据表 (sensor_data)
存储实时传感器数据。

```sql
-- 核心字段
id              BIGINT          传感器数据ID（主键）
sensor_id       VARCHAR(100)    传感器ID
sensor_type     ENUM            传感器类型（temperature/humidity/gps/door/shock/light）
vehicle_id      BIGINT          关联车辆ID
transport_id    BIGINT          关联运输任务ID
data_value      DECIMAL(10,4)   数据值
longitude       DECIMAL(10,7)   经度
latitude        DECIMAL(10,7)   纬度
collection_time TIMESTAMP       采集时间
```

#### 5.4 温度记录表 (temperature_logs)
存储历史温度数据。

```sql
-- 核心字段
id                  BIGINT          温度记录ID（主键）
sensor_id           VARCHAR(100)    温度传感器ID
vehicle_id          BIGINT          关联车辆ID
transport_id        BIGINT          关联运输任务ID
temperature         DECIMAL(5,2)    温度值（℃）
humidity            DECIMAL(5,2)    湿度值（%）
location_longitude  DECIMAL(10,7)   位置经度
location_latitude   DECIMAL(10,7)   位置纬度
is_normal           TINYINT         是否正常
alert_triggered     TINYINT         是否触发预警
record_time         TIMESTAMP       记录时间
```

### 6. 监管合规模块

#### 6.1 合规检查记录表 (compliance_checks)
管理监管员的合规检查活动。

```sql
-- 核心字段
id                  BIGINT          合规检查ID（主键）
check_code          VARCHAR(50)     检查编号（唯一）
check_type          ENUM            检查类型（routine/spot/complaint/follow_up）
check_category      ENUM            检查分类（vehicle/driver/facility/process/documentation）
target_type         ENUM            检查对象类型（supplier/logistics_company/vehicle/driver/route）
target_id           BIGINT          检查对象ID
regulator_id        BIGINT          监管员ID
check_date          DATE            检查日期
compliance_score    DECIMAL(5,2)    合规评分（0-100）
violations_found    INT             发现违规项数
check_status        ENUM            检查状态
```

#### 6.2 监管报告表 (regulator_reports)
管理监管报告的生成和审批。

```sql
-- 核心字段
id                  BIGINT          监管报告ID（主键）
report_code         VARCHAR(50)     报告编号（唯一）
report_type         ENUM            报告类型（daily/weekly/monthly/quarterly/annual/special）
report_title        VARCHAR(255)    报告标题
report_period_start DATE            报告期间开始
report_period_end   DATE            报告期间结束
regulator_id        BIGINT          监管员ID
statistics_data     JSON            统计数据（JSON格式）
report_status       ENUM            报告状态（draft/reviewing/approved/published）
```

### 7. 系统辅助表

#### 7.1 产品分类表 (product_categories)
管理产品分类层级结构。

#### 7.2 系统配置表 (sys_config)
存储系统配置参数。

#### 7.3 操作日志表 (sys_operation_log)
记录用户操作日志。

## 索引设计

### 1. 主要索引策略
- **主键索引**: 所有表的主键自动创建聚簇索引
- **唯一索引**: 业务唯一字段（如编码、用户名等）
- **外键索引**: 所有外键字段创建索引
- **状态索引**: 经常用于过滤的状态字段
- **时间索引**: 用于时间范围查询的字段
- **联合索引**: 根据查询模式创建组合索引

### 2. 重要索引示例
```sql
-- 用户相关索引
INDEX idx_username (username)
INDEX idx_user_type (user_type)
INDEX idx_company_id (company_id)

-- 订单相关索引
INDEX idx_order_code (order_code)
INDEX idx_supplier_id (supplier_id)
INDEX idx_order_status (order_status)
INDEX idx_created_at (created_at)

-- 传感器数据索引
INDEX idx_sensor_id (sensor_id)
INDEX idx_vehicle_id (vehicle_id)
INDEX idx_collection_time (collection_time)

-- 预警记录索引
INDEX idx_alert_type (alert_type)
INDEX idx_alert_level (alert_level)
INDEX idx_related_type_id (related_type, related_id)
```

## 约束设计

### 1. 主键约束
所有表都有BIGINT类型的自增主键。

### 2. 外键约束
重要的关联关系使用外键约束，确保数据完整性。

### 3. 唯一约束
业务唯一性字段添加唯一约束。

### 4. 检查约束
使用ENUM类型限制字段值范围。

## 性能优化

### 1. 分区策略（可选）
对于大数据量的表，可以考虑分区：
- `sensor_data`: 按月分区
- `temperature_logs`: 按月分区
- `alert_records`: 按月分区
- `sys_operation_log`: 按月分区

### 2. 读写分离
支持MySQL主从复制和读写分离架构。

### 3. 缓存策略
- 用户权限信息缓存到Redis
- 车辆实时位置缓存到Redis
- 系统配置信息缓存到Redis

## 安全设计

### 1. 数据安全
- 密码字段加密存储
- 敏感信息脱敏处理
- 完整的操作日志记录

### 2. 访问控制
- RBAC权限模型
- 数据级权限隔离
- API接口权限控制

### 3. 数据备份
- 定期数据库备份
- 增量备份策略
- 灾难恢复方案

## 部署说明

### 1. 初始化步骤
```bash
# 1. 创建数据库
mysql -u root -p < database/schema.sql

# 2. 初始化数据
mysql -u root -p freshlogistics < database/init_data.sql
```

### 2. 配置要求
- MySQL 8.0+
- 内存: 4GB+
- 存储: 100GB+（根据数据量调整）
- 字符集: utf8mb4

### 3. 监控建议
- 监控慢查询日志
- 监控数据库连接数
- 监控磁盘空间使用
- 定期优化表结构

## 维护建议

### 1. 定期维护
- 清理过期日志数据
- 优化表碎片
- 更新统计信息
- 检查索引使用情况

### 2. 扩展考虑
- 支持分库分表
- 支持数据归档
- 支持大数据分析
- 支持实时数据同步

## 版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|----------|--------|
| 1.0.0 | 2025-01-27 | 初始版本 | 系统设计师 |

---

**注意**: 本设计文档基于Fresh Logistics系统需求，实际使用时请根据具体业务场景进行调整优化。
