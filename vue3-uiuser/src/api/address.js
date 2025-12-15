import request from '@/utils/request'

// 查询收货地址列表
export function getAddressList() {
  return request({
    url: '/app/address/list',
    method: 'get'
  })
}

// 获取收货地址详细信息
export function getAddress(addressId) {
  return request({
    url: '/app/address/' + addressId,
    method: 'get'
  })
}

// 新增收货地址
export function addAddress(data) {
  return request({
    url: '/app/address',
    method: 'post',
    data: data
  })
}

// 修改收货地址
export function updateAddress(data) {
  return request({
    url: '/app/address',
    method: 'put',
    data: data
  })
}

// 删除收货地址
export function delAddress(addressIds) {
  return request({
    url: '/app/address/' + addressIds,
    method: 'delete'
  })
}

// 设置默认地址
export function setDefaultAddress(addressId) {
  return request({
    url: '/app/address/' + addressId + '/default',
    method: 'put'
  })
}

