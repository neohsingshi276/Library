<template>
  <div class="login-container">
    <!-- 登录页面 -->
    <div 
      class="login" 
      :style="loginResourceType === 'image' && loginBackground ? { backgroundImage: `url('${loginBackground}')` } : {}"
    >
      <video v-if="loginResourceType === 'video' && loginBackground" :src="loginBackground" autoplay loop muted class="login-video"></video>
    </div>
    <!-- 登录表单 -->
    <div class="login-form">
      <el-card shadow="never" class="login-card" style="margin-left: 130px;">
        <el-form ref="loginRef" :model="loginForm" :rules="loginRules">
          <h3 class="title">Fashion Clothing Exchange & Second-hand Trading Admin System</h3>
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="Account"
            >
              <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="Password"
              @keyup.enter="handleLogin"
            >
              <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="Verification Code"
              style="width: 58%"
              @keyup.enter="handleLogin"
            >
              <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img"/>
            </div>
          </el-form-item>
          <div style="float: right;" v-if="register">
            <router-link class="link-type" :to="'/register'">Register Now</router-link>
          </div>
          <div class="login-options-row">
            <el-checkbox v-model="loginForm.rememberMe">Remember Password</el-checkbox>
            <router-link to="/emailLogin" class="link-type">
              Email Login
            </router-link>
          </div>
          <el-form-item style="width:100%;">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              style="width:350px"
              @click.prevent="handleLogin"
            >
              <span v-if="!loading">Login</span>
              <span v-else>Logging in...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    <!--  底部  -->
    <div class="el-login-footer">
    </div>
  </div>
</template>

<script setup>
import { getCodeImg } from "@/api/login";
import { getResourceBySceneAndType } from "@/api/system/resourceConfig";
import Cookies from "js-cookie";
import { encrypt, decrypt } from "@/utils/jsencrypt";
import useUserStore from '@/store/modules/user'

const userStore = useUserStore()
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const loginForm = ref({
  username: "admin",
  password: "admin123",
  rememberMe: false,
  code: "",
  uuid: ""
});

const loginRules = {
  username: [{ required: true, trigger: "blur", message: "Please enter your account" }],
  password: [{ required: true, trigger: "blur", message: "Please enter your password" }],
  code: [{ required: true, trigger: "change", message: "Please enter verification code" }]
};

const codeUrl = ref("");
const loading = ref(false);
// 验证码开关
const captchaEnabled = ref(true);
// 注册开关
const register = ref(false);
const redirect = ref(undefined);
// 登录背景资源
const loginBackground = ref("");
const loginResourceType = ref("image"); // image 或 video

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect;
}, { immediate: true });

function goToEmailLogin() {
  const query = { ...(route.query || {}) };
  router.push({ path: '/emailLogin', query });
}

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true;
      // 勾选了需要记住密码设置在 cookie 中设置记住用户名和密码
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 });
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 });
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 });
      } else {
        // 否则移除
        Cookies.remove("username");
        Cookies.remove("password");
        Cookies.remove("rememberMe");
      }
      // 调用action的登录方法
      userStore.login(loginForm.value).then(() => {
        const query = route.query;
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== "redirect") {
            acc[cur] = query[cur];
          }
          return acc;
        }, {});
        router.push({ path: redirect.value || "/", query: otherQueryParams });
      }).catch(() => {
        loading.value = false;
        // 重新获取验证码
        if (captchaEnabled.value) {
          getCode();
        }
      });
    }
  });
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img;
      loginForm.value.uuid = res.uuid;
    }
  });
}

function getCookie() {
  const username = Cookies.get("username");
  const password = Cookies.get("password");
  const rememberMe = Cookies.get("rememberMe");
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password),
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
  };
}


// 处理资源URL
function processResourceUrl(url) {
  if (!url) return '';
  
  url = String(url).trim();
  
  // 如果是完整URL（http/https），直接使用（阿里云OSS）
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  
  // 如果是相对路径（以/开头），拼接基础API路径（本地存储）
  if (url.startsWith('/')) {
    const baseUrl = import.meta.env.VITE_APP_BASE_API || '';
    // 确保baseUrl不以/结尾，url以/开头
    return baseUrl + url;
  }
  
  // 其他情况，拼接基础API路径
  const baseUrl = import.meta.env.VITE_APP_BASE_API || '';
  return baseUrl + '/' + url;
}

