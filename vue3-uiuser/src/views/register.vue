<template>
  <div class="register">
    <video v-if="registerResourceType === 'video' && registerBackground" :src="registerBackground" autoplay loop muted class="login-video"></video>
    <div v-else class="register-image" :style="registerResourceType === 'image' && registerBackground ? { backgroundImage: `url('${registerBackground}')` } : {}"></div>
    <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
      <h3 class="title">Fashion Clothing Exchange & Second-hand Trading Platform</h3>
      <!-- Account -->
      <el-form-item prop="userName">
        <el-input 
          v-model="registerForm.userName" 
          type="text" 
          auto-complete="off" 
          placeholder="Account"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <!-- Nickname -->
      <el-form-item prop="nickName">
        <el-input 
          v-model="registerForm.nickName" 
          type="text" 
          auto-complete="off" 
          placeholder="Nickname"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <!-- Phone Number -->
      <el-form-item prop="phonenumber">
        <el-input 
          v-model="registerForm.phonenumber" 
          type="text" 
          auto-complete="off" 
          placeholder="Phone Number"
        >
          <template #prefix><svg-icon icon-class="phone" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <!-- Password -->
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          auto-complete="off"
          placeholder="Password"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <!-- Confirm Password -->
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          auto-complete="off"
          placeholder="Confirm Password"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <!-- Email -->
      <el-form-item prop="email">
        <el-input 
          v-model="registerForm.email" 
          type="text" 
          auto-complete="off" 
          placeholder="Email"
          @input="validateEmail"
        >
          <template #prefix><svg-icon icon-class="email" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <!-- Verification Code -->
      <el-form-item prop="code" v-if="captchaEnabled">
        <div class="captcha-container">
          <el-input
            v-model="registerForm.code"
            auto-complete="off"
            placeholder="Verification Code"
            @keyup.enter="handleRegister"
          >
            <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
          </el-input>
          <el-button 
            class="captcha-button" 
            :disabled="!isEmailValid || isCountingDown" 
            @click="getCode"
          >
            {{ getCodeButtonText }}
          </el-button>
        </div>
      </el-form-item>
      <!-- Register Button -->
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium" 
          type="primary"
          style="width:100%;"
          @click.prevent="handleRegister"
        >
          <span v-if="!loading">Register</span>
          <span v-else>Registering...</span>
        </el-button>
        <div style="float: right;">
          <router-link class="link-type" :to="'/login'">Login with Existing Account</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!-- Footer -->
    <div class="el-register-footer">
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { useRouter } from 'vue-router';
import { sendEmailCode, register } from "@/api/login";
import { getResourceBySceneAndType } from "@/api/system/resourceConfig";

const router = useRouter();

const registerForm = ref({
  userName: "",
  password: "",
  confirmPassword: "",
  nickName: "",
  email: "",
  phonenumber: "",
  code: "",
});

const equalToPassword = (rule, value, callback) => {
  if (registerForm.value.password !== value) {
    callback(new Error("The two passwords do not match"));
  } else {
    callback();
  }
};

// Add computed property to check if email is valid
const isValidEmail = computed(() => {
  const emailRegex = /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/;
  return emailRegex.test(registerForm.value.email);
});

const registerRules = {
  userName: [
    { required: true, trigger: "blur", message: "Please enter your account" },
    { min: 2, max: 20, message: "Account length must be between 2 and 20 characters", trigger: "blur" }
  ],
  password: [
    { required: true, trigger: "blur", message: "Please enter your password" },
    { min: 5, max: 20, message: "Password length must be between 5 and 20 characters", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, trigger: "blur", message: "Please enter your password again" },
    { required: true, validator: equalToPassword, trigger: "blur" }
  ],
  nickName: [{ required: true, trigger: "blur", message: "Please enter your nickname" }],
  email: [{ required: true, type: 'email', message: 'Please enter a valid email address', trigger: ['blur', 'change'] }],
  phonenumber: [{ required: true, pattern: /^1[3456789]\d{9}$/, message: 'Please enter a valid phone number', trigger: 'blur' }],
  code: [{ required: true, trigger: "change", message: "Please enter the verification code" }]
};

