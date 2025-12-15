<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="150px">
      <el-form-item label="Nickname" prop="nickName">
        <el-input
          v-model="queryParams.nickName"
          placeholder="Please enter nickname"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="Gender" prop="sex">
        <el-select v-model="queryParams.sex" placeholder="Please select gender" clearable>
          <el-option
            v-for="dict in sys_user_sex"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="Account Status" prop="status">
        <el-select v-model="queryParams.status" placeholder="Please select account status" clearable>
          <el-option
            v-for="dict in sys_account_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">Search</el-button>
        <el-button icon="Refresh" @click="resetQuery">Reset</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:client:edit']"
        >Edit</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:client:remove']"
        >Delete</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:client:export']"
        >Export</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="clientList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="Serial No." width="70" align="center">
        <template #default="scope">
          {{ (scope.$index+1)+(queryParams.pageNum-1)*queryParams.pageSize }}
        </template>
      </el-table-column>
      <el-table-column label="User Account" align="center" prop="userName" />
      <el-table-column label="User Nickname" align="center" prop="nickName" />
      <el-table-column label="User Gender" align="center" prop="sex">
        <template #default="scope">
          <dict-tag :options="sys_user_sex" :value="scope.row.sex"/>
        </template>
      </el-table-column>
      <el-table-column label="Avatar" align="center" prop="avatar" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.avatar" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="Account Status" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_account_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="Credit Score" align="center" prop="creditScore" width="100">
        <template #default="scope">
          <span>{{ scope.row.creditScore || 100 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Total Donation" align="center" prop="totalDonation" width="120">
        <template #default="scope">
          <span>¥{{ (scope.row.totalDonation || 0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Create Time" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Actions" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-tooltip content="Edit" placement="top" v-if="scope.row.userId !== 1">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:client:edit']"></el-button>
          </el-tooltip>
          <el-tooltip content="Delete" placement="top" v-if="scope.row.userId !== 1">
            <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:client:remove']"></el-button>
          </el-tooltip>
          <el-tooltip content="Reset Password" placement="top" v-if="scope.row.userId !== 1">
            <el-button link type="primary" icon="Key" @click="handleResetPwd(scope.row)" v-hasPermi="['system:client:edit']"></el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- Add or Edit User Information Dialog -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form ref="clientRef" :model="form" :rules="rules" label-width="180px">
        <el-form-item label="User Account" prop="userName">
          <el-input v-model="form.userName" disabled placeholder="Please enter user account" />
        </el-form-item>
        <el-form-item label="User Nickname" prop="nickName">
          <el-input v-model="form.nickName" placeholder="Please enter user nickname" />
        </el-form-item>
        <el-form-item label="User Email" prop="email">
          <el-input v-model="form.email" disabled placeholder="Please enter user email" />
        </el-form-item>
        <el-form-item label="Phone Number" prop="phonenumber">
          <el-input v-model="form.phonenumber" disabled placeholder="Please enter phone number" />
        </el-form-item>
        <el-form-item label="User Gender" prop="sex">
          <el-select v-model="form.sex" disabled placeholder="Please select user gender">
            <el-option
              v-for="dict in sys_user_sex"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Avatar" prop="avatar">
          <image-upload v-model="form.avatar"/>
        </el-form-item>
        <el-form-item label="Account Status" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_account_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Credit Score" prop="creditScore">
          <el-input v-model="form.creditScore" disabled placeholder="Credit Score" />
        </el-form-item>
        <el-form-item label="Total Donation" prop="totalDonation">
          <el-input v-model="form.totalDonation" disabled placeholder="Total Donation Amount" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">Confirm</el-button>
          <el-button @click="cancel">Cancel</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Client">
import { listClient, getClient, delClient, addClient, updateClient,resetPwd } from "@/api/system/client";

const { proxy } = getCurrentInstance();
const { sys_account_status, sys_user_sex } = proxy.useDict('sys_account_status', 'sys_user_sex');

const clientList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const names = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    nickName: null,
    email: null,
    phonenumber: null,
    sex: null,
    status: null,
  },
  rules: {
    userName: [
      { required: true, message: "User account cannot be empty", trigger: "blur" }
    ],
    nickName: [
      { required: true, message: "User nickname cannot be empty", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** Query user information list */
function getList() {
  loading.value = true;
  listClient(queryParams.value).then(response => {
    clientList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// Cancel button
function cancel() {
  open.value = false;
  reset();
}

// Form reset
function reset() {
  form.value = {
    userId: null,
    userName: null,
    nickName: null,
    email: null,
    phonenumber: null,
    sex: null,
    avatar: null,
    password: null,
    status: null,
    creditScore: null,
    totalDonation: null,
    createTime: null,
    updateTime: null,
    remark: null
  };
  proxy.resetForm("clientRef");
}

/** Search button operation */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** Reset button operation */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// Multi-select selection change
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.userId);
  names.value = selection.map(item => item.nickName);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** Add button operation */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "Add User Information";
}

/** Edit button operation */
function handleUpdate(row) {
  reset();
  const _userId = row.userId || ids.value
  getClient(_userId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "Edit User Information";
  });
}

function handleResetPwd(row){
  resetPwd(row).then(response => {
    proxy.$modal.msgSuccess("Password reset successful, current user password is '123456'");
  });
}

/** Submit button */
function submitForm() {
  proxy.$refs["clientRef"].validate(valid => {
    if (valid) {
      if (form.value.userId != null) {
        updateClient(form.value).then(response => {
          proxy.$modal.msgSuccess("Modified successfully");
          open.value = false;
          getList();
        });
      } else {
        addClient(form.value).then(response => {
          proxy.$modal.msgSuccess("Added successfully");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** Delete button operation */
function handleDelete(row) {
  const _userIds = row.userId || ids.value;
  const _names = row.nickName || names.value
  proxy.$modal.confirm('Are you sure you want to delete user "' + _names + '" data item?').then(function() {
    return delClient(_userIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("Deleted successfully");
  }).catch(() => {});
}

/** Export button operation */
function handleExport() {
  proxy.download('system/client/export', {
    ...queryParams.value
  }, `client_${new Date().getTime()}.xlsx`)
}

getList();
</script>
