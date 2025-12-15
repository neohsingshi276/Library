<template>
  <div class="favorite-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <el-icon class="title-icon"><Star /></el-icon>
          <span class="title-text">My Favorites</span>
        </div>
        <div class="header-subtitle">Favorite products will be displayed here for your convenience</div>
      </div>
      <div class="header-actions">
        <el-button 
          type="primary" 
          :icon="Refresh" 
          @click="getList"
          :loading="loading"
        >
          Refresh
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <el-card shadow="never" class="filter-card" v-show="showSearch">
      <el-form :model="queryParams" ref="queryRef" :inline="true" class="filter-form">
        <el-form-item label="Keyword" prop="title">
          <el-input
            v-model="queryParams.title"
            placeholder="Search product title"
            clearable
            @keyup.enter="handleQuery"
            style="width: 250px"
            prefix-icon="Search"
          />
        </el-form-item>
        <el-form-item label="Category" prop="categoryId">
          <el-cascader
            v-model="queryParams.categoryPath"
            :options="categoryOptions"
            :props="cascaderProps"
            placeholder="All Categories"
            clearable
            style="width: 250px"
            @change="handleCategoryChange"
          />
        </el-form-item>
        <el-form-item label="Price" prop="priceRange">
          <el-select
            v-model="queryParams.priceRange"
            placeholder="Price Range"
            clearable
            style="width: 150px"
          >
            <el-option label="¥0-50" value="0-50" />
            <el-option label="¥50-100" value="50-100" />
            <el-option label="¥100-200" value="100-200" />
            <el-option label="¥200-500" value="200-500" />
            <el-option label="Above ¥500" value="500+" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">Search</el-button>
          <el-button icon="Refresh" @click="resetQuery">Reset</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 商品列表区域 -->
    <div class="products-section">
      <div v-loading="loading" class="products-container">
        <el-empty
          v-if="!loading && (!productList || productList.length === 0)"
          description="No favorite products"
          :image-size="120"
        >
          <el-button type="primary" @click="$router.push('/product/mall')">Go Shopping</el-button>
        </el-empty>
        <el-row v-else :gutter="20">
          <el-col
            v-for="item in productList"
            :key="item.productId"
            :span="6"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <el-card
              shadow="hover"
              class="product-card"
            >
              <!-- 商品图片 -->
              <div class="card-image-wrapper">
                <div class="card-image" @click="handleView(item)">
                  <el-image
                    v-if="item.images"
                    :src="item.images.split(',')[0]"
                    fit="cover"
                    class="cover"
                    lazy
                    :preview-src-list="[]"
                    :hide-on-click-modal="true"
                  />
                  <div v-else class="no-image-placeholder" @click.stop="handleView(item)">
                    <el-icon :size="40"><Picture /></el-icon>
                    <span>No Image</span>
                  </div>
                </div>
                <!-- 收藏按钮 -->
                <div class="favorite-badge" @click.stop="handleToggleFavorite(item)">
                  <el-icon 
                    :class="['favorite-icon', { 'is-favorite': true }]"
                    :size="24"
                  >
                    <StarFilled />
                  </el-icon>
                </div>
                <!-- 成色标签 -->
                <div class="condition-badge" v-if="item.conditionLevel">
                  <el-tag size="small" effect="dark">{{ item.conditionLevel }}</el-tag>
                </div>
              </div>

              <!-- 商品信息 -->
              <div class="card-body">
                <!-- 标题 -->
                <div class="card-title" :title="item.title" @click="handleView(item)">{{ item.title }}</div>

                <!-- 类别和卖家 -->
                <div class="card-meta">
                  <span class="category" v-if="item.categoryName">
                    <el-icon><Folder /></el-icon>
                    {{ item.categoryName }}
                  </span>
                  <span class="seller" v-if="item.sellerName">
                    <el-icon><User /></el-icon>
                    {{ item.sellerName }}
                  </span>
                </div>

                <!-- 品牌和尺码 -->
                <div class="card-attributes" v-if="item.brand || item.size">
                  <span class="attr-item" v-if="item.brand">
                    <span class="attr-label">Brand</span>
                    <span class="attr-value">{{ item.brand }}</span>
                  </span>
                  <span class="attr-item" v-if="item.size">
                    <span class="attr-label">Size</span>
                    <span class="attr-value">{{ item.size }}</span>
                  </span>
                </div>

                <!-- 价格 -->
                <div class="price-section">
                  <div class="price-row">
                    <span class="sale-price">¥{{ item.salePrice || 0 }}</span>
                    <span v-if="item.originalPrice" class="orig-price">¥{{ item.originalPrice }}</span>
                  </div>
                  <div class="price-save" v-if="item.originalPrice && item.originalPrice > item.salePrice">
                    Save ¥{{ (item.originalPrice - item.salePrice).toFixed(2) }}
                  </div>
                </div>

                <!-- 统计信息 -->
                <div class="card-stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ item.viewCount || 0 }} views
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ item.favoriteCount || 0 }} favorites
                  </span>
                </div>

                <!-- 操作按钮 -->
                <div class="card-actions">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="handleBuyNow(item)"
                    style="flex: 1;"
                  >
                    Buy Now
                  </el-button>
                  <el-button 
                    type="default" 
                    size="small" 
                    @click="handleView(item)"
                    style="flex: 1; margin-left: 8px;"
                  >
                    View Details
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <pagination
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </div>

    <!-- 商品详情对话框 -->
    <el-dialog
      title="Product Details"
      v-model="viewOpen"
      width="900px"
      append-to-body
      class="product-detail-dialog"
    >
      <div v-if="viewProduct" class="product-detail-content">
        <!-- 商品头部信息 -->
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
            <div class="product-price-display">
              <span class="price-label">Price: </span>
              <span class="price-value">¥{{ viewProduct.salePrice || 0 }}</span>
              <span class="original-price-value" v-if="viewProduct.originalPrice">
                Original Price: ¥{{ viewProduct.originalPrice }}
              </span>
            </div>
            <div class="product-meta-info">
              <div class="meta-item">
                <span class="meta-label">Category: </span>
                <span class="meta-value">{{ viewProduct.categoryName || 'Uncategorized' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">Seller: </span>
                <span class="meta-value">{{ viewProduct.sellerName || 'ID: ' + viewProduct.sellerId }}</span>
              </div>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 商品描述 -->
        <div class="info-section" v-if="viewProduct.description">
          <div class="section-title">Product Description</div>
          <div class="section-content description-content">{{ viewProduct.description }}</div>
        </div>

        <!-- 商品属性 -->
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

        <!-- 其他信息 -->
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
              <div class="info-label">Listing Time</div>
              <div class="info-value">{{ viewProduct.createTime }}</div>
            </div>
          </div>
        </div>

        <!-- 收藏按钮 -->
        <div class="favorite-section">
          <el-button
            type="warning"
            :icon="StarFilled"
            :loading="favoriteLoading"
            @click="handleToggleFavorite(viewProduct)"
            size="large"
            style="width: 100%; margin-top: 20px;"
          >
            Favorited
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="Favorite">
import { getCurrentInstance, ref, reactive, toRefs, onMounted } from "vue";
import {
  Picture,
  Folder,
  User,
  View,
  Star,
  StarFilled,
  Search,
  Refresh
} from "@element-plus/icons-vue";
import { ElMessage } from 'element-plus';
import { getFavoriteList, getProductDetail, getCategoryTreeSelect, toggleFavorite } from "@/api/product";
import { getToken } from '@/utils/auth';
import { useRouter } from 'vue-router';

const { proxy } = getCurrentInstance();
const router = useRouter();

const productList = ref([]);
const total = ref(0);
const loading = ref(true);
const showSearch = ref(true);
const viewOpen = ref(false);
const viewProduct = ref(null);
const categoryOptions = ref([]);
const favoriteLoading = ref(false);

// 级联选择器配置
const cascaderProps = {
  value: 'id',
  label: 'label',
  children: 'children',
  checkStrictly: false,
  emitPath: true
};

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 12,
    title: null,
    categoryId: null,
    categoryPath: null,
    priceRange: null,
    minPrice: null,
    maxPrice: null
  }
});

