<template>
  <div class="register-container">
    <!-- 注册页面 -->
    <div class="register" />
    <!-- 注册表单 -->
    <div class="register-form">
      <el-card shadow="never" class="register-card" style="margin-left: 130px;">
        <el-form ref="registerRef" :model="registerForm" :rules="registerRules">
          <h3 class="title">Fashion Clothing Exchange & Second-hand Trading Admin System</h3>
          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username" 
              type="text" 
              auto-complete="off" 
              placeholder="Account"
            >
              <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
            </el-input>
          </el-form-item>
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
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="registerForm.code"
              auto-complete="off"
              placeholder="Verification Code"
              style="width: 58%"
              @keyup.enter="handleRegister"
            >
              <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
            </el-input>
            <div class="register-code">
              <img :src="codeUrl" @click="getCode" class="register-code-img"/>
            </div>
          </el-form-item>
          <el-form-item style="width:100%;">
            <el-button
              :loading="loading"
              size="medium" 
              type="primary"
              style="width:350px"
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
      </el-card>
    </div>
    <!--  底部  -->
    <div class="el-register-footer">
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from "element-plus";
import { getCodeImg, register } from "@/api/login";

const router = useRouter();
const { proxy } = getCurrentInstance();

const registerForm = ref({
  username: "",
  password: "",
  confirmPassword: "",
  code: "",
  uuid: ""
});

const equalToPassword = (rule, value, callback) => {
  if (registerForm.value.password !== value) {
    callback(new Error("The two passwords do not match"));
  } else {
    callback();
  }
};

const registerRules = {
  username: [
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
  code: [{ required: true, trigger: "change", message: "Please enter verification code" }]
};

const codeUrl = ref("");
const loading = ref(false);
const captchaEnabled = ref(true);

function handleRegister() {
  proxy.$refs.registerRef.validate(valid => {
    if (valid) {
      loading.value = true;
      register(registerForm.value).then(res => {
        const username = registerForm.value.username;
        ElMessageBox.alert("<font color='red'>Congratulations! Your account " + username + " has been successfully registered!</font>", "System Notification", {
          dangerouslyUseHTMLString: true,
          type: "success",
        }).then(() => {
          router.push("/login");
        }).catch(() => {});
      }).catch(() => {
        loading.value = false;
        if (captchaEnabled) {
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
      registerForm.value.uuid = res.uuid;
    }
  });
}

getCode();
</script>

<style lang='scss' scoped>
.register-container {
  display: flex;
  align-items: stretch;
  height: 100vh;

  .register {
    flex: 3;
    background: rgba(38, 72, 176) url("../assets/images/login-background.jpg") no-repeat center / cover;
    border-top-right-radius: 10px;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    justify-content: center;
    padding: 0 100px;
  }

  .register-form {
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
    
    .register-code {
      width: 12%;
      height: 38px;
      display: inline-block;
      margin-left: 13%;
      
      img {
        cursor: pointer;
        vertical-align: middle;
      }
      
      .register-code-img {
        height: 38px;
      }
    }
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
</style>
