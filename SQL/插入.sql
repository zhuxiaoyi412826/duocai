-- resources/插入SQL.sql
-- 多彩商城测试数据插入脚本
-- 生成时间: 2023-10-15

-- 1. 用户表(100条)
INSERT INTO `users` (`username`, `password`, `email`, `phone`, `avatar`, `status`) VALUES
('张三', '$2a$10$xVCHqIA2QY1R5Jk8M0Q/P.9MZiJQxYjZ9XlWYcLdO3rJKl7sZ5XW', 'zhangsan@example.com', '13800138001', 'https://example.com/avatars/1.jpg', 1),
('李四', '$2a$10$xVCHqIA2QY1R5Jk8M0Q/P.9MZiJQxYjZ9XlWYcLdO3rJKl7sZ5XW', 'lisi@example.com', '13800138002', 'https://example.com/avatars/2.jpg', 1),
('王五', '$2a$10$xVCHqIA2QY1R5Jk8M0Q/P.9MZiJQxYjZ9XlWYcLdO3rJKl7sZ5XW', 'wangwu@example.com', '13800138100', 'https://example.com/avatars/100.jpg', 1);

-- 2. 分类表(20条)
INSERT INTO `categories` (`name`, `parent_id`, `level`, `sort`, `icon`) VALUES
('电子产品', NULL, 1, 1, 'electronics'),
('手机通讯', 1, 2, 1, 'phone'),
('电脑办公', 1, 2, 2, 'laptop'),
('家居生活', NULL, 1, 2, 'home');

-- 3. 优惠券表(10条)
INSERT INTO `coupons` (`name`, `type`, `amount`, `min_amount`, `start_time`, `end_time`, `total_count`, `remaining_count`) VALUES
('新客专享', '满减', 50.00, 200.00, '2023-10-01 00:00:00', '2023-12-31 23:59:59', 1000, 500),
('双十一特惠', '折扣', 0.80, 100.00, '2023-11-01 00:00:00', '2023-11-11 23:59:59', 5000, 2000),
('会员专享', '满减', 100.00, 500.00, '2023-10-01 00:00:00', '2023-12-31 23:59:59', 2000, 1500);

-- 4. 商品表(100条)
INSERT INTO `products` (`name`, `category_id`, `price`, `market_price`, `main_image`, `description`, `sales`) VALUES
('华为Mate60', 2, 5999.00, 6499.00, 'https://example.com/products/1.jpg', '麒麟9000s芯片，鸿蒙系统', 500),
('iPhone15 Pro', 2, 7999.00, 8499.00, 'https://example.com/products/2.jpg', 'A17 Pro芯片，钛金属边框', 300),
('联想小新Pro16', 3, 5499.00, 5999.00, 'https://example.com/products/100.jpg', '16英寸2.5K屏，标压处理器', 200);

-- 5. 商品规格表(每个商品2-3个规格)
INSERT INTO `product_specs` (`product_id`, `name`, `value`, `price_diff`, `stock`) VALUES
(1, '颜色', '雅丹黑', 0.00, 100),
(1, '颜色', '南糯紫', 0.00, 80),
(1, '存储', '12GB+256GB', 0.00, 50),
(100, '配置', 'i7-13700H 32G 1T', 1000.00, 30);

-- 6. 地址表(每个用户1-3个地址)
INSERT INTO `addresses` (`user_id`, `receiver`, `phone`, `province`, `city`, `district`, `detail`, `is_default`) VALUES
(1, '张三', '13800138001', '北京市', '北京市', '朝阳区', '建国路88号', 1),
(1, '张三', '13800138001', '上海市', '上海市', '浦东新区', '张江高科技园区', 0),
(100, '王五', '13800138100', '广东省', '广州市', '天河区', '体育西路103号', 1);

-- 7. 用户优惠券表(随机分配)
INSERT INTO `user_coupons` (`user_id`, `coupon_id`, `status`, `get_time`) VALUES
(1, 1, '未使用', '2023-10-05 10:00:00'),
(2, 2, '已使用', '2023-09-15 14:30:00'),
(100, 3, '未使用', '2023-10-10 09:15:00');

-- 8. 订单表(50条)
INSERT INTO `orders` (`order_no`, `user_id`, `total_amount`, `payment_amount`, `status`, `address_id`) VALUES
('DD202310150001', 1, 5999.00, 5949.00, '已完成', 1),
('DD202310150002', 2, 7999.00, 7999.00, '待发货', 3),
('DD202310150050', 50, 5499.00, 5399.00, '待付款', 100);

-- 9. 订单明细表
INSERT INTO `order_items` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `total_price`) VALUES
(1, 1, '华为Mate60', 'https://example.com/products/1.jpg', 5999.00, 1, 5999.00),
(2, 2, 'iPhone15 Pro', 'https://example.com/products/2.jpg', 7999.00, 1, 7999.00),
(50, 100, '联想小新Pro16', 'https://example.com/products/100.jpg', 5499.00, 1, 5499.00);

-- 10. 物流表(与订单关联)
INSERT INTO `shippings` (`order_id`, `shipping_no`, `company`, `status`, `receiver`, `phone`, `address`) VALUES
(1, 'SF123456789', '顺丰速运', '已签收', '张三', '13800138001', '北京市朝阳区建国路88号'),
(2, 'YT987654321', '圆通快递', '运输中', '李四', '13800138002', '上海市浦东新区张江高科技园区'),
(50, 'JD123123123', '京东物流', '待发货', '王五', '13800138100', '广东省广州市天河区体育西路103号');

-- 11. 操作日志表(100条)
INSERT INTO `operation_logs` (`user_id`, `module`, `operation`, `ip`, `operation_time`) VALUES
(1, '用户模块', '登录', '192.168.1.1', '2023-10-15 08:00:00'),
(2, '订单模块', '创建订单', '192.168.1.2', '2023-10-15 08:05:00'),
(100, '商品模块', '浏览商品', '192.168.1.100', '2023-10-15 17:30:00');
