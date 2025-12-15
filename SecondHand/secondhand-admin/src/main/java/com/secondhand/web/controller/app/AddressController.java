package com.secondhand.web.controller.app;

import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.system.domain.ClientAddress;
import com.secondhand.system.service.IClientAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址Controller
 */
@RestController
@RequestMapping("/app/address")
public class AddressController extends BaseController
{
    @Autowired
    private IClientAddressService clientAddressService;

    /**
     * 查询收货地址列表
     */
    @GetMapping("/list")
    public AjaxResult list()
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ClientAddress address = new ClientAddress();
        address.setUserId(userId);
        List<ClientAddress> list = clientAddressService.selectClientAddressList(address);
        return success(list);
    }

    /**
     * 获取收货地址详细信息
     */
    @GetMapping("/{addressId}")
    public AjaxResult getInfo(@PathVariable Long addressId)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ClientAddress address = clientAddressService.selectClientAddressById(addressId);
        if (address == null || !address.getUserId().equals(userId))
        {
            return error("收货地址不存在或无权限访问");
        }
        return success(address);
    }

    /**
     * 新增收货地址
     */
    @PostMapping
    public AjaxResult add(@RequestBody ClientAddress address)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        address.setUserId(userId);
        return toAjax(clientAddressService.insertClientAddress(address));
    }

    /**
     * 修改收货地址
     */
    @PutMapping
    public AjaxResult edit(@RequestBody ClientAddress address)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ClientAddress existing = clientAddressService.selectClientAddressById(address.getAddressId());
        if (existing == null || !existing.getUserId().equals(userId))
        {
            return error("收货地址不存在或无权限修改");
        }
        
        address.setUserId(userId);
        return toAjax(clientAddressService.updateClientAddress(address));
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/{addressIds}")
    public AjaxResult remove(@PathVariable Long[] addressIds)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        // 验证所有地址都属于当前用户
        for (Long addressId : addressIds)
        {
            ClientAddress address = clientAddressService.selectClientAddressById(addressId);
            if (address == null || !address.getUserId().equals(userId))
            {
                return error("收货地址不存在或无权限删除");
            }
        }
        
        return toAjax(clientAddressService.deleteClientAddressByIds(addressIds));
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/{addressId}/default")
    public AjaxResult setDefault(@PathVariable Long addressId)
    {
        Long userId = getUserId();
        if (userId == null)
        {
            return error("请先登录");
        }
        
        ClientAddress address = clientAddressService.selectClientAddressById(addressId);
        if (address == null || !address.getUserId().equals(userId))
        {
            return error("收货地址不存在或无权限操作");
        }
        
        return toAjax(clientAddressService.setDefaultAddress(addressId, userId));
    }
}

