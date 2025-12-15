<template>
  <div class="return-list-container">
    <el-card shadow="never" class="return-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">Return/Refund Management</span>
        </div>
      </template>

      <!-- Status Filter (Full version: includes all return/refund statuses) -->
      <div class="status-tabs">
        <el-radio-group v-model="activeStatus" @change="handleStatusChange" size="default">
          <el-radio-button label="">All</el-radio-button>
          <el-radio-button label="pending">Pending Review</el-radio-button>
          <el-radio-button label="approved">Pending Processing</el-radio-button>
          <el-radio-button label="shipping">Returning</el-radio-button>
          <el-radio-button label="received">Pending Refund</el-radio-button>
          <el-radio-button label="completed">Completed</el-radio-button>
          <el-radio-button label="rejected">Rejected</el-radio-button>
          <el-radio-button label="cancelled">Cancelled</el-radio-button>
        </el-radio-group>
      </div>

      <!-- Return List -->
      <div class="return-list" v-loading="loading">
        <div v-if="returnList.length === 0 && !loading" class="empty-state">
          <el-empty description="No return records" :image-size="120" />
        </div>

        <div v-for="returnItem in returnList" :key="returnItem.returnId" class="return-item">
          <!-- Return Header -->
          <div class="return-header">
            <div class="return-info">
              <span class="return-no">Return No.: {{ returnItem.returnNo || returnItem.returnId }}</span>
              <span class="return-time">{{ formatDate(returnItem.createTime) }}</span>
            </div>
            <div class="return-status">
              <el-tag :type="getStatusType(returnItem.returnStatus)" size="default">
                {{ getStatusText(returnItem.returnStatus) }}
              </el-tag>
            </div>
          </div>

          <!-- Order Information -->
          <div class="order-info-row">
            <span class="label">Related Order:</span>
            <span class="value">{{ returnItem.orderNo }}</span>
          </div>

          <!-- Product Information -->
          <div class="product-section">
            <div class="product-info" @click="handleViewOrderDetail(returnItem.orderId)">
              <div class="product-image">
                <el-image
                  v-if="returnItem.productImage"
                  :src="returnItem.productImage.split(',')[0]"
                  fit="cover"
                  class="image"
                />
                <div v-else class="no-image">
                  <el-icon :size="40"><Picture /></el-icon>
                </div>
              </div>
              <div class="product-details">
                <div class="product-title">{{ returnItem.productTitle || 'Product Title' }}</div>
                <div class="product-specs">
                  <span>{{ isRefundOnly(returnItem) ? 'Refund' : 'Return' }} Reason: {{ returnItem.returnReason || 'Not filled' }}</span>
                  <span v-if="returnItem.receiveStatus" class="return-type-tag">
                    <el-tag :type="isRefundOnly(returnItem) ? 'info' : 'warning'" size="small">
                      {{ isRefundOnly(returnItem) ? 'Refund Only' : 'Return/Refund' }}
                    </el-tag>
                  </span>
                </div>
              </div>
            </div>
            <div class="return-amount">
              <div class="amount-label">Refund Amount</div>
              <div class="amount-value">¥{{ returnItem.returnAmount || returnItem.orderAmount || 0 }}</div>
            </div>
          </div>

          <!-- Return/Refund Progress -->
          <div class="progress-section" v-if="returnItem.returnStatus !== 'cancelled' && returnItem.returnStatus !== 'rejected'">
            <div class="progress-title">{{ isRefundOnly(returnItem) ? 'Refund Progress' : 'Return Progress' }}</div>
            <!-- Refund only flow: Apply -> Merchant Review -> Refund Complete -->
            <el-steps v-if="isRefundOnly(returnItem)" :active="getRefundOnlyProgressStep(returnItem)" finish-status="success" align-center>
              <el-step title="Apply Refund" :description="formatDate(returnItem.createTime)" />
              <el-step title="Merchant Review" :description="returnItem.approveTime ? formatDate(returnItem.approveTime) : (returnItem.returnStatus === 'pending' ? 'Waiting for Review' : 'Reviewed')" />
              <el-step title="Refund Complete" :description="returnItem.refundTime ? formatDate(returnItem.refundTime) : 'Pending Refund'" />
            </el-steps>
            <!-- Return/Refund flow: Apply -> Merchant Review -> Fill Shipping -> Confirm Receipt -> Refund Complete -->
            <el-steps v-else :active="getReturnRefundProgressStep(returnItem)" finish-status="success" align-center>
              <el-step title="Apply Return" :description="formatDate(returnItem.createTime)" />
              <el-step title="Merchant Review" :description="returnItem.approveTime ? formatDate(returnItem.approveTime) : (returnItem.returnStatus === 'pending' ? 'Waiting for Review' : 'Reviewed')" />
              <el-step title="Fill Shipping" :description="returnItem.returnShippingTime ? formatDate(returnItem.returnShippingTime) : 'Pending Fill'" />
              <el-step title="Confirm Receipt" :description="returnItem.sellerReceiveTime ? formatDate(returnItem.sellerReceiveTime) : 'Pending Confirm'" />
              <el-step title="Refund Complete" :description="returnItem.refundTime ? formatDate(returnItem.refundTime) : 'Pending Refund'" />
            </el-steps>
          </div>

          <!-- Action Buttons (Simplified: Reference order page style) -->
          <div class="return-actions">
            <!-- Seller actions: Can approve or reject in pending review status -->
            <el-button
              v-if="returnItem.returnStatus === 'pending'"
              type="success"
              size="small"
              @click="handleApproveReturn(returnItem)"
            >
              {{ isRefundOnly(returnItem) ? 'Approve Refund' : 'Approve Return' }}
            </el-button>
            <el-button
              v-if="returnItem.returnStatus === 'pending'"
              type="danger"
              size="small"
              @click="handleRejectReturn(returnItem)"
            >
              Reject
            </el-button>
            
            <!-- Seller actions: Refund only flow, need to refund after approval -->
            <el-button
              v-if="returnItem.returnStatus === 'approved' && isRefundOnly(returnItem)"
              type="primary"
              size="small"
              @click="handleRefundReturn(returnItem)"
            >
              Confirm Refund
            </el-button>
            
            <!-- Seller actions: Return/Refund flow, can confirm receipt in pending receipt status -->
            <el-button
              v-if="returnItem.returnStatus === 'shipping' && !isRefundOnly(returnItem)"
              type="primary"
              size="small"
              @click="handleConfirmReceive(returnItem)"
            >
              Confirm Receipt
            </el-button>
            
            <!-- Seller actions: After confirming receipt, need to refund -->
            <el-button
              v-if="returnItem.returnStatus === 'received' && !isRefundOnly(returnItem)"
              type="primary"
              size="small"
              @click="handleRefundReturn(returnItem)"
            >
              Confirm Refund
            </el-button>
            
            <el-button
              type="default"
              size="small"
              @click="handleViewDetail(returnItem.returnId)"
            >
              View Details
            </el-button>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- Cancel Return Dialog -->
    <el-dialog
      v-model="cancelDialogVisible"
      title="Cancel Return Application"
      width="500px"
      append-to-body
    >
      <el-form :model="cancelForm" :rules="cancelRules" ref="cancelFormRef" label-width="100px">
        <el-form-item label="Cancel Reason" prop="cancelReason">
          <el-input
            v-model="cancelForm.cancelReason"
            type="textarea"
            :rows="4"
            placeholder="Please enter cancel reason"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleCancelConfirm" :loading="cancelLoading">Confirm</el-button>
      </template>
    </el-dialog>

    <!-- Fill Shipping Dialog -->
    <el-dialog
      v-model="shippingDialogVisible"
      title="Fill Return Shipping Information"
      width="500px"
      append-to-body
    >
      <el-form :model="shippingForm" :rules="shippingRules" ref="shippingFormRef" label-width="100px">
        <el-form-item label="Express Company" prop="expressCompany">
          <el-select
            v-model="shippingForm.expressCompany"
            placeholder="Please select express company"
            filterable
            style="width: 100%"
          >
            <el-option label="SF Express" value="顺丰速运" />
            <el-option label="YTO Express" value="圆通速递" />
            <el-option label="ZTO Express" value="中通快递" />
            <el-option label="STO Express" value="申通快递" />
            <el-option label="Yunda Express" value="韵达速递" />
            <el-option label="Best Express" value="百世快递" />
            <el-option label="Deppon Express" value="德邦快递" />
            <el-option label="JD Logistics" value="京东物流" />
            <el-option label="Other" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="Tracking Number" prop="expressNo">
          <el-input
            v-model="shippingForm.expressNo"
            placeholder="Please enter tracking number"
            maxlength="50"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shippingDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleShippingConfirm" :loading="shippingLoading">Confirm</el-button>
      </template>
    </el-dialog>

    <!-- Reject Return Dialog -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="Reject Return Application"
      width="500px"
      append-to-body
    >
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectFormRef" label-width="100px">
        <el-form-item label="Reject Reason" prop="rejectReason">
          <el-input
            v-model="rejectForm.rejectReason"
            type="textarea"
            :rows="4"
            placeholder="Please enter reject reason"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleRejectConfirm" :loading="rejectLoading">Confirm</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getReturnList, cancelReturn, fillReturnShipping, approveReturn, rejectReturn, confirmReturnReceive, refundReturn } from '@/api/order'
