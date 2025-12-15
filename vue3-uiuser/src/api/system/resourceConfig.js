import request from '@/utils/request'

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


