package com.secondhand.system.service.impl;

import com.secondhand.common.utils.DateUtils;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.system.domain.ClientAddress;
import com.secondhand.system.mapper.ClientAddressMapper;
import com.secondhand.system.service.IClientAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收货地址Service业务层处理
 */
@Service
public class ClientAddressServiceImpl implements IClientAddressService
{
    private static final Logger log = LoggerFactory.getLogger(ClientAddressServiceImpl.class);

    @Autowired
    private ClientAddressMapper clientAddressMapper;

    @Override
    public ClientAddress selectClientAddressById(Long addressId)
    {
        return clientAddressMapper.selectClientAddressById(addressId);
    }

    @Override
    public List<ClientAddress> selectClientAddressList(ClientAddress clientAddress)
    {
        return clientAddressMapper.selectClientAddressList(clientAddress);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertClientAddress(ClientAddress clientAddress)
    {
        clientAddress.setCreateTime(DateUtils.getNowDate());
        clientAddress.setUpdateTime(DateUtils.getNowDate());
        
        // 如果设置为默认地址，取消其他默认地址
        if ("1".equals(clientAddress.getIsDefault()))
        {
            clientAddressMapper.cancelOtherDefaultAddress(clientAddress.getUserId(), null);
        }
        else if (StringUtils.isEmpty(clientAddress.getIsDefault()))
        {
            // 检查是否已有地址，如果没有则设为默认
            ClientAddress query = new ClientAddress();
            query.setUserId(clientAddress.getUserId());
            List<ClientAddress> existing = clientAddressMapper.selectClientAddressList(query);
            if (existing == null || existing.isEmpty())
            {
                clientAddress.setIsDefault("1");
            }
            else
            {
                clientAddress.setIsDefault("0");
            }
        }
        
        return clientAddressMapper.insertClientAddress(clientAddress);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateClientAddress(ClientAddress clientAddress)
    {
        clientAddress.setUpdateTime(DateUtils.getNowDate());
        
        // 如果设置为默认地址，取消其他默认地址
        if ("1".equals(clientAddress.getIsDefault()))
        {
            clientAddressMapper.cancelOtherDefaultAddress(clientAddress.getUserId(), clientAddress.getAddressId());
        }
        
        return clientAddressMapper.updateClientAddress(clientAddress);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteClientAddressByIds(Long[] addressIds)
    {
        return clientAddressMapper.deleteClientAddressByIds(addressIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteClientAddressById(Long addressId)
    {
        return clientAddressMapper.deleteClientAddressById(addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setDefaultAddress(Long addressId, Long userId)
    {
        // 取消其他默认地址
        clientAddressMapper.cancelOtherDefaultAddress(userId, addressId);
        
        // 设置当前地址为默认
        ClientAddress address = new ClientAddress();
        address.setAddressId(addressId);
        address.setIsDefault("1");
        address.setUpdateTime(DateUtils.getNowDate());
        return clientAddressMapper.updateClientAddress(address);
    }
}

