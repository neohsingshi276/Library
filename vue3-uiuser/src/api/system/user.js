import request from '@/utils/request'

// 查询用户个人信息
export function getUserProfile() {
  return request({
    url: '/client/info',
    method: 'get'
  })
}

// 修改用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/client/info',
    method: 'put',
    data: data
  })
}

// 用户密码重置
export function updateUserPwd(oldPassword, newPassword) {
  const data = {
    oldPassword,
    newPassword
  }
  return request({
    url: '/client/info/updatePwd',
    method: 'put',
    params: data
  })
}

// 用户头像上传
export function uploadAvatar(data) {
  return request({
    url: '/client/info/avatar',
    method: 'post',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    data: data
  })
}

// 修改邮箱（通过验证码）
export function updateEmail(newEmail, code) {
  return request({
    url: '/client/profile/updateEmail',
    method: 'put',
    data: { newEmail, code }
  })
}

