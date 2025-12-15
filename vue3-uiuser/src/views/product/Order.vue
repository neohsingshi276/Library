<template>
  <div class="order-container">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <el-icon class="title-icon"><ShoppingCart /></el-icon>
          <span class="title-text">Confirm Order</span>
        </div>
      </div>
    </div>

    <div v-loading="loading" class="order-content">
      <!-- Product Information -->
      <el-card shadow="never" class="order-card" v-if="product">
        <template #header>
          <div class="card-header">
            <span>Product Information</span>
          </div>
        </template>
        <div class="product-info">
          <div class="product-image">
            <el-image
              v-if="product.images"
              :src="product.images.split(',')[0]"
              fit="cover"
              style="width: 120px; height: 120px; border-radius: 8px;"
            />
            <div v-else class="no-image">
              <el-icon :size="40"><Picture /></el-icon>
            </div>
          </div>
          <div class="product-details">
            <div class="product-title">{{ product.title }}</div>
            <div class="product-meta">
              <span v-if="product.categoryName" class="meta-item">
                <el-icon><Folder /></el-icon>
                {{ product.categoryName }}
              </span>
              <span v-if="product.brand" class="meta-item">Brand: {{ product.brand }}</span>
              <span v-if="product.size" class="meta-item">Size: {{ product.size }}</span>
            </div>
            <div class="product-price">
              <span class="price-label">Price: </span>
              <span class="price-value">¥{{ product.salePrice || 0 }}</span>
              <span v-if="product.originalPrice" class="original-price">Original: ¥{{ product.originalPrice }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- Shipping Address -->
      <el-card shadow="never" class="order-card">
        <template #header>
          <div class="card-header">
            <span>Shipping Address</span>
            <el-button type="text" @click="showAddressDialog = true" v-if="addressList.length > 0">
              Manage Addresses
            </el-button>
            <el-button type="text" @click="showAddressDialog = true" v-else>
              Add Address
            </el-button>
          </div>
        </template>
        <div v-if="selectedAddress" class="address-info">
          <div class="address-content">
            <div class="address-main">
              <div class="address-line">
                <span class="receiver-name">{{ selectedAddress.receiverName }}</span>
                <span class="receiver-phone">{{ selectedAddress.receiverPhone }}</span>
                <el-tag v-if="selectedAddress.isDefault === '1'" type="warning" size="small" class="default-tag">Default</el-tag>
              </div>
              <div class="address-line address-location">
                <span class="location-text">
                  {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detailAddress }}
                </span>
              </div>
            </div>
            <div class="address-edit-btn" @click="showAddressDialog = true">
              <el-icon><Edit /></el-icon>
            </div>
          </div>
        </div>
        <div v-else class="no-address" @click="showAddressDialog = true">
          <el-icon><Location /></el-icon>
          <span>Please select a shipping address</span>
        </div>
      </el-card>

      <!-- Order Notes -->
      <el-card shadow="never" class="order-card">
        <template #header>
          <div class="card-header">
            <span>Order Notes</span>
          </div>
        </template>
        <el-input
          v-model="remark"
          type="textarea"
          :rows="3"
          placeholder="Optional: notes for this purchase (e.g., please ship as soon as possible)"
          maxlength="200"
          show-word-limit
        />
      </el-card>

      <!-- Order Summary -->
      <el-card shadow="never" class="order-card summary-card">
        <div class="summary-item">
          <span class="label">Product Amount</span>
          <span class="value">¥{{ product?.salePrice || 0 }}</span>
        </div>
        <div class="summary-item">
          <span class="label">Shipping</span>
          <span class="value">Free Shipping</span>
        </div>
        <el-divider />
        <div class="summary-total">
          <span class="label">Total Payment</span>
          <span class="total-value">¥{{ product?.salePrice || 0 }}</span>
        </div>
      </el-card>

      <!-- Submit Order Button -->
      <div class="order-footer">
        <div class="footer-left">
          <span class="total-text">Total: </span>
          <span class="total-amount">¥{{ product?.salePrice || 0 }}</span>
        </div>
        <el-button
          type="primary"
          size="large"
          :loading="submitting"
          :disabled="!selectedAddress"
          @click="handleSubmitOrder"
          style="width: 200px;"
        >
          Submit Order
        </el-button>
      </div>
    </div>

    <!-- Address Selection/Management Dialog -->
    <el-dialog
      v-model="showAddressDialog"
      title="Shipping Address"
      width="800px"
      append-to-body
    >
      <div class="address-dialog-content">
        <div class="address-list" v-if="addressList.length > 0">
          <div
            v-for="address in addressList"
            :key="address.addressId"
            class="address-item"
            :class="{ 'selected': selectedAddress && selectedAddress.addressId === address.addressId }"
            @click="selectAddress(address)"
          >
            <div class="address-item-content">
              <div class="address-item-main">
                <div class="address-item-line">
                  <span class="address-receiver-name">{{ address.receiverName }}</span>
                  <span class="address-receiver-phone">{{ address.receiverPhone }}</span>
                  <el-tag v-if="address.isDefault === '1'" type="warning" size="small" class="address-default-tag">Default</el-tag>
                </div>
                <div class="address-item-line address-item-location">
                  <span class="address-location-text">
                    {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}
                  </span>
                </div>
              </div>
              <div class="address-item-actions" @click.stop>
                <el-button type="text" size="small" @click="handleEditAddress(address)">Edit</el-button>
                <el-button type="text" size="small" @click="handleDeleteAddress(address.addressId)">Delete</el-button>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="No shipping addresses" :image-size="100">
          <el-button type="primary" @click="showAddDialog = true">Add Address</el-button>
        </el-empty>
      </div>
      <template #footer>
        <el-button @click="showAddressDialog = false">Cancel</el-button>
        <el-button type="primary" @click="showAddDialog = true">Add New Address</el-button>
      </template>
    </el-dialog>

    <!-- Add/Edit Address Dialog -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingAddress ? 'Edit Address' : 'Add Address'"
      width="640px"
      append-to-body
      @close="handleCancelAddress"
      class="address-form-dialog"
    >
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="90px" class="address-form">
        <el-form-item label="Recipient" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="Please enter recipient name" />
        </el-form-item>
        <el-form-item label="Phone" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="Please enter phone number" />
        </el-form-item>
        <el-form-item label="Province" prop="province">
          <el-input v-model="addressForm.province" placeholder="Please enter province" />
        </el-form-item>
        <el-form-item label="City" prop="city">
          <el-input v-model="addressForm.city" placeholder="Please enter city" />
        </el-form-item>
        <el-form-item label="District" prop="district">
          <el-input v-model="addressForm.district" placeholder="Please enter district" />
        </el-form-item>
        <el-form-item label="Detailed Address" prop="detailAddress">
          <el-input
            v-model="addressForm.detailAddress"
            type="textarea"
            :rows="3"
            placeholder="Please enter detailed address"
          />
        </el-form-item>
        <el-form-item label="Postal Code">
          <el-input v-model="addressForm.postalCode" placeholder="Optional" />
        </el-form-item>
        <el-form-item label="Set as Default">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleCancelAddress">Cancel</el-button>
        <el-button type="primary" @click="handleSaveAddress">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Order">
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ShoppingCart, Picture, Folder, Location, Edit } from '@element-plus/icons-vue';
import { getProductDetail } from '@/api/product';
import { getAddressList, addAddress, updateAddress, delAddress, setDefaultAddress } from '@/api/address';
import { createOrder } from '@/api/order';
import { getToken } from '@/utils/auth';

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const submitting = ref(false);
const product = ref(null);
const addressList = ref([]);
const selectedAddress = ref(null);
const remark = ref('');
const showAddressDialog = ref(false);
const showAddDialog = ref(false);
const editingAddress = ref(null);
const addressFormRef = ref();

