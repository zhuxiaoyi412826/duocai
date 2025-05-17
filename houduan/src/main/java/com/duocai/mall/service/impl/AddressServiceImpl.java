package com.duocai.mall.service.impl;

import com.duocai.mall.mapper.AddressMapper;
import com.duocai.mall.model.Address;
import com.duocai.mall.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收货地址服务实现类
 * @author trae
 */
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressMapper addressMapper;
    
    public AddressServiceImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Address addAddress(Address address, Long userId) {
        // 设置用户ID和创建时间
        address.setUserId(userId);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        
        // 如果是默认地址，需要将其他地址设置为非默认
        if (address.getIsDefault() == 1) {
            addressMapper.clearDefaultAddress(userId);
        }
        
        // 如果是第一个地址，自动设置为默认地址
        List<Address> existingAddresses = addressMapper.selectByUserId(userId);
        if (existingAddresses.isEmpty()) {
            address.setIsDefault(1);
        }
        
        // 保存地址信息
        addressMapper.insert(address);
        return address;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Address updateAddress(Address address, Long userId) {
        // 检查地址是否存在且属于当前用户
        Address existingAddress = addressMapper.selectById(address.getId());
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new RuntimeException("收货地址不存在");
        }
        
        // 如果设置为默认地址，需要将其他地址设置为非默认
        if (address.getIsDefault() == 1) {
            addressMapper.clearDefaultAddress(userId);
        }
        
        // 更新地址信息
        address.setUserId(userId);
        address.setUpdatedAt(LocalDateTime.now());
        addressMapper.update(address);
        
        return addressMapper.selectById(address.getId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAddress(Long id, Long userId) {
        // 检查地址是否存在且属于当前用户
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("收货地址不存在");
        }
        
        // 如果删除的是默认地址，需要重新设置默认地址
        if (address.getIsDefault() == 1) {
            List<Address> otherAddresses = addressMapper.selectByUserId(userId);
            if (otherAddresses.size() > 1) {
                Address newDefaultAddress = otherAddresses.stream()
                        .filter(a -> !a.getId().equals(id))
                        .findFirst()
                        .orElse(null);
                if (newDefaultAddress != null) {
                    newDefaultAddress.setIsDefault(1);
                    addressMapper.update(newDefaultAddress);
                }
            }
        }
        
        return addressMapper.deleteById(id) > 0;
    }
    
    @Override
    public Address getAddressById(Long id, Long userId) {
        Address address = addressMapper.selectById(id);
        if (address != null && !address.getUserId().equals(userId)) {
            return null;
        }
        return address;
    }
    
    @Override
    public List<Address> getUserAddresses(Long userId) {
        return addressMapper.selectByUserId(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(Long id, Long userId) {
        // 检查地址是否存在且属于当前用户
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("收货地址不存在");
        }
        
        // 将其他地址设置为非默认
        addressMapper.clearDefaultAddress(userId);
        
        // 设置新的默认地址
        address.setIsDefault(1);
        address.setUpdatedAt(LocalDateTime.now());
        return addressMapper.update(address) > 0;
    }
    
    @Override
    public Address getDefaultAddress(Long userId) {
        return addressMapper.selectDefaultByUserId(userId);
    }

    @Override
    public Address createAddress(Address address) {
        throw new UnsupportedOperationException("Unimplemented method 'createAddress'");
    }
}