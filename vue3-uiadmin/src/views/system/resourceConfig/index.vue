<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="150px">
         <el-form-item label="Resource Name" prop="resourceName">
            <el-input
               v-model="queryParams.resourceName"
               placeholder="Please enter resource name"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <el-form-item label="Resource Type" prop="resourceType">
            <el-select v-model="queryParams.resourceType" placeholder="Resource type" clearable style="width: 240px">
               <el-option label="Image" value="image" />
               <el-option label="Video" value="video" />
            </el-select>
         </el-form-item>
         <el-form-item label="Resource Scene" prop="resourceScene">
            <el-input
               v-model="queryParams.resourceScene"
               placeholder="Please enter resource scene"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <el-form-item label="Status" prop="status">
            <el-select v-model="queryParams.status" placeholder="Status" clearable style="width: 240px">
               <el-option label="Enable" value="0" />
               <el-option label="Disable" value="1" />
            </el-select>
         </el-form-item>
         <el-form-item label="Create Time" style="width: 308px;">
            <el-date-picker
               v-model="dateRange"
               value-format="YYYY-MM-DD"
               type="daterange"
               range-separator="-"
               start-placeholder="Start date"
               end-placeholder="End date"
            ></el-date-picker>
         </el-form-item>
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">Search</el-button>
            <el-button icon="Refresh" @click="resetQuery">Reset</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
         <el-col :span="1.5">
            <el-button
               type="primary"
               plain
               icon="Plus"
               @click="handleAdd"
               v-hasPermi="['system:resourceConfig:add']"
            >Add</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="success"
               plain
               icon="Edit"
               :disabled="single"
               @click="handleUpdate"
               v-hasPermi="['system:resourceConfig:edit']"
            >Edit</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="danger"
               plain
               icon="Delete"
               :disabled="multiple"
               @click="handleDelete"
               v-hasPermi="['system:resourceConfig:remove']"
            >Delete</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="warning"
               plain
               icon="Download"
               @click="handleExport"
               v-hasPermi="['system:resourceConfig:export']"
            >Export</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="resourceConfigList" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="55" align="center" />
         <el-table-column type="index" label="No." align="center" width="80">
            <template #default="scope">
               <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
            </template>
         </el-table-column>
         <el-table-column label="Resource Name" align="center" prop="resourceName" :show-overflow-tooltip="true" />
         <el-table-column label="Resource Type" align="center" prop="resourceType" width="100">
            <template #default="scope">
               <el-tag v-if="scope.row.resourceType === 'image'" type="success">Image</el-tag>
               <el-tag v-else-if="scope.row.resourceType === 'video'" type="warning">Video</el-tag>
            </template>
         </el-table-column>
         <el-table-column label="Resource Scene" align="center" prop="resourceScene" width="140">
         </el-table-column>
         <el-table-column label="Resource Info" align="center" prop="resourceUrl" min-width="200">
            <template #default="scope">
               <div class="resource-preview">
                  <!-- Image type shows image preview -->
                  <div v-if="scope.row.resourceType === 'image'" class="image-preview">
                     <el-image
                        :src="getResourceUrl(scope.row.resourceUrl)"
                        :preview-src-list="[getResourceUrl(scope.row.resourceUrl)]"
                        :z-index="3000"
                        fit="cover"
                        style="width: 80px; height: 80px; border-radius: 4px; cursor: pointer;"
                        :lazy="true"
                        preview-teleported
                     />
                  </div>
                  <!-- Video type shows video preview -->
                  <div v-else-if="scope.row.resourceType === 'video'" class="video-preview">
                     <video
                        :src="getResourceUrl(scope.row.resourceUrl)"
                        controls
                        style="width: 120px; height: 80px; border-radius: 4px; object-fit: cover; cursor: pointer;"
                        @click.stop
                     />
                  </div>
                  <!-- No resource shows hint -->
                  <span v-else style="color: #909399; font-size: 12px;">No Resource</span>
               </div>
            </template>
         </el-table-column>
         <el-table-column label="Display Order" align="center" prop="sortOrder" width="100" />
         <el-table-column label="Status" align="center" prop="status" width="80">
            <template #default="scope">
               <el-tag v-if="scope.row.status === '0'" type="success">Enable</el-tag>
               <el-tag v-else type="danger">Disable</el-tag>
            </template>
         </el-table-column>
         <el-table-column label="Create Time" align="center" prop="createTime" width="180">
            <template #default="scope">
               <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
         </el-table-column>
         <el-table-column label="Actions" align="center" width="150" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:resourceConfig:edit']" >Edit</el-button>
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:resourceConfig:remove']">Delete</el-button>
            </template>
         </el-table-column>
      </el-table>

      <pagination
         v-show="total > 0"
         :total="total"
         v-model:page="queryParams.pageNum"
         v-model:limit="queryParams.pageSize"
         @pagination="getList"
      />

      <!-- Add or Edit Resource Config Dialog -->
      <el-dialog :title="title" v-model="open" width="900px" append-to-body class="resource-config-dialog">
         <el-form ref="resourceConfigRef" :model="form" :rules="rules" label-width="150px">
            <el-row :gutter="20">
               <el-col :span="12">
                  <el-form-item label="Resource Type" prop="resourceType">
                     <el-select v-model="form.resourceType" placeholder="Please select resource type" style="width: 100%" @change="handleResourceTypeChange">
                        <el-option label="Image" value="image" />
                        <el-option label="Video" value="video" />
                     </el-select>
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="Resource Scene" prop="resourceScene">
                     <el-input v-model="form.resourceScene" placeholder="Please enter resource scene" />
                  </el-form-item>
               </el-col>
            </el-row>
            
            <el-form-item label="Resource Name" prop="resourceName">
               <el-input v-model="form.resourceName" placeholder="Please enter resource name" />
            </el-form-item>
            
            <el-form-item label="Resource Info" prop="resourceUrl">
               <!-- Image type shows image upload component -->
               <ImageUpload
                  v-if="form.resourceType === 'image'"
                  v-model="form.resourceUrl"
                  :limit="1"
                  :fileSize="10"
                  :fileType="['jpg', 'jpeg', 'png', 'gif', 'webp']"
               />
               <!-- Video type shows video upload component -->
               <VideoUpload
                  v-else-if="form.resourceType === 'video'"
                  v-model="form.resourceUrl"
                  :fileSize="300"
                  :fileType="['mp4', 'avi', 'mov', 'wmv', 'flv', 'webm']"
                  :isShowTip="true"
               />
               <!-- Not selected type shows hint -->
               <el-alert
                  v-else
                  title="Please select resource type first"
                  type="info"
                  :closable="false"
                  show-icon
               />
            </el-form-item>
            
            <el-row :gutter="20">
               <el-col :span="12">
                  <el-form-item label="Display Order" prop="sortOrder">
                     <el-input-number v-model="form.sortOrder" :min="0" :max="999" controls-position="right" style="width: 100%" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="Status" prop="status">
                     <el-radio-group v-model="form.status">
                        <el-radio label="0">Enable</el-radio>
                        <el-radio label="1">Disable</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
            </el-row>
            
            <el-form-item label="Remark" prop="remark">
               <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="Please enter remark" />
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

