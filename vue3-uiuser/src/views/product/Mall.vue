<template>
  <div class="mall-container">
    <!-- Search and Filter Area -->
    <div class="search-filter-section">
      <el-card shadow="never" class="filter-card">
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
              <el-option label="¥500+" value="500+" />
            </el-select>
          </el-form-item>
          <el-form-item label="Sort" prop="sortBy">
            <el-select
              v-model="queryParams.sortBy"
              placeholder="Sort By"
              style="width: 150px"
            >
              <el-option label="Latest" value="createTime" />
              <el-option label="Price: Low to High" value="priceAsc" />
              <el-option label="Price: High to Low" value="priceDesc" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">Search</el-button>
            <el-button icon="Refresh" @click="resetQuery">Reset</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- Product List Area -->
    <div class="products-section">
      <div v-loading="loading" class="products-container">
        <el-empty
          v-if="!loading && (!productList || productList.length === 0)"
          description="No products"
          :image-size="120"
        />
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
              @click="handleView(item)"
            >
              <!-- Product Image -->
              <div class="card-image-wrapper">
                <div class="card-image">
                  <el-image
                    v-if="item.images"
                    :src="item.images.split(',')[0]"
                    :preview-src-list="[]"
                    fit="cover"
                    class="cover"
                    lazy
                    :preview-disabled="true"
                    @click.stop
                  />
                  <div v-else class="no-image-placeholder">
                    <el-icon :size="40"><Picture /></el-icon>
                    <span>No Image</span>
                  </div>
                </div>
                <!-- Favorite Button -->
                <div 
                  class="favorite-badge" 
                  v-if="getToken()"
                  @click.stop="handleToggleFavorite(item)"
                >
                  <el-icon 
                    :class="['favorite-icon', { 'is-favorite': favoriteStatusMap.get(item.productId) }]"
                    :size="24"
                    v-loading="favoriteLoadingMap.get(item.productId)"
                  >
                    <StarFilled v-if="favoriteStatusMap.get(item.productId)" />
                    <Star v-else />
                  </el-icon>
                </div>
                <!-- Condition Badge -->
                <div class="condition-badge" v-if="item.conditionLevel">
                  <el-tag size="small" effect="dark">{{ item.conditionLevel }}</el-tag>
                </div>
              </div>

              <!-- Product Information -->
              <div class="card-body">
                <!-- Title -->
                <div class="card-title" :title="item.title">{{ item.title }}</div>

                <!-- Category and Seller -->
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

                <!-- Brand and Size -->
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

                <!-- Price -->
                <div class="price-section">
                  <div class="price-row">
                    <span class="sale-price">¥{{ item.salePrice || 0 }}</span>
                    <span v-if="item.originalPrice" class="orig-price">¥{{ item.originalPrice }}</span>
                  </div>
                  <div class="price-save" v-if="item.originalPrice && item.originalPrice > item.salePrice">
                    Save ¥{{ (item.originalPrice - item.salePrice).toFixed(2) }}
                  </div>
                </div>

                <!-- Statistics -->
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

                <!-- Action Buttons -->
                <div class="card-actions" v-if="getToken()">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click.stop="handleBuyNow(item)"
                    class="action-btn buy-btn"
                  >
                    Buy Now
                  </el-button>
                  <el-button 
                    type="default" 
                    size="small" 
                    @click.stop="handleView(item)"
                    class="action-btn view-btn"
                  >
                    View Details
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="total > 0">
        <pagination
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </div>

    <!-- Product Details Dialog -->
    <el-dialog
      title="Product Details"
      v-model="viewOpen"
      width="900px"
      append-to-body
      class="product-detail-dialog"
    >
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
            <div class="product-price-display">
              <span class="price-label">Price:</span>
              <span class="price-value">¥{{ viewProduct.salePrice || 0 }}</span>
              <span class="original-price-value" v-if="viewProduct.originalPrice">
                Original: ¥{{ viewProduct.originalPrice }}
              </span>
            </div>
            <div class="product-meta-info">
              <div class="meta-item">
                <span class="meta-label">Category:</span>
                <span class="meta-value">{{ viewProduct.categoryName || 'Uncategorized' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">Seller:</span>
                <span class="meta-value">{{ viewProduct.sellerName || 'ID: ' + viewProduct.sellerId }}</span>
              </div>
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
              <div class="info-label">Published Time</div>
              <div class="info-value">{{ viewProduct.createTime }}</div>
            </div>
          </div>
        </div>

        <!-- Favorite Button -->
        <div class="favorite-section" v-if="getToken()">
          <el-button
            :type="isFavorite ? 'warning' : 'primary'"
            :icon="isFavorite ? StarFilled : Star"
            :loading="favoriteLoading"
            @click="handleToggleFavorite(viewProduct)"
            size="large"
            style="width: 100%; margin-top: 20px;"
          >
            {{ isFavorite ? 'Favorited' : 'Add to Favorites' }}
          </el-button>
        </div>

        <!-- Initiate Exchange Request Button -->
        <div class="exchange-section" v-if="getToken() && viewProduct && viewProduct.status === 'published'">
          <el-button
            type="success"
            icon="Switch"
            :loading="exchangeLoading"
            @click="handleOpenExchangeDialog"
            size="large"
            style="width: 100%; margin-top: 10px;"
          >
            Initiate Exchange Request
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- Initiate Exchange Request Dialog -->
    <el-dialog
      title="Initiate Exchange Request"
      v-model="exchangeDialogOpen"
      width="800px"
      append-to-body
      @close="handleCloseExchangeDialog"
    >
      <div v-if="exchangeDialogOpen">
        <el-alert
          title="Please select the product you want to exchange"
          type="info"
          :closable="false"
          style="margin-bottom: 20px;"
        />
        <div v-loading="myProductsLoading" style="min-height: 300px;">
          <el-empty
            v-if="!myProductsLoading && (!myProductsList || myProductsList.length === 0)"
            description="You don't have any published products yet, cannot initiate exchange"
            :image-size="100"
          />
          <el-row v-else :gutter="20">
            <el-col
              v-for="item in myProductsList"
              :key="item.productId"
              :span="8"
              :xs="24"
              :sm="12"
              style="margin-bottom: 20px;"
            >
              <el-card
                shadow="hover"
                class="product-select-card"
                :class="{ 'selected': selectedProductId === item.productId }"
                @click="handleSelectProduct(item)"
              >
                <div class="product-select-image">
                  <el-image
                    v-if="item.images"
                    :src="item.images.split(',')[0]"
                    fit="cover"
                    style="width: 100%; height: 150px; border-radius: 4px;"
                  />
                  <div v-else class="no-image-placeholder-small">
                    <el-icon :size="30"><Picture /></el-icon>
                  </div>
                </div>
                <div class="product-select-info">
                  <div class="product-select-title">{{ item.title }}</div>
                  <div class="product-select-price">¥{{ item.salePrice || 0 }}</div>
                </div>
                <div v-if="selectedProductId === item.productId" class="selected-badge">
                  <el-icon><Check /></el-icon>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="exchangeDialogOpen = false">Cancel</el-button>
          <el-button
            type="primary"
            :disabled="!selectedProductId || exchangeSubmitting"
            :loading="exchangeSubmitting"
            @click="handleSubmitExchange"
          >
            Confirm Exchange Request
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Mall">
import { getCurrentInstance, ref, reactive, toRefs, onMounted, watch } from "vue";
import {
  Picture,
  Folder,
  User,
  View,
  Star,
  StarFilled,
  Search,
  Refresh,
  Switch,
  Check
} from "@element-plus/icons-vue";
import { ElMessage } from 'element-plus';
import { listPublishedProducts, getProductDetail, getCategoryTreeSelect, toggleFavorite, getFavoriteStatus, listMyPublishedProduct } from "@/api/product";
import { createExchangeRequest } from "@/api/exchange";
import { getToken } from '@/utils/auth';
import { useRouter } from 'vue-router';

const { proxy } = getCurrentInstance();
const router = useRouter();

const productList = ref([]);
const total = ref(0);
const loading = ref(true);
const viewOpen = ref(false);
const viewProduct = ref(null);
const categoryOptions = ref([]);
const isFavorite = ref(false);
const favoriteLoading = ref(false);
// Store favorite status for each product
const favoriteStatusMap = ref(new Map());
// Store product IDs that are loading favorite status
const favoriteLoadingMap = ref(new Map());

// Exchange related
const exchangeDialogOpen = ref(false);
const exchangeLoading = ref(false);
const exchangeSubmitting = ref(false);
const selectedProductId = ref(null);
const myProductsList = ref([]);
const myProductsLoading = ref(false);

// Cascader configuration
const cascaderProps = {
  value: 'id',
  label: 'label',
  children: 'children',
  checkStrictly: false, // Can only select the last level
  emitPath: true // Return full path
};

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 12,
    title: null,
    categoryId: null,
    categoryPath: null, // Cascader value (array)
    priceRange: null,
    sortBy: "createTime"
  }
});

