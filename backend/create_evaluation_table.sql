-- 创建供应商评价表
CREATE TABLE IF NOT EXISTS supplier_evaluations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    supplier_id BIGINT NOT NULL,
    order_id BIGINT,
    order_code VARCHAR(50),
    evaluator_id BIGINT,
    evaluator_name VARCHAR(100),
    
    -- 评分字段（1-5星）
    service_rating DECIMAL(2,1) DEFAULT 5.0 COMMENT '服务评分',
    quality_rating DECIMAL(2,1) DEFAULT 5.0 COMMENT '质量评分', 
    delivery_rating DECIMAL(2,1) DEFAULT 5.0 COMMENT '配送评分',
    overall_rating DECIMAL(2,1) DEFAULT 5.0 COMMENT '综合评分',
    
    -- 评价内容
    evaluation_content TEXT COMMENT '评价内容',
    suggestions TEXT COMMENT '改进建议',
    
    -- 状态和时间
    evaluation_status VARCHAR(20) DEFAULT 'submitted' COMMENT '评价状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_order_id (order_id),
    INDEX idx_created_at (created_at)
) COMMENT='供应商评价表';

-- 插入示例评价数据
INSERT INTO supplier_evaluations (
    supplier_id, order_code, evaluator_name, 
    service_rating, quality_rating, delivery_rating, overall_rating,
    evaluation_content, suggestions
) VALUES 
(1, 'ORD20250920001', '采购部张经理', 4.5, 4.8, 4.2, 4.5, '新鲜农场的产品质量很好，配送及时，服务态度良好', '希望能进一步提升包装质量'),
(2, 'ORD20250921002', '采购部李经理', 4.2, 4.5, 4.0, 4.2, '绿色蔬菜基地的蔬菜新鲜度不错，但配送时间稍有延迟', '建议优化配送时间管理'),
(3, 'ORD20250922003', '采购部王经理', 4.8, 4.9, 4.6, 4.8, '海鲜直供公司的产品质量优秀，冷链保护到位，非常满意', '继续保持现有服务水平'),
(1, 'ORD20250927001', '质量部刘主管', 4.0, 4.3, 3.8, 4.0, '产品质量总体良好，但包装有改进空间', '加强产品包装保护'),
(2, 'ORD20250927002', '采购部周经理', 4.6, 4.4, 4.5, 4.5, '服务响应及时，产品符合要求，配送准时', '无特别建议，继续保持');

SELECT 'supplier_evaluations表创建完成' as result;
