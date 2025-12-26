-- ================================================================
-- Fresh Logistics 冷链物流智能监测预警系统 - 数据库设计
-- 字符集: utf8mb4
-- 引擎: InnoDB
-- 创建时间: 2025-01-27
-- ================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS freshlogistics 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE freshlogistics;

-- ================================================================
-- 1. 用户权限管理模块 (RBAC权限模型)
-- ================================================================

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    real_name VARCHAR(100) NOT NULL COMMENT '真实姓名',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态:0=禁用,1=启用',
    user_type ENUM('supplier', 'logistics', 'purchaser', 'regulator', 'admin') NOT NULL COMMENT '用户类型',
    company_id BIGINT COMMENT '关联公司ID',
    last_login_time DATETIME COMMENT '最后登录时间',
    login_ip VARCHAR(50) COMMENT '最后登录IP',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间(软删除)',
    
    INDEX idx_username (username),
    INDEX idx_user_type (user_type),
    INDEX idx_company_id (company_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description TEXT COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态:0=禁用,1=启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    resource_type ENUM('menu', 'button', 'api', 'data') NOT NULL COMMENT '资源类型',
    resource_path VARCHAR(255) COMMENT '资源路径',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    level INT DEFAULT 1 COMMENT '层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态:0=禁用,1=启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_permission_code (permission_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_resource_type (resource_type)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id),
    
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ================================================================
-- 2. 供应商管理模块
-- ================================================================

-- 供应商表
CREATE TABLE suppliers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '供应商ID',
    supplier_code VARCHAR(50) NOT NULL UNIQUE COMMENT '供应商编码',
    supplier_name VARCHAR(200) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(100) NOT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    address TEXT COMMENT '地址',
    business_license VARCHAR(100) COMMENT '营业执照号',
    qualification_level ENUM('A', 'B', 'C', 'D') DEFAULT 'C' COMMENT '资质等级',
    cooperation_status ENUM('active', 'inactive', 'suspended') DEFAULT 'active' COMMENT '合作状态',
    credit_rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '信用评级(0-5分)',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间(软删除)',
    
    INDEX idx_supplier_code (supplier_code),
    INDEX idx_supplier_name (supplier_name),
    INDEX idx_cooperation_status (cooperation_status)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='供应商表';

-- 产品表
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '产品ID',
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '产品编码',
    product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
    category_id BIGINT COMMENT '产品分类ID',
    product_type ENUM('fruit', 'vegetable', 'meat', 'seafood', 'dairy', 'other') NOT NULL COMMENT '产品类型',
    unit VARCHAR(20) NOT NULL COMMENT '单位',
    spec VARCHAR(100) COMMENT '规格',
    min_temp DECIMAL(4,1) COMMENT '最低温度(℃)',
    max_temp DECIMAL(4,1) COMMENT '最高温度(℃)',
    min_humidity DECIMAL(5,2) COMMENT '最低湿度(%)',
    max_humidity DECIMAL(5,2) COMMENT '最高湿度(%)',
    shelf_life INT COMMENT '保质期(天)',
    storage_requirements TEXT COMMENT '储存要求',
    description TEXT COMMENT '产品描述',
    status TINYINT DEFAULT 1 COMMENT '状态:0=禁用,1=启用',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间(软删除)',
    
    INDEX idx_product_code (product_code),
    INDEX idx_product_name (product_name),
    INDEX idx_product_type (product_type),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='产品表';

-- 供应商产品关联表
CREATE TABLE supplier_products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    supply_price DECIMAL(10,2) COMMENT '供应价格',
    min_order_quantity INT COMMENT '最小订购量',
    supply_capacity INT COMMENT '供应能力(每日)',
    quality_grade ENUM('A', 'B', 'C') DEFAULT 'B' COMMENT '质量等级',
    lead_time INT COMMENT '交货周期(天)',
    status TINYINT DEFAULT 1 COMMENT '状态:0=停供,1=正常供应',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_supplier_product (supplier_id, product_id),
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_product_id (product_id),
    
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='供应商产品关联表';

-- ================================================================
-- 3. 物流运输管理模块
-- ================================================================

-- 车辆表
CREATE TABLE vehicles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '车辆ID',
    vehicle_code VARCHAR(50) NOT NULL UNIQUE COMMENT '车辆编码',
    license_plate VARCHAR(20) NOT NULL UNIQUE COMMENT '车牌号',
    vehicle_type ENUM('small_truck', 'medium_truck', 'large_truck', 'refrigerated_truck') NOT NULL COMMENT '车辆类型',
    brand VARCHAR(50) COMMENT '品牌',
    model VARCHAR(50) COMMENT '型号',
    load_capacity DECIMAL(8,2) COMMENT '载重量(吨)',
    volume_capacity DECIMAL(8,2) COMMENT '容积(立方米)',
    fuel_type ENUM('gasoline', 'diesel', 'electric', 'hybrid') COMMENT '燃料类型',
    purchase_date DATE COMMENT '购买日期',
    insurance_expiry DATE COMMENT '保险到期日',
    annual_inspection_date DATE COMMENT '年检日期',
    gps_device_id VARCHAR(100) COMMENT 'GPS设备ID',
    temperature_sensor_id VARCHAR(100) COMMENT '温度传感器ID',
    humidity_sensor_id VARCHAR(100) COMMENT '湿度传感器ID',
    vehicle_status ENUM('available', 'in_transit', 'maintenance', 'retired') DEFAULT 'available' COMMENT '车辆状态',
    owner_type ENUM('self_owned', 'leased', 'third_party') DEFAULT 'self_owned' COMMENT '所有权类型',
    logistics_company_id BIGINT COMMENT '所属物流公司ID',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间(软删除)',
    
    INDEX idx_license_plate (license_plate),
    INDEX idx_vehicle_status (vehicle_status),
    INDEX idx_logistics_company_id (logistics_company_id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='车辆表';

-- 司机表
CREATE TABLE drivers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '司机ID',
    driver_code VARCHAR(50) NOT NULL UNIQUE COMMENT '司机编码',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    id_card VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    driving_license VARCHAR(50) NOT NULL COMMENT '驾驶证号',
    license_type VARCHAR(10) NOT NULL COMMENT '准驾车型',
    license_expiry DATE NOT NULL COMMENT '驾驶证有效期',
    cold_chain_certificate VARCHAR(100) COMMENT '冷链从业资格证号',
    certificate_expiry DATE COMMENT '资格证有效期',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    driver_status ENUM('available', 'driving', 'rest', 'leave', 'resigned') DEFAULT 'available' COMMENT '司机状态',
    hire_date DATE COMMENT '入职日期',
    logistics_company_id BIGINT COMMENT '所属物流公司ID',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间(软删除)',
    
    INDEX idx_driver_code (driver_code),
    INDEX idx_id_card (id_card),
    INDEX idx_phone (phone),
    INDEX idx_driver_status (driver_status),
    INDEX idx_logistics_company_id (logistics_company_id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='司机表';

-- 运输路线表
CREATE TABLE transport_routes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '路线ID',
    route_code VARCHAR(50) NOT NULL UNIQUE COMMENT '路线编码',
    route_name VARCHAR(200) NOT NULL COMMENT '路线名称',
    start_point VARCHAR(200) NOT NULL COMMENT '起点',
    start_longitude DECIMAL(10,7) COMMENT '起点经度',
    start_latitude DECIMAL(10,7) COMMENT '起点纬度',
    end_point VARCHAR(200) NOT NULL COMMENT '终点',
    end_longitude DECIMAL(10,7) COMMENT '终点经度',
    end_latitude DECIMAL(10,7) COMMENT '终点纬度',
    route_points TEXT COMMENT '路径坐标点集合(JSON格式)',
    estimated_distance DECIMAL(8,2) COMMENT '预估距离(公里)',
    estimated_duration INT COMMENT '预估时长(分钟)',
    max_delay_tolerance INT DEFAULT 30 COMMENT '最大延迟容忍时长(分钟)',
    fence_radius INT DEFAULT 500 COMMENT '电子围栏偏离半径(米)',
    route_type ENUM('highway', 'city', 'mixed') DEFAULT 'mixed' COMMENT '路线类型',
    difficulty_level ENUM('easy', 'medium', 'hard') DEFAULT 'medium' COMMENT '难度等级',
    status TINYINT DEFAULT 1 COMMENT '状态:0=禁用,1=启用',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_route_code (route_code),
    INDEX idx_route_name (route_name),
    INDEX idx_start_point (start_point),
    INDEX idx_end_point (end_point)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='运输路线表';

-- 运输任务表
CREATE TABLE transports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '运输任务ID',
    transport_code VARCHAR(50) NOT NULL UNIQUE COMMENT '运输单号',
    order_id BIGINT NOT NULL COMMENT '关联订单ID',
    vehicle_id BIGINT NOT NULL COMMENT '车辆ID',
    driver_id BIGINT NOT NULL COMMENT '司机ID',
    route_id BIGINT COMMENT '路线ID',
    planned_start_time DATETIME NOT NULL COMMENT '计划出发时间',
    actual_start_time DATETIME COMMENT '实际出发时间',
    planned_arrival_time DATETIME NOT NULL COMMENT '计划到达时间',
    actual_arrival_time DATETIME COMMENT '实际到达时间',
    current_longitude DECIMAL(10,7) COMMENT '当前经度',
    current_latitude DECIMAL(10,7) COMMENT '当前纬度',
    current_address VARCHAR(255) COMMENT '当前位置',
    transport_status ENUM('pending', 'in_transit', 'delayed', 'arrived', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '运输状态',
    delay_reason TEXT COMMENT '延误原因',
    completion_notes TEXT COMMENT '完成备注',
    total_distance DECIMAL(8,2) COMMENT '总里程(公里)',
    fuel_consumption DECIMAL(6,2) COMMENT '油耗(升)',
    transport_cost DECIMAL(10,2) COMMENT '运输费用',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_transport_code (transport_code),
    INDEX idx_order_id (order_id),
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_driver_id (driver_id),
    INDEX idx_transport_status (transport_status),
    INDEX idx_planned_start_time (planned_start_time),
    
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (driver_id) REFERENCES drivers(id),
    FOREIGN KEY (route_id) REFERENCES transport_routes(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='运输任务表';

-- ================================================================
-- 4. 订单管理模块
-- ================================================================

-- 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_code VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    purchaser_id BIGINT NOT NULL COMMENT '采购商ID',
    order_type ENUM('standard', 'urgent', 'scheduled') DEFAULT 'standard' COMMENT '订单类型',
    order_status ENUM('created', 'confirmed', 'in_transit', 'delivered', 'completed', 'cancelled', 'exception') DEFAULT 'created' COMMENT '订单状态',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '货币单位',
    order_weight DECIMAL(8,3) COMMENT '总重量(公斤)',
    order_volume DECIMAL(8,3) COMMENT '总体积(立方米)',
    pickup_address TEXT NOT NULL COMMENT '取货地址',
    pickup_contact VARCHAR(100) COMMENT '取货联系人',
    pickup_phone VARCHAR(20) COMMENT '取货联系电话',
    delivery_address TEXT NOT NULL COMMENT '送货地址',
    delivery_contact VARCHAR(100) COMMENT '送货联系人',
    delivery_phone VARCHAR(20) COMMENT '送货联系电话',
    required_delivery_time DATETIME COMMENT '要求送达时间',
    special_requirements TEXT COMMENT '特殊要求',
    temperature_requirement VARCHAR(50) COMMENT '温度要求',
    order_notes TEXT COMMENT '订单备注',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    cancelled_at TIMESTAMP NULL COMMENT '取消时间',
    cancel_reason TEXT COMMENT '取消原因',
    
    INDEX idx_order_code (order_code),
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_purchaser_id (purchaser_id),
    INDEX idx_order_status (order_status),
    INDEX idx_created_at (created_at),
    INDEX idx_required_delivery_time (required_delivery_time),
    
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (purchaser_id) REFERENCES sys_user(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='订单表';

-- 订单明细表
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    quantity DECIMAL(10,3) NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price DECIMAL(12,2) NOT NULL COMMENT '小计金额',
    product_batch VARCHAR(100) COMMENT '产品批次号',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '到期日期',
    quality_grade ENUM('A', 'B', 'C') COMMENT '质量等级',
    item_notes TEXT COMMENT '明细备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    INDEX idx_product_batch (product_batch),
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='订单明细表';

-- 物流订单表
CREATE TABLE logistics_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '物流订单ID',
    logistics_code VARCHAR(50) NOT NULL UNIQUE COMMENT '物流单号',
    order_id BIGINT NOT NULL COMMENT '关联订单ID',
    logistics_company_id BIGINT COMMENT '物流公司ID',
    transport_id BIGINT COMMENT '运输任务ID',
    logistics_type ENUM('self_delivery', 'third_party', 'mixed') DEFAULT 'third_party' COMMENT '物流方式',
    logistics_status ENUM('pending', 'picked_up', 'in_transit', 'out_for_delivery', 'delivered', 'failed', 'returned') DEFAULT 'pending' COMMENT '物流状态',
    logistics_fee DECIMAL(10,2) COMMENT '物流费用',
    insurance_fee DECIMAL(10,2) COMMENT '保险费用',
    pickup_time DATETIME COMMENT '取货时间',
    delivery_time DATETIME COMMENT '送达时间',
    signature_person VARCHAR(100) COMMENT '签收人',
    signature_time DATETIME COMMENT '签收时间',
    signature_image VARCHAR(255) COMMENT '签收凭证图片',
    logistics_notes TEXT COMMENT '物流备注',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_logistics_code (logistics_code),
    INDEX idx_order_id (order_id),
    INDEX idx_transport_id (transport_id),
    INDEX idx_logistics_status (logistics_status),
    INDEX idx_pickup_time (pickup_time),
    INDEX idx_delivery_time (delivery_time),
    
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (transport_id) REFERENCES transports(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='物流订单表';

-- ================================================================
-- 5. 监控预警模块
-- ================================================================

-- 预警规则表
CREATE TABLE alert_rules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    rule_code VARCHAR(50) NOT NULL UNIQUE COMMENT '规则编码',
    rule_name VARCHAR(200) NOT NULL COMMENT '规则名称',
    rule_type ENUM('timeout', 'temperature', 'humidity', 'route_deviation') NOT NULL COMMENT '规则类型',
    rule_category ENUM('product_type', 'route', 'vehicle', 'global') NOT NULL COMMENT '规则分类',
    target_id BIGINT COMMENT '目标对象ID(产品类型/路线/车辆等)',
    rule_condition JSON NOT NULL COMMENT '规则条件(JSON格式)',
    threshold_value DECIMAL(10,3) COMMENT '阈值',
    comparison_operator ENUM('>', '<', '>=', '<=', '=', '!=', 'between') COMMENT '比较运算符',
    alert_level ENUM('info', 'warning', 'error', 'critical') DEFAULT 'warning' COMMENT '预警级别',
    rule_description TEXT COMMENT '规则描述',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用:0=禁用,1=启用',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_rule_code (rule_code),
    INDEX idx_rule_type (rule_type),
    INDEX idx_rule_category (rule_category),
    INDEX idx_target_id (target_id),
    INDEX idx_is_enabled (is_enabled)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='预警规则表';

-- 预警记录表
CREATE TABLE alert_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预警记录ID',
    alert_code VARCHAR(50) NOT NULL UNIQUE COMMENT '预警编号',
    rule_id BIGINT NOT NULL COMMENT '触发规则ID',
    alert_type ENUM('timeout', 'temperature', 'humidity', 'route_deviation') NOT NULL COMMENT '预警类型',
    alert_level ENUM('info', 'warning', 'error', 'critical') NOT NULL COMMENT '预警级别',
    related_type ENUM('order', 'transport', 'vehicle', 'sensor') NOT NULL COMMENT '关联对象类型',
    related_id BIGINT NOT NULL COMMENT '关联对象ID',
    alert_title VARCHAR(255) NOT NULL COMMENT '预警标题',
    alert_message TEXT NOT NULL COMMENT '预警消息',
    trigger_value DECIMAL(10,3) COMMENT '触发值',
    threshold_value DECIMAL(10,3) COMMENT '阈值',
    alert_data JSON COMMENT '预警数据详情(JSON格式)',
    alert_status ENUM('pending', 'processing', 'processed', 'ignored', 'false_alarm') DEFAULT 'pending' COMMENT '处理状态',
    processor_id BIGINT COMMENT '处理人ID',
    process_time DATETIME COMMENT '处理时间',
    process_notes TEXT COMMENT '处理备注',
    auto_processed TINYINT DEFAULT 0 COMMENT '是否自动处理:0=手动,1=自动',
    notification_sent TINYINT DEFAULT 0 COMMENT '是否已发送通知:0=未发送,1=已发送',
    location_info VARCHAR(255) COMMENT '位置信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_alert_code (alert_code),
    INDEX idx_rule_id (rule_id),
    INDEX idx_alert_type (alert_type),
    INDEX idx_alert_level (alert_level),
    INDEX idx_related_type_id (related_type, related_id),
    INDEX idx_alert_status (alert_status),
    INDEX idx_created_at (created_at),
    
    FOREIGN KEY (rule_id) REFERENCES alert_rules(id),
    FOREIGN KEY (processor_id) REFERENCES sys_user(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='预警记录表';

-- 传感器数据表
CREATE TABLE sensor_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '传感器数据ID',
    sensor_id VARCHAR(100) NOT NULL COMMENT '传感器ID',
    sensor_type ENUM('temperature', 'humidity', 'gps', 'door', 'shock', 'light') NOT NULL COMMENT '传感器类型',
    vehicle_id BIGINT COMMENT '关联车辆ID',
    transport_id BIGINT COMMENT '关联运输任务ID',
    data_value DECIMAL(10,4) NOT NULL COMMENT '数据值',
    data_unit VARCHAR(20) COMMENT '数据单位',
    longitude DECIMAL(10,7) COMMENT '经度',
    latitude DECIMAL(10,7) COMMENT '纬度',
    altitude DECIMAL(8,2) COMMENT '海拔高度(米)',
    signal_strength INT COMMENT '信号强度',
    battery_level DECIMAL(5,2) COMMENT '电池电量(%)',
    data_quality ENUM('good', 'poor', 'invalid') DEFAULT 'good' COMMENT '数据质量',
    collection_time TIMESTAMP NOT NULL COMMENT '采集时间',
    received_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '接收时间',
    
    INDEX idx_sensor_id (sensor_id),
    INDEX idx_sensor_type (sensor_type),
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_transport_id (transport_id),
    INDEX idx_collection_time (collection_time),
    INDEX idx_received_time (received_time),
    
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (transport_id) REFERENCES transports(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='传感器数据表';

-- 温度记录表(历史数据)
CREATE TABLE temperature_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '温度记录ID',
    sensor_id VARCHAR(100) NOT NULL COMMENT '温度传感器ID',
    vehicle_id BIGINT COMMENT '关联车辆ID',
    transport_id BIGINT COMMENT '关联运输任务ID',
    temperature DECIMAL(5,2) NOT NULL COMMENT '温度值(℃)',
    humidity DECIMAL(5,2) COMMENT '湿度值(%)',
    location_longitude DECIMAL(10,7) COMMENT '位置经度',
    location_latitude DECIMAL(10,7) COMMENT '位置纬度',
    is_normal TINYINT DEFAULT 1 COMMENT '是否正常:0=异常,1=正常',
    alert_triggered TINYINT DEFAULT 0 COMMENT '是否触发预警:0=否,1=是',
    record_time TIMESTAMP NOT NULL COMMENT '记录时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_sensor_id (sensor_id),
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_transport_id (transport_id),
    INDEX idx_record_time (record_time),
    INDEX idx_is_normal (is_normal),
    INDEX idx_alert_triggered (alert_triggered),
    
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (transport_id) REFERENCES transports(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='温度记录表';

-- ================================================================
-- 6. 监管合规模块
-- ================================================================

-- 合规检查记录表
CREATE TABLE compliance_checks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '合规检查ID',
    check_code VARCHAR(50) NOT NULL UNIQUE COMMENT '检查编号',
    check_type ENUM('routine', 'spot', 'complaint', 'follow_up') NOT NULL COMMENT '检查类型',
    check_category ENUM('vehicle', 'driver', 'facility', 'process', 'documentation') NOT NULL COMMENT '检查分类',
    target_type ENUM('supplier', 'logistics_company', 'vehicle', 'driver', 'route') NOT NULL COMMENT '检查对象类型',
    target_id BIGINT NOT NULL COMMENT '检查对象ID',
    regulator_id BIGINT NOT NULL COMMENT '监管员ID',
    check_date DATE NOT NULL COMMENT '检查日期',
    check_location VARCHAR(255) COMMENT '检查地点',
    check_items TEXT NOT NULL COMMENT '检查项目',
    check_results TEXT NOT NULL COMMENT '检查结果',
    compliance_score DECIMAL(5,2) COMMENT '合规评分(0-100)',
    violations_found INT DEFAULT 0 COMMENT '发现违规项数',
    violation_details TEXT COMMENT '违规详情',
    corrective_actions TEXT COMMENT '整改措施',
    follow_up_required TINYINT DEFAULT 0 COMMENT '是否需要后续跟进:0=否,1=是',
    follow_up_date DATE COMMENT '跟进日期',
    check_status ENUM('planned', 'in_progress', 'completed', 'cancelled') DEFAULT 'planned' COMMENT '检查状态',
    attachments TEXT COMMENT '附件文件列表(JSON格式)',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_check_code (check_code),
    INDEX idx_check_type (check_type),
    INDEX idx_target_type_id (target_type, target_id),
    INDEX idx_regulator_id (regulator_id),
    INDEX idx_check_date (check_date),
    INDEX idx_check_status (check_status),
    
    FOREIGN KEY (regulator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='合规检查记录表';

-- 监管报告表
CREATE TABLE regulator_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '监管报告ID',
    report_code VARCHAR(50) NOT NULL UNIQUE COMMENT '报告编号',
    report_type ENUM('daily', 'weekly', 'monthly', 'quarterly', 'annual', 'special') NOT NULL COMMENT '报告类型',
    report_title VARCHAR(255) NOT NULL COMMENT '报告标题',
    report_period_start DATE NOT NULL COMMENT '报告期间开始',
    report_period_end DATE NOT NULL COMMENT '报告期间结束',
    regulator_id BIGINT NOT NULL COMMENT '监管员ID',
    summary TEXT COMMENT '报告摘要',
    key_findings TEXT COMMENT '主要发现',
    statistics_data JSON COMMENT '统计数据(JSON格式)',
    compliance_trends TEXT COMMENT '合规趋势分析',
    risk_assessment TEXT COMMENT '风险评估',
    recommendations TEXT COMMENT '建议措施',
    report_content LONGTEXT COMMENT '报告正文',
    report_status ENUM('draft', 'reviewing', 'approved', 'published') DEFAULT 'draft' COMMENT '报告状态',
    reviewer_id BIGINT COMMENT '审核人ID',
    review_time DATETIME COMMENT '审核时间',
    review_comments TEXT COMMENT '审核意见',
    publish_time DATETIME COMMENT '发布时间',
    attachments TEXT COMMENT '附件文件列表(JSON格式)',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_report_code (report_code),
    INDEX idx_report_type (report_type),
    INDEX idx_regulator_id (regulator_id),
    INDEX idx_report_period (report_period_start, report_period_end),
    INDEX idx_report_status (report_status),
    INDEX idx_publish_time (publish_time),
    
    FOREIGN KEY (regulator_id) REFERENCES sys_user(id),
    FOREIGN KEY (reviewer_id) REFERENCES sys_user(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='监管报告表';

-- ================================================================
-- 7. 系统辅助表
-- ================================================================

-- 产品分类表
CREATE TABLE product_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    level INT DEFAULT 1 COMMENT '层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    icon VARCHAR(100) COMMENT '图标',
    description TEXT COMMENT '分类描述',
    status TINYINT DEFAULT 1 COMMENT '状态:0=禁用,1=启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_category_code (category_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='产品分类表';

-- 系统配置表
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type ENUM('string', 'number', 'boolean', 'json') DEFAULT 'string' COMMENT '配置类型',
    config_group VARCHAR(50) COMMENT '配置分组',
    description TEXT COMMENT '配置描述',
    is_editable TINYINT DEFAULT 1 COMMENT '是否可编辑:0=否,1=是',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_config_key (config_key),
    INDEX idx_config_group (config_group)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='系统配置表';

-- 操作日志表
CREATE TABLE sys_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) NOT NULL COMMENT '操作类型',
    method VARCHAR(100) COMMENT '请求方法',
    request_url VARCHAR(255) COMMENT '请求URL',
    request_ip VARCHAR(50) COMMENT '请求IP',
    user_agent TEXT COMMENT '用户代理',
    request_params TEXT COMMENT '请求参数',
    response_data TEXT COMMENT '响应数据',
    execution_time INT COMMENT '执行时间(毫秒)',
    operation_status ENUM('success', 'failure', 'error') DEFAULT 'success' COMMENT '操作状态',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_operation (operation),
    INDEX idx_operation_status (operation_status),
    INDEX idx_created_at (created_at),
    INDEX idx_request_ip (request_ip)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='操作日志表';

-- ================================================================
-- 索引优化建议
-- ================================================================

-- 1. 为经常查询的字段创建联合索引
-- 2. 为外键字段创建索引
-- 3. 为时间字段创建索引以支持时间范围查询
-- 4. 为状态字段创建索引以支持状态过滤
-- 5. 根据实际查询模式调整索引策略

-- ================================================================
-- 分区建议(可选)
-- ================================================================

-- 对于大数据量表，可以考虑分区策略：
-- 1. sensor_data 按月分区
-- 2. temperature_logs 按月分区
-- 3. alert_records 按月分区
-- 4. sys_operation_log 按月分区

-- ================================================================
-- 注释说明
-- ================================================================

-- 1. 所有表使用 utf8mb4 字符集，支持完整的Unicode字符
-- 2. 采用 InnoDB 存储引擎，支持事务和外键约束
-- 3. 主键统一使用 BIGINT 类型的自增ID
-- 4. 时间字段使用 TIMESTAMP 类型，自动维护创建和更新时间
-- 5. 状态字段使用 ENUM 类型，提高查询效率
-- 6. 金额字段使用 DECIMAL 类型，确保精度
-- 7. 地理坐标使用 DECIMAL(10,7) 类型，满足GPS精度要求
-- 8. 大文本字段使用 TEXT 或 LONGTEXT 类型
-- 9. JSON数据使用 JSON 类型(MySQL 5.7+)或 TEXT 类型
-- 10. 软删除使用 deleted_at 字段，值为NULL表示未删除