import { getToken } from '@/utils/auth'
import useUserStore from '@/store/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const returnList = ref([])
const total = ref(0)
const activeStatus = ref('')
// Return/Refund page specifically serves sellers, always seller view
const isSellerView = computed(() => true)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  returnStatus: ''
})

// Cancel return related
const cancelDialogVisible = ref(false)
const cancelLoading = ref(false)
const cancelForm = reactive({
  cancelReason: ''
})
const cancelFormRef = ref(null)
const currentCancelReturn = ref(null)

const cancelRules = {
  cancelReason: [
    { required: true, message: 'Please enter cancel reason', trigger: 'blur' }
  ]
}

// Fill shipping related
const shippingDialogVisible = ref(false)
const shippingLoading = ref(false)
const shippingForm = reactive({
  expressCompany: '',
  expressNo: ''
})
const shippingFormRef = ref(null)
const currentShippingReturn = ref(null)

const shippingRules = {
  expressCompany: [
    { required: true, message: 'Please select express company', trigger: 'change' }
  ],
  expressNo: [
    { required: true, message: 'Please enter tracking number', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: 'Tracking number can only contain letters and numbers', trigger: 'blur' }
  ]
}

// Get return list (seller view)
function getList() {
  loading.value = true
  const params = {
    ...queryParams,
    returnStatus: activeStatus.value || null
  }
  
  getReturnList(params)
    .then((response) => {
      console.log('Return list response:', response)
      // TableDataInfo directly returned, not wrapped in data
      if (response) {
        returnList.value = response.rows || response.data?.rows || response.data?.list || []
        total.value = response.total || response.data?.total || 0
      } else {
        returnList.value = []
        total.value = 0
      }
      loading.value = false
    })
    .catch((error) => {
      console.error('Failed to query return list:', error)
      // If 404 error, backend API not implemented yet
      if (error.response && error.response.status === 404) {
        ElMessage.warning('Return API not implemented yet, please contact administrator')
      } else {
        ElMessage.error(error.msg || error.message || 'Failed to query return list')
      }
      returnList.value = []
      total.value = 0
      loading.value = false
    })
}

