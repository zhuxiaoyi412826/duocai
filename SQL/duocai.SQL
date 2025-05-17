-- resources/数据库初始化.sql
-- 多彩商城数据库初始化脚本
-- 生成时间: 2023-10-15

-- 用户表
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(加密)',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint DEFAULT '1' COMMENT '状态(0禁用1正常)',
  `last_login` datetime DEFAULT NULL COMMENT '最后登录时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_email` (`email`),
  UNIQUE KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `brand_id` bigint DEFAULT NULL COMMENT '品牌ID',
  `price` decimal(10,2) NOT NULL COMMENT '销售价',
  `market_price` decimal(10,2) DEFAULT NULL COMMENT '市场价',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `main_image` varchar(255) DEFAULT NULL COMMENT '主图URL',
  `description` text COMMENT '商品描述',
  `sales` int DEFAULT '0' COMMENT '销量',
  `status` tinyint DEFAULT '1' COMMENT '状态(0下架1上架)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_brand` (`brand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 商品规格表
CREATE TABLE `product_specs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `name` varchar(50) NOT NULL COMMENT '规格名称',
  `value` varchar(100) NOT NULL COMMENT '规格值',
  `price_diff` decimal(10,2) DEFAULT '0.00' COMMENT '价格差异',
  `stock` int DEFAULT '0' COMMENT '库存',
  `image` varchar(255) DEFAULT NULL COMMENT '规格图片',
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`),
  CONSTRAINT `fk_spec_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';

-- 购物车表
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `spec_id` bigint DEFAULT NULL COMMENT '规格ID',
  `quantity` int DEFAULT '1' COMMENT '购买数量',
  `selected` tinyint DEFAULT '1' COMMENT '是否选中(1-选中 0-未选中)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  UNIQUE KEY `uk_user_product_spec` (`user_id`, `product_id`, `spec_id`) COMMENT '用户-商品-规格唯一索引',
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户购物车表';

-- 评论表
CREATE TABLE `product_reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `content` text NOT NULL COMMENT '评论内容',
  `rating` tinyint NOT NULL COMMENT '评分(1-5星)',
  `images` json DEFAULT NULL COMMENT '评论图片JSON数组',
  `is_anonymous` tinyint DEFAULT '0' COMMENT '是否匿名(1-匿名 0-实名)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除(1-已删除 0-未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`) COMMENT '商品索引',
  KEY `idx_user_id` (`user_id`) COMMENT '用户索引',
  KEY `idx_order_id` (`order_id`) COMMENT '订单索引',
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评价表';

