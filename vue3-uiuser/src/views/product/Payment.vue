<template>
  <div class="payment-container">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <el-icon class="title-icon"><CreditCard /></el-icon>
          <span class="title-text">Order Payment</span>
        </div>
        <div class="header-subtitle" v-if="order">Order No.: {{ order.orderNo }}</div>
      </div>
    </div>

    <div v-loading="loading" class="payment-content">
      <!-- Order Information -->
      <el-card shadow="never" class="payment-card" v-if="order">
        <template #header>
          <div class="card-header">
            <span>Order Information</span>
            <el-tag :type="getOrderStatusType(order.orderStatus)" size="large">
              {{ getOrderStatusText(order.orderStatus) }}
            </el-tag>
          </div>
        </template>

        <!-- Product Information -->
        <div class="order-product">
          <div class="product-image">
            <el-image
              v-if="order.productImage"
              :src="order.productImage"
              fit="cover"
              style="width: 100px; height: 100px; border-radius: 8px;"
            />
            <div v-else class="no-image">
              <el-icon :size="32"><Picture /></el-icon>
            </div>
          </div>
          <div class="product-info">
            <div class="product-title">{{ order.productTitle }}</div>
            <div class="product-price">
              <span class="price-label">Unit Price: </span>
              <span class="price-value">¥{{ order.salePrice || 0 }}</span>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- Shipping Information -->
        <div class="order-section">
          <div class="section-title">Shipping Information</div>
          <div class="section-content">
            <div class="info-row">
              <span class="label">Recipient: </span>
              <span class="value">{{ order.receiverName }}</span>
            </div>
            <div class="info-row">
              <span class="label">Contact Phone: </span>
              <span class="value">{{ order.receiverPhone }}</span>
            </div>
            <div class="info-row">
              <span class="label">Shipping Address: </span>
              <span class="value">
                {{ order.receiverProvince }} {{ order.receiverCity }} {{ order.receiverDistrict }} {{ order.receiverDetailAddress }}
              </span>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- Order Amount -->
        <div class="order-section">
          <div class="section-title">Order Amount</div>
          <div class="section-content">
            <div class="amount-row">
              <span class="label">Product Amount: </span>
              <span class="value">¥{{ order.salePrice || 0 }}</span>
            </div>
            <div class="amount-row">
              <span class="label">Shipping: </span>
              <span class="value">Free Shipping</span>
            </div>
            <el-divider />
            <div class="amount-total">
              <span class="label">Total Payment: </span>
              <span class="total-value">¥{{ order.orderAmount || 0 }}</span>
            </div>
          </div>
        </div>

        <!-- Payment Methods (Only shown for pending orders) -->
        <div v-if="order.orderStatus === 'pending'" class="payment-methods">
          <div class="section-title">Select Payment Method</div>
          <div class="methods-list">
            <div
              v-for="method in paymentMethods"
              :key="method.value"
              class="method-item"
              :class="{ 'selected': selectedPayMethod === method.value }"
              @click="selectedPayMethod = method.value"
            >
              <div class="method-icon">
                <el-icon :size="32"><CreditCard /></el-icon>
              </div>
              <div class="method-info">
                <div class="method-name">{{ method.label }}</div>
                <div class="method-desc">{{ method.desc }}</div>
              </div>
              <div class="method-radio">
                <el-radio :model-value="selectedPayMethod === method.value" />
              </div>
            </div>
          </div>
        </div>

        <!-- Payment Button -->
        <div v-if="order.orderStatus === 'pending'" class="payment-actions">
          <el-button
            type="primary"
            size="large"
            :loading="paying"
            :disabled="!selectedPayMethod"
            @click="handlePay"
            style="width: 100%; height: 50px; font-size: 16px;"
          >
            Pay Now ¥{{ order.orderAmount || 0 }}
          </el-button>
        </div>

        <!-- Paid Information -->
        <div v-if="order.orderStatus === 'paid'" class="paid-info">
          <el-result icon="success" title="Payment Successful" sub-title="Your order has been paid successfully, please wait for the seller to ship">
            <template #extra>
              <el-button type="primary" @click="router.push('/order/list')">View Orders</el-button>
            </template>
          </el-result>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup name="Payment">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { CreditCard, Picture } from '@element-plus/icons-vue';
