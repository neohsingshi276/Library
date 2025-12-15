<template>
  <div class="order-list-container">
    <el-card shadow="never" class="order-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">My Orders</span>
        </div>
      </template>

      <!-- Role Filter -->
      <div class="role-tabs">
        <el-radio-group v-model="activeRole" @change="handleRoleChange" size="default">
          <el-radio-button label="buyer">Buyer</el-radio-button>
          <el-radio-button label="seller">Seller</el-radio-button>
        </el-radio-group>
      </div>

      <!-- Order Status Filter (Different groups by role) -->
      <div class="status-tabs">
        <el-radio-group v-model="activeStatus" @change="handleStatusChange" size="default">
          <el-radio-button
            v-for="opt in statusOptions"
            :key="opt.value"
            :label="opt.value"
          >
            {{ opt.label }}
          </el-radio-button>
        </el-radio-group>
      </div>

      <!-- 订单列表 -->
      <div class="order-list" v-loading="loading">
        <div v-if="orderList.length === 0 && !loading" class="empty-state">
          <el-empty description="No orders" :image-size="120" />
        </div>

        <div v-for="order in orderList" :key="order.orderId" class="order-item">
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">Order No.: {{ order.orderNo }}</span>
              <span class="order-time">{{ formatDate(order.createTime) }}</span>
            </div>
            <div class="order-status">
              <el-tag :type="getStatusType(order.orderStatus)" size="default">
                {{ getStatusText(order.orderStatus, order) }}
              </el-tag>
            </div>
          </div>

          <!-- 订单商品 -->
          <div class="order-content">
            <div class="product-info" @click="handleViewDetail(order.orderId)">
              <div class="product-image">
                <el-image
                  v-if="order.productImage"
                  :src="order.productImage.split(',')[0]"
                  fit="cover"
                  class="image"
                />
                <div v-else class="no-image">
                  <el-icon :size="40"><Picture /></el-icon>
                </div>
              </div>
              <div class="product-details">
                <div class="product-title">{{ order.productTitle || 'Product Title' }}</div>
                <div class="product-specs">
                  <span v-if="order.salePrice">Price: ¥{{ order.salePrice }}</span>
                </div>
              </div>
            </div>
            <div class="order-amount">
              <div class="amount-label">Order Amount</div>
              <div class="amount-value">¥{{ order.orderAmount || 0 }}</div>
            </div>
          </div>

          <!-- Order Action Buttons -->
          <div class="order-actions">
            <!-- Buyer Actions -->
            <template v-if="!isSellerView">
              <el-button
                v-if="order.orderStatus === 'pending'"
                type="primary"
                size="small"
                @click="handlePay(order)"
              >
                Pay Now
              </el-button>
              <el-button
                v-if="order.orderStatus === 'pending'"
                type="default"
                size="small"
                @click="handleCancel(order)"
              >
                Cancel Order
              </el-button>
              <el-button
                v-if="order.orderStatus === 'shipped'"
                type="primary"
                size="small"
                @click="handleConfirmReceive(order)"
              >
                Confirm Receipt
              </el-button>
              <!-- Can apply for return/refund in paid, shipped, completed status, but not in refunded/returned status -->
              <el-button
                v-if="['paid', 'shipped', 'completed'].includes(order.orderStatus) && !order.returnId && !order.returnStatus && order.orderStatus !== 'refunded' && order.orderStatus !== 'returned'"
                type="warning"
                size="small"
                @click="handleApplyReturn(order)"
              >
                Apply Return/Refund
              </el-button>
              <!-- Fill return shipping (when return status is approved) -->
              <el-button
                v-if="order.orderStatus === 'return_requested' && order.returnInfo && order.returnInfo.returnStatus === 'approved' && order.returnInfo.returnType === 'return_refund'"
                type="primary"
                size="small"
                @click="handleFillReturnShipping(order)"
              >
                Fill Shipping
              </el-button>
              <!-- Refunded status: View refund details -->
              <el-button
                v-if="order.orderStatus === 'refunded' && order.returnId"
                type="primary"
                size="small"
                @click="handleViewReturnDetail(order)"
              >
                View Refund Details
              </el-button>
              <!-- Returned status: View return details -->
              <el-button
                v-if="order.orderStatus === 'returned' && order.returnId"
                type="primary"
                size="small"
                @click="handleViewReturnDetail(order)"
              >
                View Return Details
              </el-button>
            </template>

            <!-- Seller Actions -->
            <template v-else>
              <el-button
                v-if="order.orderStatus === 'paid'"
                type="primary"
                size="small"
                @click="handleShip(order)"
              >
                Ship Now
              </el-button>
            </template>

            <el-button
              type="default"
              size="small"
              @click="handleViewDetail(order.orderId)"
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

    <!-- Cancel Order Dialog -->
    <el-dialog
      v-model="cancelDialogVisible"
      title="Cancel Order"
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

    <!-- Ship Dialog (Seller) -->
    <el-dialog
      v-model="shipDialogVisible"
      title="Fill Shipping Information"
      width="500px"
      append-to-body
    >
      <el-form :model="shipForm" :rules="shipRules" ref="shipFormRef" label-width="100px">
        <el-form-item label="Express Company" prop="expressCompany">
          <el-select
            v-model="shipForm.expressCompany"
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
            v-model="shipForm.expressNo"
            placeholder="Please enter tracking number"
            maxlength="50"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="shipLoading" @click="handleShipSubmit">Confirm Shipment</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getOrderList, cancelOrder, confirmReceive, applyReturn, shipOrder, approveReturn, rejectReturn, confirmReturnReceive, refundReturn, getOrderReturns } from '@/api/order'