// Address Form
const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  postalCode: '',
  isDefault: false
});

// Address Validation Rules
const addressRules = {
  receiverName: [{ required: true, message: 'Please enter recipient name', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: 'Please enter phone number', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: 'Please enter a valid phone number', trigger: 'blur' }
  ],
  province: [{ required: true, message: 'Please enter province', trigger: 'blur' }],
  city: [{ required: true, message: 'Please enter city', trigger: 'blur' }],
  district: [{ required: true, message: 'Please enter district', trigger: 'blur' }],
  detailAddress: [{ required: true, message: 'Please enter detailed address', trigger: 'blur' }]
};

// Load Product Information
function loadProduct() {
  const productId = route.query.productId;
  if (!productId) {
    ElMessage.error('Product ID does not exist');
    router.back();
    return;
  }

  loading.value = true;
  getProductDetail(productId)
    .then(response => {
      product.value = response.data;
    })
    .catch(error => {
      ElMessage.error('Failed to load product information');
      router.back();
    })
    .finally(() => {
      loading.value = false;
    });
}

// Load Address List
function loadAddressList() {
  getAddressList()
    .then(response => {
      addressList.value = response.data || [];
      // Automatically select default address
      const defaultAddress = addressList.value.find(addr => addr.isDefault === '1');
      if (defaultAddress) {
        selectedAddress.value = defaultAddress;
      } else if (addressList.value.length > 0) {
        selectedAddress.value = addressList.value[0];
      }
    })
    .catch(() => {
      addressList.value = [];
    });
}

