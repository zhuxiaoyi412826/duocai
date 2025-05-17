package com.duocai.mall.service;

import com.duocai.mall.model.Address;
import java.util.List;

/**
 * 收货地址服务接口
 * @author trae
 */
public interface AddressService {
    /**
     * 添加收货地址
     * @param address 地址信息
     * @param userId 用户ID
     * @return 创建成功的地址信息
     */
    Address addAddress(Address address, Long userId);
    
    /**
     * 更新收货地址
     * @param address 地址信息
     * @param userId 用户ID
     * @return 更新后的地址信息
     */
    Address updateAddress(Address address, Long userId);
    
    /**
     * 删除收货地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteAddress(Long id, Long userId);
    
    /**
     * 获取收货地址详情
     * @param id 地址ID
     * @param userId 用户ID
     * @return 地址信息
     */
    Address getAddressById(Long id, Long userId);
    
    /**
     * 获取用户的收货地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> getUserAddresses(Long userId);
    
    /**
     * 设置默认收货地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 是否设置成功
     */
    boolean setDefaultAddress(Long id, Long userId);
    
    /**
     * 获取用户默认收货地址
     * @param userId 用户ID
     * @return 默认地址信息
     */
    Address getDefaultAddress(Long userId);

    Address createAddress(Address address);
}