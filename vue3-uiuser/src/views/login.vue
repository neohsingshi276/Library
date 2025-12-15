<template>
  <div class="login">
    <video v-if="loginResourceType === 'video' && loginBackground" :src="loginBackground" autoplay loop muted class="login-video"></video>
    <div v-else class="login-image" :style="loginResourceType === 'image' && loginBackground ? { backgroundImage: `url('${loginBackground}')` } : {}"></div>
    <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">Fashion Clothing Exchange & Second-hand Trading Platform</h3>
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
          style="width: 63%"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
        </div>
      </el-form-item>
      <div class="login-options-row">
        <el-checkbox v-model="loginForm.rememberMe">Remember Password</el-checkbox>
        <div v-if="register" class="register-links">
          <router-link class="link-type" :to="'/register'">Register Now</router-link>
          <span class="link-separator">|</span>
          <router-link to="/emailLogin" class="link-type">
            Email Login
          </router-link>
        </div>
      </div>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.prevent="handleLogin"
        >
          <span v-if="!loading">Login</span>
          <span v-else>Logging in...</span>
        </el-button>
      </el-form-item>
    </el-form>
    <!-- Footer -->
    <div class="el-login-footer">
      <span></span>
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
  code: [{ required: true, trigger: "change", message: "Please enter the verification code" }]
};

const codeUrl = ref("");
const loading = ref(false);
// Captcha enabled flag
const captchaEnabled = ref(true);
// Registration enabled flag
const register = ref(true);
const redirect = ref(undefined);
// Login background resource
const loginBackground = ref("");
const loginResourceType = ref("video"); // image or video

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect;
}, { immediate: true });

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true;
      // If remember password is checked, set username and password in cookie
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 });
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 });
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 });
      } else {
        // Otherwise remove
        Cookies.remove("username");
        Cookies.remove("password");
        Cookies.remove("rememberMe");
      }
      // Call login method from action
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
        // Re-fetch verification code
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

// Process resource URL
function processResourceUrl(url) {
  if (!url) return '';
  
  url = String(url).trim();
  
  // If it's a complete URL (http/https), use it directly (Aliyun OSS)
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  
  // If it's a relative path (starts with /), concatenate base API path (local storage)
  if (url.startsWith('/')) {
    const baseUrl = import.meta.env.VITE_APP_BASE_API || '';
    return baseUrl + url;
  }
  
  // Other cases, concatenate base API path
  const baseUrl = import.meta.env.VITE_APP_BASE_API || '';
  return baseUrl + '/' + url;
}

// Get login background resource
async function getLoginResource() {
  try {
    const res = await getResourceBySceneAndType('login_user', null);
    console.log('Get resource full response:', JSON.stringify(res, null, 2));
    
    if (res && res.code === 200) {
      let resource = null;
      
      // Process returned data structure
      if (Array.isArray(res.data)) {
        // If it's an array, take the first one
        if (res.data.length > 0) {
          resource = res.data[0];
        }
      } else if (res.data && typeof res.data === 'object') {
        // If it's an object, check if it has resourceType property
        if (res.data.resourceType) {
          resource = res.data;
        } else if (Array.isArray(res.data.rows)) {
          // Might be paginated data
          resource = res.data.rows.length > 0 ? res.data.rows[0] : null;
        }
      }
      
      console.log('Parsed resource data:', resource);
      
      if (resource && resource.resourceUrl) {
        loginResourceType.value = resource.resourceType || 'video';
        loginBackground.value = processResourceUrl(resource.resourceUrl);
        
        console.log('✅ Login resource loaded successfully:', { 
          type: loginResourceType.value, 
          originalUrl: resource.resourceUrl,
          finalUrl: loginBackground.value,
          baseUrl: import.meta.env.VITE_APP_BASE_API
        });
      } else {
        console.warn('⚠️ Resource data is empty or missing resourceUrl, resource:', resource);
        // If not configured, use default video
        loginResourceType.value = 'video';
        loginBackground.value = require("../assets/login.mp4");
      }
    } else {
      console.warn('⚠️ Response code is not 200 or data is empty, res:', res);
      // If not configured, use default video
      loginResourceType.value = 'video';
      loginBackground.value = require("../assets/login.mp4");
    }
  } catch (error) {
    console.error('❌ Failed to get login resource:', error);
    console.error('Error details:', error.response || error.message);
    // If fetch fails, use default video
    loginResourceType.value = 'video';
    loginBackground.value = require("../assets/login.mp4");
  }
}

getCode();
getCookie();
getLoginResource();
</script>

<style lang='scss' scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-size: cover;

  .login-video {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    z-index: -1;
  }
  
  .login-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    z-index: -1;
  }
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  
  .el-input {
    height: 38px;
    
    input {
      height: 38px;
    }
  }
  
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
  
  .login-code {
    width: 33%;
    height: 38px;
    float: right;
    margin-left: 12px;
    
    img {
      cursor: pointer;
      vertical-align: middle;
    }
    
    .login-code-img {
      height: 38px;
      width: 100%;
    }
  }
}

.register-links {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  
  .link-type {
    color: #409EFF;
    text-decoration: none;
    font-size: 14px;
    
    &:hover {
      color: #66b1ff;
    }
  }
  
  .link-separator {
    color: #dcdfe6;
    font-size: 14px;
  }
}

.login-options-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 25px;
  
  .register-links {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    
    .link-type {
      color: #409EFF;
      text-decoration: none;
      font-size: 14px;
      
      &:hover {
        color: #66b1ff;
      }
    }
    
    .link-separator {
      color: #dcdfe6;
      font-size: 14px;
    }
  }
}

.login-switch-row {
  text-align: right;
  margin-bottom: 10px;
  
  .link-type {
    color: #409EFF;
    text-decoration: none;
    font-size: 14px;
    
    &:hover {
      color: #66b1ff;
    }
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
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
