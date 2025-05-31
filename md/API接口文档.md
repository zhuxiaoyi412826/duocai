# 多彩商城RESTful API文档

## 基础信息
- 基础URL: `https://api.duocai.com/v1`
- 响应格式: JSON
- 认证方式: JWT

## 通用响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1630000000
}
```

## 用户认证

### 用户注册
`POST /auth/register`

请求参数:
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "phone": "string"
}
```

### 用户登录
`POST /auth/login`

请求参数:
```json
{
  "username": "string",
  "password": "string"
}
```

## 商品管理

### 获取商品列表
`GET /products`

查询参数:
- category: 商品分类
- page: 页码
- size: 每页数量

### 获取商品详情
`GET /products/{id}`

## 购物车

### 获取购物车
`GET /cart`

### 添加商品到购物车
`POST /cart`

请求参数:
```json
{
  "product_id": "number",
  "quantity": "number"
}
```

## 订单管理

### 创建订单
`POST /orders`

请求参数:
```json
{
  "cart_items": "array",
  "address_id": "number"
}
```

### 获取订单列表
`GET /orders`

## 地址管理

### 获取地址列表
`GET /addresses`

### 添加地址
`POST /addresses`

请求参数:
```json
{
  "receiver": "string",
  "phone": "string",
  "province": "string",
  "city": "string",
  "district": "string",
  "detail": "string",
  "is_default": "boolean"
}
```

## 支付系统

### 创建支付
`POST /payments`

请求参数:
```json
{
  "order_id": "number",
  "payment_method": "string"
}
```

你这个API 接口文档写的不完整，根据表结构和用户操作在补充一些，还有一个接口文档要包括如下部分1 请求方式 2 请求地址 3 请求参数， 要写的完整，要写出增加 查询 删除 修改 这些 ，遵循markdown格式