import { getOrderByNo, payOrder } from '@/api/order';
import { getToken } from '@/utils/auth';

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const paying = ref(false);
const order = ref(null);
const selectedPayMethod = ref('alipay');

const paymentMethods = [
  {
    value: 'alipay',
    label: 'Alipay',
    desc: 'Secure and fast payment method',
    icon: CreditCard
  },
  {
    value: 'wechat',
    label: 'WeChat Pay',
    desc: 'WeChat secure payment',
    icon: CreditCard
  }
];

// Get order status text
function getOrderStatusText(status) {
  const statusMap = {
    'pending': 'Pending Payment',
    'paid': 'Paid',
    'shipped': 'Shipped',
    'completed': 'Completed',
    'cancelled': 'Cancelled'
  };
  return statusMap[status] || status;
}

// Get order status type
function getOrderStatusType(status) {
  const typeMap = {
    'pending': 'warning',
    'paid': 'success',
    'shipped': 'primary',
    'completed': 'success',
    'cancelled': 'info'
  };
  return typeMap[status] || 'info';
}

// Load order information
function loadOrder() {
  const orderNo = route.query.orderNo;
  if (!orderNo) {
    ElMessage.error('Order number does not exist');
    router.back();
    return;
  }

  loading.value = true;
  getOrderByNo(orderNo)
    .then(response => {
      order.value = response.data;
    })
    .catch(error => {
      ElMessage.error('Failed to load order information');
      router.back();
    })
    .finally(() => {
      loading.value = false;
    });
}

// Pay order
function handlePay() {
  if (!selectedPayMethod.value) {
    ElMessage.warning('Please select a payment method');
    return;
  }

  paying.value = true;
  payOrder(order.value.orderNo, selectedPayMethod.value)
    .then(() => {
      ElMessage.success('Payment successful');
      // Reload order information
      loadOrder();
    })
    .catch(error => {
      ElMessage.error(error.msg || 'Payment failed');
    })
    .finally(() => {
      paying.value = false;
    });
}

onMounted(() => {
  if (!getToken()) {
    ElMessage.warning('Please log in first');
    router.push('/login');
    return;
  }
  loadOrder();
});
</script>

<style scoped>
.payment-container {
  padding: 20px 24px 32px;
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

.page-header {
  margin-bottom: 20px;
  padding: 24px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
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

.payment-content {
  max-width: 900px;
  margin: 0 auto;
}

.payment-card {
  border-radius: 12px;
  border: 1px solid #ebeef5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.order-product {
  display: flex;
  gap: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.product-image {
  flex-shrink: 0;
}

.no-image {
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
  color: #c0c4cc;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.product-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.product-price {
  font-size: 14px;
}

.price-label {
  color: #909399;
}

.price-value {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
  margin-left: 8px;
}

.order-section {
  margin: 16px 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.section-content {
  padding-left: 8px;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-row .label {
  width: 100px;
  color: #909399;
  flex-shrink: 0;
}

.info-row .value {
  color: #303133;
  flex: 1;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.amount-row .label {
  color: #606266;
}

.amount-row .value {
  color: #303133;
  font-weight: 500;
}

.amount-total {
  display: flex;
  justify-content: space-between;
  padding-top: 12px;
  font-size: 18px;
}

.amount-total .label {
  color: #303133;
  font-weight: 600;
}

.total-value {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.payment-methods {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.methods-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.method-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.method-item:hover {
  border-color: #409eff;
}

.method-item.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.method-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
  color: #409eff;
}

.method-info {
  flex: 1;
}

.method-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.method-desc {
  font-size: 13px;
  color: #909399;
}

.method-radio {
  flex-shrink: 0;
}

.payment-actions {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.paid-info {
  margin-top: 24px;
  padding: 24px;
  background: #f0f9ff;
  border-radius: 8px;
}
</style>