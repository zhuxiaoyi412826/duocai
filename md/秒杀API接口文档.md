# 秒杀系统API接口文档

## 1. 活动管理接口

### 1.1 获取活动列表GET /api/v1/activities**参数**：
- `status`: 活动状态（可选）

**响应**：{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 1,
            "activity_name": "双11限时秒杀",
            "status": 1,
            "start_time": "2025-11-11 00:00:00",
            "end_time": "2025-11-11 23:59:59",
            "total_stock": 1000,
            "remaining_stock": 850
        }
    ]
}
### 1.2 获取活动详情GET /api/v1/activities/{id}**响应**：{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "activity_name": "双11限时秒杀",
        "status": 1,
        "start_time": "2025-11-11 00:00:00",
        "end_time": "2025-11-11 23:59:59",
        "total_stock": 1000,
        "remaining_stock": 850,
        "products": [
            {
                "id": 101,
                "product_name": "iPhone 15",
                "price": 5999.00,
                "description": "全新iPhone 15",
                "image_url": "https://example.com/iphone15.jpg"
            }
        ]
    }
}
### 1.3 创建活动POST /api/v1/activities**请求体**：{
    "activity_name": "618大促",
    "start_time": "2025-06-18 00:00:00",
    "end_time": "2025-06-18 23:59:59",
    "total_stock": 500
}
## 2. 商品管理接口

### 2.1 获取商品列表GET /api/v1/products**参数**：
- `is_seckill`: 是否秒杀商品（可选）

**响应**：{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 101,
            "product_name": "iPhone 15",
            "category_id": 1,
            "price": 5999.00,
            "is_seckill": 1
        }
    ]
}
## 3. 秒杀接口

### 3.1 参与秒杀POST /api/v1/seckill/{activityId}/{productId}**请求体**：{
    "user_id": 12345,
    "quantity": 1
}**响应**：{
    "code": 200,
    "message": "秒杀成功",
    "data": {
        "order_id": 202506180001,
        "expire_time": "2025-06-18 10:15:30"
    }
}
### 3.2 查询秒杀结果GET /api/v1/seckill/result/{orderId}**响应**：{
    "code": 200,
    "message": "查询成功",
    "data": {
        "order_id": 202506180001,
        "status": 0,
        "create_time": "2025-06-18 10:00:30"
    }
}
## 4. 订单管理接口

### 4.1 创建订单POST /api/v1/orders**请求体**：{
    "user_id": 12345,
    "activity_id": 1,
    "product_id": 101,
    "quantity": 1
}
### 4.2 查询订单GET /api/v1/orders/{orderId}
### 4.3 支付订单PUT /api/v1/orders/{orderId}/pay
### 4.4 取消订单PUT /api/v1/orders/{orderId}/cancel
## 5. 用户限购接口

### 5.1 查询用户限购信息GET /api/v1/users/{userId}/limits/{activityId}**响应**：{
    "code": 200,
    "message": "成功",
    "data": {
        "user_id": 12345,
        "activity_id": 1,
        "limit_count": 1,
        "bought_count": 0
    }
}    