// Status filter
function handleStatusChange() {
  queryParams.pageNum = 1
  getList()
}

// View details (seller view)
function handleViewDetail(returnId) {
  router.push(`/order/return/${returnId}?role=seller`)
}

// View order details
function handleViewOrderDetail(orderId) {
  router.push(`/order/detail/${orderId}`)
}

// Cancel return
function handleCancelReturn(returnItem) {
  currentCancelReturn.value = returnItem
  cancelForm.cancelReason = ''
  cancelDialogVisible.value = true
}

// Confirm cancel return
function handleCancelConfirm() {
  cancelFormRef.value.validate((valid) => {
    if (!valid) return
    
    cancelLoading.value = true
    cancelReturn(currentCancelReturn.value.returnId, cancelForm.cancelReason)
      .then(() => {
        ElMessage.success('Return application cancelled')
        cancelDialogVisible.value = false
        getList()
      })
      .catch((error) => {
        ElMessage.error(error.msg || 'Failed to cancel return')
      })
      .finally(() => {
        cancelLoading.value = false
      })
  })
}

// Fill shipping
function handleFillShipping(returnItem) {
  currentShippingReturn.value = returnItem
  shippingForm.expressCompany = ''
  shippingForm.expressNo = ''
  shippingDialogVisible.value = true
}