-- 分类表
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父分类ID',
  `level` tinyint DEFAULT '1' COMMENT '分类层级',
  `sort` int DEFAULT '0' COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `is_show` tinyint DEFAULT '1' COMMENT '是否显示',
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 订单表
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总额',
  `payment_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `freight_amount` decimal(10,2) DEFAULT '0.00' COMMENT '运费',
  `coupon_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠券抵扣',
  `status` varchar(20) NOT NULL COMMENT '订单状态',
  `address_id` bigint NOT NULL COMMENT '收货地址ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '订单备注',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_no` (`order_no`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `spec_info` varchar(100) DEFAULT NULL COMMENT '规格信息',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `total_price` decimal(10,2) NOT NULL COMMENT '总价',
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`),
  CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 创建库存表
CREATE TABLE inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    spec_id BIGINT NOT NULL COMMENT '规格ID',
    quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    low_stock INT DEFAULT 5 COMMENT '低库存预警值',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 外键约束（确保关联表存在）
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (spec_id) REFERENCES product_specs(id),
    
    -- 唯一索引（确保同一商品规格在同一仓库中唯一）
    UNIQUE KEY unique_product_spec_warehouse (product_id, spec_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';


-- 物流表
CREATE TABLE `shippings` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '物流ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `shipping_no` varchar(50) NOT NULL COMMENT '物流单号',
  `company` varchar(50) NOT NULL COMMENT '物流公司',
  `status` varchar(20) NOT NULL COMMENT '物流状态',
  `receiver` varchar(50) NOT NULL COMMENT '收货人',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `address` varchar(255) NOT NULL COMMENT '收货地址',
  `shipped_at` datetime DEFAULT NULL COMMENT '发货时间',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_shipping_no` (`shipping_no`),
  KEY `idx_order` (`order_id`),
  CONSTRAINT `fk_shipping_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流表';

-- 地址表
CREATE TABLE `addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver` varchar(50) NOT NULL COMMENT '收货人',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `province` varchar(50) NOT NULL COMMENT '省份',
  `city` varchar(50) NOT NULL COMMENT '城市',
  `district` varchar(50) NOT NULL COMMENT '区县',
  `detail` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认',
  `tag` varchar(20) DEFAULT NULL COMMENT '地址标签',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址表';

-- 优惠券表
CREATE TABLE `coupons` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `name` varchar(100) NOT NULL COMMENT '优惠券名称',
  `type` varchar(20) NOT NULL COMMENT '类型(满减/折扣)',
  `amount` decimal(10,2) NOT NULL COMMENT '优惠金额/折扣率',
  `min_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最低消费金额',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `total_count` int NOT NULL COMMENT '发行数量',
  `remaining_count` int NOT NULL COMMENT '剩余数量',
  `status` tinyint DEFAULT '1' COMMENT '状态(0禁用1启用)',
  PRIMARY KEY (`id`),
  KEY `idx_time_status` (`start_time`,`end_time`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 用户优惠券表
CREATE TABLE `user_coupons` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
  `order_id` bigint DEFAULT NULL COMMENT '使用的订单ID',
  `status` varchar(20) NOT NULL COMMENT '状态(未使用/已使用/已过期)',
  `get_time` datetime NOT NULL COMMENT '领取时间',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`,`status`),
  CONSTRAINT `fk_uc_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`),
  CONSTRAINT `fk_uc_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- 操作日志表
CREATE TABLE `operation_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `module` varchar(50) NOT NULL COMMENT '操作模块',
  `operation` varchar(50) NOT NULL COMMENT '操作类型',
  `method` varchar(20) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `status` tinyint DEFAULT NULL COMMENT '操作状态',
  `error_msg` text COMMENT '错误信息',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_operation` (`user_id`,`operation_time`),
  CONSTRAINT `fk_log_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 多彩商城图片表SQL创建语句
CREATE TABLE `images` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `url` varchar(255) NOT NULL COMMENT '图片访问URL',
  `path` varchar(255) NOT NULL COMMENT '图片存储路径',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '图片类型：1-商品图 2-用户头像 3-评价图 4-广告图 5-其他',
  `related_id` bigint unsigned DEFAULT NULL COMMENT '关联的业务ID(商品ID/用户ID等)',
  `name` varchar(100) DEFAULT NULL COMMENT '图片原始文件名',
  `size` int unsigned DEFAULT NULL COMMENT '图片大小(字节)',
  `width` smallint unsigned DEFAULT NULL COMMENT '图片宽度(像素)',
  `height` smallint unsigned DEFAULT NULL COMMENT '图片高度(像素)',
  `format` varchar(10) DEFAULT NULL COMMENT '图片格式(jpg/png/gif等)',
  `hash` varchar(32) DEFAULT NULL COMMENT '图片MD5哈希值',
  `is_main` tinyint(1) DEFAULT '0' COMMENT '是否主图：1-是 0-否',
  `sort_order` smallint DEFAULT '99' COMMENT '排序权重(越小越靠前)',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` bigint unsigned DEFAULT NULL COMMENT '创建人ID',
  `updated_by` bigint unsigned DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  KEY `idx_type_related` (`type`,`related_id`),
  UNIQUE KEY `uk_hash` (`hash`) COMMENT '图片哈希唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='多彩商城图片表';
-- 初始化管理员账号
INSERT INTO `users` (`username`, `password`, `email`, `status`) 
VALUES ('admin', '$2a$10$xVCHqIA2QY1R5Jk8M0Q/P.9MZiJQxYjZ9XlWYcLdO3rJKl7sZ5XW', 'admin@duocai.com', 1);
