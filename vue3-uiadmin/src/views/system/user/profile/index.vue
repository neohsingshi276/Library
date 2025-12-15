<template>
   <div class="app-container">
      <el-row :gutter="20">
         <el-col :span="6" :xs="24">
            <el-card class="box-card">
               <template v-slot:header>
                 <div class="clearfix">
                   <span>Personal Information</span>
                 </div>
               </template>
              <div class="profile-card-body">
                 <div class="avatar-wrapper">
                    <userAvatar />
                 </div>
                 <ul class="list-group list-group-striped info-list">
                    <li class="list-group-item info-item">
                       <div class="item-left">
                         <svg-icon icon-class="user" class="item-icon" />
                         <span class="item-label">Username</span>
                       </div>
                       <div class="item-value">{{ state.user.userName }}</div>
                    </li>
                    <li class="list-group-item info-item">
                       <div class="item-left">
                         <svg-icon icon-class="phone" class="item-icon" />
                         <span class="item-label">Phone Number</span>
                       </div>
                       <div class="item-value">{{ state.user.phonenumber }}</div>
                    </li>
                    <li class="list-group-item info-item">
                       <div class="item-left">
                         <svg-icon icon-class="email" class="item-icon" />
                         <span class="item-label">Email</span>
                       </div>
                       <div class="item-value">{{ state.user.email }}</div>
                    </li>
                    <li class="list-group-item info-item">
                       <div class="item-left">
                         <svg-icon icon-class="tree" class="item-icon" />
                         <span class="item-label">Department</span>
                       </div>
                       <div class="item-value" v-if="state.user.dept">{{ state.user.dept.deptName }} / {{ state.postGroup }}</div>
                       <div class="item-value" v-else>Not Set</div>
                    </li>
                    <li class="list-group-item info-item">
                       <div class="item-left">
                         <svg-icon icon-class="peoples" class="item-icon" />
                         <span class="item-label">Role</span>
                       </div>
                       <div class="item-value">{{ state.roleGroup || 'Not Set' }}</div>
                    </li>
                    <li class="list-group-item info-item">
                       <div class="item-left">
                         <svg-icon icon-class="date" class="item-icon" />
                         <span class="item-label">Create Date</span>
                       </div>
                       <div class="item-value">{{ state.user.createTime }}</div>
                    </li>
                 </ul>
              </div>
            </el-card>
         </el-col>
         <el-col :span="18" :xs="24">
            <el-card>
               <template v-slot:header>
                 <div class="clearfix">
                   <span>Basic Information</span>
                 </div>
               </template>
               <el-tabs v-model="activeTab">
                  <el-tab-pane label="Basic Information" name="userinfo">
                     <userInfo :user="state.user" />
                  </el-tab-pane>
                  <el-tab-pane label="Change Password" name="resetPwd">
                     <resetPwd />
                  </el-tab-pane>
                  <el-tab-pane label="Email Binding" name="emailBind">
                     <emailBind @email-updated="state.user.email = $event" />
                  </el-tab-pane>
               </el-tabs>
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="Profile">
import userAvatar from "./userAvatar";
import userInfo from "./userInfo";
import resetPwd from "./resetPwd";
import emailBind from "./emailBind";
import { getUserProfile } from "@/api/system/user";

const activeTab = ref("userinfo");
const state = reactive({
  user: {},
  roleGroup: {},
  postGroup: {}
});

function getUser() {
  getUserProfile().then(response => {
    state.user = response.data;
    state.roleGroup = response.roleGroup;
    state.postGroup = response.postGroup;
  });
};

getUser();
</script>

<style scoped>
.profile-card-body {
  padding-bottom: 8px;
}

.avatar-wrapper {
  text-align: center;
  padding: 12px 0 16px;
}

.info-list {
  margin-top: 4px;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 10px;
  padding-bottom: 10px;
}

.item-left {
  display: flex;
  align-items: center;
  color: #606266;
  font-size: 13px;
}

.item-icon {
  margin-right: 6px;
  font-size: 14px;
}

.item-label {
  font-weight: 500;
}

.item-value {
  max-width: 55%;
  text-align: right;
  color: #303133;
  font-size: 13px;
  word-break: break-all;
}
</style>