<script setup name="ResourceConfig">
import { listResourceConfig, getResourceConfig, delResourceConfig, addResourceConfig, updateResourceConfig } from "@/api/system/resourceConfig";
import ImageUpload from "@/components/ImageUpload";
import VideoUpload from "@/components/VideoUpload";

const { proxy } = getCurrentInstance();

const resourceConfigList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    resourceName: undefined,
    resourceType: undefined,
    resourceScene: undefined,
    status: undefined
  },
  rules: {
    resourceType: [{ required: true, message: "Resource type cannot be empty", trigger: "change" }],
    resourceScene: [{ required: true, message: "Resource scene cannot be empty", trigger: "change" }],
    resourceName: [{ required: false, message: "Resource name cannot be empty", trigger: "blur" }],
    resourceUrl: [{ required: true, message: "Resource URL cannot be empty", trigger: "blur" }],
    sortOrder: [{ required: false, message: "Display order cannot be empty", trigger: "blur" }],
    status: [{ required: true, message: "Status cannot be empty", trigger: "change" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** Query resource config list */
function getList() {
  loading.value = true;
  listResourceConfig(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    resourceConfigList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}
/** Cancel button */
function cancel() {
  open.value = false;
  reset();
}
/** Form reset */
function reset() {
  form.value = {
    resourceId: undefined,
    resourceType: "image",
    resourceScene: undefined,
    resourceName: undefined,
    resourceUrl: undefined,
    sortOrder: 0,
    status: "0",
    remark: undefined
  };
  proxy.resetForm("resourceConfigRef");
}
/** Search button action */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** Reset button action */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}
/** Multi-select checkbox data */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.resourceId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** Add button action */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "Add Resource Config";
}
/** Edit button action */
function handleUpdate(row) {
  reset();
  const resourceId = row.resourceId || ids.value[0];
  getResourceConfig(resourceId).then(response => {
    form.value = response.data;
    // Ensure resourceUrl is string format (upload component needs)
    if (form.value.resourceUrl && typeof form.value.resourceUrl !== 'string') {
      form.value.resourceUrl = String(form.value.resourceUrl);
    }
    open.value = true;
    title.value = "Edit Resource Config";
  });
}
/** Submit button */
function submitForm() {
  proxy.$refs["resourceConfigRef"].validate(valid => {
    if (valid) {
      // Process upload component return value (may be comma separated string, take first only)
      if (form.value.resourceUrl && form.value.resourceUrl.includes(',')) {
        form.value.resourceUrl = form.value.resourceUrl.split(',')[0];
      }
      if (form.value.resourceId != undefined) {
        updateResourceConfig(form.value).then(response => {
          proxy.$modal.msgSuccess("Edit successful");
          open.value = false;
          getList();
        });
      } else {
        addResourceConfig(form.value).then(response => {
          proxy.$modal.msgSuccess("Add successful");
          open.value = false;
          getList();
        });
      }
    }
  });
}
/** Delete button action */
function handleDelete(row) {
  const resourceIds = row.resourceId || ids.value;
  proxy.$modal.confirm('Are you sure to delete resource ID "' + resourceIds + '"?').then(function () {
    return delResourceConfig(resourceIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("Delete successful");
  }).catch(() => {});
}
/** Export button action */
function handleExport() {
  proxy.download("system/resourceConfig/export", {
    ...queryParams.value
  }, `resourceConfig_${new Date().getTime()}.xlsx`);
}

/** Resource type change clear URL */
function handleResourceTypeChange() {
  form.value.resourceUrl = undefined;
}

/** Get complete resource URL */
function getResourceUrl(url) {
  if (!url) return '';
  const baseUrl = import.meta.env.VITE_APP_BASE_API;
  const urlStr = String(url).trim();
  
  // If full URL (http/https), use directly (Aliyun OSS)
  if (urlStr.startsWith('http://') || urlStr.startsWith('https://')) {
    return urlStr;
  }
  
  // If relative path (starts with /), join base API path (local storage)
  if (urlStr.startsWith('/')) {
    return baseUrl + urlStr;
  }
  
  // Other cases, join base API path
  return baseUrl + '/' + urlStr;
}

getList();
</script>

<style scoped lang="scss">
.resource-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 0;
  
  .image-preview {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .video-preview {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

:deep(.resource-config-dialog) {
  .el-dialog__body {
    padding: 20px 25px;
  }
  
  .el-form-item {
    margin-bottom: 20px;
  }
  
  .el-form-item__label {
    font-weight: 500;
    color: #606266;
  }
}

// Fix image preview overlay issue
:deep(.el-image-viewer__wrapper) {
  z-index: 3000 !important;
}

:deep(.el-image-viewer__mask) {
  z-index: 3000 !important;
}
</style>