import { getToken } from '@/utils/auth'
import useUserStore from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const activeStatus = ref('')
const activeRole = ref('buyer')
const isSellerView = ref(false)
const sellerId = computed(() => userStore.id || '')
const statusOptions = computed(() => {
  if (isSellerView.value) {
    return [
      { value: '', label: 'All' },
      { value: 'paid', label: 'Pending Shipment' },
      { value: 'shipped', label: 'Shipped/Pending Receipt' },
      { value: 'completed', label: 'Completed' },
      { value: 'return_requested', label: 'Return/Refund in Progress' },
      { value: 'refunded', label: 'Refunded' },
      { value: 'returned', label: 'Returned' },
      { value: 'cancelled', label: 'Cancelled' }
    ]
  }
  return [
    { value: '', label: 'All Orders' },
    { value: 'pending', label: 'Pending Payment' },
    { value: 'paid', label: 'Pending Shipment' },
    { value: 'shipped', label: 'Pending Receipt' },
    { value: 'completed', label: 'Completed' },
    { value: 'return_requested', label: 'Return/Refund in Progress' },
    { value: 'refunded', label: 'Refunded' },
    { value: 'returned', label: 'Returned' },
    { value: 'cancelled', label: 'Cancelled' }
  ]
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  orderStatus: '',
  role: 'buyer'
})

// 取消订单相关
const cancelDialogVisible = ref(false)
const cancelLoading = ref(false)
const cancelForm = reactive({
  cancelReason: ''
})
const cancelFormRef = ref(null)
const currentCancelOrder = ref(null)

const cancelRules = {
  cancelReason: [
    { required: true, message: 'Please enter cancel reason', trigger: 'blur' }
  ]
}

// 发货相关
const shipDialogVisible = ref(false)
const shipLoading = ref(false)
const currentShipOrder = ref(null)
const shipForm = reactive({
  expressCompany: '',
  expressNo: ''
})
const shipFormRef = ref(null)
const shipRules = {
  expressCompany: [
    { required: true, message: 'Please select express company', trigger: 'change' }
  ],
  expressNo: [
    { required: true, message: 'Please enter tracking number', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: 'Tracking number can only contain letters and numbers', trigger: 'blur' }
  ]
}