// Select Address
function selectAddress(address) {
  selectedAddress.value = address;
  showAddressDialog.value = false;
}

// Edit Address
function handleEditAddress(address) {
  editingAddress.value = address;
  addressForm.receiverName = address.receiverName;
  addressForm.receiverPhone = address.receiverPhone;
  addressForm.province = address.province || '';
  addressForm.city = address.city || '';
  addressForm.district = address.district || '';
  addressForm.detailAddress = address.detailAddress;
  addressForm.postalCode = address.postalCode || '';
  addressForm.isDefault = address.isDefault === '1';
  showAddDialog.value = true;
}

// Delete Address
function handleDeleteAddress(addressId) {
  ElMessageBox.confirm('Are you sure you want to delete this shipping address?', 'Prompt', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  }).then(() => {
    delAddress(addressId)
      .then(() => {
        ElMessage.success('Deleted successfully');
        loadAddressList();
        if (selectedAddress.value && selectedAddress.value.addressId === addressId) {
          selectedAddress.value = null;
        }
      })
      .catch(() => {
        ElMessage.error('Deletion failed');
      });
  }).catch(() => {});
}

// Save Address
function handleSaveAddress() {
  addressFormRef.value.validate(valid => {
    if (!valid) return;

    const addressData = {
      receiverName: addressForm.receiverName,
      receiverPhone: addressForm.receiverPhone,
      province: addressForm.province,
      city: addressForm.city,
      district: addressForm.district,
      detailAddress: addressForm.detailAddress,
      postalCode: addressForm.postalCode || '',
      isDefault: addressForm.isDefault ? '1' : '0'
    };

    const request = editingAddress.value
      ? updateAddress({ ...addressData, addressId: editingAddress.value.addressId })
      : addAddress(addressData);

    request
      .then(() => {
        ElMessage.success(editingAddress.value ? 'Modified successfully' : 'Added successfully');
        showAddDialog.value = false;
        resetAddressForm();
        loadAddressList();
      })
      .catch(() => {
        ElMessage.error(editingAddress.value ? 'Modification failed' : 'Addition failed');
      });
  });
}

// Reset Address Form
function resetAddressForm() {
  editingAddress.value = null;
  addressForm.receiverName = '';
  addressForm.receiverPhone = '';
  addressForm.province = '';
  addressForm.city = '';
  addressForm.district = '';
  addressForm.detailAddress = '';
  addressForm.postalCode = '';
  addressForm.isDefault = false;
  if (addressFormRef.value) {
    addressFormRef.value.resetFields();
    addressFormRef.value.clearValidate();
  }
}

// Cancel Address Edit/Add
function handleCancelAddress() {
  resetAddressForm();
  showAddDialog.value = false;
}

