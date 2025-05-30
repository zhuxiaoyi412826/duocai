package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.entity.Product;
import com.seckill.mapper.ProductMapper;
import com.seckill.service.ProductService;
import com.seckill.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createProduct(Product product) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        return save(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "product", key = "#product.id")
    public boolean updateProduct(Product product) {
        product.setUpdateTime(LocalDateTime.now());
        return updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "product", key = "#productId")
    public boolean deleteProduct(Long productId) {
        return removeById(productId);
    }

    @Override
    @Cacheable(value = "product", key = "#productId", unless = "#result == null")
    public ProductVO getProductDetail(Long productId) {
        Product product = getById(productId);
        if (product == null) {
            return null;
        }
        return convertToVO(product);
    }

    @Override
    public IPage<ProductVO> getOnSaleProducts(Integer page, Integer size) {
        Page<Product> productPage = new Page<>(page, size);
        IPage<Product> productIPage = productMapper.selectOnSaleProducts(productPage);
        return convertToVOPage(productIPage);
    }

    @Override
    public List<ProductVO> getProductsByCategory(Long categoryId) {
        List<Product> products = productMapper.selectByCategoryId(categoryId);
        return products.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<ProductVO> searchProducts(String keyword, Integer page, Integer size) {
        Page<Product> productPage = new Page<>(page, size);
        IPage<Product> productIPage = productMapper.searchProducts(keyword, productPage);
        return convertToVOPage(productIPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "product", key = "#productId")
    public boolean updateStock(Long productId, Integer stock) {
        return productMapper.updateStock(productId, stock) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "product", key = "#productId")
    public boolean deductStock(Long productId, Integer quantity) {
        Product product = getById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        return productMapper.deductStock(productId, quantity, product.getVersion()) > 0;
    }

    @Override
    public List<ProductVO> getProductsByIds(List<Long> productIds) {
        List<Product> products = listByIds(productIds);
        return products.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkProductAvailable(Long productId) {
        Product product = getById(productId);
        return product != null && product.getStatus() == 1;
    }

    @Override
    public Integer getCurrentStock(Long productId) {
        Product product = getById(productId);
        return product != null ? product.getStock() : 0;
    }

    /**
     * 将商品实体转换为VO对象
     */
    private ProductVO convertToVO(Product product) {
        if (product == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        return productVO;
    }

    /**
     * 将商品分页数据转换为VO分页数据
     */
    private IPage<ProductVO> convertToVOPage(IPage<Product> productPage) {
        IPage<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        List<ProductVO> voList = productPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }
}