// Get order list
function getList() {
  loading.value = true
  const params = {
    ...queryParams,
    orderStatus: activeStatus.value || null,
    // 买家视角：用 userId；卖家视角：用 sellerId，仅传一个，避免后端同时按 user_id 和 seller_id 过滤
    userId: isSellerView.value ? null : sellerId.value,
    sellerId: isSellerView.value ? sellerId.value : null
  }
  
  getOrderList(params)
    .then((response) => {
      // 后端可能返回 {rows,total} 或 {data:{rows,total}}
      const data = response?.data || response || {}
      orderList.value = data.rows || data.list || []
      total.value = data.total || 0
      
      // For orders with return/refund status (buyer view), get return records to get returnId and returnInfo
      if (!isSellerView.value && orderList.value.length > 0) {
        const returnOrders = orderList.value.filter(o => 
          o.orderStatus === 'refunded' || o.orderStatus === 'returned' || o.orderStatus === 'return_requested'
        )
        if (returnOrders.length > 0) {
          Promise.all(
            returnOrders.map(order => 
              getOrderReturns(order.orderId)
                .then((returnResponse) => {
                  if (returnResponse && returnResponse.data && returnResponse.data.length > 0) {
                    if (order.orderStatus === 'refunded' || order.orderStatus === 'returned') {
                      // Find completed refund record
                      const completedReturn = returnResponse.data.find(r => 
                        r.returnStatus === 'completed' || 
                        r.returnStatus === 'refunded'
                      )
                      if (completedReturn) {
                        order.returnId = completedReturn.returnId
                        order.returnInfo = completedReturn // Save return info for status display
                      }
                    } else if (order.orderStatus === 'return_requested') {
                      // Find active return record (including requested, approved, shipped, received status)
                      const activeReturn = returnResponse.data.find(r => 
                        r.returnStatus === 'requested' || 
                        r.returnStatus === 'approved' || 
                        r.returnStatus === 'shipped' ||
                        r.returnStatus === 'pending' ||
                        r.returnStatus === 'shipping' ||
                        r.returnStatus === 'received'
                      )
                      if (activeReturn) {
                        order.returnId = activeReturn.returnId
                        order.returnInfo = activeReturn // Save return info for status display and button judgment
                      }
                    }
                  }
                })
                .catch(() => {
                  // Failure to get refund record does not affect main process
                })
            )
          ).finally(() => {
            loading.value = false
          })
        } else {
          loading.value = false
        }
      } else {
        loading.value = false
      }
    })
    .catch((error) => {
      console.error('Failed to query order list:', error)
      orderList.value = []
      total.value = 0
      loading.value = false
    })
}

// Status filter
function handleStatusChange() {
  queryParams.pageNum = 1
  getList()
}

// Role filter
function handleRoleChange() {
  isSellerView.value = activeRole.value === 'seller'
  queryParams.role = activeRole.value
  queryParams.seller = isSellerView.value
  queryParams.pageNum = 1
  getList()
}

// View details
function handleViewDetail(orderId) {
  router.push(`/order/detail/${orderId}`)
}

// View refund/return details
function handleViewReturnDetail(order) {
  if (!order.returnId) {
    ElMessage.warning('Refund record not found')
    return
  }
  router.push(`/order/return/${order.returnId}`)
}

// Cancel order
function handleCancel(order) {
  currentCancelOrder.value = order
  cancelForm.cancelReason = ''
  cancelDialogVisible.value = true
}

// Confirm cancel order
function handleCancelConfirm() {
  cancelFormRef.value.validate((valid) => {
    if (!valid) return
    
    cancelLoading.value = true
    cancelOrder(currentCancelOrder.value.orderNo, cancelForm.cancelReason)
      .then(() => {
        ElMessage.success('Order cancelled')
        cancelDialogVisible.value = false
        getList()
      })
      .catch((error) => {
        ElMessage.error(error.msg || 'Failed to cancel order')
      })
      .finally(() => {
        cancelLoading.value = false
      })
  })
}

// Pay now
function handlePay(order) {
  router.push(`/product/payment/${order.orderNo}`)
}

// Seller ship order
function handleShip(order) {
  currentShipOrder.value = order
  shipForm.expressCompany = ''
  shipForm.expressNo = ''
  shipDialogVisible.value = true
}

function handleShipSubmit() {
  shipFormRef.value.validate((valid) => {
    if (!valid) return
    shipLoading.value = true
    shipOrder(currentShipOrder.value.orderNo, shipForm.expressCompany, shipForm.expressNo)
      .then(() => {
        ElMessage.success('Shipment successful')
        shipDialogVisible.value = false
        getList()
      })
      .catch((error) => {
        ElMessage.error(error.msg || 'Shipment failed')
      })
      .finally(() => {
        shipLoading.value = false
      })
  })
}

