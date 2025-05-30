-- 创建活动表
CREATE TABLE seckill_activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_name VARCHAR(255) NOT NULL COMMENT '活动名称',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '活动状态（0-未开始，1-进行中，2-已结束，3-已取消）',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    total_stock INT NOT NULL COMMENT '总库存',
    remaining_stock INT NOT NULL DEFAULT 0 COMMENT '剩余库存',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 创建商品表
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(255) NOT NULL COMMENT '商品名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    price DECIMAL(10, 2) NOT NULL COMMENT '秒杀价',
    description TEXT COMMENT '商品描述',
    image_url VARCHAR(255) COMMENT '商品图片地址',
    is_seckill TINYINT NOT NULL DEFAULT 0 COMMENT '是否参与秒杀（0-否，1-是）'
);

-- 创建库存流水表
CREATE TABLE stock_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL COMMENT '关联活动ID',
    type TINYINT NOT NULL COMMENT '操作类型（1-扣减库存，2-回滚库存）',
    quantity INT NOT NULL COMMENT '操作数量',
    operator VARCHAR(50) NOT NULL COMMENT '操作人/系统',
    operate_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    remark VARCHAR(255) COMMENT '备注'
);

-- 创建用户限购表
CREATE TABLE user_limit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    activity_id BIGINT NOT NULL COMMENT '关联活动ID',
    limit_count INT NOT NULL COMMENT '限购数量',
    bought_count INT NOT NULL DEFAULT 0 COMMENT '已购买数量',
    UNIQUE KEY uk_user_activity (user_id, activity_id)
);

-- 创建秒杀订单表
CREATE TABLE seckill_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    activity_id BIGINT NOT NULL COMMENT '关联活动ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    order_status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态（0-未支付，1-已支付，2-已取消，3-已发货，4-已完成）',
    pay_time DATETIME COMMENT '支付时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 创建用户参与记录表
CREATE TABLE user_participation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    activity_id BIGINT NOT NULL COMMENT '关联活动ID',
    action_type TINYINT NOT NULL COMMENT '行为类型（1-点击参与，2-进入排队，3-秒杀失败）',
    action_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
    ip_address VARCHAR(50) COMMENT '用户IP'
);

-- 创建优惠券表
CREATE TABLE coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    coupon_name VARCHAR(255) NOT NULL COMMENT '优惠券名称',
    discount DECIMAL(10, 2) NOT NULL COMMENT '优惠金额/折扣率',
    valid_start DATETIME NOT NULL COMMENT '有效期开始时间',
    valid_end DATETIME NOT NULL COMMENT '有效期结束时间',
    usage_limit INT NOT NULL DEFAULT 1 COMMENT '每人限用次数'
);

-- 创建活动规则表
CREATE TABLE rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL COMMENT '关联活动ID',
    rule_type TINYINT NOT NULL COMMENT '规则类型（1-地域限制，2-会员等级，3-其他）',
    rule_value VARCHAR(255) NOT NULL COMMENT '规则内容'
);

-- 创建通知表
CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    activity_id BIGINT NOT NULL COMMENT '关联活动ID',
    type TINYINT NOT NULL COMMENT '通知类型（1-短信，2-APP推送）',
    content TEXT NOT NULL COMMENT '通知内容',
    send_status TINYINT NOT NULL DEFAULT 0 COMMENT '发送状态（0-未发送，1-已发送，2-发送失败）'
);

-- 添加外键约束
ALTER TABLE product ADD CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id);
ALTER TABLE stock_log ADD CONSTRAINT fk_stock_activity FOREIGN KEY (activity_id) REFERENCES seckill_activity(id);
ALTER TABLE user_limit ADD CONSTRAINT fk_limit_activity FOREIGN KEY (activity_id) REFERENCES seckill_activity(id);
ALTER TABLE seckill_order ADD CONSTRAINT fk_order_activity FOREIGN KEY (activity_id) REFERENCES seckill_activity(id);
ALTER TABLE seckill_order ADD CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES product(id);
ALTER TABLE user_participation ADD CONSTRAINT fk_participation_activity FOREIGN KEY (activity_id) REFERENCES seckill_activity(id);
ALTER TABLE rule ADD CONSTRAINT fk_rule_activity FOREIGN KEY (activity_id) REFERENCES seckill_activity(id);
ALTER TABLE notification ADD CONSTRAINT fk_notification_activity FOREIGN KEY (activity_id) REFERENCES seckill_activity(id);    