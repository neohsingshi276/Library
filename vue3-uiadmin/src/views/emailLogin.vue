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
          <el-form-item prop="email">
            <el-input
              v-model="loginForm.email"
              type="text"
              auto-complete="off"
              placeholder="Email Account"
            >
              <template #prefix><svg-icon icon-class="email" class="el-input__icon input-icon" /></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="Email Verification Code"
              style="width: 58%"
              @keyup.enter="handleLogin"
            >
              <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
            </el-input>
            <el-button
              :disabled="emailCountdown > 0 || emailSending"
              :loading="emailSending"
              @click="handleSendCode"
              class="code-btn"
            >
              {{ emailCountdown > 0 ? emailCountdown + 's Retry' : (emailSending ? 'Sending...' : 'Get Verification Code') }}
            </el-button>
          </el-form-item>
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
          <div class="login-switch-row">
            <el-link type="primary" @click="goToAccountLogin">
              Account Login
            </el-link>
          </div>
        </el-form>
      </el-card>
    </div>
    <!--  底部  -->
    <div class="el-login-footer">
    </div>
  </div>
</template>

<script setup>
import { sendEmailCode, emailLogin } from "@/api/login";
import { getResourceBySceneAndType } from "@/api/system/resourceConfig";
import useUserStore from '@/store/modules/user'

const userStore = useUserStore()
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const loginForm = ref({
  email: "",
  code: ""
});

const loginRules = {
  email: [
    { required: true, trigger: "blur", message: "Please enter email account" },
    { type: "email", trigger: "blur", message: "Please enter a valid email address" }
  ],
  code: [{ required: true, trigger: "blur", message: "Please enter email verification code" }]
};

const loading = ref(false);
const emailSending = ref(false);
const emailCountdown = ref(0);
const redirect = ref(undefined);
// 登录背景资源
const loginBackground = ref("");
const loginResourceType = ref("image"); // image 或 video

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect;
}, { immediate: true });

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true;
      userStore.emailLogin({ email: loginForm.value.email, code: loginForm.value.code }).then(() => {
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
      });
    }
  });
}

function handleSendCode() {
  if (!loginForm.value.email) {
    ElMessage.error("Please enter email account first");
    return;
  }
  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(loginForm.value.email)) {
    ElMessage.error("Please enter a valid email address");
    return;
  }
  emailSending.value = true;
  sendEmailCode(loginForm.value.email)
    .then(() => {
      ElMessage.success("Verification code sent, please check your email");
      emailCountdown.value = 120;
      const timer = setInterval(() => {
        emailCountdown.value--;
        if (emailCountdown.value <= 0) {
          clearInterval(timer);
        }
      }, 1000);
    })
    .catch((error) => {
      console.error('sendEmailCode error:', error);
      ElMessage.warning("Request may have timed out, please check if you have received the verification code in your email. If not, please try again later");
      // 也启动倒计时，避免频繁点击
      emailCountdown.value = 120;
      const timer = setInterval(() => {
        emailCountdown.value--;
        if (emailCountdown.value <= 0) {
          clearInterval(timer);
        }
      }, 1000);
    })
    .finally(() => {
      emailSending.value = false;
    });
}

function goToAccountLogin() {
  const query = { ...(route.query || {}) };
  router.push({ path: '/login', query });
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
    
    .code-btn {
      width: 40%;
      margin-left: 2%;
      height: 44px;
      line-height: 44px;
    }
    
    .login-switch-row {
      text-align: left;
      margin-top: 15px;
      
      .el-link {
        cursor: pointer;
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

