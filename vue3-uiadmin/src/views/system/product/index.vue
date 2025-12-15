<template>
  <div class="app-container page-container">
    <el-card shadow="never" class="search-card" v-show="showSearch">
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="130px">
        <el-form-item label="Product Title" prop="title">
          <el-input
            v-model="queryParams.title"
            placeholder="Please enter product title"
            clearable
            @keyup.enter="handleQuery"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="Product Status" prop="status">
          <el-select v-model="queryParams.status" placeholder="Please select status" clearable style="width: 150px">
            <el-option label="Pending Review" value="pending" />
            <el-option label="Published" value="published" />
            <el-option label="Offline" value="offline" />
            <el-option label="Sold Out" value="sold" />
            <el-option label="Rejected" value="rejected" />
          </el-select>
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
              type="default"
              plain
              icon="RefreshRight"
              @click="getList"
            >Refresh</el-button>
          </div>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="productList"
        border
        stripe
      >
        <el-table-column type="index" width="60" label="#" />
        <el-table-column label="Product Image" width="100" align="center">
          <template #default="scope">
            <el-image
              v-if="scope.row.images"
              :src="scope.row.images.split(',')[0]"
              :preview-src-list="scope.row.images.split(',')"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
              lazy
            />
            <span v-else style="color: #909399">No Image</span>
          </template>
        </el-table-column>
        <el-table-column label="Product Title" prop="title" min-width="200" show-overflow-tooltip />
        <el-table-column label="Seller" prop="sellerName" width="120" align="center" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.sellerName">{{ scope.row.sellerName }}</span>
            <span v-else style="color: #909399">ID: {{ scope.row.sellerId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Category ID" prop="categoryId" width="100" align="center" />
        <el-table-column label="Sale Price" prop="salePrice" width="100" align="center">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: 600">¥{{ scope.row.salePrice || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Original Price" prop="originalPrice" width="100" align="center">
          <template #default="scope">
            <span v-if="scope.row.originalPrice">¥{{ scope.row.originalPrice }}</span>
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>
        <el-table-column label="Product Status" prop="status" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'published'" type="success">Published</el-tag>
            <el-tag v-else-if="scope.row.status === 'pending'" type="warning">Pending Review</el-tag>
            <el-tag v-else-if="scope.row.status === 'sold'" type="info">Sold Out</el-tag>
            <el-tag v-else-if="scope.row.status === 'offline'" type="info">Offline</el-tag>
            <el-tag v-else-if="scope.row.status === 'rejected'" type="danger">Rejected</el-tag>
            <el-tag v-else>{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="AI Check Status" prop="aiCheckStatus" width="110" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.aiCheckStatus === 'passed'" type="success">Passed</el-tag>
            <el-tag v-else-if="scope.row.aiCheckStatus === 'pending'" type="warning">Pending</el-tag>
            <el-tag v-else-if="scope.row.aiCheckStatus === 'failed'" type="danger">Failed</el-tag>
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>
        <el-table-column label="Auditor" prop="auditBy" width="100" align="center" />
        <el-table-column label="Audit Time" prop="auditTime" width="160" align="center" />
        <el-table-column label="Create Time" prop="createTime" width="160" align="center" />
        <el-table-column label="Actions" align="center" class-name="small-padding fixed-width" width="320" fixed="right">
          <template #default="scope">
            <el-button link type="primary" icon="View" @click="handleView(scope.row)">View</el-button>
            <el-button
              v-if="scope.row.status === 'pending'"
              link
              type="success"
              icon="Check"
              @click="handleAudit(scope.row)"
              v-hasPermi="['system:product:audit']"
            >Audit</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:product:remove']">Delete</el-button>
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
    </el-card>

    <!-- Product Detail Dialog -->
    <el-dialog title="Product Details" v-model="viewOpen" width="900px" append-to-body class="product-detail-dialog">
      <div v-if="viewProduct" class="product-detail-content">
        <!-- Product Images and Basic Info -->
        <div class="product-header">
          <div class="product-image-wrapper">
            <el-image
              v-if="viewProduct.images"
              :src="viewProduct.images.split(',')[0]"
              :preview-src-list="viewProduct.images.split(',')"
              fit="cover"
              class="main-image"
            />
            <div v-else class="no-image-placeholder">
              <el-icon :size="60"><Picture /></el-icon>
              <span>No Image</span>
            </div>
          </div>
          <div class="product-basic-info">
            <div class="product-title">{{ viewProduct.title }}</div>
            <div class="product-status-row">
              <el-tag v-if="viewProduct.status === 'published'" type="success">Published</el-tag>
              <el-tag v-else-if="viewProduct.status === 'pending'" type="warning">Pending Review</el-tag>
              <el-tag v-else-if="viewProduct.status === 'sold'" type="info">Sold Out</el-tag>
              <el-tag v-else-if="viewProduct.status === 'offline'" type="info">Offline</el-tag>
              <el-tag v-else-if="viewProduct.status === 'rejected'" type="danger">Rejected</el-tag>
              <el-tag v-else>{{ viewProduct.status }}</el-tag>
              <el-tag v-if="viewProduct.aiCheckStatus === 'passed'" type="success" style="margin-left: 8px">AI Passed</el-tag>
            </div>
            <div class="product-price-row">
              <span class="price-label">Sale Price</span>
              <span class="price-main">¥{{ viewProduct.salePrice || 0 }}</span>
              <span v-if="viewProduct.originalPrice" class="price-original">Original ¥{{ viewProduct.originalPrice }}</span>
            </div>
            <div class="product-seller">
              <span class="label">Seller:</span>
              <span v-if="viewProduct.sellerName" class="value">{{ viewProduct.sellerName }}</span>
              <span v-else class="value text-muted">ID: {{ viewProduct.sellerId }}</span>
            </div>
          </div>
        </div>

        <!-- Product Description -->
        <div v-if="viewProduct.description" class="detail-section">
          <div class="section-title">Product Description</div>
          <div class="section-content description-text">{{ viewProduct.description }}</div>
        </div>

        <!-- Product Attributes -->
        <div class="detail-section">
          <div class="section-title">Product Attributes</div>
          <div class="attribute-list">
            <div class="attribute-item">
              <span class="attr-label">Brand</span>
              <span class="attr-value">{{ viewProduct.brand || '-' }}</span>
            </div>
            <div class="attribute-item">
              <span class="attr-label">Size</span>
              <span class="attr-value">{{ viewProduct.size || '-' }}</span>
            </div>
            <div class="attribute-item">
              <span class="attr-label">Condition</span>
              <span class="attr-value">{{ viewProduct.conditionLevel || '-' }}</span>
            </div>
            <div class="attribute-item">
              <span class="attr-label">Product Category</span>
              <span class="attr-value">{{ viewProduct.categoryName || ('ID: ' + viewProduct.categoryId) }}</span>
            </div>
          </div>
        </div>

        <!-- Price and Service Fee -->
        <div v-if="viewProduct.donationRatio || viewProduct.donationAmount" class="detail-section">
          <div class="section-title">Service Fee Information</div>
          <div class="donation-info">
            <div class="donation-item" v-if="viewProduct.donationRatio">
              <span class="donation-label">Service Fee Ratio</span>
              <span class="donation-value">{{ viewProduct.donationRatio }}%</span>
            </div>
            <div class="donation-item" v-if="viewProduct.donationAmount">
              <span class="donation-label">Service Fee Amount</span>
              <span class="donation-value amount">¥{{ viewProduct.donationAmount }}</span>
            </div>
          </div>
        </div>

        <!-- Audit Information -->
        <div v-if="viewProduct.auditBy || viewProduct.auditTime" class="detail-section">
          <div class="section-title">Audit Information</div>
          <div class="info-list">
            <div class="info-row">
              <span class="info-label">Auditor</span>
              <span class="info-value">{{ viewProduct.auditBy || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">Audit Time</span>
              <span class="info-value">{{ viewProduct.auditTime || '-' }}</span>
            </div>
            <div v-if="viewProduct.auditRemark" class="info-row full-width">
              <span class="info-label">Audit Remark</span>
              <span class="info-value">{{ viewProduct.auditRemark }}</span>
            </div>
          </div>
        </div>

        <!-- Statistics and Time -->
        <div class="detail-section">
          <div class="section-title">Other Information</div>
          <div class="info-list">
            <div class="info-row">
              <span class="info-label">View Count</span>
              <span class="info-value">{{ viewProduct.viewCount || 0 }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">Favorite Count</span>
              <span class="info-value">{{ viewProduct.favoriteCount || 0 }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">Create Time</span>
              <span class="info-value">{{ viewProduct.createTime }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">Update Time</span>
              <span class="info-value">{{ viewProduct.updateTime }}</span>
            </div>
            <div v-if="viewProduct.aiCheckTime" class="info-row">
              <span class="info-label">AI Check Time</span>
              <span class="info-value">{{ viewProduct.aiCheckTime }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- Audit Dialog -->
    <el-dialog :title="auditTitle" v-model="auditOpen" width="150px" append-to-body destroy-on-close class="audit-dialog">
      <div class="audit-content">
        <!-- Product Information -->
        <div class="audit-product-info">
          <div class="audit-product-title">{{ currentProduct?.title }}</div>
          <div class="audit-product-price">Sale Price：¥{{ currentProduct?.salePrice || 0 }}</div>
        </div>

        <el-form ref="auditRef" :model="auditForm" :rules="auditRules" label-width="0" class="audit-form">
          <!-- Audit Action Selection -->
          <div class="audit-action-group">
            <el-radio-group v-model="auditForm.auditAction" @change="handleAuditActionChange" size="large">
              <el-radio-button label="pass">Approve</el-radio-button>
              <el-radio-button label="reject">Reject</el-radio-button>
            </el-radio-group>
          </div>

          <!-- Service Fee Setting (Shown when approving) -->
          <div v-if="auditForm.auditAction === 'pass'" class="audit-form-section">
            <div class="form-section-title">Platform Service Fee Setting</div>
            <el-form-item prop="donationRatio">
              <div class="donation-input-group">
                <label class="input-label">Service Fee Ratio (%)</label>
                <el-input-number
                  v-model="auditForm.donationRatio"
                  :min="0"
                  :max="100"
                  :precision="2"
                  :step="0.1"
                  placeholder="0.00"
                  controls-position="right"
                  @change="calculateDonationAmount"
                  style="width: 100%"
                />
                <div class="input-hint">Service fee calculated automatically based on sale price, maximum 50 yuan</div>
              </div>
            </el-form-item>

            <!-- Estimated Amount Display -->
            <div v-if="auditForm.donationRatio > 0" class="donation-result">
              <div class="result-label">Estimated Service Fee</div>
              <div class="result-amount">
                <span class="amount-text">¥{{ calculatedDonationAmount.toFixed(2) }}</span>
                <el-tag v-if="calculatedDonationAmount >= 50" type="warning" size="small">Maximum Reached</el-tag>
              </div>
            </div>
          </div>

          <!-- Audit Remark/Rejection Reason -->
          <div class="audit-form-section">
            <div class="form-section-title">
              {{ auditForm.auditAction === 'reject' ? 'Rejection Reason' : 'Audit Remark' }}
              <el-tag v-if="auditForm.auditAction === 'reject'" type="danger" size="small" style="margin-left: 8px">Required</el-tag>
            </div>
            <el-form-item :prop="auditForm.auditAction === 'reject' ? 'auditRemark' : 'auditRemark'">
              <el-input
                v-model="auditForm.auditRemark"
                type="textarea"
                :rows="4"
                :placeholder="auditForm.auditAction === 'reject' ? 'Please enter rejection reason...' : 'Please enter audit remark (optional)...'"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </div>
        </el-form>
      </div>

      <template #footer>
        <div class="audit-footer">
          <el-button @click="auditOpen = false">Cancel</el-button>
          <el-button
            v-if="auditForm.auditAction === 'pass'"
            type="success"
            :loading="submitLoading"
            @click="submitAudit('pass')"
          >Approve</el-button>
          <el-button
            v-if="auditForm.auditAction === 'reject'"
            type="danger"
            :loading="submitLoading"
            @click="submitAudit('reject')"
          >Reject</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Product">
import { getCurrentInstance, ref, reactive, toRefs, onMounted } from "vue";
import {
  InfoFilled, Document, Box, Money, Cpu, Checked, DataAnalysis, Clock,
  Picture, Check, Close, EditPen
} from "@element-plus/icons-vue";
import { listProduct, getProduct, delProduct, auditProductPass, auditProductReject } from "@/api/system/product";

const { proxy } = getCurrentInstance();

const productList = ref([]);
const total = ref(0);
const loading = ref(true);
const showSearch = ref(true);
const submitLoading = ref(false);
const viewOpen = ref(false);
const viewProduct = ref(null);
const auditOpen = ref(false);
const auditTitle = ref("");
const auditType = ref("");
const currentProduct = ref(null);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: null,
    status: null,
  },
  auditForm: {
    auditRemark: "",
    donationRatio: null,
    auditAction: "pass"
  },
  auditRules: {
    auditRemark: [
      { required: false, message: "Please enter audit remark", trigger: "blur" }
    ],
    donationRatio: [
      { type: 'number', min: 0, max: 100, message: "Donation ratio must be between 0-100", trigger: "blur" }
    ]
  }
});

const calculatedDonationAmount = ref(0);

const { queryParams, auditForm, auditRules } = toRefs(data);

/** Query product list */
function getList() {
  loading.value = true;
  listProduct(queryParams.value).then(response => {
    productList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** Search button action */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** Reset button action */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** View button action */
function handleView(row) {
  getProduct(row.productId).then(response => {
    viewProduct.value = response.data;
    viewOpen.value = true;
  });
}

/** Audit button action */
function handleAudit(row, type) {
  currentProduct.value = row;
  auditType.value = 'pass'; // Default to pass, user can select in dialog
  auditForm.value.auditRemark = "";
  auditForm.value.donationRatio = row.donationRatio || null;
  auditForm.value.auditAction = 'pass'; // Default select approve
  calculatedDonationAmount.value = 0;
  auditTitle.value = 'Product Audit';

  // Initialize audit rules
  auditRules.value.auditRemark = [
    { required: false, message: "Please enter audit remark", trigger: "blur" }
  ];

  // Calculate initial donation amount
  if (auditForm.value.donationRatio && currentProduct.value.salePrice) {
    calculateDonationAmount();
  }

  auditOpen.value = true;
}

/** Calculate donation amount */
function calculateDonationAmount() {
  if (!auditForm.value.donationRatio || !currentProduct.value?.salePrice) {
    calculatedDonationAmount.value = 0;
    return;
  }

  const salePrice = parseFloat(currentProduct.value.salePrice) || 0;
  const ratio = parseFloat(auditForm.value.donationRatio) || 0;

  // Calculate donation amount
  let amount = (salePrice * ratio) / 100;

  // Rule: Donation amount cannot exceed 50 yuan
  if (amount > 50) {
    amount = 50;
  }

  // If less than 0, set to 0
  if (amount < 0) {
    amount = 0;
  }

  calculatedDonationAmount.value = amount;
}

/** Audit action change */
function handleAuditActionChange() {
  // When switching audit action, clear remark and donation ratio
  if (auditForm.value.auditAction === 'reject') {
    auditForm.value.donationRatio = null;
    calculatedDonationAmount.value = 0;
    auditRules.value.auditRemark = [
      { required: true, message: "Rejection reason cannot be empty", trigger: "blur" }
    ];
  } else {
    auditRules.value.auditRemark = [
      { required: false, message: "Please enter audit remark", trigger: "blur" }
    ];
  }
}

/** Submit audit */
function submitAudit(action) {
  const finalAction = action || auditForm.value.auditAction || auditType.value;

  proxy.$refs["auditRef"].validate(valid => {
    if (valid) {
      // If rejecting, must fill in rejection reason
      if (finalAction === 'reject' && !auditForm.value.auditRemark?.trim()) {
        proxy.$modal.msgError("Rejection reason is required for audit rejection");
        return;
      }

      submitLoading.value = true;
      const productId = currentProduct.value.productId;
      const remark = auditForm.value.auditRemark || "";

      let promise;
      if (finalAction === 'pass') {
        // Audit pass, pass donation ratio
        const donationRatio = auditForm.value.donationRatio || null;
        promise = auditProductPass(productId, remark, donationRatio);
      } else {
        // Audit reject
        promise = auditProductReject(productId, remark);
      }

      promise.then(() => {
        proxy.$modal.msgSuccess(finalAction === 'pass' ? "Audit approved successfully" : "Audit rejected successfully");
        auditOpen.value = false;
        getList();
      }).catch(error => {
        proxy.$modal.msgError(error.msg || "Audit failed");
      }).finally(() => {
        submitLoading.value = false;
      });
    }
  });
}

/** Delete button action */
function handleDelete(row) {
  const productIds = row.productId || [];
  proxy.$modal.confirm('Are you sure to delete product ID "' + productIds + '"?').then(function() {
    return delProduct(productIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("Deleted successfully");
  }).catch(() => {});
}

onMounted(() => {
  getList();
});
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions {
  display: flex;
  gap: 10px;
}

/* Product Detail Dialog - Simple & Elegant Style */
:deep(.product-detail-dialog .el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.product-detail-dialog .el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

:deep(.product-detail-dialog .el-dialog__body) {
  padding: 0;
}

.product-detail-content {
  padding: 32px;
  max-height: 75vh;
  overflow-y: auto;
}

/* Product Header Area */
.product-header {
  display: flex;
  gap: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 32px;
}

.product-image-wrapper {
  flex-shrink: 0;
}

.main-image {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  cursor: pointer;
}

.no-image-placeholder {
  width: 200px;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  color: #c0c4cc;
  background: #fafafa;
}

.no-image-placeholder span {
  margin-top: 12px;
  font-size: 14px;
  color: #909399;
}

.product-basic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  line-height: 1.5;
}

.product-status-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price-label {
  font-size: 14px;
  color: #909399;
}

.price-main {
  font-size: 28px;
  font-weight: 600;
  color: #f56c6c;
}

.price-original {
  font-size: 16px;
  color: #c0c4cc;
  text-decoration: line-through;
}

.product-seller {
  font-size: 14px;
  color: #606266;
}

.product-seller .label {
  color: #909399;
}

.product-seller .value {
  color: #303133;
}

.text-muted {
  color: #909399;
}

/* Detail Sections */
.detail-section {
  margin-bottom: 32px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.section-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

.description-text {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 6px;
  line-height: 1.8;
}

/* Attribute List */
.attribute-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px 40px;
}

.attribute-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.attr-label {
  font-size: 14px;
  color: #909399;
  min-width: 80px;
}

.attr-value {
  font-size: 14px;
  color: #303133;
}

/* Service Fee Information */
.donation-info {
  display: flex;
  gap: 40px;
}

.donation-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.donation-label {
  font-size: 14px;
  color: #909399;
}

.donation-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.donation-value.amount {
  color: #67c23a;
}

/* Info List */
.info-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px 40px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.info-row.full-width {
  grid-column: 1 / -1;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 14px;
  color: #909399;
  min-width: 100px;
  flex-shrink: 0;
}

.info-value {
  font-size: 14px;
  color: #303133;
  word-break: break-word;
}

/* Audit Dialog - Simple & Elegant Style */
:deep(.audit-dialog .el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.audit-dialog .el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

:deep(.audit-dialog .el-dialog__body) {
  padding: 0;
}

.audit-content {
  padding: 32px;
}

/* Product Information */
.audit-product-info {
  padding-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 24px;
}

.audit-product-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  line-height: 1.5;
}

.audit-product-price {
  font-size: 16px;
  color: #606266;
}

.audit-product-price .price-value {
  font-size: 20px;
  font-weight: 600;
  color: #f56c6c;
  margin-left: 8px;
}

/* Audit Form */
.audit-form {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.audit-action-group {
  display: flex;
  justify-content: center;
}

:deep(.audit-action-group .el-radio-group) {
  width: 100%;
  display: flex;
  gap: 16px;
}

:deep(.audit-action-group .el-radio-button) {
  flex: 1;
}

:deep(.audit-action-group .el-radio-button__inner) {
  width: 100%;
  padding: 14px 20px;
  font-size: 15px;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  transition: all 0.2s;
}

:deep(.audit-action-group .el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: #409eff;
  border-color: #409eff;
  color: #fff;
}

:deep(.audit-action-group .el-radio-button:last-child .el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: #f56c6c;
  border-color: #f56c6c;
}

/* Form Sections */
.audit-form-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.audit-form-section .el-form-item) {
  margin-bottom: 0;
}

/* Service Fee Input Group */
.donation-input-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.input-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.input-hint {
  font-size: 12px;
  color: #909399;
  margin-top: -4px;
}

/* Service Fee Result */
.donation-result {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.result-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.result-amount {
  display: flex;
  align-items: center;
  gap: 12px;
}

.amount-text {
  font-size: 24px;
  font-weight: 600;
  color: #67c23a;
}

/* Dialog Footer */
.audit-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #ebeef5;
}

/* Responsive */
@media (max-width: 768px) {
  .product-header {
    flex-direction: column;
  }

  .attribute-list,
  .info-list {
    grid-template-columns: 1fr;
  }

  .donation-info {
    flex-direction: column;
    gap: 20px;
  }
}
</style>