const { queryParams } = toRefs(data);

/** Query product list */
function getList() {
  loading.value = true;
  
  // Process price range
  const params = { ...queryParams.value };
  if (params.priceRange) {
    const [min, max] = params.priceRange.split(/[-+]/);
    if (min) params.minPrice = parseFloat(min);
    if (max && max !== '+') params.maxPrice = parseFloat(max);
    delete params.priceRange;
  }

  // Process category: get the last value from cascader path
  if (params.categoryPath && Array.isArray(params.categoryPath) && params.categoryPath.length > 0) {
    // Ensure conversion to number type
    const lastCategoryId = params.categoryPath[params.categoryPath.length - 1];
    if (lastCategoryId != null && lastCategoryId !== '') {
      params.categoryId = typeof lastCategoryId === 'string' ? parseInt(lastCategoryId, 10) : Number(lastCategoryId);
    } else {
      params.categoryId = null;
    }
  } else {
    params.categoryId = null; // Clear category ID
  }
  delete params.categoryPath;

  // Process sorting
  if (params.sortBy === 'priceAsc') {
    params.orderBy = 'sale_price';
    params.order = 'asc';
  } else if (params.sortBy === 'priceDesc') {
    params.orderBy = 'sale_price';
    params.order = 'desc';
  } else {
    params.orderBy = 'create_time';
    params.order = 'desc';
  }
  delete params.sortBy;

  listPublishedProducts(params)
    .then((response) => {
      console.log('Product list response:', response);
      // Compatible with different response formats
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
      console.log('Product list:', productList.value, 'Total:', total.value);
      
      // If user is logged in, batch query favorite status
      if (getToken() && productList.value.length > 0) {
        checkAllFavoriteStatus();
      }
      
      loading.value = false;
    })
    .catch((error) => {
      console.error('Failed to query product list:', error);
      productList.value = [];
      total.value = 0;
      loading.value = false;
    });
}

