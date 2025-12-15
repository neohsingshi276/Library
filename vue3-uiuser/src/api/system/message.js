import request from '@/utils/request'

// 获取通知列表
export function noticelist() {
  return request({
    url: '/client/message/noticelist',
    method: 'get'
  })
}

// 获取公告列表
export function announcementlist() {
  return request({
    url: '/client/message/announcementlist',
    method: 'get'
  })
}

// 获取公告详情
export function getMessageInfo(id) {
  return request({
    url: '/client/message/'+id,
    method: 'get'
  })
}