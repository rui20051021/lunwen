-- ================================================================
-- 修复中文字符显示问题
-- ================================================================

USE freshlogistics;

-- 修复用户表中文数据
UPDATE sys_user SET real_name='系统管理员' WHERE username='admin';

-- 修复角色表中文数据
UPDATE sys_role SET 
    role_name = CASE role_code
        WHEN 'admin' THEN '系统管理员'
        WHEN 'supplier' THEN '供应商'
        WHEN 'logistics' THEN '物流商'
        WHEN 'purchaser' THEN '采购商'
        WHEN 'regulator' THEN '监管员'
        ELSE role_name
    END,
    description = CASE role_code
        WHEN 'admin' THEN '系统管理员，拥有所有权限'
        WHEN 'supplier' THEN '供应商角色，负责产品供应和订单管理'
        WHEN 'logistics' THEN '物流商角色，负责运输和车辆管理'
        WHEN 'purchaser' THEN '采购商角色，负责采购和收货确认'
        WHEN 'regulator' THEN '监管员角色，负责监管和合规检查'
        ELSE description
    END;

-- 修复权限表中文数据
UPDATE sys_permission SET 
    permission_name = CASE permission_code
        WHEN 'system:manage' THEN '系统管理'
        WHEN 'system:user:list' THEN '用户列表'
        WHEN 'system:user:add' THEN '添加用户'
        WHEN 'system:user:edit' THEN '编辑用户'
        WHEN 'system:user:delete' THEN '删除用户'
        WHEN 'system:role:list' THEN '角色列表'
        WHEN 'system:role:add' THEN '添加角色'
        WHEN 'system:role:edit' THEN '编辑角色'
        WHEN 'system:role:delete' THEN '删除角色'
        WHEN 'supplier:manage' THEN '供应商管理'
        WHEN 'supplier:list' THEN '供应商列表'
        WHEN 'supplier:add' THEN '添加供应商'
        WHEN 'supplier:edit' THEN '编辑供应商'
        WHEN 'supplier:delete' THEN '删除供应商'
        WHEN 'supplier:product:list' THEN '供应商产品'
        WHEN 'supplier:product:add' THEN '添加产品'
        WHEN 'supplier:product:edit' THEN '编辑产品'
        WHEN 'order:manage' THEN '订单管理'
        WHEN 'order:list' THEN '订单列表'
        WHEN 'order:add' THEN '创建订单'
        WHEN 'order:edit' THEN '编辑订单'
        WHEN 'order:view' THEN '查看订单'
        WHEN 'order:cancel' THEN '取消订单'
        WHEN 'logistics:manage' THEN '物流管理'
        WHEN 'logistics:vehicle:list' THEN '车辆列表'
        WHEN 'logistics:vehicle:add' THEN '添加车辆'
        WHEN 'logistics:vehicle:edit' THEN '编辑车辆'
        WHEN 'logistics:driver:list' THEN '司机列表'
        WHEN 'logistics:driver:add' THEN '添加司机'
        WHEN 'logistics:transport:list' THEN '运输任务'
        WHEN 'logistics:transport:assign' THEN '分配任务'
        WHEN 'monitor:manage' THEN '监控预警'
        WHEN 'monitor:alert:list' THEN '预警列表'
        WHEN 'monitor:alert:process' THEN '处理预警'
        WHEN 'monitor:sensor:list' THEN '传感器数据'
        WHEN 'monitor:rule:list' THEN '预警规则'
        WHEN 'monitor:rule:add' THEN '添加规则'
        WHEN 'analysis:manage' THEN '数据分析'
        WHEN 'analysis:efficiency' THEN '时效分析'
        WHEN 'analysis:loss' THEN '损耗分析'
        WHEN 'analysis:alert' THEN '预警统计'
        WHEN 'regulation:manage' THEN '监管管理'
        WHEN 'regulation:check:list' THEN '合规检查'
        WHEN 'regulation:check:add' THEN '新增检查'
        WHEN 'regulation:report:list' THEN '监管报告'
        WHEN 'regulation:report:add' THEN '创建报告'
        ELSE permission_name
    END;

