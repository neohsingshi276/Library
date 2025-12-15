<template>
  <div class="return-detail-container" v-loading="loading">
    <el-card shadow="never" class="detail-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="header-title">Return Details</span>
            <el-tag :type="getStatusType(returnInfo.returnStatus)" size="default" style="margin-left: 12px">
              {{ getStatusText(returnInfo.returnStatus) }}
            </el-tag>
          </div>
          <el-button type="default" @click="router.back()">Back</el-button>
        </div>
      </template>

      <!-- Return Basic Information -->
      <div class="info-section">
        <div class="section-title">Return Information</div>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">Return No.:</span>
            <span class="value">{{ returnInfo.returnNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">Related Order:</span>
            <span class="value link" @click="handleViewOrder">{{ returnInfo.orderNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">Return Type:</span>
            <el-tag :type="isRefundOnly ? 'info' : 'warning'" size="small">
              {{ isRefundOnly ? 'Refund Only' : 'Return/Refund' }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">Application Time:</span>
            <span class="value">{{ formatDateTime(returnInfo.createTime) }}</span>
          </div>
          <div class="info-item">
            <span class="label">Return Reason:</span>
            <span class="value">{{ returnInfo.returnReason || 'Not filled' }}</span>
          </div>
          <div class="info-item">
            <span class="label">Refund Amount:</span>
            <span class="value amount">¥{{ returnInfo.returnAmount || returnInfo.orderAmount || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- Return Description -->
      <div class="description-section" v-if="returnInfo.returnDescription">
        <div class="section-title">Return Description</div>
        <div class="description-content">{{ returnInfo.returnDescription }}</div>
      </div>

      <!-- Return Images -->
      <div class="images-section" v-if="returnInfo.returnImages">
        <div class="section-title">Return Images</div>
        <div class="images-list">
          <el-image
            v-for="(img, index) in imageList"
            :key="index"
            :src="img"
            fit="cover"
            class="return-image"
            :preview-src-list="imageList"
            :initial-index="index"
          />
        </div>
      </div>

      <!-- Return Progress -->
      <div class="progress-section">
        <div class="section-title">Return Progress</div>
        <!-- Refund only flow -->
        <el-steps v-if="isRefundOnly" :active="getRefundOnlyProgressStep" finish-status="success" align-center>
          <el-step title="Apply Refund" :description="formatDateTime(returnInfo.createTime)" />
          <el-step title="Merchant Review" :description="returnInfo.approveTime ? formatDateTime(returnInfo.approveTime) : (returnInfo.returnStatus === 'pending' ? 'Waiting for Review' : 'Reviewed')" />
          <el-step title="Refund Complete" :description="returnInfo.refundTime ? formatDateTime(returnInfo.refundTime) : 'Pending Refund'" />
        </el-steps>
        <!-- Return/Refund flow -->
        <el-steps v-else :active="getReturnRefundProgressStep" finish-status="success" align-center>
          <el-step title="Apply Return" :description="formatDateTime(returnInfo.createTime)" />
          <el-step title="Merchant Review" :description="returnInfo.approveTime ? formatDateTime(returnInfo.approveTime) : (returnInfo.returnStatus === 'pending' ? 'Waiting for Review' : 'Reviewed')" />
          <el-step title="Fill Shipping" :description="returnInfo.returnShippingTime ? formatDateTime(returnInfo.returnShippingTime) : 'Pending Fill'" />
          <el-step title="Confirm Receipt" :description="returnInfo.sellerReceiveTime ? formatDateTime(returnInfo.sellerReceiveTime) : 'Pending Confirm'" />
          <el-step title="Refund Complete" :description="returnInfo.refundTime ? formatDateTime(returnInfo.refundTime) : 'Pending Refund'" />
        </el-steps>
      </div>

      <!-- Return Shipping Information (Displayed only for return/refund with shipping) -->
      <div class="shipping-section" v-if="!isRefundOnly && returnInfo.returnExpressCompany">
        <div class="section-title">Return Shipping Information</div>
        <div class="shipping-info">
          <div class="shipping-item">
            <span class="label">Express Company:</span>
            <span class="value">{{ returnInfo.returnExpressCompany }}</span>
          </div>
          <div class="shipping-item">
            <span class="label">Tracking Number:</span>
            <span class="value">{{ returnInfo.returnExpressNo }}</span>
          </div>
          <div class="shipping-item" v-if="returnInfo.returnShippingTime">
            <span class="label">Shipping Time:</span>
            <span class="value">{{ formatDateTime(returnInfo.returnShippingTime) }}</span>
          </div>
        </div>
      </div>

      <!-- Reject Reason (Displayed when rejected) -->
      <div class="reject-section" v-if="returnInfo.returnStatus === 'rejected' && returnInfo.rejectReason">
        <div class="section-title">Reject Reason</div>
        <div class="reject-content">{{ returnInfo.rejectReason }}</div>
      </div>

      <!-- Action Buttons (Simplified: Reference order page style) -->
      <div class="action-section">
        <!-- Seller actions: Can approve or reject in pending review status -->
        <el-button
          v-if="returnInfo.returnStatus === 'pending'"
          type="success"
          @click="handleApproveReturn"
        >
          {{ isRefundOnly ? 'Approve Refund' : 'Approve Return' }}
        </el-button>
        <el-button
          v-if="returnInfo.returnStatus === 'pending'"
          type="danger"
          @click="handleRejectReturn"
        >
          Reject
        </el-button>
        
        <!-- Seller actions: Refund only flow, need to refund after approval -->
        <el-button
          v-if="returnInfo.returnStatus === 'approved' && isRefundOnly"
          type="primary"
          @click="handleRefundReturn"
        >
          Confirm Refund
        </el-button>
        
        <!-- Seller actions: Return/Refund flow, can confirm receipt in pending receipt status -->
        <el-button
          v-if="returnInfo.returnStatus === 'shipping' && !isRefundOnly"
          type="primary"
          @click="handleConfirmReceive"
        >
          Confirm Receipt
        </el-button>
        
        <!-- Seller actions: After confirming receipt, need to refund -->
        <el-button
          v-if="returnInfo.returnStatus === 'received' && !isRefundOnly"
          type="primary"
          @click="handleRefundReturn"
        >
          Confirm Refund
        </el-button>

        <el-button
          type="default"
          @click="router.back()"
        >
          Back
        </el-button>
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
import { getReturnDetail, cancelReturn, fillReturnShipping, approveReturn, rejectReturn, confirmReturnReceive, refundReturn } from '@/api/order'
import { getToken } from '@/utils/auth'
import useUserStore from '@/store/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const returnInfo = ref({})
// Determine if seller view: prioritize URL parameter, if not then determine by data (sellerId === current user ID)
const isSellerView = computed(() => {
  // Prioritize URL parameter
  if (route.query.role === 'seller') return true
  if (route.query.role === 'buyer') return false
  // If no URL parameter, determine by data: if current user ID equals sellerId, then is seller
  if (returnInfo.value.sellerId && userStore.id) {
    const isSeller = String(returnInfo.value.sellerId) === String(userStore.id)
    return isSeller
  }
  // Default return false (buyer view)
  return false
})

// Computed properties
const isRefundOnly = computed(() => {
  return returnInfo.value.receiveStatus === 'not_received' || returnInfo.value.returnType === 'refund'
})

const imageList = computed(() => {
  if (!returnInfo.value.returnImages) return []
  return returnInfo.value.returnImages.split(',').filter(img => img.trim())
})

const getRefundOnlyProgressStep = computed(() => {
  const status = returnInfo.value.returnStatus
  const stepMap = {
    'pending': 1,        // Frontend status: corresponds to backend requested and timeout_approved
    'approved': 2,
    'refunded': 3,
    'completed': 3
  }
  return stepMap[status] || 0
})

const getReturnRefundProgressStep = computed(() => {
  const status = returnInfo.value.returnStatus
  const stepMap = {
    'pending': 1,        // Frontend status: corresponds to backend requested and timeout_approved
    'approved': 2,
    'shipping': 3,
    'received': 4,
    'refunded': 5,
    'completed': 5
  }
  return stepMap[status] || 0
})

// Cancel return related
const cancelDialogVisible = ref(false)
const cancelLoading = ref(false)
const cancelForm = reactive({
  cancelReason: ''
})
const cancelFormRef = ref(null)

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

const shippingRules = {
  expressCompany: [
    { required: true, message: 'Please select express company', trigger: 'change' }
  ],
  expressNo: [
    { required: true, message: 'Please enter tracking number', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: 'Tracking number can only contain letters and numbers', trigger: 'blur' }
  ]
}

// Reject return related
const rejectDialogVisible = ref(false)
const rejectLoading = ref(false)
const rejectForm = reactive({
  rejectReason: ''
})
const rejectFormRef = ref(null)

const rejectRules = {
  rejectReason: [
    { required: true, message: 'Please enter reject reason', trigger: 'blur' }
  ]
}

// Get return details
function getDetail() {
  const returnId = route.params.returnId
  if (!returnId) {
    ElMessage.error('Return ID does not exist')
    router.back()
    return
  }

  loading.value = true
  getReturnDetail(returnId)
    .then((response) => {
      if (response && response.data) {
        returnInfo.value = response.data
        // If URL has no role parameter, but data has sellerId, automatically determine and update URL
        if (!route.query.role && returnInfo.value.sellerId && userStore.id) {
          const isSeller = String(returnInfo.value.sellerId) === String(userStore.id)
          if (isSeller && route.query.role !== 'seller') {
            // Update URL parameter without refreshing page
            router.replace({ query: { ...route.query, role: 'seller' } })
          } else if (!isSeller && route.query.role !== 'buyer') {
            router.replace({ query: { ...route.query, role: 'buyer' } })
          }
        }
      } else {
        ElMessage.error('Return record does not exist')
        router.back()
      }
      loading.value = false
    })
    .catch((error) => {
      ElMessage.error(error.msg || 'Failed to get return details')
      loading.value = false
      router.back()
    })
}

// View order details
function handleViewOrder() {
  if (returnInfo.value.orderId) {
    router.push(`/order/detail/${returnInfo.value.orderId}`)
  }
}

// Cancel return
function handleCancelReturn() {
  cancelForm.cancelReason = ''
  cancelDialogVisible.value = true
}

// Confirm cancel return
function handleCancelConfirm() {
  cancelFormRef.value.validate((valid) => {
    if (!valid) return
    
    cancelLoading.value = true
    cancelReturn(returnInfo.value.returnId, cancelForm.cancelReason)
      .then(() => {
        ElMessage.success('Return application cancelled')
        cancelDialogVisible.value = false
        getDetail()
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
function handleFillShipping() {
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
      returnInfo.value.returnId,
      shippingForm.expressCompany,
      shippingForm.expressNo
    )
      .then(() => {
        ElMessage.success('Shipping information submitted')
        shippingDialogVisible.value = false
        getDetail()
      })
      .catch((error) => {
        ElMessage.error(error.msg || 'Submission failed')
      })
      .finally(() => {
        shippingLoading.value = false
      })
  })
}

// Approve return (seller operation)
function handleApproveReturn() {
  ElMessageBox.confirm(
    `Confirm approval of this ${isRefundOnly.value ? 'refund' : 'return'} application?`,
    `Approve ${isRefundOnly.value ? 'Refund' : 'Return'}`,
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  )
    .then(() => {
      approveReturn(returnInfo.value.returnId)
        .then(() => {
          ElMessage.success(`${isRefundOnly.value ? 'Refund' : 'Return'} application approved`)
          getDetail()
        })
        .catch((error) => {
          ElMessage.error(error.msg || error.message || 'Operation failed')
        })
    })
    .catch(() => {})
}

// Reject return (seller operation)
function handleRejectReturn() {
  rejectForm.rejectReason = ''
  rejectDialogVisible.value = true
}

// Confirm reject return
function handleRejectConfirm() {
  rejectFormRef.value.validate((valid) => {
    if (!valid) return
    
    rejectLoading.value = true
    rejectReturn(returnInfo.value.returnId, rejectForm.rejectReason)
      .then(() => {
        ElMessage.success('Return application rejected')
        rejectDialogVisible.value = false
        getDetail()
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
function handleConfirmReceive() {
  ElMessageBox.confirm('Confirm receipt of returned goods?', 'Confirm Receipt', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  })
    .then(() => {
      confirmReturnReceive(returnInfo.value.returnId)
        .then(() => {
          ElMessage.success('Return receipt confirmed')
          getDetail()
        })
        .catch((error) => {
          ElMessage.error(error.msg || error.message || 'Operation failed')
        })
    })
    .catch(() => {})
}

// Confirm refund (seller operation)
function handleRefundReturn() {
  const title = isRefundOnly.value ? 'Confirm Refund' : 'Confirm Refund'
  const message = `Confirm refund to buyer? Refund amount: ¥${returnInfo.value.returnAmount || returnInfo.value.orderAmount || 0}`
  
  ElMessageBox.confirm(message, title, {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  })
    .then(() => {
      refundReturn(returnInfo.value.returnId)
        .then(() => {
          ElMessage.success('Refund successful')
          getDetail()
        })
        .catch((error) => {
          ElMessage.error(error.msg || error.message || 'Operation failed')
        })
    })
    .catch(() => {})
}

// Format date time
function formatDateTime(date) {
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

onMounted(() => {
  if (!getToken()) {
    ElMessage.warning('Please login first')
    router.push('/login')
    return
  }
  getDetail()
})
</script>

<style scoped lang="scss">
.return-detail-container {
  padding: 20px 24px 32px;
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

.detail-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #409eff;
}

.info-section,
.description-section,
.images-section,
.progress-section,
.shipping-section,
.reject-section {
  margin-bottom: 24px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;

  .label {
    color: #909399;
    margin-right: 8px;
    min-width: 80px;
  }

  .value {
    color: #303133;
    font-weight: 500;

    &.link {
      color: #409eff;
      cursor: pointer;

      &:hover {
        text-decoration: underline;
      }
    }

    &.amount {
      color: #f56c6c;
      font-size: 16px;
      font-weight: 600;
    }
  }
}

.description-content {
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.images-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.return-image {
  width: 120px;
  height: 120px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  cursor: pointer;
}

.shipping-info {
  padding: 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}

.shipping-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;

  &:last-child {
    margin-bottom: 0;
  }

  .label {
    color: #909399;
    width: 100px;
    flex-shrink: 0;
  }

  .value {
    color: #303133;
    font-weight: 500;
  }
}

.reject-content {
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  font-size: 14px;
  color: #f56c6c;
  line-height: 1.6;
}

.action-section {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
  margin-top: 24px;
}
</style>