/** Category change handling */
function handleCategoryChange(value) {
  queryParams.value.categoryPath = value;
  // If selection is cleared, ensure categoryId is also null
  if (!value || (Array.isArray(value) && value.length === 0)) {
    queryParams.value.categoryId = null;
    queryParams.value.categoryPath = null;
  }
  handleQuery();
}

/** Get category tree */
function getCategoryTree() {
  getCategoryTreeSelect().then((response) => {
    categoryOptions.value = response.data || [];
  }).catch(() => {
    categoryOptions.value = [];
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
  queryParams.value.pageNum = 1;
  queryParams.value.pageSize = 12;
  queryParams.value.categoryId = null;
  queryParams.value.categoryPath = null;
  queryParams.value.sortBy = "createTime";
  handleQuery();
}

/** View product details */
function handleView(row) {
  getProductDetail(row.productId).then((response) => {
    // Update view count in list in real-time
    const product = productList.value.find(p => p.productId === row.productId);
    if (product && response.data) {
      product.viewCount = response.data.viewCount || product.viewCount;
    }
    
    viewProduct.value = response.data;
    viewOpen.value = true;
    
    // If user is logged in, query favorite status
    if (getToken()) {
      checkFavoriteStatus(row.productId);
    } else {
      isFavorite.value = false;
    }
  });
}

/** Check favorite status of a single product */
function checkFavoriteStatus(productId) {
  if (!getToken() || !productId) {
    isFavorite.value = false;
    return;
  }
  
  getFavoriteStatus(productId).then((response) => {
    const favorite = response.data === true;
    isFavorite.value = favorite;
    favoriteStatusMap.value.set(productId, favorite);
  }).catch(() => {
    isFavorite.value = false;
    favoriteStatusMap.value.set(productId, false);
  });
}

/** Batch check favorite status of all products */
function checkAllFavoriteStatus() {
  if (!getToken() || productList.value.length === 0) {
    return;
  }
  
  // Batch query favorite status
  const promises = productList.value.map(item => {
    return getFavoriteStatus(item.productId)
      .then(response => {
        favoriteStatusMap.value.set(item.productId, response.data === true);
      })
      .catch(() => {
        favoriteStatusMap.value.set(item.productId, false);
      });
  });
  
  Promise.all(promises).catch(() => {
    // Silently handle errors
  });
}

/** Open exchange request dialog */
function handleOpenExchangeDialog() {
  if (!getToken()) {
    ElMessage.warning('Please log in first');
    return;
  }
  
  // Check if it's a product published by self
  if (viewProduct.value && viewProduct.value.sellerId) {
    // Need to get current user ID here, skip check for now, backend will validate
  }
  
  exchangeDialogOpen.value = true;
  selectedProductId.value = null;
  loadMyProducts();
}

/** Close exchange request dialog */
function handleCloseExchangeDialog() {
  exchangeDialogOpen.value = false;
  selectedProductId.value = null;
  myProductsList.value = [];
}

/** Load my published products list */
function loadMyProducts() {
  myProductsLoading.value = true;
  listMyPublishedProduct({
    pageNum: 1,
    pageSize: 100
  }).then((response) => {
    // Response format: { rows: [], total: 0 } or { data: { rows: [], total: 0 } }
    if (response && response.rows) {
      myProductsList.value = response.rows || [];
    } else if (response && response.data && response.data.rows) {
      myProductsList.value = response.data.rows || [];
    } else {
      myProductsList.value = [];
    }
    myProductsLoading.value = false;
  }).catch((error) => {
    console.error('Failed to load my products:', error);
    myProductsList.value = [];
    myProductsLoading.value = false;
    ElMessage.error(error.msg || 'Failed to load product list');
  });
}

/** Select product to exchange */
function handleSelectProduct(product) {
  selectedProductId.value = product.productId;
}

/** Submit exchange request */
function handleSubmitExchange() {
  if (!selectedProductId.value || !viewProduct.value) {
    ElMessage.warning('Please select a product to exchange');
    return;
  }
  
  exchangeSubmitting.value = true;
  createExchangeRequest({
    receiverProductId: viewProduct.value.productId,
    requesterProductId: selectedProductId.value
  }).then(() => {
    ElMessage.success('Exchange request submitted successfully');
    exchangeDialogOpen.value = false;
    selectedProductId.value = null;
  }).catch((error) => {
    ElMessage.error(error.msg || 'Failed to submit exchange request');
  }).finally(() => {
    exchangeSubmitting.value = false;
  });
}

/** Toggle favorite status */
function handleToggleFavorite(product) {
  if (!getToken()) {
    ElMessage.warning('Please log in first');
    return;
  }
  
  const productId = product?.productId || viewProduct.value?.productId;
  if (!productId) {
    return;
  }
  
  // Set loading status
  favoriteLoadingMap.value.set(productId, true);
  if (viewProduct.value && viewProduct.value.productId === productId) {
    favoriteLoading.value = true;
  }
  
  const wasFavorite = favoriteStatusMap.value.get(productId) || false;
  
  toggleFavorite(productId)
    .then((response) => {
      // Backend returns boolean type in data, indicating whether currently favorited
      const isNowFavorite = response.data === true;
      
      // Update favorite status
      favoriteStatusMap.value.set(productId, isNowFavorite);
      
      // If operating in detail page, update detail page status
      if (viewProduct.value && viewProduct.value.productId === productId) {
        isFavorite.value = isNowFavorite;
        
        // Update favorite count in detail page
        if (!wasFavorite && isNowFavorite) {
          viewProduct.value.favoriteCount = (viewProduct.value.favoriteCount || 0) + 1;
          ElMessage.success('Favorited successfully');
        } else if (wasFavorite && !isNowFavorite) {
          viewProduct.value.favoriteCount = Math.max((viewProduct.value.favoriteCount || 1) - 1, 0);
          ElMessage.success('Unfavorited successfully');
        }
      } else {
        // Operating in list page
        if (!wasFavorite && isNowFavorite) {
          ElMessage.success('Favorited successfully');
        } else if (wasFavorite && !isNowFavorite) {
          ElMessage.success('Unfavorited successfully');
        }
      }
      
      // Update favorite count and status in list
      const listProduct = productList.value.find(p => p.productId === productId);
      if (listProduct) {
        if (!wasFavorite && isNowFavorite) {
          listProduct.favoriteCount = (listProduct.favoriteCount || 0) + 1;
        } else if (wasFavorite && !isNowFavorite) {
          listProduct.favoriteCount = Math.max((listProduct.favoriteCount || 1) - 1, 0);
        }
      }
    })
    .catch((error) => {
      const errorMsg = error.msg || error.message || 'Operation failed';
      ElMessage.error(errorMsg);
    })
    .finally(() => {
      favoriteLoadingMap.value.set(productId, false);
      if (viewProduct.value && viewProduct.value.productId === productId) {
        favoriteLoading.value = false;
      }
    });
}

/** Buy now */
function handleBuyNow(product) {
  if (!getToken()) {
    ElMessage.warning('Please log in first');
    return;
  }
  
  if (!product || !product.productId) {
    return;
  }
  
  // Navigate to order page, pass product ID
  router.push({
    path: '/product/order',
    query: {
      productId: product.productId
    }
  });
}

// Watch sort changes
watch(
  () => queryParams.value.sortBy,
  () => {
    handleQuery();
  }
);

onMounted(() => {
  getCategoryTree();
  getList();
});
</script>

<style scoped>
.mall-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

/* Search and filter area */
.search-filter-section {
  margin-bottom: 20px;
}

.filter-card {
  border-radius: 8px;
}

.filter-form {
  padding: 10px 0;
}

/* Product list area */
.products-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
}

.products-container {
  min-height: 400px;
}

/* Product card styles */
.product-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* Image area */
.card-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 75%; /* 4:3 ratio */
  overflow: hidden;
  background-color: #f5f7fa;
}

