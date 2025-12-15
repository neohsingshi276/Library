import request from '@/utils/request'

// 创建订单
export function createOrder(data) {
  return request({
    url: '/app/order/create',
    method: 'post',
    data: data
  })
}

// 查询订单列表
export function getOrderList(params) {
  return request({
    url: '/app/order/list',
    method: 'get',
    params: params
  })
}

// 获取订单详细信息
export function getOrder(orderId) {
  return request({
    url: '/app/order/' + orderId,
    method: 'get'
  })
}

// 根据订单号获取订单信息
export function getOrderByNo(orderNo) {
  return request({
    url: '/app/order/no/' + orderNo,
    method: 'get'
  })
}

// 支付订单（模拟支付）
export function payOrder(orderNo, payMethod) {
  return request({
    url: '/app/order/' + orderNo + '/pay',
    method: 'post',
    data: { payMethod }
  })
}

// 取消订单
export function cancelOrder(orderNo, cancelReason) {
  return request({
    url: '/app/order/' + orderNo + '/cancel',
    method: 'post',
    data: { cancelReason }
  })
}

// 确认收货
export function confirmReceive(orderNo) {
  return request({
    url: '/app/order/' + orderNo + '/confirm',
    method: 'post'
  })
}

// 卖家发货
export function shipOrder(orderNo, expressCompany, expressNo) {
  return request({
    url: '/app/order/' + orderNo + '/ship',
    method: 'post',
    data: { expressCompany, expressNo }
  })
}

// 申请退货/退款
export function applyReturn(orderId, returnType, returnReason, returnDescription, returnImages, receiveStatus) {
  return request({
    url: '/app/order/' + orderId + '/return',
    method: 'post',
    data: { returnType, returnReason, returnDescription, returnImages, receiveStatus }
  })
}

// 填写退货物流信息
export function fillReturnShipping(returnId, expressCompany, expressNo) {
  return request({
    url: '/app/return/' + returnId + '/shipping',
    method: 'post',
    data: { expressCompany, expressNo }
  })
}

// 获取退货列表
export function getReturnList(params) {
  return request({
    url: '/app/return/list',
    method: 'get',
    params: params
  })
}

// 获取退货详情
export function getReturnDetail(returnId) {
  return request({
    url: '/app/return/' + returnId,
    method: 'get'
  })
}

// 取消退货申请
export function cancelReturn(returnId, cancelReason) {
  return request({
    url: '/app/return/' + returnId + '/cancel',
    method: 'post',
    data: { cancelReason }
  })
}

// 卖家同意退货申请
export function approveReturn(returnId) {
  return request({
    url: '/app/return/' + returnId + '/approve',
    method: 'post'
  })
}

// 卖家拒绝退货申请
export function rejectReturn(returnId, rejectReason) {
  return request({
    url: '/app/return/' + returnId + '/reject',
    method: 'post',
    data: { rejectReason }
  })
}

// 卖家确认收到退货
export function confirmReturnReceive(returnId) {
  return request({
    url: '/app/return/' + returnId + '/confirm-receive',
    method: 'post'
  })
}

// 卖家退款（如后端未在收货接口自动退款，则调用此接口）
export function refundReturn(returnId) {
  return request({
    url: '/app/return/' + returnId + '/refund',
    method: 'post'
  })
}

// 查询订单的退款/退货记录
export function getOrderReturns(orderId) {
  return request({
    url: '/app/order/' + orderId + '/returns',
    method: 'get'
  })
}

// 创建评价
export function createReview(data) {
  return request({
    url: '/app/review/create',
    method: 'post',
    data: data
  })
}

// 获取评价详情
export function getReview(reviewId) {
  return request({
    url: '/app/review/' + reviewId,
    method: 'get'
  })
}

// 查询订单的所有评价
export function getReviewsByOrderId(orderId) {
  return request({
    url: '/app/review/order/' + orderId,
    method: 'get'
  })
}

// 查询商品的所有评价
export function getReviewsByProductId(productId, params) {
  return request({
    url: '/app/review/product/' + productId,
    method: 'get',
    params: params
  })
}

// 查询我的评价列表
export function getMyReviews(params) {
  return request({
    url: '/app/review/my-reviews',
    method: 'get',
    params: params
  })
}

// 查询收到的评价列表
export function getReceivedReviews(params) {
  return request({
    url: '/app/review/received-reviews',
    method: 'get',
    params: params
  })
}

// 检查是否可以评价订单
export function checkCanReview(orderId) {
  return request({
    url: '/app/review/check/' + orderId,
    method: 'get'
  })
}