-- 修复产品分类中文数据
UPDATE product_categories SET 
    category_name = CASE category_code
        WHEN 'fruit' THEN '水果类'
        WHEN 'vegetable' THEN '蔬菜类'
        WHEN 'meat' THEN '肉类'
        WHEN 'seafood' THEN '海鲜水产'
        WHEN 'dairy' THEN '乳制品'
        WHEN 'frozen' THEN '冷冻食品'
        WHEN 'fruit_citrus' THEN '柑橘类'
        WHEN 'fruit_berry' THEN '浆果类'
        WHEN 'vegetable_leafy' THEN '叶菜类'
        WHEN 'vegetable_root' THEN '根茎类'
        WHEN 'meat_pork' THEN '猪肉类'
        WHEN 'meat_beef' THEN '牛肉类'
        WHEN 'seafood_fish' THEN '鱼类'
        WHEN 'seafood_shellfish' THEN '贝类'
        WHEN 'dairy_milk' THEN '牛奶类'
        WHEN 'dairy_cheese' THEN '奶酪类'
        ELSE category_name
    END,
    description = CASE category_code
        WHEN 'fruit' THEN '新鲜水果类产品'
        WHEN 'vegetable' THEN '新鲜蔬菜类产品'
        WHEN 'meat' THEN '新鲜肉类产品'
        WHEN 'seafood' THEN '海鲜及水产品'
        WHEN 'dairy' THEN '乳制品及奶制品'
        WHEN 'frozen' THEN '冷冻类食品'
        WHEN 'fruit_citrus' THEN '橙子、柠檬、柚子等'
        WHEN 'fruit_berry' THEN '草莓、蓝莓、葡萄等'
        WHEN 'vegetable_leafy' THEN '白菜、菠菜、生菜等'
        WHEN 'vegetable_root' THEN '萝卜、土豆、红薯等'
        WHEN 'meat_pork' THEN '各类猪肉制品'
        WHEN 'meat_beef' THEN '各类牛肉制品'
        WHEN 'seafood_fish' THEN '各类新鲜鱼类'
        WHEN 'seafood_shellfish' THEN '各类贝壳类海鲜'
        WHEN 'dairy_milk' THEN '各类牛奶产品'
        WHEN 'dairy_cheese' THEN '各类奶酪产品'
        ELSE description
    END;

-- 修复产品数据中文
UPDATE products SET 
    product_name = CASE product_code
        WHEN 'PRD001' THEN '新鲜橙子'
        WHEN 'PRD002' THEN '有机菠菜'
        WHEN 'PRD003' THEN '新鲜三文鱼'
        WHEN 'PRD004' THEN '优质牛肉'
        WHEN 'PRD005' THEN '新鲜牛奶'
        WHEN 'PRD006' THEN '冷冻虾仁'
        WHEN 'PRD007' THEN '新鲜草莓'
        WHEN 'PRD008' THEN '土豆'
        ELSE product_name
    END,
    description = CASE product_code
        WHEN 'PRD001' THEN '新鲜甜橙，富含维生素C'
        WHEN 'PRD002' THEN '有机种植菠菜，绿色健康'
        WHEN 'PRD003' THEN '挪威进口三文鱼，新鲜美味'
        WHEN 'PRD004' THEN '澳洲进口牛肉，肉质鲜美'
        WHEN 'PRD005' THEN '新鲜全脂牛奶，营养丰富'
        WHEN 'PRD006' THEN '急冻虾仁，保持新鲜'
        WHEN 'PRD007' THEN '当季新鲜草莓，甜美可口'
        WHEN 'PRD008' THEN '新鲜土豆，适合多种烹饪'
        ELSE description
    END,
    unit = CASE product_code
        WHEN 'PRD001' THEN '公斤'
        WHEN 'PRD002' THEN '公斤'
        WHEN 'PRD003' THEN '公斤'
        WHEN 'PRD004' THEN '公斤'
        WHEN 'PRD005' THEN '升'
        WHEN 'PRD006' THEN '公斤'
        WHEN 'PRD007' THEN '盒'
        WHEN 'PRD008' THEN '公斤'
        ELSE unit
    END;

