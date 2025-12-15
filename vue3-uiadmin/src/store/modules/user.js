import { login, logout, getInfo, emailLogin as emailLoginApi } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'
import defAva from '@/assets/images/profile.jpg'

const useUserStore = defineStore(
  'user',
  {
    state: () => ({
      token: getToken(),
      id: '',
      name: '',
      avatar: '',
      roles: [],
      permissions: []
    }),
    actions: {
      // 登录
      login(userInfo) {
        const username = userInfo.username.trim()
        const password = userInfo.password
        const code = userInfo.code
        const uuid = userInfo.uuid
        return new Promise((resolve, reject) => {
          login(username, password, code, uuid).then(res => {
            setToken(res.token)
            this.token = res.token
            // 清空用户信息，确保路由守卫会调用 getInfo() 重新获取
            this.roles = []
            this.permissions = []
            this.id = ''
            this.name = ''
            this.avatar = ''
            resolve()
          }).catch(error => {
            reject(error)
          })
        })
      },
      // 邮箱登录
      emailLogin(payload) {
        const email = payload.email.trim()
        const code = payload.code
        return new Promise((resolve, reject) => {
          emailLoginApi(email, code).then(res => {
            setToken(res.token)
            this.token = res.token
            // 清空用户信息，确保路由守卫会调用 getInfo() 重新获取
            this.roles = []
            this.permissions = []
            this.id = ''
            this.name = ''
            this.avatar = ''
            resolve()
          }).catch(error => {
            reject(error)
          })
        })
      },
      // 获取用户信息
      getInfo() {
        return new Promise((resolve, reject) => {
          getInfo().then(res => {
            const user = res.user
            const avatar = (user.avatar == "" || user.avatar == null) ? defAva : user.avatar;

            if (res.roles && res.roles.length > 0) { // 验证返回的roles是否是一个非空数组
              this.roles = res.roles
              this.permissions = res.permissions
            } else {
              this.roles = ['ROLE_DEFAULT']
            }
            this.id = user.userId
            this.name = user.userName
            this.avatar = avatar
            resolve(res)
          }).catch(error => {
            reject(error)
          })
        })
      },
      // 退出系统
      logOut() {
        return new Promise((resolve, reject) => {
          logout(this.token).then(() => {
            this.token = ''
            this.roles = []
            this.permissions = []
            removeToken()
            resolve()
          }).catch(error => {
            reject(error)
          })
        })
      }
    }
  })

export default useUserStore
