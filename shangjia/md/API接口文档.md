1. API 概述
1.1 基本信息
API 名称：电商平台商家管理系统 API
版本：v1.0.0
基础 URL：https://api.example.com/merchant/v1
认证方式：JWT Token（放在请求头的Authorization字段，格式：Bearer {token}）
1.2 响应格式
json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1623456789012
}
2. 商家管理 API
2.1 获取商家信息
plaintext
GET /merchants/{merchantId}
参数：
merchantId（路径参数，必填）：商家 ID
响应：
json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 10001,
    "merchant_name": "小米官方旗舰店",
    "contact_person": "张三",
    "contact_phone": "13800138000",
    "email": "contact@example.com",
    "business_license": "123456789012345",
    "status": 1,
    "create_time": "2023-01-01 10:00:00",
    "update_time": "2023-05-15 15:30:00",
    "extra_info": {
      "business_scope": "电子产品、数码配件",
      "logo_url": "https://example.com/logo.png"
    }
  }
}
2.2 更新商家信息
plaintext
PUT /merchants/{merchantId}
请求体：
json
{
  "contact_person": "李四",
  "contact_phone": "13900139000",
  "email": "new_contact@example.com",
  "extra_info": {
    "business_scope": "电子产品、数码配件、智能家居",
    "logo_url": "https://example.com/new_logo.png"
  }
}
3. 商品管理 API
3.1 创建商品
plaintext
POST /products
请求体：
json
{
  "merchant_id": 10001,
  "product_name": "小米12 Pro",
  "category_id": 1001,
  "brand_id": 2001,
  "price": 4999.00,
  "original_price": 5299.00,
  "stock": 100,
  "status": 1,
  "description": "骁龙8 Gen1处理器，120Hz AMOLED屏幕...",
  "specs": [
    {
      "spec_name": "颜色",
      "spec_value": "黑色",
      "price": 4999.00,
      "stock": 50
    },
    {
      "spec_name": "颜色",
      "spec_value": "白色",
      "price": 4999.00,
      "stock": 50
    }
  ],
  "images": [
    {
      "image_url": "https://example.com/product/1.jpg",
      "is_main": 1
    },
    {
      "image_url": "https://example.com/product/2.jpg",
      "is_main": 0
    }
  ]
}
3.2 获取商品列表
plaintext
GET /products
参数：
merchant_id（查询参数，必填）：商家 ID
status（查询参数，可选）：商品状态，0 = 下架，1 = 上架，2 = 草稿
keyword（查询参数，可选）：搜索关键词
page（查询参数，可选，默认 1）：页码
page_size（查询参数，可选，默认 20）：每页数量
响应：
json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 105,
    "page": 1,
    "page_size": 20,
    "list": [
      {
        "id": 100001,
        "product_name": "小米12 Pro",
        "category_id": 1001,
        "brand_id": 2001,
        "price": 4999.00,
        "original_price": 5299.00,
        "stock": 100,
        "sales_volume": 250,
        "status": 1,
        "main_image": "https://example.com/product/1.jpg",
        "create_time": "2023-01-10 15:30:00"
      },
      // 更多商品...
    ]
  }
}
4. 订单管理 API
4.1 获取订单列表
plaintext
GET /orders
参数：
merchant_id（查询参数，必填）：商家 ID
order_status（查询参数，可选）：订单状态，1 = 待付款，2 = 已付款，3 = 已发货，4 = 已完成，5 = 已取消，6 = 退款中，7 = 已退款
start_time（查询参数，可选）：开始时间（格式：YYYY-MM-DD）
end_time（查询参数，可选）：结束时间（格式：YYYY-MM-DD）
page（查询参数，可选，默认 1）：页码
page_size（查询参数，可选，默认 20）：每页数量
响应：
json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total": 328,
    "page": 1,
    "page_size": 20,
    "list": [
      {
        "id": 200001,
        "order_sn": "20230515100001",
        "merchant_id": 10001,
        "user_id": 30001,
        "total_amount": 5998.00,
        "discount_amount": 300.00,
        "shipping_fee": 0.00,
        "actual_amount": 5698.00,
        "payment_method": 1,
        "order_status": 3,
        "shipping_address": {
          "name": "王五",
          "phone": "13700137000",
          "province": "广东省",
          "city": "深圳市",
          "district": "南山区",
          "address": "科技园100号",
          "zip_code": "518000"
        },
        "create_time": "2023-05-15 10:30:00"
      },
      // 更多订单...
    ]
  }
}
4.2 订单发货
plaintext
POST /orders/{orderId}/ship
参数：
orderId（路径参数，必填）：订单 ID
请求体：
json
{
  "shipping_company": "顺丰速运",
  "shipping_number": "SF1234567890123"
}
5. 营销管理 API
5.1 创建优惠券
plaintext
POST /coupons
请求体：
json
{
  "merchant_id": 10001,
  "coupon_name": "满500减100",
  "coupon_type": 1,
  "condition_amount": 500.00,
  "discount_amount": 100.00,
  "start_time": "2023-06-01 00:00:00",
  "end_time": "2023-06-30 23:59:59",
  "total_count": 1000,
  "remaining_count": 1000,
  "status": 1
}
5.2 获取优惠券列表
plaintext
GET /coupons
参数：
merchant_id（查询参数，必填）：商家 ID
status（查询参数，可选）：状态，0 = 停用，1 = 启用
page（查询参数，可选，默认 1）：页码
page_size（查询参数，可选，默认 20）：每页数量
6. 数据分析 API
6.1 获取商家销售统计数据
plaintext
GET /statistics/sales
参数：
merchant_id（查询参数，必填）：商家 ID
start_date（查询参数，可选）：开始日期（格式：YYYY-MM-DD）
end_date（查询参数，可选）：结束日期（格式：YYYY-MM-DD）
响应：
json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "total_orders": 1258,
    "total_products": 3562,
    "total_amount": 2865432.15,
    "average_order_amount": 2277.76,
    "daily_sales": [
      {
        "stat_date": "2023-05-01",
        "order_count": 42,
        "product_count": 128,
        "total_amount": 95642.30
      },
      {
        "stat_date": "2023-05-02",
        "order_count": 38,
        "product_count": 115,
        "total_amount": 89753.65
      },
      // 更多日期数据...
    ]
  }
}
6.2 获取商品销售排行
plaintext
GET /statistics/products/rank
参数：
merchant_id（查询参数，必填）：商家 ID
top_n（查询参数，可选，默认 10）：取前 N 个商品
start_date（查询参数，可选）：开始日期（格式：YYYY-MM-DD）
end_date（查询参数，可选）：结束日期（格式：YYYY-MM-DD）
7. 账户管理 API
7.1 获取商家账户信息
plaintext
GET /accounts/{merchantId}
参数：
merchantId（路径参数，必填）：商家 ID
响应：
json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 40001,
    "merchant_id": 10001,
    "balance": 125632.45,
    "frozen_amount": 0.00,
    "total_income": 5689452.36,
    "total_withdraw": 5563819.91,
    "status": 1,
    "create_time": "2023-01-01 10:00:00"
  }
}
7.2 申请提现
plaintext
POST /accounts/withdraw
请求体：
json
{
  "merchant_id": 10001,
  "amount": 50000.00,
  "bank_account": {
    "bank_name": "招商银行",
    "account_name": "小米科技有限责任公司",
    "account_number": "6225880200000000",
    "branch": "深圳科技园支行"
  }
}
8. 错误码说明
错误码	描述
200	成功
400	参数错误
401	未授权
403	禁止访问
404	资源不存在
500	服务器内部错误
10001	商家不存在
10002	商品不存在
10003	订单不存在
10004	优惠券不存在
10005	账户余额不足
9. 接口权限说明
所有接口需要商家身份认证，通过 JWT Token 进行权限验证。不同模块的接口权限可进一步细化：
商家管理：仅超级管理员可访问
商品管理：运营人员可访问
订单管理：客服、运营人员可访问
营销活动：营销人员可访问
数据分析：运营、财务人员可访问
账户管理：财务人员可访问
以上 API 接口设计满足电商平台商家功能的基本需求，可根据实际业务场景进行调整和扩展。