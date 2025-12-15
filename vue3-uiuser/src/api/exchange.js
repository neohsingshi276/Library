import request from '@/utils/request'

// 查询我发起的交换请求列表
export function getMyExchangeRequests(params) {
  return request({
    url: '/app/exchange/my/requests',
    method: 'get',
    params: params
  })
}

// 查询我接收的交换请求列表
export function getMyExchangeReceives(params) {
  return request({
    url: '/app/exchange/my/receives',
    method: 'get',
    params: params
  })
}

// 查询交换详情
export function getExchangeInfo(exchangeId) {
  return request({
    url: '/app/exchange/' + exchangeId,
    method: 'get'
  })
}

// 发起交换请求
export function createExchangeRequest(data) {
  return request({
    url: '/app/exchange/create',
    method: 'post',
    data: data
  })
}

// 接受交换请求
export function acceptExchange(exchangeId) {
  return request({
    url: '/app/exchange/' + exchangeId + '/accept',
    method: 'put'
  })
}

// 拒绝交换请求
export function rejectExchange(exchangeId, rejectReason) {
  return request({
    url: '/app/exchange/' + exchangeId + '/reject',
    method: 'put',
    data: { rejectReason }
  })
}

// 发起者确认完成
export function requesterComplete(exchangeId) {
  return request({
    url: '/app/exchange/' + exchangeId + '/requester/complete',
    method: 'put'
  })
}

// 接收者确认完成
export function receiverComplete(exchangeId) {
  return request({
    url: '/app/exchange/' + exchangeId + '/receiver/complete',
    method: 'put'
  })
}

// 取消交换
export function cancelExchange(exchangeId, cancelReason) {
  return request({
    url: '/app/exchange/' + exchangeId + '/cancel',
    method: 'put',
    data: { cancelReason }
  })
}






