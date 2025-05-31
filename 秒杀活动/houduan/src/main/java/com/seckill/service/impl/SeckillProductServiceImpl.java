package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.entity.SeckillProduct;
import com.seckill.mapper.SeckillProductMapper;
import com.seckill.service.ProductService;
import com.seckill.service.SeckillProductService;
import com.seckill.vo.SeckillProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 秒杀商品服务实现类
 */
@Service
public class SeckillProductServiceImpl extends ServiceImpl<SeckillProductMapper, SeckillProduct> implements SeckillProductService {

    @Autowired
    private SeckillProductMapper seckillProductMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String SECKILL_PRODUCT_STOCK_PREFIX = "seckill:stock:";
    private static final String SECKILL_PRODUCT_INFO_PREFIX = "seckill:product:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSeckillProduct(SeckillProduct seckillProduct) {
        // 检查商品是否存在且可用
        if (!productService.checkProductAvailable(seckillProduct.getProductId())) {
            throw new RuntimeException("商品不存在或已下架");
        }

        seckillProduct.setCreateTime(LocalDateTime.now());
        seckillProduct.setUpdateTime(LocalDateTime.now());
        // 由于SeckillProduct类中没有version字段，这里移除了setVersion的调用
        return save(seckillProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "seckill_product", key = "#seckillProduct.id")
    public boolean updateSeckillProduct(SeckillProduct seckillProduct) {
        seckillProduct.setUpdateTime(LocalDateTime.now());
        return updateById(seckillProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "seckill_product", key = "#id")
    public boolean deleteSeckillProduct(Long id) {
        // 删除Redis中的相关数据
        String stockKey = SECKILL_PRODUCT_STOCK_PREFIX + id;
        String productKey = SECKILL_PRODUCT_INFO_PREFIX + id;
        redisTemplate.delete(stockKey);
        redisTemplate.delete(productKey);

        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByActivityId(Long activityId) {
        List<SeckillProduct> products = seckillProductMapper.selectByActivityId(activityId);
        // 删除Redis中的相关数据
        for (SeckillProduct product : products) {
            String stockKey = SECKILL_PRODUCT_STOCK_PREFIX + product.getId();
            String productKey = SECKILL_PRODUCT_INFO_PREFIX + product.getId();
            redisTemplate.delete(stockKey);
            redisTemplate.delete(productKey);
        }
        
        return removeBatchByIds(products.stream().map(SeckillProduct::getId).collect(Collectors.toList()));
    }

    @Override
    @Cacheable(value = "seckill_product", key = "#id", unless = "#result == null")
    public SeckillProductVO getSeckillProductDetail(Long id) {
        SeckillProduct seckillProduct = getById(id);
        if (seckillProduct == null) {
            return null;
        }
        return convertToVO(seckillProduct);
    }

    @Override
    public IPage<SeckillProductVO> getSeckillProductsPage(Long activityId, Integer page, Integer size) {
        Page<SeckillProduct> productPage = new Page<>(page, size);
        IPage<SeckillProduct> productIPage = seckillProductMapper.selectPageByActivityId(activityId, productPage);
        return convertToVOPage(productIPage);
    }

    @Override
    public List<SeckillProductVO> getSeckillProductsByActivityId(Long activityId) {
        List<SeckillProduct> products = seckillProductMapper.selectByActivityId(activityId);
        return products.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long id, Integer quantity) {
        // 先检查Redis中的库存
        String stockKey = SECKILL_PRODUCT_STOCK_PREFIX + id;
        Long remainStock = redisTemplate.opsForValue().decrement(stockKey, quantity);
        
        if (remainStock != null && remainStock < 0) {
            // 库存不足，恢复Redis中的库存
            redisTemplate.opsForValue().increment(stockKey, quantity);
            return false;
        }

        // 更新数据库库存（使用乐观锁）
        SeckillProduct product = getById(id);
        if (product == null) {
            throw new RuntimeException("秒杀商品不存在");
        }
        
        // 使用乐观锁更新库存
        int version = product.getVersion();
        boolean success = seckillProductMapper.deductStock(id, quantity, version) > 0;
        if (!success) {
            // 数据库更新失败，恢复Redis中的库存
            redisTemplate.opsForValue().increment(stockKey, quantity);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean restoreStock(Long id, Integer quantity) {
        // 恢复Redis中的库存
        String stockKey = SECKILL_PRODUCT_STOCK_PREFIX + id;
        redisTemplate.opsForValue().increment(stockKey, quantity);
        
        // 更新数据库库存
        return seckillProductMapper.restoreStock(id, quantity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "seckill_product", key = "#id")
    public boolean updateStatus(Long id, Integer status) {
        return seckillProductMapper.updateStatus(id, status) > 0;
    }

    @Override
    public SeckillProductVO getSeckillProductByProductIdAndActivityId(Long productId, Long activityId) {
        SeckillProduct product = seckillProductMapper.selectByProductIdAndActivityId(productId, activityId);
        return convertToVO(product);
    }

    @Override
    public boolean checkAvailable(Long id) {
        SeckillProduct product = getById(id);
        if (product == null) {
            return false;
        }
        // 使用正确的字段名替换，status字段不存在，使用其他逻辑判断可用性
        return product.getSeckillStock() > 0;
    }

    @Override
    public Integer getCurrentStock(Long id) {
        // 优先从Redis获取
        String stockKey = SECKILL_PRODUCT_STOCK_PREFIX + id;
        Object stock = redisTemplate.opsForValue().get(stockKey);
        if (stock != null) {
            return Integer.parseInt(stock.toString());
        }
        
        // Redis中没有，从数据库获取
        SeckillProduct product = getById(id);
        return product != null ? product.getSeckillStock() : 0;
    }

    @Override
    public void preloadProductsForActivity(Long activityId) {
        List<SeckillProduct> products = seckillProductMapper.selectByActivityId(activityId);
        for (SeckillProduct product : products) {
            String stockKey = SECKILL_PRODUCT_STOCK_PREFIX + product.getId();
            String productKey = SECKILL_PRODUCT_INFO_PREFIX + product.getId();
            
            // 预热库存数据
            redisTemplate.opsForValue().set(stockKey, product.getSeckillStock());
            // 预热商品数据
            redisTemplate.opsForValue().set(productKey, convertToVO(product));
            
            // 设置过期时间（比活动结束时间多一小时）
            redisTemplate.expire(stockKey, 25, TimeUnit.HOURS);
            redisTemplate.expire(productKey, 25, TimeUnit.HOURS);
        }
    }

    /**
     * 将秒杀商品实体转换为VO对象
     */
    private SeckillProductVO convertToVO(SeckillProduct product) {
        if (product == null) {
            return null;
        }
        SeckillProductVO productVO = new SeckillProductVO();
        BeanUtils.copyProperties(product, productVO);
        return productVO;
    }

    /**
     * 将秒杀商品分页数据转换为VO分页数据
     */
    private IPage<SeckillProductVO> convertToVOPage(IPage<SeckillProduct> productPage) {
        IPage<SeckillProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        List<SeckillProductVO> voList = productPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }
}