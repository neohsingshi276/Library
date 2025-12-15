package com.secondhand.system.service;

import com.secondhand.system.domain.ClientAddress;

import java.util.List;

/**
 * 收货地址Service接口
 */
public interface IClientAddressService
{
    /**
     * 查询收货地址
     */
    ClientAddress selectClientAddressById(Long addressId);

    /**
     * 查询收货地址列表
     */
    List<ClientAddress> selectClientAddressList(ClientAddress clientAddress);

    /**
     * 新增收货地址
     */
    int insertClientAddress(ClientAddress clientAddress);

    /**
     * 修改收货地址
     */
    int updateClientAddress(ClientAddress clientAddress);

    /**
     * 批量删除收货地址
     */
    int deleteClientAddressByIds(Long[] addressIds);

    /**
     * 删除收货地址信息
     */
    int deleteClientAddressById(Long addressId);

    /**
     * 设置默认地址
     */
    int setDefaultAddress(Long addressId, Long userId);
}

