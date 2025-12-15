<template>
  <div class="app-container page-container">
    <el-card shadow="never" class="search-card" v-show="showSearch">
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="90px">
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
            <el-option label="Exchanging" value="exchanging" />
            <el-option label="Exchanged" value="exchanged" />
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
              type="primary"
              icon="Plus"
              @click="handleAdd"
            >Publish Product</el-button>
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

      <div v-loading="loading" class="card-grid">
        <el-empty v-if="!loading && (!productList || productList.length === 0)" description="No products yet, go publish one~" />
        <el-row v-else :gutter="20">
          <el-col v-for="item in productList" :key="item.productId" :span="6" :xs="24" :sm="12" :md="8" :lg="6">
            <el-card shadow="hover" class="product-card" :class="{ 'sold-out': item.status === 'sold' }">
              <!-- Product Image Area -->
              <div class="card-image-wrapper">
                <div class="card-image">
                  <el-image
                    v-if="item.images"
                    :src="item.images.split(',')[0]"
                    :preview-src-list="item.images.split(',')"
                    fit="cover"
                    class="cover"
                    lazy
                  />
                  <div v-else class="no-image-placeholder">
                    <el-icon :size="40"><Picture /></el-icon>
                    <span>No Image</span>
                  </div>
                </div>
                <!-- Status Tag -->
                <div class="status-badge">
                  <el-tag v-if="item.status === 'published'" type="success" size="small" effect="dark">Published</el-tag>
                  <el-tag v-else-if="item.status === 'pending'" type="warning" size="small" effect="dark">Pending Review</el-tag>
                  <el-tag v-else-if="item.status === 'exchanging'" type="info" size="small" effect="dark">Exchanging</el-tag>
                  <el-tag v-else-if="item.status === 'exchanged'" type="info" size="small" effect="dark">Exchanged</el-tag>
                  <el-tag v-else-if="item.status === 'sold'" type="info" size="small" effect="dark">Sold Out</el-tag>
                  <el-tag v-else-if="item.status === 'offline'" type="info" size="small" effect="dark">Offline</el-tag>
                  <el-tag v-else-if="item.status === 'rejected'" type="danger" size="small" effect="dark">Rejected</el-tag>
                  <el-tag v-else size="small" effect="dark">{{ item.status }}</el-tag>
                </div>
              </div>
              
              <!-- Product Information Area -->
              <div class="card-body">
                <!-- Title -->
                <div class="card-title" :title="item.title">{{ item.title }}</div>
                
                <!-- Category -->
                <div class="card-category">
                  <el-icon><Folder /></el-icon>
                  <span v-if="item.categoryName">{{ item.categoryName }}</span>
                  <span v-else class="muted">Uncategorized</span>
                </div>
                
                <!-- Brand and Size -->
                <div class="card-attributes">
                  <div class="attr-item" v-if="item.brand">
                    <span class="attr-label">Brand</span>
                    <span class="attr-value">{{ item.brand }}</span>
                  </div>
                  <div class="attr-item" v-if="item.size">
                    <span class="attr-label">Size</span>
                    <span class="attr-value">{{ item.size }}</span>
                  </div>
                </div>
                
                <!-- Price -->
                <div class="price-section">
                  <div class="price-row">
                    <span class="sale-price">¥{{ item.salePrice || 0 }}</span>
                    <span v-if="item.originalPrice" class="orig-price">¥{{ item.originalPrice }}</span>
                  </div>
                </div>
                
                <!-- Action Buttons -->
                <div class="card-actions">
                  <el-button link type="primary" icon="View" @click="handleView(item)">View Details</el-button>
                  <el-button
                    v-if="item.status === 'published'"
                    link
                    type="warning"
                    icon="Bottom"
                    @click="handleOffline(item)"
                  >Take Offline</el-button>
                  <el-button
                    v-if="item.status === 'offline'"
                    link
                    type="success"
                    icon="Top"
                    @click="handleOnline(item)"
                  >Put Online</el-button>
                  <el-button
                    v-if="canDelete(item)"
                    link
                    type="danger"
                    icon="Delete"
                    @click="handleDelete(item)"
                  >Delete</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- Product Details Dialog -->
    <el-dialog title="Product Details" v-model="viewOpen" width="900px" append-to-body class="product-detail-dialog">
      <div v-if="viewProduct" class="product-detail-content">
        <!-- Product Header Information -->
        <div class="product-header-section">
          <div class="product-main-image">
            <el-image
              v-if="viewProduct.images"
              :src="viewProduct.images.split(',')[0]"
              :preview-src-list="viewProduct.images.split(',')"
              fit="cover"
              style="width: 200px; height: 200px; border-radius: 8px;"
            />
            <div v-else class="no-image-placeholder">
              <el-icon :size="48"><Picture /></el-icon>
              <span>No Image</span>
            </div>
          </div>
          <div class="product-header-info">
            <div class="product-title-main">{{ viewProduct.title }}</div>
            <div class="product-status-tag">
              <el-tag v-if="viewProduct.status === 'published'" type="success" size="large">Published</el-tag>
              <el-tag v-else-if="viewProduct.status === 'pending'" type="warning" size="large">Pending Review</el-tag>
              <el-tag v-else-if="viewProduct.status === 'exchanging'" type="info" size="large">Exchanging</el-tag>
              <el-tag v-else-if="viewProduct.status === 'exchanged'" type="info" size="large">Exchanged</el-tag>
              <el-tag v-else-if="viewProduct.status === 'sold'" type="info" size="large">Sold Out</el-tag>
              <el-tag v-else-if="viewProduct.status === 'offline'" type="info" size="large">Offline</el-tag>
              <el-tag v-else-if="viewProduct.status === 'rejected'" type="danger" size="large">Rejected</el-tag>
              <el-tag v-else size="large">{{ viewProduct.status }}</el-tag>
            </div>
            <div class="product-price-display">
              <span class="price-label">Price:</span>
              <span class="price-value">¥{{ viewProduct.salePrice || 0 }}</span>
              <span class="original-price-value" v-if="viewProduct.originalPrice">Original: ¥{{ viewProduct.originalPrice }}</span>
            </div>
            <div class="product-category-info">
              <span class="category-label">Product Category:</span>
              <span v-if="viewProduct.categoryName" class="category-name">{{ viewProduct.categoryName }}</span>
              <span v-else class="category-id">ID: {{ viewProduct.categoryId }}</span>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- Product Description -->
        <div class="info-section" v-if="viewProduct.description">
          <div class="section-title">Product Description</div>
          <div class="section-content description-content">{{ viewProduct.description }}</div>
        </div>

        <!-- Product Attributes -->
        <div class="info-section">
          <div class="section-title">Product Attributes</div>
          <div class="section-content info-grid">
            <div class="info-item">
              <div class="info-label">Brand</div>
              <div class="info-value">{{ viewProduct.brand || '-' }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">Size</div>
              <div class="info-value">{{ viewProduct.size || '-' }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">Condition</div>
              <div class="info-value">{{ viewProduct.conditionLevel || '-' }}</div>
            </div>
          </div>
        </div>

        <!-- Service Fee Information -->
        <div class="info-section" v-if="viewProduct.donationRatio || viewProduct.donationAmount">
          <div class="section-title">Service Fee Information</div>
          <div class="section-content info-grid">
            <div class="info-item" v-if="viewProduct.donationRatio">
              <div class="info-label">Donation Ratio</div>
              <div class="info-value">{{ viewProduct.donationRatio }}%</div>
            </div>
            <div class="info-item" v-if="viewProduct.donationAmount">
              <div class="info-label">Donation Amount</div>
              <div class="info-value">¥{{ viewProduct.donationAmount }}</div>
            </div>
          </div>
        </div>

        <!-- AI Detection Information -->
        <div class="info-section">
          <div class="section-title">AI Detection Information</div>
          <div class="section-content info-grid">
            <div class="info-item">
              <div class="info-label">Detection Status</div>
              <div class="info-value">
                <el-tag v-if="viewProduct.aiCheckStatus === 'passed'" type="success">Passed</el-tag>
                <el-tag v-else-if="viewProduct.aiCheckStatus === 'pending'" type="warning">Pending</el-tag>
                <el-tag v-else-if="viewProduct.aiCheckStatus === 'failed'" type="danger">Failed</el-tag>
                <span v-else class="text-muted">-</span>
              </div>
            </div>
            <div class="info-item">
              <div class="info-label">Detection Time</div>
              <div class="info-value">{{ viewProduct.aiCheckTime || '-' }}</div>
            </div>
          </div>
        </div>

        <!-- Review Information -->
        <div class="info-section" v-if="viewProduct.auditBy || viewProduct.auditTime || viewProduct.auditRemark">
          <div class="section-title">Review Information</div>
          <div class="section-content info-grid">
            <div class="info-item">
              <div class="info-label">Reviewer</div>
              <div class="info-value">{{ viewProduct.auditBy || '-' }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">Review Time</div>
              <div class="info-value">{{ viewProduct.auditTime || '-' }}</div>
            </div>
            <div class="info-item full-width" v-if="viewProduct.auditRemark">
              <div class="info-label">Review Remarks</div>
              <div class="info-value">{{ viewProduct.auditRemark }}</div>
            </div>
          </div>
        </div>

        <!-- Other Information -->
        <div class="info-section">
          <div class="section-title">Other Information</div>
          <div class="section-content info-grid">
            <div class="info-item">
              <div class="info-label">View Count</div>
              <div class="info-value">{{ viewProduct.viewCount || 0 }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">Favorite Count</div>
              <div class="info-value">{{ viewProduct.favoriteCount || 0 }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">Create Time</div>
              <div class="info-value">{{ viewProduct.createTime }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">Update Time</div>
              <div class="info-value">{{ viewProduct.updateTime }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="MyProducts">
import { getCurrentInstance, ref, reactive, toRefs, onMounted } from "vue";
import { useRouter } from "vue-router";
import { Picture, View, Delete, Plus, RefreshRight, Search, Folder, Top, Bottom } from "@element-plus/icons-vue";
import { listMyProduct, getMyProduct, delMyProduct, onlineMyProduct, offlineMyProduct } from "@/api/product";

const { proxy } = getCurrentInstance();
const router = useRouter();

const productList = ref([]);
const total = ref(0);
const loading = ref(true);
const showSearch = ref(true);
const viewOpen = ref(false);
const viewProduct = ref(null);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: null,
    status: null,
  }
});

const { queryParams } = toRefs(data);

/** Query product list */
function getList() {
  loading.value = true;
  listMyProduct(queryParams.value).then(response => {
    productList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
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

/** Publish product button operation */
function handleAdd() {
  router.push('/product/ai-publish');
}

/** View button operation */
function handleView(row) {
  getMyProduct(row.productId).then(response => {
    viewProduct.value = response.data;
    viewOpen.value = true;
  });
}

/** Delete button operation */
function handleDelete(row) {
  const productIds = row.productId;
  proxy.$modal.confirm('Are you sure to delete product with ID "' + productIds + '"?').then(function() {
    return delMyProduct(productIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("Deleted successfully");
  }).catch(() => {});
}

/** Only pending review/rejected can be deleted, cannot delete after publishing */
function canDelete(row) {
  return row.status === 'pending' || row.status === 'rejected';
}

/** Put product online */
function handleOnline(row) {
  proxy.$modal.confirm('Are you sure to put this product online? The product will be displayed on the platform after going online.').then(() => {
    onlineMyProduct(row.productId).then(() => {
      proxy.$modal.msgSuccess("Put online successfully");
      getList();
    }).catch(() => {});
  }).catch(() => {});
}

/** Take product offline */
function handleOffline(row) {
  proxy.$modal.confirm('Are you sure to take this product offline? The product will no longer be displayed on the platform after going offline.').then(() => {
    offlineMyProduct(row.productId).then(() => {
      proxy.$modal.msgSuccess("Taken offline successfully");
      getList();
    }).catch(() => {});
  }).catch(() => {});
}

onMounted(() => {
  getList();
});
</script>

<style scoped>
.page-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.search-card, .table-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.actions {
  display: flex;
  gap: 10px;
}

/* 商品详情对话框样式 */
:deep(.product-detail-dialog) {
  border-radius: 12px;
}

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
  padding: 24px;
  background-color: #fff;
}

.product-detail-content {
  max-height: 70vh;
  overflow-y: auto;
}

.product-header-section {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  margin-bottom: 24px;
}

.product-main-image {
  flex-shrink: 0;
  width: 200px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 14px;
}

.no-image-placeholder .el-icon {
  margin-bottom: 8px;
}

.product-header-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.product-title-main {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.3;
}

.product-status-tag {
  margin-top: 4px;
}

.product-price-display {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-top: 8px;
}

.price-label {
  font-size: 14px;
  color: #909399;
}

.price-value {
  font-size: 22px;
  font-weight: 700;
  color: #f56c6c;
}

.original-price-value {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
}

.product-category-info {
  font-size: 14px;
  color: #606266;
}

.category-label {
  font-weight: 500;
  color: #909399;
}

.category-name {
  font-weight: 600;
  color: #303133;
}

.category-id {
  color: #909399;
}

.el-divider {
  margin: 24px 0;
}

.info-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.section-content {
  padding-top: 8px;
}

.description-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  border-left: 3px solid #409eff;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 13px;
  color: #909399;
  font-weight: 500;
}

.info-value {
  font-size: 14px;
  color: #303133;
  word-break: break-word;
}

.text-muted {
  color: #909399;
}

/* 卡片网格布局 */
.card-grid {
  min-height: 400px;
}

/* 商品卡片样式 */
.product-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.product-card.sold-out {
  opacity: 0.75;
}

.product-card.sold-out .card-image::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 1;
}

/* 图片区域 */
.card-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 75%; /* 4:3 比例 */
  overflow: hidden;
  background-color: #f5f7fa;
}

.card-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.card-image .cover {
  width: 100%;
  height: 100%;
  border-radius: 0;
}

.no-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 14px;
  background-color: #f5f7fa;
}

.no-image-placeholder .el-icon {
  margin-bottom: 8px;
}

/* 状态标签 */
.status-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 2;
}

/* 卡片内容区域 */
.card-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 标题 */
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 48px;
  margin-bottom: 4px;
}

/* 类别 */
.card-category {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.card-category .el-icon {
  font-size: 14px;
}

.card-category .muted {
  color: #c0c4cc;
}

/* 品牌和尺码属性 */
.card-attributes {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  padding: 8px 0;
  border-top: 1px solid #f0f2f5;
  border-bottom: 1px solid #f0f2f5;
}

.attr-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}

.attr-label {
  color: #909399;
  font-weight: 500;
}

.attr-value {
  color: #606266;
  font-weight: 600;
}

/* 价格区域 */
.price-section {
  margin-top: auto;
  padding-top: 8px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.sale-price {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
  line-height: 1;
}

.orig-price {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
}

/* 操作按钮 */
.card-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #f0f2f5;
  margin-top: 8px;
}

.card-actions .el-button {
  padding: 0;
  font-size: 13px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .card-body {
    padding: 12px;
  }
  
  .card-title {
    font-size: 15px;
    min-height: 45px;
  }
  
  .sale-price {
    font-size: 18px;
  }
}
</style>

