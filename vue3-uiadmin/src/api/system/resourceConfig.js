import request from '@/utils/request'

// 查询资源配置列表
export function listResourceConfig(query) {
  return request({
    url: '/system/resourceConfig/list',
    method: 'get',
    params: query
  })
}

// 查询资源配置详细
export function getResourceConfig(resourceId) {
  return request({
    url: '/system/resourceConfig/' + resourceId,
    method: 'get'
  })
}

// 根据场景和类型获取资源（公开接口）
export function getResourceBySceneAndType(resourceScene, resourceType) {
  return request({
    url: '/system/resourceConfig/getBySceneAndType',
    method: 'get',
    params: {
      resourceScene,
      resourceType
    },
    headers: {
      isToken: false
    }
  })
}

// 根据场景和类型获取资源列表（公开接口）
export function listResourceBySceneAndType(resourceScene, resourceType) {
  return request({
    url: '/system/resourceConfig/listBySceneAndType',
    method: 'get',
    params: {
      resourceScene,
      resourceType
    },
    headers: {
      isToken: false
    }
  })
}

// 新增资源配置
export function addResourceConfig(data) {
  return request({
    url: '/system/resourceConfig',
    method: 'post',
    data: data
  })
}

// 修改资源配置
export function updateResourceConfig(data) {
  return request({
    url: '/system/resourceConfig',
    method: 'put',
    data: data
  })
}

// 删除资源配置
export function delResourceConfig(resourceId) {
  return request({
    url: '/system/resourceConfig/' + resourceId,
    method: 'delete'
  })
}

// 导出资源配置
export function exportResourceConfig(query) {
  return request({
    url: '/system/resourceConfig/export',
    method: 'post',
    data: query
  })
}