const codeUrl = ref("");
const loading = ref(false);
const captchaEnabled = ref(true);
const countDown = ref(120);
const isCountingDown = ref(false);
// Registration background resource
const registerBackground = ref("");
const registerResourceType = ref("video"); // image or video

const getCodeButtonText = computed(() => isCountingDown.value ? `Resend in ${countDown.value}s` : 'Get Verification Code');

function validateEmail() {
  // Email validation logic is already handled in computed
}

function handleRegister() {
  loading.value = true;
  register(registerForm.value).then(res => {
    const userName = registerForm.value.userName;
    ElMessageBox.alert("<font color='red'>Congratulations! Your account " + userName + " has been successfully registered!</font>", "System Notification", {
      dangerouslyUseHTMLString: true,
      type: "success",
    }).then(() => {
      router.push("/login");
    }).catch(() => {});
  }).catch(() => {
    loading.value = false;
    if (captchaEnabled.value) {
      getCode();
    }
  });
}

function getCode() {
  if (!isCountingDown.value && isValidEmail.value) {
    sendEmailCode(registerForm.value.email).then(res => {
      // Send successful
    }).catch(() => {
      // Send failed, reset countdown
      resetCountdown();
    });
    startCountdown();
  }
}

function startCountdown() {
  isCountingDown.value = true;
  const interval = setInterval(() => {
    countDown.value--;
    if (countDown.value <= 0) {
      clearInterval(interval);
      isCountingDown.value = false;
      countDown.value = 120; // Reset counter
    }
  }, 1000);
}

function resetCountdown() {
  isCountingDown.value = false;
  countDown.value = 120;
}


function processResourceUrl(url) {
  if (!url) return '';
  
  url = String(url).trim();
  

  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  
  if (url.startsWith('/')) {
    const baseUrl = import.meta.env.VITE_APP_BASE_API || '';
    return baseUrl + url;
  }
  

  const baseUrl = import.meta.env.VITE_APP_BASE_API || '';
  return baseUrl + '/' + url;
}


async function getRegisterResource() {
  try {
    const res = await getResourceBySceneAndType('register_user', null);
   
    
    if (res && res.code === 200) {
      let resource = null;
      
  
      if (Array.isArray(res.data)) {
  
        if (res.data.length > 0) {
          resource = res.data[0];
        }
      } else if (res.data && typeof res.data === 'object') {
  
        if (res.data.resourceType) {
          resource = res.data;
        } else if (Array.isArray(res.data.rows)) {
        
          resource = res.data.rows.length > 0 ? res.data.rows[0] : null;
        }
      }

      if (resource && resource.resourceUrl) {
        registerResourceType.value = resource.resourceType || 'video';
        registerBackground.value = processResourceUrl(resource.resourceUrl);
        
      } else {
       

        registerResourceType.value = 'video';
        registerBackground.value = require("../assets/login.mp4");
      }
    } else {

 
      registerResourceType.value = 'video';
      registerBackground.value = require("../assets/login.mp4");
    }
  } catch (error) {
   
    registerResourceType.value = 'video';
    registerBackground.value = require("../assets/login.mp4");
  }
}

onMounted(() => {
  getRegisterResource();
});
</script>

<style lang='scss' scoped>
.register {
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
  
  .register-image {
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

.register-form {
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
  
  .captcha-container {
    display: flex;
    gap: 12px;
    align-items: center;
    
    .el-input {
      flex: 1;
    }
    
    .captcha-button {
      height: 38px;
      min-width: 120px;
      white-space: nowrap;
    }
  }
}

.register-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.register-code {
  width: 33%;
  height: 38px;
  float: right;
  
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-register-footer {
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

.register-code-img {
  height: 38px;
}
</style>