-- 修复供应商数据中文
UPDATE suppliers SET 
    supplier_name = CASE supplier_code
        WHEN 'SUP001' THEN '新鲜农场有限公司'
        WHEN 'SUP002' THEN '绿色蔬菜基地'
        WHEN 'SUP003' THEN '海鲜直供公司'
        ELSE supplier_name
    END,
    contact_person = CASE supplier_code
        WHEN 'SUP001' THEN '张经理'
        WHEN 'SUP002' THEN '李总'
        WHEN 'SUP003' THEN '王主任'
        ELSE contact_person
    END,
    address = CASE supplier_code
        WHEN 'SUP001' THEN '北京市顺义区农业园区1号'
        WHEN 'SUP002' THEN '山东省寿光市蔬菜产业园'
        WHEN 'SUP003' THEN '大连市海鲜批发市场A区'
        ELSE address
    END;

-- 修复司机数据中文
UPDATE drivers SET 
    name = CASE driver_code
        WHEN 'DRV001' THEN '张师傅'
        WHEN 'DRV002' THEN '李师傅'
        WHEN 'DRV003' THEN '王师傅'
        ELSE name
    END;

-- 修复运输路线中文
UPDATE transport_routes SET 
    route_name = CASE route_code
        WHEN 'ROUTE001' THEN '北京-天津线'
        WHEN 'ROUTE002' THEN '北京-石家庄线'
        WHEN 'ROUTE003' THEN '北京-济南线'
        ELSE route_name
    END,
    start_point = CASE route_code
        WHEN 'ROUTE001' THEN '北京市朝阳区'
        WHEN 'ROUTE002' THEN '北京市丰台区'
        WHEN 'ROUTE003' THEN '北京市大兴区'
        ELSE start_point
    END,
    end_point = CASE route_code
        WHEN 'ROUTE001' THEN '天津市河西区'
        WHEN 'ROUTE002' THEN '石家庄市桥西区'
        WHEN 'ROUTE003' THEN '济南市历下区'
        ELSE end_point
    END;

-- 修复预警规则中文
UPDATE alert_rules SET 
    rule_name = CASE rule_code
        WHEN 'RULE_TIMEOUT_DEFAULT' THEN '默认超时预警规则'
        WHEN 'RULE_TEMP_FRUIT' THEN '水果温度预警规则'
        WHEN 'RULE_TEMP_MEAT' THEN '肉类温度预警规则'
        WHEN 'RULE_TEMP_SEAFOOD' THEN '海鲜温度预警规则'
        WHEN 'RULE_HUMIDITY_LOW' THEN '湿度过低预警规则'
        WHEN 'RULE_HUMIDITY_HIGH' THEN '湿度过高预警规则'
        WHEN 'RULE_ROUTE_DEVIATION' THEN '路径偏离预警规则'
        ELSE rule_name
    END,
    rule_description = CASE rule_code
        WHEN 'RULE_TIMEOUT_DEFAULT' THEN '当运输任务延迟超过30分钟时触发预警'
        WHEN 'RULE_TEMP_FRUIT' THEN '水果类产品温度超过8℃时触发预警'
        WHEN 'RULE_TEMP_MEAT' THEN '肉类产品温度超过4℃时触发预警'
        WHEN 'RULE_TEMP_SEAFOOD' THEN '海鲜产品温度超过2℃时触发预警'
        WHEN 'RULE_HUMIDITY_LOW' THEN '湿度低于80%时触发预警'
        WHEN 'RULE_HUMIDITY_HIGH' THEN '湿度高于95%时触发预警'
        WHEN 'RULE_ROUTE_DEVIATION' THEN '车辆偏离预设路线超过500米时触发预警'
        ELSE rule_description
    END;

-- 验证修复结果
SELECT '修复完成' as '状态';
