<template>
   <el-form ref="userRef" :model="form" :rules="rules" label-width="150px">
      <el-form-item label="Nickname" prop="nickName">
         <el-input v-model="form.nickName" maxlength="30" />
      </el-form-item>
      <el-form-item label="Phone Number" prop="phonenumber">
         <el-input v-model="form.phonenumber" maxlength="11" />
      </el-form-item>
      <el-form-item label="Gender">
         <el-radio-group v-model="form.sex">
            <el-radio label="0">Male</el-radio>
            <el-radio label="1">Female</el-radio>
         </el-radio-group>
      </el-form-item>
      <el-form-item>
      <el-button type="primary" @click="submit">Save</el-button>
      <el-button type="danger" @click="close">Close</el-button>
      </el-form-item>
   </el-form>
</template>

<script setup>
import { updateUserProfile } from "@/api/system/user";

const props = defineProps({
  user: {
    type: Object
  }
});

const { proxy } = getCurrentInstance();

const form = ref({});
const rules = ref({
  nickName: [{ required: true, message: "Nickname cannot be empty", trigger: "blur" }],
  phonenumber: [{ required: true, message: "Phone number cannot be empty", trigger: "blur" }, { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "Please enter a valid phone number", trigger: "blur" }],
});

/** 提交按钮 */
function submit() {
  proxy.$refs.userRef.validate(valid => {
    if (valid) {
      updateUserProfile(form.value).then(response => {
        proxy.$modal.msgSuccess("Updated successfully");
        props.user.phonenumber = form.value.phonenumber;
      });
    }
  });
};

/** 关闭按钮 */
function close() {
  proxy.$tab.closePage();
};

// 回显当前登录用户信息
watch(() => props.user, user => {
  if (user) {
    form.value = { nickName: user.nickName, phonenumber: user.phonenumber, sex: user.sex };
  }
},{ immediate: true });
</script>
