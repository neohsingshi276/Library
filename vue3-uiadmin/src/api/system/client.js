import request from '@/utils/request'

// 查询用户信息列表
export function listClient(query) {
  return request({
    url: '/system/client/list',
    method: 'get',
    params: query
  })
}

// 查询用户信息详细
export function getClient(userId) {
  return request({
    url: '/system/client/' + userId,
    method: 'get'
  })
}

// 新增用户信息
export function addClient(data) {
  return request({
    url: '/system/client',
    method: 'post',
    data: data
  })
}

// 修改用户信息
export function updateClient(data) {
  return request({
    url: '/system/client',
    method: 'put',
    data: data
  })
}

// 删除用户信息
export function delClient(userId) {
  return request({
    url: '/system/client/' + userId,
    method: 'delete'
  })
}

// 用户密码重置
export function resetPwd(data) {
  return request({
    url: '/system/client/resetPwd',
    method: 'put',
    data: data
  })
}