const { queryParams } = toRefs(data);

/** 查询收藏列表 */
function getList() {
  loading.value = true;
  
  // 处理价格区间
  const params = { ...queryParams.value };
  if (params.priceRange) {
    const [min, max] = params.priceRange.split(/[-+]/);
    if (min) params.minPrice = parseFloat(min);
    if (max && max !== '+') params.maxPrice = parseFloat(max);
    delete params.priceRange;
  }

  // 处理分类
  if (params.categoryPath && Array.isArray(params.categoryPath) && params.categoryPath.length > 0) {
    const lastCategoryId = params.categoryPath[params.categoryPath.length - 1];
    if (lastCategoryId != null && lastCategoryId !== '') {
      params.categoryId = typeof lastCategoryId === 'string' ? parseInt(lastCategoryId, 10) : Number(lastCategoryId);
    } else {
      params.categoryId = null;
    }
  } else {
    params.categoryId = null;
  }
  delete params.categoryPath;

  getFavoriteList(params)
    .then((response) => {
      if (response && response.data) {
        productList.value = response.data.rows || response.data.list || [];
        total.value = response.data.total || 0;
      } else if (response && response.rows) {
        productList.value = response.rows || [];
        total.value = response.total || 0;
      } else {
        productList.value = [];
        total.value = 0;
      }
      loading.value = false;
    })
    .catch((error) => {
      console.error('查询收藏列表失败:', error);
      productList.value = [];
      total.value = 0;
      loading.value = false;
    });
}

