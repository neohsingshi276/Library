import request from '@/utils/request'

// 查询已发布的商品列表（公开接口，无需登录）
export function listPublishedProducts(params) {
  return request({
    url: '/app/product/list',
    method: 'get',
    params: params
  })
}

// 获取商品详情（公开接口，无需登录）
export function getProductDetail(productId) {
  return request({
    url: '/app/product/' + productId,
    method: 'get'
  })
}

// 获取分类树结构（公开接口，无需登录）
export function getCategoryTreeSelect() {
  return request({
    url: '/app/product/category/treeselect',
    method: 'get'
  })
}

// 切换收藏状态（收藏/取消收藏）
export function toggleFavorite(productId) {
  return request({
    url: '/app/product/' + productId + '/favorite',
    method: 'post'
  })
}

// 获取收藏状态
export function getFavoriteStatus(productId) {
  return request({
    url: '/app/product/' + productId + '/favorite/status',
    method: 'get'
  })
}

// 获取收藏列表
export function getFavoriteList(params) {
  return request({
    url: '/app/product/favorite/list',
    method: 'get',
    params: params
  })
}

// AI识别 + 创建商品
export function aiCreateProduct(data) {
  return request({
    url: '/app/product/ai',
    method: 'post',
    data: data
  })
}

// 仅AI识别（不创建商品）
export function aiDetectProduct(data) {
  return request({
    url: '/app/product/ai/detect',
    method: 'post',
    data: data
  })
}

// 查询当前用户的商品列表
export function listMyProduct(params) {
  return request({
    url: '/app/product/my/list',
    method: 'get',
    params: params
  })
}

// 获取当前用户的商品详情
export function getMyProduct(productId) {
  return request({
    url: '/app/product/my/' + productId,
    method: 'get'
  })
}

// 删除当前用户的商品
export function delMyProduct(productId) {
  return request({
    url: '/app/product/my/' + productId,
    method: 'delete'
  })
}

// 上架商品
export function onlineMyProduct(productId) {
  return request({
    url: '/app/product/my/' + productId + '/online',
    method: 'put'
  })
}

// 下架商品
export function offlineMyProduct(productId) {
  return request({
    url: '/app/product/my/' + productId + '/offline',
    method: 'put'
  })
}

// 查询当前用户已上架的商品列表（用于交换选择）
export function listMyPublishedProduct(params) {
  return request({
    url: '/app/product/my/published',
    method: 'get',
    params: params
  })
}
