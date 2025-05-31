-- 秒杀商品表
CREATE TABLE `seckill_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀商品ID',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `goods_name` varchar(128) NOT NULL COMMENT '商品名称',
  `goods_img` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `goods_price` decimal(10,2) NOT NULL COMMENT '商品原价',
  `seckill_price` decimal(10,2) NOT NULL COMMENT '秒杀价',
  `stock_count` int(11) NOT NULL COMMENT '库存数量',
  `start_time` datetime NOT NULL COMMENT '秒杀开始时间',
  `end_time` datetime NOT NULL COMMENT '秒杀结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0-未开始，1-进行中，2-已结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_goods_id` (`goods_id`),
  KEY `idx_status` (`status`),
  KEY `idx_time` (`start_time`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀商品表';

-- 秒杀订单表
CREATE TABLE `seckill_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀订单ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `seckill_price` decimal(10,2) NOT NULL COMMENT '秒杀价格',
  `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '购买数量',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '订单状态：0-未支付，1-已支付，2-已取消，3-已退款',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_goods` (`user_id`,`goods_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀订单表';

-- 秒杀活动表
CREATE TABLE `seckill_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `name` varchar(128) NOT NULL COMMENT '活动名称',
  `description` varchar(512) DEFAULT NULL COMMENT '活动描述',
  `start_time` datetime NOT NULL COMMENT '活动开始时间',
  `end_time` datetime NOT NULL COMMENT '活动结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '活动状态：0-未开始，1-进行中，2-已结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_time` (`start_time`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';