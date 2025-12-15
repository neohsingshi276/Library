<template>
  <div class="email-bind">
    <el-form
      ref="emailFormRef"
      :model="emailForm"
      :rules="emailRules"
      label-width="150px"
    >
      <!-- 未绑定邮箱：只显示邮箱 + 验证码 -->
      <template v-if="!isBound">
        <el-form-item label="Email" prop="email">
          <el-input v-model="emailForm.email" placeholder="Please enter email" />
        </el-form-item>
      </template>
      <!-- 已绑定邮箱：显示原邮箱（禁用）+ 新邮箱 -->
      <template v-else>
        <el-form-item label="Original Email">
          <el-input v-model="originalEmail" disabled />
        </el-form-item>
        <el-form-item label="New Email" prop="newEmail">
          <el-input v-model="emailForm.newEmail" placeholder="Please enter new email" />
        </el-form-item>
      </template>

      <el-form-item label="Verification Code" prop="code">
        <el-input
          v-model="emailForm.code"
          placeholder="Please enter verification code"
          style="width: 220px; margin-right: 8px;"
        />
        <el-button
          :disabled="emailCountdown > 0 || emailSending"
          @click="handleSendCode"
        >
          {{ emailCountdown > 0 ? emailCountdown + 's Retry' : 'Get Verification Code' }}
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleBindEmail">Save</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup name="EmailBind">
import { getUserProfile, updateEmail } from "@/api/system/user";
import { sendEmailCode } from "@/api/login";

const { proxy } = getCurrentInstance();
const emit = defineEmits(['email-updated']);

const emailFormRef = ref(null);
const emailForm = reactive({
  email: "",     // 未绑定时使用
  newEmail: "",  // 已绑定时使用
  code: ""
});

const originalEmail = ref("");
const isBound = ref(false);

const emailRules = computed(() => {
  return {
    email: !isBound.value
      ? [
          { required: true, message: "Email cannot be empty", trigger: "blur" },
          { type: "email", message: "Please enter a valid email address", trigger: ["blur", "change"] }
        ]
      : [],
    newEmail: isBound.value
      ? [
          { required: true, message: "New email cannot be empty", trigger: "blur" },
          { type: "email", message: "Please enter a valid new email address", trigger: ["blur", "change"] }
        ]
      : [],
    code: [{ required: true, message: "Verification code cannot be empty", trigger: "blur" }]
  };
});

const emailSending = ref(false);
const emailCountdown = ref(0);

function initUserEmail() {
  getUserProfile().then(res => {
    const user = res.data || {};
    originalEmail.value = user.email || "";
    isBound.value = !!originalEmail.value;
    // 如果未绑定，默认写到 email；如果已绑定，则留空 newEmail 让用户填写
    if (!isBound.value) {
      emailForm.email = "";
    } else {
      emailForm.email = "";
      emailForm.newEmail = "";
    }
  });
}

initUserEmail();

function handleSendCode() {
  const targetEmail = isBound.value ? emailForm.newEmail : emailForm.email;
  if (!targetEmail) {
    proxy.$modal.msgError(isBound.value ? "Please enter new email first" : "Please enter email first");
    return;
  }
  emailSending.value = true;
  sendEmailCode(targetEmail)
    .then(() => {
      proxy.$modal.msgSuccess("Verification code sent, please check your email");
      emailCountdown.value = 120;
      const timer = setInterval(() => {
        emailCountdown.value--;
        if (emailCountdown.value <= 0) {
          clearInterval(timer);
        }
      }, 1000);
    })
    .catch((error) => {
      // 即使前端提示超时，后端邮件可能已经发送成功
      console.error('sendEmailCode error:', error);
      proxy.$modal.msgWarning("Request may have timed out, please check if you have received the verification code in your email. If not, please try again later");
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

function handleBindEmail() {
  emailFormRef.value.validate(valid => {
    if (!valid) return;
    const targetEmail = isBound.value ? emailForm.newEmail : emailForm.email;
    updateEmail(targetEmail, emailForm.code).then(() => {
      proxy.$modal.msgSuccess("Email updated successfully");
      // 刷新当前页面绑定状态，停留在“邮箱绑定”页
      initUserEmail();
      emailForm.code = "";
      emailCountdown.value = 0;
      // 通知父组件刷新卡片上的邮箱显示
      emit('email-updated', targetEmail);
    });
  });
}

</script>

<style scoped>
.email-bind {
  padding-top: 10px;
}
</style>