// Confirm receipt
function handleConfirmReceive(order) {
  ElMessageBox.confirm('Confirm receipt of goods? You will not be able to apply for return/refund after confirmation.', 'Confirm Receipt', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  })
    .then(() => {
      confirmReceive(order.orderNo)
        .then(() => {
          ElMessage.success('Receipt confirmed')
          getList()
        })
        .catch((error) => {
          ElMessage.error(error.msg || 'Failed to confirm receipt')
        })
    })
    .catch(() => {})
}

// Apply return
function handleApplyReturn(order) {
  router.push(`/order/detail/${order.orderId}?action=return`)
}

// Fill return shipping
function handleFillReturnShipping(order) {
  router.push(`/order/detail/${order.orderId}?action=fillShipping`)
}

// Seller approve return
function handleApproveReturn(order) {
  if (!order.returnId) {
    ElMessage.warning('Return record not found')
    return
  }
  approveReturn(order.returnId)
    .then(() => {
      ElMessage.success('Return approved')
      getList()
    })
    .catch((error) => {
      ElMessage.error(error.msg || 'Operation failed')
    })
}

// Seller reject return
function handleRejectReturn(order) {
  if (!order.returnId) {
    ElMessage.warning('Return record not found')
    return
  }
  ElMessageBox.prompt('Please enter rejection reason', 'Reject Return', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    inputPlaceholder: 'e.g., Goods already shipped, returns not supported'
  }).then(({ value }) => {
    rejectReturn(order.returnId, value || 'Seller rejected return')
      .then(() => {
        ElMessage.success('Return rejected')
        getList()
      })
      .catch((error) => {
        ElMessage.error(error.msg || 'Operation failed')
      })
  }).catch(() => {})
}

// Seller confirm receipt and refund
function handleConfirmReturn(order) {
  if (!order.returnId) {
    ElMessage.warning('Return record not found')
    return
  }
  ElMessageBox.confirm('Confirm receipt of returned goods and refund?', 'Confirm Receipt and Refund', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  }).then(() => {
    confirmReturnReceive(order.returnId)
      .then(() => refundReturn(order.returnId))
      .then(() => {
        ElMessage.success('Receipt confirmed and refunded')
        getList()
      })
      .catch((error) => {
        ElMessage.error(error.msg || 'Operation failed')
      })
  }).catch(() => {})
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
function getStatusText(status, order) {
  // If order status is return_requested, display different text based on return status
  if (status === 'return_requested' && order && order.returnInfo) {
    const returnStatus = order.returnInfo.returnStatus
    if (returnStatus === 'requested') {
      return 'Pending'
    } else if (returnStatus === 'approved') {
      return 'Pending Shipment'
    } else if (returnStatus === 'shipped') {
      return 'Returning'
    } else if (returnStatus === 'received') {
      return 'Merchant Received'
    }
  }
  
  const statusMap = {
    'pending': 'Pending Payment',
    'paid': 'Pending Shipment',
    'shipped': 'Pending Receipt',
    'completed': 'Completed',
    'refunded': 'Refunded',
    'returned': 'Returned', // Return type displays as "Returned" (returned status is all return type)
    'return_requested': 'Pending', // Default shows pending, will be overridden if returnInfo exists
    'cancelled': 'Cancelled'
  }
  return statusMap[status] || status
}

// Get status type
function getStatusType(status) {
  const typeMap = {
    'pending': 'warning',
    'paid': 'info',
    'shipped': 'primary',
    'completed': 'success',
    'refunded': 'info',
    'returned': 'info',
    'return_requested': 'warning',
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
  isSellerView.value = activeRole.value === 'seller'
  getList()
})
</script>

<style scoped lang="scss">
.order-list-container {
  padding: 20px 24px 32px;
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

.order-card {
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

.role-tabs {
  padding: 12px 0 8px;
}

.status-tabs {
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.order-list {
  min-height: 400px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.order-item {
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

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
  margin-bottom: 16px;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.order-no {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.order-time {
  font-size: 13px;
  color: #909399;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
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
}

.order-amount {
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

.order-actions {
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