// Confirm fill shipping
function handleShippingConfirm() {
  shippingFormRef.value.validate((valid) => {
    if (!valid) return
    
    shippingLoading.value = true
    fillReturnShipping(
      currentShippingReturn.value.returnId,
      shippingForm.expressCompany,
      shippingForm.expressNo
    )
      .then(() => {
        ElMessage.success('Shipping information submitted')
        shippingDialogVisible.value = false
        getList()
      })
      .catch((error) => {
        ElMessage.error(error.msg || 'Submission failed')
      })
      .finally(() => {
        shippingLoading.value = false
      })
  })
}

// Reject return related
const rejectDialogVisible = ref(false)
const rejectLoading = ref(false)
const rejectForm = reactive({
  rejectReason: ''
})
const rejectFormRef = ref(null)
const currentRejectReturn = ref(null)

const rejectRules = {
  rejectReason: [
    { required: true, message: 'Please enter reject reason', trigger: 'blur' }
  ]
}

// Approve return (seller operation)
function handleApproveReturn(returnItem) {
  ElMessageBox.confirm('Confirm approval of this return application?', 'Approve Return', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  })
    .then(() => {
      approveReturn(returnItem.returnId)
        .then(() => {
          ElMessage.success('Return application approved')
          getList()
        })
        .catch((error) => {
          ElMessage.error(error.msg || error.message || 'Operation failed')
        })
    })
    .catch(() => {})
}

// Reject return (seller operation)
function handleRejectReturn(returnItem) {
  currentRejectReturn.value = returnItem
  rejectForm.rejectReason = ''
  rejectDialogVisible.value = true
}

// Confirm reject return
function handleRejectConfirm() {
  rejectFormRef.value.validate((valid) => {
    if (!valid) return
    
    rejectLoading.value = true
    rejectReturn(currentRejectReturn.value.returnId, rejectForm.rejectReason)
      .then(() => {
        ElMessage.success('Return application rejected')
        rejectDialogVisible.value = false
        getList()
      })
      .catch((error) => {
        ElMessage.error(error.msg || error.message || 'Operation failed')
      })
      .finally(() => {
        rejectLoading.value = false
      })
  })
}

// Confirm receipt of return (seller operation)
function handleConfirmReceive(returnItem) {
  ElMessageBox.confirm('Confirm receipt of returned goods?', 'Confirm Receipt', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  })
    .then(() => {
      confirmReturnReceive(returnItem.returnId)
        .then(() => {
          ElMessage.success('Return receipt confirmed')
          getList()
        })
        .catch((error) => {
          ElMessage.error(error.msg || error.message || 'Operation failed')
        })
    })
    .catch(() => {})
}

// Confirm refund (seller operation)
function handleRefundReturn(returnItem) {
  const title = isRefundOnly(returnItem) ? 'Confirm Refund' : 'Confirm Refund'
  const message = isRefundOnly(returnItem) 
    ? 'Confirm refund to buyer? Refund amount: ¥' + (returnItem.returnAmount || returnItem.orderAmount || 0)
    : 'Confirm refund to buyer? Refund amount: ¥' + (returnItem.returnAmount || returnItem.orderAmount || 0)
  
  ElMessageBox.confirm(message, title, {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  })
    .then(() => {
      refundReturn(returnItem.returnId)
        .then(() => {
          ElMessage.success('Refund successful')
          getList()
        })
        .catch((error) => {
          ElMessage.error(error.msg || error.message || 'Operation failed')
        })
    })
    .catch(() => {})
}

