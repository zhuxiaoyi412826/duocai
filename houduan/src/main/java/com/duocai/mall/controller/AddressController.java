package com.duocai.mall.controller;

import com.duocai.mall.model.Address;
import com.duocai.mall.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收货地址控制器
 * @author trae
 */
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {
    private final AddressService addressService;
    
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }
    
    /**
     * 添加收货地址
     * @param address 地址信息
     * @return 添加成功的地址信息
     */
    @PostMapping
    public ResponseEntity<?> createAddress(@RequestBody Address address) {
        Address createdAddress = addressService.createAddress(address);
        return ResponseEntity.ok(createdAddress);
    }
    
    /**
     * 更新收货地址
     * @param address 地址信息
     * @return 更新后的地址信息
     */
    @PutMapping("/addresses/{id}")
    public ResponseEntity<?> updateAddress(@RequestBody Address address, @RequestParam Long userId) {
        Address updatedAddress = addressService.updateAddress(address, userId);
        return ResponseEntity.ok(updatedAddress);
    }
    
    /**
     * 删除收货地址
     * @param id 地址ID
     * @return 删除结果
     */
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id, @RequestParam Long userId) {
        boolean success = addressService.deleteAddress(id, userId);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * 获取用户的收货地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    @GetMapping
    public ResponseEntity<?> getAddresses(@RequestParam Long userId) {
        List<Address> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }
    
    /**
     * 获取收货地址详情
     * @param id 地址ID
     * @return 地址信息
     */
    @GetMapping("/addresses/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Long id, @RequestParam Long userId) {
        Address address = addressService.getAddressById(id, userId);
        return ResponseEntity.ok(address);
    }
    
    /**
     * 设置默认收货地址
     * @param id 地址ID
     * @return 设置结果
     */
    @PutMapping("/addresses/{id}/default")
    public ResponseEntity<?> setDefaultAddress(@PathVariable Long id, @RequestParam Long userId) {
        boolean success = addressService.setDefaultAddress(id, userId);
        return ResponseEntity.ok(Map.of("success", success));
    }
}