// 获取登录背景资源
async function getLoginResource() {
  try {
    const res = await getResourceBySceneAndType('login_admin', null);
    console.log('获取资源完整响应:', JSON.stringify(res, null, 2));
    
    if (res && res.code === 200) {
      let resource = null;
      
      // 处理返回的数据结构
      if (Array.isArray(res.data)) {
        // 如果是数组，取第一个
        if (res.data.length > 0) {
          resource = res.data[0];
        }
      } else if (res.data && typeof res.data === 'object') {
        // 如果是对象，检查是否有resourceType属性
        if (res.data.resourceType) {
          resource = res.data;
        } else if (Array.isArray(res.data.rows)) {
          // 可能是分页数据
          resource = res.data.rows.length > 0 ? res.data.rows[0] : null;
        }
      }
      
      console.log('解析后的资源数据:', resource);
      
      if (resource && resource.resourceUrl) {
        loginResourceType.value = resource.resourceType || 'image';
        loginBackground.value = processResourceUrl(resource.resourceUrl);
        
        console.log('✅ 加载登录资源成功:', { 
          type: loginResourceType.value, 
          originalUrl: resource.resourceUrl,
          finalUrl: loginBackground.value,
          baseUrl: import.meta.env.VITE_APP_BASE_API
        });
      } else {
        console.warn('⚠️ 资源数据为空或缺少resourceUrl，resource:', resource);
        // 如果没有配置，使用默认图片
        loginResourceType.value = 'image';
        loginBackground.value = require("../assets/images/login-background.jpg");
      }
    } else {
      console.warn('⚠️ 响应code不是200或data为空，res:', res);
      // 如果没有配置，使用默认图片
      loginResourceType.value = 'image';
      loginBackground.value = require("../assets/images/login-background.jpg");
    }
  } catch (error) {
    console.error('❌ 获取登录资源失败:', error);
    console.error('错误详情:', error.response || error.message);
    // 如果获取失败，使用默认图片
    loginResourceType.value = 'image';
    loginBackground.value = require("../assets/images/login-background.jpg");
  }
}

getCode();
getCookie();
getLoginResource();
</script>

<style lang='scss' scoped>
.login-container {
  display: flex;
  align-items: stretch;
  height: 100vh;

  .login {
    flex: 3;
    background: rgba(38, 72, 176);
    background-repeat: no-repeat;
    background-position: center;
    background-size: cover;
    border-top-right-radius: 10px;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    justify-content: center;
    padding: 0 100px;
    position: relative;
    overflow: hidden;
    
    .login-video {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
      z-index: 0;
    }
  }

  .login-form {
    flex: 2;
    display: flex;
    flex-direction: column;
    justify-content: center;
    
    .title {
      font-size: 24px;
      margin: 0px auto 30px auto;
      text-align: center;
      color: #707070;
    }
    
    .el-card {
      border: none;
      padding: 0;
      display: flex;
    }
    
    .el-input {
      width: 350px;
      height: 44px;
      
      .el-input__inner {
        background: #f4f5fb;
      }
    }
    
    .input-icon {
      height: 39px;
      width: 14px;
      margin-left: 0px;
    }
    
    .login-code {
      width: 12%;
      height: 38px;
      display: inline-block;
      margin-left: 13%;
      
      img {
        cursor: pointer;
        vertical-align: middle;
      }
      
      .login-code-img {
        height: 38px;
      }
    }
    
    .login-options-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 25px;
      
      .link-type {
        color: #409EFF;
        text-decoration: none;
        font-size: 14px;
        
        &:hover {
          color: #66b1ff;
        }
      }
    }
    
    .login-switch-row {
      text-align: left;
      margin-top: 15px;
      cursor: pointer;
      
      .link-type {
        color: #409EFF;
        text-decoration: none;
        font-size: 14px;
        
        &:hover {
          color: #66b1ff;
        }
      }
    }
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
</style>
