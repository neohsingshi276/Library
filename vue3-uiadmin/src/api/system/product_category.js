import request from '@/utils/request'

// 查询商品类别列表
export function listProduct_category(query) {
  return request({
    url: '/system/product_category/list',
    method: 'get',
    params: query
  })
}

// 查询商品类别详细
export function getProduct_category(categoryId) {
  return request({
    url: '/system/product_category/' + categoryId,
    method: 'get'
  })
}

// 新增商品类别
export function addProduct_category(data) {
  return request({
    url: '/system/product_category',
    method: 'post',
    data: data
  })
}

// 修改商品类别
export function updateProduct_category(data) {
  return request({
    url: '/system/product_category',
    method: 'put',
    data: data
  })
}

// 删除商品类别
export function delProduct_category(categoryId) {
  return request({
    url: '/system/product_category/' + categoryId,
    method: 'delete'
  })
}

// 查询商品类别下拉树结构
export function treeselectProduct_category() {
  return request({
    url: '/system/product_category/treeselect',
    method: 'get'
  })
}