// Determine if refund only (goods not received)
function isRefundOnly(returnItem) {
  return returnItem.receiveStatus === 'not_received' || returnItem.returnType === 'refund'
}

// Determine if current return item is seller view (return/refund page specifically serves sellers, always returns true)
function isItemSeller(returnItem) {
  // Return/refund page specifically serves sellers, always returns true
  return true
}

// Refund only flow progress steps (3 steps: Apply -> Review -> Refund Complete)
function getRefundOnlyProgressStep(returnItem) {
  const status = returnItem.returnStatus
  const stepMap = {
    'pending': 1,        // Apply refund (frontend status, corresponds to backend requested and timeout_approved)
    'approved': 2,       // Merchant review
    'refunded': 3,       // Refund complete
    'completed': 3       // Refund complete
  }
  return stepMap[status] || 0
}

// Return/Refund flow progress steps (5 steps: Apply -> Review -> Fill Shipping -> Confirm Receipt -> Refund Complete)
function getReturnRefundProgressStep(returnItem) {
  const status = returnItem.returnStatus
  const stepMap = {
    'pending': 1,        // Apply return (frontend status, corresponds to backend requested and timeout_approved)
    'approved': 2,       // Merchant review
    'shipping': 3,       // Fill shipping
    'received': 4,       // Confirm receipt
    'refunded': 5,       // Refund complete
    'completed': 5       // Refund complete
  }
  return stepMap[status] || 0
}

// Format date
function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// Get status text
function getStatusText(status) {
  const statusMap = {
    'pending': 'Pending Review',        // Frontend status: corresponds to backend requested and timeout_approved
    'approved': 'Pending Processing',
    'shipping': 'Pending Receipt Confirmation',
    'received': 'Pending Refund',
    'refunded': 'Refunded',
    'completed': 'Completed',
    'rejected': 'Rejected',
    'cancelled': 'Cancelled'
  }
  return statusMap[status] || status
}

// Get status type
function getStatusType(status) {
  const typeMap = {
    'pending': 'warning',       // Frontend status: corresponds to backend requested and timeout_approved
    'approved': 'primary',
    'shipping': 'info',
    'received': 'warning',
    'refunded': 'success',
    'completed': 'success',
    'rejected': 'danger',
    'cancelled': 'info'
  }
  return typeMap[status] || ''
}

// Pagination
function handleSizeChange(val) {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getList()
}

function handlePageChange(val) {
  queryParams.pageNum = val
  getList()
}

onMounted(() => {
  if (!getToken()) {
    ElMessage.warning('Please login first')
    router.push('/login')
    return
  }
  getList()
})
</script>

<style scoped lang="scss">
.return-list-container {
  padding: 20px 24px 32px;
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

.return-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.status-tabs {
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.return-list {
  min-height: 400px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.return-item {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 16px;
  padding: 20px;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.1);
  }
}

.return-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
  margin-bottom: 16px;
}

.return-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.return-no {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.return-time {
  font-size: 13px;
  color: #909399;
}

.order-info-row {
  margin-bottom: 16px;
  font-size: 14px;

  .label {
    color: #909399;
    margin-right: 8px;
  }

  .value {
    color: #303133;
    font-weight: 500;
  }
}

.product-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  cursor: pointer;
  transition: opacity 0.3s;

  &:hover {
    opacity: 0.8;
  }
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  overflow: hidden;
  background: #f5f7fa;
  flex-shrink: 0;
}

.image {
  width: 100%;
  height: 100%;
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.product-details {
  flex: 1;
  min-width: 0;
}

.product-title {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-specs {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 8px;
}

.return-type-tag {
  margin-left: 8px;
}

.return-amount {
  text-align: right;
  margin-left: 20px;
}

.amount-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.amount-value {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

.progress-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #fafafa;
  border-radius: 6px;
}

.progress-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.return-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f0f2f5;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style>