.card-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  cursor: default;
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

/* Favorite button */
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
  background: rgba(255, 255, 255, 1);
}

.favorite-icon {
  color: #909399;
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

/* Condition badge */
.condition-badge {
  position: absolute;
  bottom: 8px;
  left: 8px;
  z-index: 2;
}

/* Card content area */
.card-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* Title */
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

/* Category and seller */
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

/* Brand and size */
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

/* Price area */
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

/* Statistics */
.card-stats {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  padding-top: 8px;
  border-top: 1px solid #f0f2f5;
  margin-top: 8px;
}

/* Action buttons */
.card-actions {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.action-btn {
  flex: 1;
  max-width: 50%;
}

.buy-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #fff;
  font-weight: 600;
}

.buy-btn:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3f8f 100%);
  color: #fff;
}

.view-btn {
  border-color: #dcdfe6;
  color: #606266;
}

.view-btn:hover {
  border-color: #409eff;
  color: #409eff;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-item .el-icon {
  font-size: 14px;
}

/* Pagination */
.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* Exchange related styles */
.product-select-card {
  position: relative;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.product-select-card:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.product-select-card.selected {
  border-color: #67c23a;
  background-color: #f0f9ff;
}

.product-select-image {
  margin-bottom: 10px;
}

.product-select-info {
  text-align: center;
}

.product-select-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-select-price {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

.selected-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  background-color: #67c23a;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.no-image-placeholder-small {
  width: 100%;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #909399;
}

/* Product detail dialog styles */
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

/* Responsive adjustments */
@media (max-width: 768px) {
  .mall-container {
    padding: 10px;
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

