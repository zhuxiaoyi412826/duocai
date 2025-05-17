package com.duocai.mall.mapper;

import com.duocai.mall.model.Address;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址数据访问接口
 * @author trae
 */
@Mapper
public interface AddressMapper {
    /**
     * 根据ID查询地址
     * @param id 地址ID
     * @return 地址信息
     */
    Address selectById(Long id);
    
    /**
     * 查询用户的所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> selectByUserId(Long userId);
    
    /**
     * 插入新地址
     * @param address 地址信息
     */
    void insert(Address address);
    
    /**
     * 更新地址信息
     * @param address 地址信息
     * @return 受影响的行数
     */
    int update(Address address);
    
    /**
     * 删除地址
     * @param id 地址ID
     * @return 删除的记录数
     */
    int deleteById(Long id);
    
    /**
     * 清除用户的默认地址
     * @param userId 用户ID
     */
    void clearDefaultAddress(Long userId);
    
    /**
     * 查询用户的默认地址
     * @param userId 用户ID
     * @return 默认地址信息
     */
    Address selectDefaultByUserId(Long userId);
}