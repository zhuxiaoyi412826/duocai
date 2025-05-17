package com.duocai.mall.controller;

import com.duocai.mall.model.CartItems;
import com.duocai.mall.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 * @author trae
 */
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;
    
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    /**
     * 添加商品到购物车
     * @param cartItem 购物车项信息
     * @return 添加成功的购物车项信息
     */
    @PostMapping("/items")
    public ResponseEntity<?> addToCart(@RequestBody CartItems cartItem) {
        CartItems addedItem = cartService.addToCart(cartItem.getUserId(), cartItem.getProductId(), cartItem.getQuantity());
        return ResponseEntity.ok(addedItem);
    }
    
    /**
     * 更新购物车商品数量
     * @param id 购物车项ID
     * @param quantity 商品数量
     * @return 更新后的购物车项信息
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> quantityMap) {
        CartItems updatedItem = cartService.updateCartItemQuantity(id, quantityMap.get("quantity"));
        return ResponseEntity.ok(updatedItem);
    }
    
    /**
     * 删除购物车商品
     * @param id 购物车项ID
     * @return 删除结果
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long id, @RequestParam Long userId) {
        boolean success = cartService.deleteCartItem(id, userId);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * 获取用户购物车列表
     * @param userId 用户ID
     * @return 购物车商品列表
     */
    @GetMapping
    public ResponseEntity<?> getCartItems(@RequestParam Long userId) {
        List<CartItems> cartItems = cartService.getUserCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }
    
    /**
     * 清空用户购物车
     * @param userId 用户ID
     * @return 清空结果
     */
    @DeleteMapping
    public ResponseEntity<?> clearCart(@RequestParam Long userId) {
        boolean success = cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * 选择或取消选择购物车商品
     * @param id 购物车项ID
     * @param selected 是否选中
     * @return 更新结果
     */
    @PutMapping("/items/{id}/selected")
    public ResponseEntity<?> updateCartItemSelected(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> selectedMap,
            @RequestParam Long userId) {
        boolean success = cartService.updateSelected(id, userId, selectedMap.get("selected") ? 1 : 0);
        return ResponseEntity.ok(Map.of("success", success));
    }
}