/** 分类变化处理 */
function handleCategoryChange(value) {
  queryParams.value.categoryPath = value;
  if (!value || (Array.isArray(value) && value.length === 0)) {
    queryParams.value.categoryId = null;
    queryParams.value.categoryPath = null;
  }
  handleQuery();
}

/** 获取分类树 */
function getCategoryTree() {
  getCategoryTreeSelect().then((response) => {
    categoryOptions.value = response.data || [];
  }).catch(() => {
    categoryOptions.value = [];
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value.pageNum = 1;
  queryParams.value.pageSize = 12;
  queryParams.value.title = null;
  queryParams.value.categoryId = null;
  queryParams.value.categoryPath = null;
  queryParams.value.priceRange = null;
  queryParams.value.minPrice = null;
  queryParams.value.maxPrice = null;
  handleQuery();
}

/** 查看商品详情 */
function handleView(row) {
  getProductDetail(row.productId).then((response) => {
    // 实时更新列表中的浏览次数
    const product = productList.value.find(p => p.productId === row.productId);
    if (product && response.data) {
      product.viewCount = response.data.viewCount || product.viewCount;
    }
    
    viewProduct.value = response.data;
    viewOpen.value = true;
  });
}

/** 切换收藏状态 */
function handleToggleFavorite(product) {
  if (!getToken()) {
    ElMessage.warning('Please login first');
    return;
  }
  
  if (!product || !product.productId) {
    return;
  }
  
  favoriteLoading.value = true;
  toggleFavorite(product.productId)
    .then((response) => {
      const wasFavorite = true; // 收藏页面中的商品都是已收藏的
      const isFavorite = response.data === true;
      
      if (!isFavorite) {
        // 取消收藏成功，从列表中移除
        ElMessage.success('Successfully removed from favorites');
        // 重新加载列表
        getList();
        // 如果详情对话框打开，关闭它
        if (viewOpen.value && viewProduct.value && viewProduct.value.productId === product.productId) {
          viewOpen.value = false;
        }
      } else {
        ElMessage.success('Successfully added to favorites');
      }
    })
    .catch((error) => {
      const errorMsg = error.msg || error.message || 'Operation failed';
      ElMessage.error(errorMsg);
    })
    .finally(() => {
      favoriteLoading.value = false;
    });
}

/** 立即购买 */
function handleBuyNow(product) {
  if (!getToken()) {
    ElMessage.warning('Please login first');
    return;
  }
  
  if (!product || !product.productId) {
    return;
  }
  
  // 跳转到订单页面，传递商品ID
  router.push({
    path: '/product/order',
    query: {
      productId: product.productId
    }
  });
}

onMounted(() => {
  getCategoryTree();
  getList();
});
</script>

<style scoped>
.favorite-container {
  padding: 20px 24px 32px;
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 24px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.header-content {
  flex: 1;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.title-icon {
  font-size: 28px;
  color: #fff;
}

.title-text {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
}

.header-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin-left: 40px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 搜索筛选区域 */
.filter-card {
  border-radius: 12px;
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.filter-form {
  padding: 10px 0;
}

/* 商品列表区域 */
.products-section {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.products-container {
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
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #409eff;
}

/* 图片区域 */
.card-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 75%;
  overflow: hidden;
  background-color: #f5f7fa;
}

.card-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
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

/* 收藏按钮 */
.favorite-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 3;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.favorite-badge:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.favorite-icon {
  color: #f56c6c;
  transition: all 0.3s ease;
}

.favorite-icon.is-favorite {
  color: #f56c6c;
  animation: pulse 0.5s ease;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

/* 成色标签 */
.condition-badge {
  position: absolute;
  bottom: 8px;
  left: 8px;
  z-index: 2;
}

/* 卡片内容区域 */
.card-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
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
  cursor: pointer;
  transition: color 0.3s;
}

.card-title:hover {
  color: #409eff;
}

/* 类别和卖家 */
.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #909399;
  flex-wrap: wrap;
}

.card-meta .category,
.card-meta .seller {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-meta .el-icon {
  font-size: 14px;
}

/* 品牌和尺码 */
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
  margin-bottom: 4px;
}

.sale-price {
  font-size: 22px;
  font-weight: 700;
  color: #f56c6c;
  line-height: 1;
}

.orig-price {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
}

.price-save {
  font-size: 12px;
  color: #67c23a;
  font-weight: 500;
}

/* 统计信息 */
.card-stats {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  padding-top: 8px;
  border-top: 1px solid #f0f2f5;
  margin-top: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-item .el-icon {
  font-size: 14px;
}

/* 操作按钮 */
.card-actions {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  gap: 8px;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
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

.product-header-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-title-main {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.3;
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

.product-meta-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  color: #909399;
  font-weight: 500;
}

.meta-value {
  color: #303133;
  font-weight: 600;
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

.favorite-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .favorite-container {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .filter-form {
    flex-direction: column;
  }

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

