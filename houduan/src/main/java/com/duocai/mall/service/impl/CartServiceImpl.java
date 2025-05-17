package com.duocai.mall.service.impl;

import com.duocai.mall.mapper.CartMapper;
import com.duocai.mall.mapper.ProductMapper;
import com.duocai.mall.model.CartItems;
import com.duocai.mall.model.Products;
import com.duocai.mall.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车服务实现类
 * @author trae
 */
@Service
public class CartServiceImpl implements CartService {
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    
    public CartServiceImpl(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartItems addToCart(Long userId, Long productId, Integer quantity) {
        // 检查商品是否存在
        Products product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 检查商品是否已下架
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }

        // 检查库存是否充足
        if (product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        // 检查购物车中是否已存在该商品
        CartItems existingItem = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (existingItem != null) {
            // 更新数量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUpdatedAt(LocalDateTime.now());
            cartMapper.update(existingItem);
            return existingItem;
        }

        // 创建新的购物车项
        CartItems cartItem = new CartItems();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        cartItem.setSelected(true);
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());

        cartMapper.insert(cartItem);
        return cartItem;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartItems updateQuantity(Long id, Long userId, Integer quantity) {
        // 检查购物车项是否存在
        CartItems cartItem = cartMapper.selectById(id);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("购物车商品不存在");
        }

        // 检查商品库存
        Products product = productMapper.selectById(cartItem.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        // 更新数量
        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartMapper.update(cartItem);

        return cartItem;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCartItem(Long id, Long userId) {
        return cartMapper.deleteByIdAndUserId(id, userId) > 0;
    }
    
    @Override
    public List<CartItems> getUserCartItems(Long userId) {
        return cartMapper.selectByUserId(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearCart(Long userId) {
        return cartMapper.deleteByUserId(userId) > 0;
    }

    @Override
    public boolean updateSelected(Long id, Long userId, Integer selected) {
        // 检查购物车项是否存在
        CartItems cartItem = cartMapper.selectById(id);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("购物车商品不存在");
        }

        // 更新选中状态
        return cartMapper.updateSelectedStatus(id, userId, selected) > 0;
    }

    @Override
    public List<CartItems> getSelectedCartItems(Long userId) {
        return cartMapper.selectSelectedItems(userId);
    }

    /**
     * 更新购物车项商品数量（接口实现，参数与CartService一致）
     * @param id 购物车项ID
     * @param quantity 数量
     * @return 更新后的购物车项信息
     */
    @Override
    public CartItems updateCartItemQuantity(Long id, Integer quantity) {
        // 检查购物车项是否存在
        CartItems cartItem = cartMapper.selectById(id);
        if (cartItem == null) {
            throw new IllegalArgumentException("购物车项不存在");
        }
        // 更新商品数量
        cartItem.setQuantity(quantity);
        cartMapper.update(cartItem);
        return cartItem;
    }

    @Override
    public boolean updateCartItemSelected(Long id, Long userId, boolean selected) {
        // 检查购物车项是否存在
        CartItems cartItem = cartMapper.selectById(id);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new IllegalArgumentException("购物车项不存在");
        }

        // 更新选中状态
        return cartMapper.updateSelectedStatus(id, userId, selected ? 1 : 0) > 0;
    }
}