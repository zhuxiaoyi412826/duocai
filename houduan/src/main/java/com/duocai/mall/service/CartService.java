package com.duocai.mall.service;

import com.duocai.mall.model.CartItems;

import java.util.List;

/**
 * 购物车服务接口
 * @author trae
 */
public interface CartService {
    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 数量
     * @return 购物车项信息
     */
    CartItems addToCart(Long userId, Long productId, Integer quantity);
    
    /**
     * 更新购物车商品数量
     * @param id 购物车项ID
     * @param userId 用户ID
     * @param quantity 数量
     * @return 更新后的购物车项信息
     */
    CartItems updateQuantity(Long id, Long userId, Integer quantity);
    
    /**
     * 删除购物车商品
     * @param id 购物车项ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteCartItem(Long id, Long userId);
    
    /**
     * 获取用户购物车列表
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItems> getUserCartItems(Long userId);
    
    /**
     * 清空用户购物车
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean clearCart(Long userId);
    
    /**
     * 选中/取消选中购物车商品
     * @param id 购物车项ID
     * @param userId 用户ID
     * @param selected 是否选中
     * @return 是否更新成功
     */
    boolean updateSelected(Long id, Long userId, Integer selected);
    
    /**
     * 获取用户选中的购物车商品
     * @param userId 用户ID
     * @return 选中的购物车项列表
     */
    List<CartItems> getSelectedCartItems(Long userId);

    CartItems updateCartItemQuantity(Long id, Integer integer);
    
    /**
     * 更新购物车项的选中状态
     * @param id 购物车项ID
     * @param userId 用户ID
     * @param selected 是否选中
     * @return 是否更新成功
     */
    boolean updateCartItemSelected(Long id, Long userId, boolean selected);
}