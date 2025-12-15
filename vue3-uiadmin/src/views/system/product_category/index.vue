<template>
  <div class="app-container page-container">
    <el-card shadow="never" class="search-card" v-show="showSearch">
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="150px">
        <el-form-item label="Category Name" prop="categoryName">
          <el-input
            v-model="queryParams.categoryName"
            placeholder="Please enter category name keyword"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="Parent Category" prop="parentId">
          <el-tree-select
            v-model="queryParams.parentId"
            :data="product_categoryOptions"
            :props="treeProps"
            check-strictly
            clearable
            placeholder="Select parent category to filter"
          />
        </el-form-item>
        <el-form-item label="Display Order" prop="orderNum">
          <el-input
            v-model.number="queryParams.orderNum"
            placeholder="= Order number"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">Search</el-button>
          <el-button icon="Refresh" @click="resetQuery">Reset</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="actions">
            <el-button
              type="primary"
              plain
              icon="Plus"
              @click="handleAdd"
              v-hasPermi="['system:product_category:add']"
            >Add</el-button>
            <el-button
              type="info"
              plain
              icon="Sort"
              @click="toggleExpandAll"
            >Expand/Collapse</el-button>
            <el-button
              type="default"
              plain
              icon="RefreshRight"
              @click="refreshAll"
            >Refresh</el-button>
          </div>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </div>
      </template>

      <el-table
        v-if="refreshTable"
        v-loading="loading"
        :data="product_categoryList"
        row-key="categoryId"
        :default-expand-all="isExpandAll"
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
        border
        stripe
      >
        <el-table-column type="index" width="60" label="#" />
        <el-table-column label="Category Name" min-width="180">
          <template #default="scope">
            <el-icon v-if="scope.row.parentId === 0"><HomeFilled /></el-icon>
            <span class="name-text">{{ scope.row.categoryName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Parent" width="120" align="center">
          <template #default="scope">
            <el-tag type="info" v-if="scope.row.parentId === 0">Root Node</el-tag>
            <span v-else>{{ scope.row.parentId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Ancestors" align="center" prop="ancestors" min-width="200" />
        <el-table-column label="Display Order" align="center" width="120">
          <template #default="scope">
            <el-tag type="success" effect="plain">{{ scope.row.orderNum ?? '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Create Time" align="center" prop="createTime" width="160" />
        <el-table-column label="Update Time" align="center" prop="updateTime" width="160" />
        <el-table-column label="Actions" align="center" class-name="small-padding fixed-width" width="280" fixed="right">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:product_category:edit']">Edit</el-button>
            <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)" v-hasPermi="['system:product_category:add']">Add Subcategory</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:product_category:remove']">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add or Edit Product Category Dialog -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body destroy-on-close>
      <el-form ref="product_categoryRef" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="Parent Category" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="product_categoryOptions"
            :props="treeProps"
            value-key="id"
            placeholder="Please select parent category"
            check-strictly
            filterable
          />
        </el-form-item>
        <el-form-item label="Category Name" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="Please enter category name" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="Display Order" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" :max="9999" controls-position="right" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">Cancel</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">Confirm</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Product_category">
import { HomeFilled } from "@element-plus/icons-vue";
import { listProduct_category, treeselectProduct_category, getProduct_category, delProduct_category, addProduct_category, updateProduct_category } from "@/api/system/product_category";

const { proxy } = getCurrentInstance();

const product_categoryList = ref([]);
const product_categoryOptions = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const title = ref("");
const isExpandAll = ref(true);
const refreshTable = ref(true);
const submitLoading = ref(false);
const treeProps = { value: 'id', label: 'label', children: 'children' };

const data = reactive({
  form: {},
  queryParams: {
    parentId: null,
    categoryName: null,
    orderNum: null,
  },
  rules: {
    categoryName: [
      { required: true, message: "Category name cannot be empty", trigger: "blur" }
    ],
    orderNum: [
      { required: true, message: "Display order cannot be empty", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** Query product category list */
function getList() {
  loading.value = true;
  listProduct_category(queryParams.value).then(response => {
    product_categoryList.value = proxy.handleTree(response.data, "categoryId", "parentId");
    loading.value = false;
  });
}

/** Query product category dropdown tree structure */
function getTreeselect() {
  treeselectProduct_category().then(response => {
    const treeData = response.data || [];
    product_categoryOptions.value = [{
      id: 0,
      label: 'Top Level Node',
      children: treeData
    }];
  });
}

function refreshAll() {
  getTreeselect();
  getList();
}

// Cancel button
function cancel() {
  open.value = false;
  reset();
}

// Form reset
function reset() {
  form.value = {
    categoryId: null,
    parentId: 0,
    categoryName: null,
    orderNum: 0,
  };
  proxy.resetForm("product_categoryRef");
}

/** Search button operation */
function handleQuery() {
  getList();
}

/** Reset button operation */
function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value.parentId = null;
  handleQuery();
}

/** Add button operation */
function handleAdd(row) {
  reset();
  getTreeselect();
  if (row != null && row.categoryId) {
    form.value.parentId = row.categoryId;
  } else {
    form.value.parentId = 0;
  }
  open.value = true;
  title.value = "Add Product Category";
}

/** Expand/Collapse operation */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}

/** Edit button operation */
async function handleUpdate(row) {
  reset();
  await getTreeselect();
  if (row != null) {
    form.value.parentId = row.parentId;
  }
  getProduct_category(row.categoryId).then(response => {
    form.value = response.data;
    form.value.parentId = response.data.parentId ?? 0;
    open.value = true;
    title.value = "Edit Product Category";
  });
}

/** Submit button */
function submitForm() {
  proxy.$refs["product_categoryRef"].validate(valid => {
    if (!valid) return;
    if (form.value.categoryId && form.value.parentId === form.value.categoryId) {
      proxy.$modal.msgWarning("Parent category cannot be itself");
      return;
    }
    submitLoading.value = true;
    const action = form.value.categoryId ? updateProduct_category : addProduct_category;
    action(form.value)
      .then(() => {
        proxy.$modal.msgSuccess(form.value.categoryId ? "Modified successfully" : "Added successfully");
        open.value = false;
        refreshAll();
      })
      .finally(() => {
        submitLoading.value = false;
      });
  });
}

/** Delete button operation */
function handleDelete(row) {
  proxy.$modal.confirm('Are you sure you want to delete product category number "' + row.categoryId + '" data item?').then(function() {
    return delProduct_category(row.categoryId);
  }).then(() => {
    refreshAll();
    proxy.$modal.msgSuccess("Deleted successfully");
  }).catch(() => {});
}

getTreeselect();
getList();
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.actions > * {
  margin-right: 8px;
}
.actions > *:last-child {
  margin-right: 0;
}
.name-text {
  margin-left: 4px;
  font-weight: 500;
}
.search-card,
.table-card {
  border: 1px solid var(--el-border-color-light);
}
</style>
