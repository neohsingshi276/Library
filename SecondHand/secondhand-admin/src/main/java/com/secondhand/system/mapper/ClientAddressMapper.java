package com.secondhand.system.mapper;

import com.secondhand.system.domain.ClientAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收货地址Mapper接口
 */
public interface ClientAddressMapper
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
     * 删除收货地址
     */
    int deleteClientAddressById(Long addressId);

    /**
     * 批量删除收货地址
     */
    int deleteClientAddressByIds(Long[] addressIds);

    /**
     * 取消其他默认地址
     */
    int cancelOtherDefaultAddress(@Param("userId") Long userId, @Param("excludeAddressId") Long excludeAddressId);
}