// Submit Order
function handleSubmitOrder() {
  if (!selectedAddress.value) {
    ElMessage.warning('Please select a shipping address');
    return;
  }

  submitting.value = true;
  createOrder({
    productId: product.value.productId,
    addressId: selectedAddress.value.addressId,
    remark: remark.value
  })
    .then(response => {
      ElMessage.success('Order created successfully');
      // Jump to payment page
      router.push({
        path: '/product/payment',
        query: {
          orderNo: response.data.orderNo
        }
      });
    })
    .catch(error => {
      ElMessage.error(error.msg || 'Failed to create order');
    })
    .finally(() => {
      submitting.value = false;
    });
}

onMounted(() => {
  if (!getToken()) {
    ElMessage.warning('Please log in first');
    router.push('/login');
    return;
  }
  loadProduct();
  loadAddressList();
});
</script>

<style scoped>
.order-container {
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

.order-content {
  max-width: 1200px;
  margin: 0 auto;
}

.order-card {
  margin-bottom: 20px;
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

.product-info {
  display: flex;
  gap: 20px;
}

.product-image {
  flex-shrink: 0;
}

.no-image {
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
  color: #c0c4cc;
}

.product-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.product-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.product-price {
  font-size: 16px;
}

.price-label {
  color: #909399;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
  margin: 0 8px;
}

.original-price {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
}

.address-info {
  padding: 0;
  background: #fff;
  border-radius: 0;
  border: none;
}

.address-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: #fff;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.address-content:hover {
  border-color: #409eff;
}

.address-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.address-line {
  display: flex;
  align-items: center;
  gap: 12px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-right: 4px;
}

.receiver-phone {
  font-size: 14px;
  color: #606266;
  margin-right: 8px;
}

.default-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 2px;
}

.address-location {
  margin-top: 4px;
}

.location-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  word-break: break-all;
}

.address-edit-btn {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  margin-left: 16px;
}

.address-edit-btn:hover {
  background: #f5f7fa;
  color: #409eff;
}

.no-address {
  padding: 40px;
  text-align: center;
  color: #909399;
  cursor: pointer;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  transition: all 0.3s;
}

.no-address:hover {
  border-color: #409eff;
  color: #409eff;
}

.no-address .el-icon {
  font-size: 48px;
  margin-bottom: 12px;
  display: block;
}

.address-dialog-content {
  max-height: 500px;
  overflow-y: auto;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-item {
  padding: 0;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 12px;
  overflow: hidden;
}

.address-item:hover {
  border-color: #409eff;
}

.address-item.selected {
  border-color: #f56c6c;
  background: #fef0f0;
}

.address-item-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
}

.address-item-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.address-item-line {
  display: flex;
  align-items: center;
  gap: 12px;
}

.address-receiver-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-right: 4px;
}

.address-receiver-phone {
  font-size: 14px;
  color: #606266;
  margin-right: 8px;
}

.address-default-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 2px;
}

.address-item-location {
  margin-top: 4px;
}

.address-location-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  word-break: break-all;
}

.address-item-actions {
  flex-shrink: 0;
  display: flex;
  gap: 8px;
  margin-left: 16px;
}

/* Address Form Dialog Styles */
.address-form-dialog :deep(.el-dialog__body) {
  padding: 24px 32px;
}

.address-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.address-form :deep(.el-form-item__label) {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  padding-right: 12px;
}

.address-form :deep(.el-input__inner),
.address-form :deep(.el-textarea__inner) {
  font-size: 14px;
  color: #303133;
  border-color: #dcdfe6;
  border-radius: 4px;
}

.address-form :deep(.el-input__inner):focus,
.address-form :deep(.el-textarea__inner):focus {
  border-color: #409eff;
}

.address-form :deep(.el-switch) {
  --el-switch-on-color: #409eff;
}

.summary-card {
  background: #fafafa;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  font-size: 14px;
}

.summary-item .label {
  color: #606266;
}

.summary-item .value {
  color: #303133;
  font-weight: 500;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  padding: 16px 0 0;
  font-size: 18px;
}

.summary-total .label {
  color: #303133;
  font-weight: 600;
}

.total-value {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.order-footer {
  position: sticky;
  bottom: 0;
  background: #fff;
  padding: 20px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  margin-top: 20px;
  border-radius: 12px;
}

.footer-left {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-text {
  font-size: 14px;
  color: #606266;
}

.total-amount {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}
</style>