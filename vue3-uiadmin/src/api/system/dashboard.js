import request from '@/utils/request'

// 获取仪表盘统计数据
export function getStatistics() {
  return request({
    url: '/system/dashboard/statistics',
    method: 'get'
  })
}

// 获取商品状态统计（饼图数据）
export function getProductStatusStatistics() {
  return request({
    url: '/system/dashboard/product/status',
    method: 'get'
  })
}

// 获取订单趋势统计（折线图数据）
export function getOrderTrendStatistics() {
  return request({
    url: '/system/dashboard/order/trend',
    method: 'get'
  })
}

// 获取交易金额统计（柱状图数据）
export function getTransactionAmountStatistics() {
  return request({
    url: '/system/dashboard/transaction/amount',
    method: 'get'
  })
}

// 获取用户增长统计（仪表盘数据）
export function getUserGrowthStatistics() {
  return request({
    url: '/system/dashboard/user/growth',
    method: 'get'
  })
}






