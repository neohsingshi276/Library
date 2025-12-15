<template>
  <div class="exchange-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>My Exchanges</span>
        </div>
      </template>

      <!-- Tabs -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="My Initiated Exchanges" name="requests">
          <div class="exchange-list" v-loading="loading">
            <el-empty
              v-if="!loading && requestsList.length === 0"
              description="No initiated exchange requests"
              :image-size="120"
            />
            <div v-else-if="!loading">
              <div
                v-for="item in requestsList"
                :key="item.exchangeId"
                class="exchange-item"
              >
                <el-card shadow="hover" class="exchange-card">
                  <div class="exchange-content">
                    <!-- Left: My Product -->
                    <div class="product-box">
                      <div class="product-label">My Product</div>
                      <div class="product-info">
                        <el-image
                          v-if="item.requesterProductImage"
                          :src="item.requesterProductImage"
                          fit="cover"
                          style="width: 80px; height: 80px; border-radius: 4px;"
                        />
                        <div v-else class="no-image-small">
                          <el-icon><Picture /></el-icon>
                        </div>
                        <div class="product-details">
                          <div class="product-title">{{ item.requesterProductTitle }}</div>
                          <div class="product-status">
                            <el-tag :type="getStatusTagType(item.requesterProductStatus)">
                              {{ getProductStatusText(item.requesterProductStatus) }}
                            </el-tag>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- Middle: Exchange Arrow and Status -->
                    <div class="exchange-middle">
                      <el-icon :size="30" color="#409eff"><Right /></el-icon>
                      <div class="exchange-status">
                        <el-tag :type="getExchangeStatusTagType(item.exchangeStatus)" size="large">
                          {{ getExchangeStatusText(item.exchangeStatus) }}
                        </el-tag>
                      </div>
                    </div>

                    <!-- Right: Other Party's Product -->
                    <div class="product-box">
                      <div class="product-label">Other Party's Product</div>
                      <div class="product-info">
                        <el-image
                          v-if="item.receiverProductImage"
                          :src="item.receiverProductImage"
                          fit="cover"
                          style="width: 80px; height: 80px; border-radius: 4px;"
                        />
                        <div v-else class="no-image-small">
                          <el-icon><Picture /></el-icon>
                        </div>
                        <div class="product-details">
                          <div class="product-title">{{ item.receiverProductTitle }}</div>
                          <div class="product-owner">
                            <el-icon><User /></el-icon>
                            <span>{{ item.receiverName || 'User ' + item.receiverId }}</span>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- Action Buttons Area -->
                    <div class="exchange-actions">
                      <div class="action-buttons">
                        <el-button
                          v-if="item.exchangeStatus === 'pending'"
                          type="info"
                          size="default"
                          plain
                          @click="handleCancelExchange(item)"
                          class="action-btn"
                        >
                          <el-icon><Close /></el-icon>
                          <span>Cancel Exchange</span>
                        </el-button>
                        <el-button
                          type="default"
                          size="default"
                          plain
                          @click="handleViewDetail(item)"
                          class="action-btn"
                        >
                          <el-icon><View /></el-icon>
                          <span>View Details</span>
                        </el-button>
                        <el-button
                          v-if="item.exchangeStatus === 'accepted' && item.contactRevealed === '1'"
                          type="primary"
                          size="default"
                          @click="handleViewContact(item)"
                          class="action-btn"
                        >
                          <el-icon><Phone /></el-icon>
                          <span>View Contact</span>
                        </el-button>
                        <el-button
                          v-if="item.exchangeStatus === 'accepted' && item.requesterComplete === '0'"
                          type="success"
                          size="default"
                          @click="handleRequesterComplete(item)"
                          class="action-btn"
                        >
                          <el-icon><Check /></el-icon>
                          <span>Confirm Complete</span>
                        </el-button>
                      </div>
                    </div>
                  </div>
                </el-card>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="My Received Exchanges" name="receives">
          <div class="exchange-list" v-loading="loading">
            <el-empty
              v-if="!loading && receivesList.length === 0"
              description="No received exchange requests"
              :image-size="120"
            />
            <div v-else-if="!loading">
              <div
                v-for="item in receivesList"
                :key="item.exchangeId"
                class="exchange-item"
              >
                <el-card shadow="hover" class="exchange-card">
                  <div class="exchange-content">
                    <!-- Left: Other Party's Product -->
                    <div class="product-box">
                      <div class="product-label">Other Party's Product</div>
                      <div class="product-info">
                        <el-image
                          v-if="item.requesterProductImage"
                          :src="item.requesterProductImage"
                          fit="cover"
                          style="width: 80px; height: 80px; border-radius: 4px;"
                        />
                        <div v-else class="no-image-small">
                          <el-icon><Picture /></el-icon>
                        </div>
                        <div class="product-details">
                          <div class="product-title">{{ item.requesterProductTitle }}</div>
                          <div class="product-owner">
                            <el-icon><User /></el-icon>
                            <span>{{ item.requesterName || 'User ' + item.requesterId }}</span>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- Middle: Exchange Arrow and Status -->
                    <div class="exchange-middle">
                      <el-icon :size="30" color="#409eff"><Right /></el-icon>
                      <div class="exchange-status">
                        <el-tag :type="getExchangeStatusTagType(item.exchangeStatus)" size="large">
                          {{ getExchangeStatusText(item.exchangeStatus) }}
                        </el-tag>
                      </div>
                    </div>

                    <!-- Right: My Product -->
                    <div class="product-box">
                      <div class="product-label">My Product</div>
                      <div class="product-info">
                        <el-image
                          v-if="item.receiverProductImage"
                          :src="item.receiverProductImage"
                          fit="cover"
                          style="width: 80px; height: 80px; border-radius: 4px;"
                        />
                        <div v-else class="no-image-small">
                          <el-icon><Picture /></el-icon>
                        </div>
                        <div class="product-details">
                          <div class="product-title">{{ item.receiverProductTitle }}</div>
                          <div class="product-status">
                            <el-tag :type="getStatusTagType(item.receiverProductStatus)">
                              {{ getProductStatusText(item.receiverProductStatus) }}
                            </el-tag>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- Action Buttons Area -->
                    <div class="exchange-actions">
                      <div class="action-buttons">
                        <el-button
                          v-if="item.exchangeStatus === 'pending'"
                          type="success"
                          size="default"
                          @click="handleAcceptExchange(item)"
                          class="action-btn primary-action"
                        >
                          <el-icon><Check /></el-icon>
                          <span>Accept</span>
                        </el-button>
                        <el-button
                          v-if="item.exchangeStatus === 'pending'"
                          type="danger"
                          size="default"
                          plain
                          @click="handleRejectExchange(item)"
                          class="action-btn"
                        >
                          <el-icon><Close /></el-icon>
                          <span>Reject</span>
                        </el-button>
                        <el-button
                          type="default"
                          size="default"
                          plain
                          @click="handleViewDetail(item)"
                          class="action-btn"
                        >
                          <el-icon><View /></el-icon>
                          <span>View Details</span>
                        </el-button>
                        <el-button
                          v-if="item.exchangeStatus === 'accepted' && item.contactRevealed === '1'"
                          type="primary"
                          size="default"
                          @click="handleViewContact(item)"
                          class="action-btn"
                        >
                          <el-icon><Phone /></el-icon>
                          <span>View Contact</span>
                        </el-button>
                        <el-button
                          v-if="item.exchangeStatus === 'accepted' && item.receiverComplete === '0'"
                          type="success"
                          size="default"
                          @click="handleReceiverComplete(item)"
                          class="action-btn"
                        >
                          <el-icon><Check /></el-icon>
                          <span>Confirm Complete</span>
                        </el-button>
                      </div>
                    </div>
                  </div>
                </el-card>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="total > 0">
        <pagination
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </el-card>

    <!-- Exchange Details Dialog -->
    <el-dialog
      title="Exchange Details"
      v-model="detailDialogOpen"
      width="800px"
      append-to-body
    >
      <div v-if="currentExchange" class="exchange-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="Exchange No.">{{ currentExchange.exchangeNo }}</el-descriptions-item>
          <el-descriptions-item label="Exchange Status">
            <el-tag :type="getExchangeStatusTagType(currentExchange.exchangeStatus)">
              {{ getExchangeStatusText(currentExchange.exchangeStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Create Time">{{ currentExchange.createTime }}</el-descriptions-item>
          <el-descriptions-item label="Accept Time" v-if="currentExchange.acceptTime">
            {{ currentExchange.acceptTime }}
          </el-descriptions-item>
          <el-descriptions-item label="Complete Time" v-if="currentExchange.completeTime">
            {{ currentExchange.completeTime }}
          </el-descriptions-item>
          <el-descriptions-item label="Reject Reason" v-if="currentExchange.rejectReason" :span="2">
            {{ currentExchange.rejectReason }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <div class="detail-products">
          <div class="detail-product-item">
            <h4>Requester's Product</h4>
            <div class="detail-product-content">
              <el-image
                v-if="currentExchange.requesterProductImage"
                :src="currentExchange.requesterProductImage"
                fit="cover"
                style="width: 120px; height: 120px; border-radius: 4px;"
              />
              <div class="detail-product-info">
                <div class="detail-product-title">{{ currentExchange.requesterProductTitle }}</div>
                <div class="detail-product-owner">
                  <el-icon><User /></el-icon>
                  <span>{{ currentExchange.requesterName || 'User ' + currentExchange.requesterId }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="detail-product-item">
            <h4>Receiver's Product</h4>
            <div class="detail-product-content">
              <el-image
                v-if="currentExchange.receiverProductImage"
                :src="currentExchange.receiverProductImage"
                fit="cover"
                style="width: 120px; height: 120px; border-radius: 4px;"
              />
              <div class="detail-product-info">
                <div class="detail-product-title">{{ currentExchange.receiverProductTitle }}</div>
                <div class="detail-product-owner">
                  <el-icon><User /></el-icon>
                  <span>{{ currentExchange.receiverName || 'User ' + currentExchange.receiverId }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- Contact Information Dialog -->
    <el-dialog
      title="Contact Information"
      v-model="contactDialogOpen"
      width="500px"
      append-to-body
    >
      <div v-if="contactInfo" class="contact-info">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="Name">
            {{ contactInfo.name }}
          </el-descriptions-item>
          <el-descriptions-item label="Phone" v-if="contactInfo.phone">
            {{ contactInfo.phone }}
          </el-descriptions-item>
          <el-descriptions-item label="Email" v-if="contactInfo.email">
            {{ contactInfo.email }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- Reject Reason Dialog -->
    <el-dialog
      title="Reject Exchange"
      v-model="rejectDialogOpen"
      width="500px"
      append-to-body
    >
      <el-form :model="rejectForm" ref="rejectFormRef">
        <el-form-item label="Reject Reason" prop="reason">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="Please enter reject reason (optional)"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogOpen = false">Cancel</el-button>
        <el-button type="danger" @click="handleConfirmReject">Confirm Reject</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Exchange">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, User, Right, Check, Close, Phone, View } from '@element-plus/icons-vue'
import {
  getMyExchangeRequests,
  getMyExchangeReceives,
  getExchangeInfo,
  acceptExchange,
  rejectExchange,
  requesterComplete,
  receiverComplete,
  cancelExchange
} from '@/api/exchange'

const activeTab = ref('requests')
const loading = ref(false)
const requestsList = ref([])
const receivesList = ref([])
const total = ref(0)
const detailDialogOpen = ref(false)
const contactDialogOpen = ref(false)
const rejectDialogOpen = ref(false)
const currentExchange = ref(null)
const contactInfo = ref(null)
const rejectForm = ref({ reason: '' })
const rejectFormRef = ref(null)
const currentRejectExchange = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

/** Query list */
function getList() {
  loading.value = true
  const api = activeTab.value === 'requests' ? getMyExchangeRequests : getMyExchangeReceives
  
  api(queryParams).then((response) => {
    console.log('Exchange list response:', response)
    // Compatible with different response formats: {rows,total} or {data:{rows,total}}
    const data = response?.data || response || {}
    const list = data.rows || data.list || []
    
    console.log('Parsed list data:', list, 'Total:', data.total)
    
    if (activeTab.value === 'requests') {
      requestsList.value = list
    } else {
      receivesList.value = list
    }
    total.value = data.total || 0
    
    loading.value = false
  }).catch((error) => {
    console.error('Failed to get exchange list:', error)
    if (activeTab.value === 'requests') {
      requestsList.value = []
    } else {
      receivesList.value = []
    }
    total.value = 0
    loading.value = false
    ElMessage.error(error.msg || error.message || 'Failed to get exchange list')
  })
}

/** Tab change */
function handleTabChange() {
  queryParams.pageNum = 1
  getList()
}

/** Accept exchange */
function handleAcceptExchange(item) {
  ElMessageBox.confirm('Are you sure to accept this exchange request?', 'Tip', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  }).then(() => {
    acceptExchange(item.exchangeId).then(() => {
      ElMessage.success('Exchange request accepted')
      getList()
    }).catch((error) => {
      ElMessage.error(error.msg || 'Operation failed')
    })
  })
}

/** Reject exchange */
function handleRejectExchange(item) {
  currentRejectExchange.value = item
  rejectForm.value.reason = ''
  rejectDialogOpen.value = true
}

/** Confirm reject */
function handleConfirmReject() {
  if (!currentRejectExchange.value) return
  
  rejectExchange(currentRejectExchange.value.exchangeId, rejectForm.value.reason).then(() => {
    ElMessage.success('Exchange request rejected')
    rejectDialogOpen.value = false
    getList()
  }).catch((error) => {
    ElMessage.error(error.msg || 'Operation failed')
  })
}

/** Cancel exchange */
function handleCancelExchange(item) {
  ElMessageBox.prompt('Please enter cancel reason (optional)', 'Cancel Exchange', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    inputType: 'textarea',
    inputPlaceholder: 'Please enter cancel reason'
  }).then(({ value }) => {
    cancelExchange(item.exchangeId, value || '').then(() => {
      ElMessage.success('Exchange cancelled')
      getList()
    }).catch((error) => {
      ElMessage.error(error.msg || 'Operation failed')
    })
  })
}

/** Requester confirm complete */
function handleRequesterComplete(item) {
  ElMessageBox.confirm('Confirm exchange completed?', 'Tip', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  }).then(() => {
    requesterComplete(item.exchangeId).then(() => {
      ElMessage.success('Completion confirmed')
      getList()
    }).catch((error) => {
      ElMessage.error(error.msg || 'Operation failed')
    })
  })
}

/** Receiver confirm complete */
function handleReceiverComplete(item) {
  ElMessageBox.confirm('Confirm exchange completed?', 'Tip', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  }).then(() => {
    receiverComplete(item.exchangeId).then(() => {
      ElMessage.success('Completion confirmed')
      getList()
    }).catch((error) => {
      ElMessage.error(error.msg || 'Operation failed')
    })
  })
}

/** View contact information */
function handleViewContact(item) {
  // Display other party's contact information based on current user role
  const isRequester = activeTab.value === 'requests'
  contactInfo.value = {
    name: isRequester ? (item.receiverName || 'User ' + item.receiverId) : (item.requesterName || 'User ' + item.requesterId),
    phone: isRequester ? item.receiverPhone : item.requesterPhone,
    email: isRequester ? item.receiverEmail : item.requesterEmail
  }
  contactDialogOpen.value = true
}

/** View details */
function handleViewDetail(item) {
  getExchangeInfo(item.exchangeId).then((response) => {
    currentExchange.value = response.data
    detailDialogOpen.value = true
  }).catch((error) => {
    ElMessage.error(error.msg || 'Failed to get details')
  })
}

/** Get exchange status text */
function getExchangeStatusText(status) {
  const statusMap = {
    'pending': 'Pending',
    'accepted': 'Accepted',
    'rejected': 'Rejected',
    'completed': 'Completed',
    'cancelled': 'Cancelled'
  }
  return statusMap[status] || status
}

/** Get exchange status tag type */
function getExchangeStatusTagType(status) {
  const typeMap = {
    'pending': 'warning',
    'accepted': 'success',
    'rejected': 'danger',
    'completed': 'info',
    'cancelled': 'info'
  }
  return typeMap[status] || ''
}

/** Get product status text */
function getProductStatusText(status) {
  const statusMap = {
    'pending': 'Pending Review',
    'published': 'Published',
    'exchanging': 'Exchanging',
    'exchanged': 'Exchanged',
    'off_shelf': 'Offline'
  }
  return statusMap[status] || status
}

/** 获取商品状态标签类型 */
function getStatusTagType(status) {
  const typeMap = {
    'pending': 'warning',
    'published': 'success',
    'exchanging': 'info',
    'exchanged': 'info',
    'off_shelf': 'danger'
  }
  return typeMap[status] || ''
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.exchange-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.card-header {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-card) {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.04);
}

:deep(.el-card__header) {
  padding: 20px;
  border-bottom: 1px solid #f0f2f5;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px 12px 0 0;
  
  .card-header {
    color: #fff;
  }
}

:deep(.el-tabs__header) {
  margin: 0 0 20px 0;
  background: #fff;
  padding: 0 20px;
  border-radius: 8px;
}

:deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
  padding: 0 24px;
  height: 50px;
  line-height: 50px;
  
  &.is-active {
    color: #667eea;
    font-weight: 600;
  }
}

:deep(.el-tabs__active-bar) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  height: 3px;
}

.exchange-list {
  min-height: 400px;
  padding: 10px 0;
}

.exchange-item {
  margin-bottom: 20px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.exchange-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #ebeef5;
  overflow: hidden;
  
  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
    border-color: #c0c4cc;
  }
}

:deep(.el-card__body) {
  padding: 24px;
}

.exchange-content {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0;
}

.product-box {
  flex: 1;
  min-width: 200px;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 8px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
  
  &:hover {
    border-color: #c0c4cc;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }
}

.product-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.product-info {
  display: flex;
  gap: 16px;
  align-items: center;
}

.product-details {
  flex: 1;
  min-width: 0;
}

.product-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.product-owner {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
  margin-top: 6px;
  
  .el-icon {
    color: #909399;
  }
}

.product-status {
  margin-top: 8px;
}

.exchange-middle {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  min-width: 120px;
  padding: 20px;
  position: relative;
  
  &::before,
  &::after {
    content: '';
    position: absolute;
    width: 40px;
    height: 2px;
    background: linear-gradient(90deg, transparent, #dcdfe6, transparent);
  }
  
  &::before {
    left: -20px;
    top: 50%;
    transform: translateY(-50%);
  }
  
  &::after {
    right: -20px;
    top: 50%;
    transform: translateY(-50%);
  }
}

.exchange-status {
  text-align: center;
  
  :deep(.el-tag) {
    padding: 8px 16px;
    font-size: 13px;
    font-weight: 500;
    border-radius: 20px;
    border: none;
  }
}

.exchange-actions {
  min-width: 280px;
  display: flex;
  align-items: center;
}

.action-buttons {
  display: flex;
  flex-direction: row;
  gap: 10px;
  width: 100%;
  flex-wrap: wrap;
}

.action-btn {
  flex: 1;
  min-width: 100px;
  height: 40px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  white-space: nowrap;
  
  .el-icon {
    font-size: 16px;
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  &.primary-action {
    background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
    border: none;
    
    &:hover {
      background: linear-gradient(135deg, #5daf34 0%, #73c557 100%);
    }
  }
}

.no-image-small {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-radius: 8px;
  color: #909399;
  border: 1px solid #ebeef5;
  flex-shrink: 0;
  
  .el-icon {
    font-size: 32px;
  }
}

:deep(.el-image) {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.exchange-detail {
  padding: 20px;
}

.detail-products {
  display: flex;
  gap: 30px;
  margin-top: 20px;
}

.detail-product-item {
  flex: 1;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.detail-product-item h4 {
  margin-bottom: 15px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.detail-product-content {
  display: flex;
  gap: 15px;
  align-items: center;
}

.detail-product-info {
  flex: 1;
}

.detail-product-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
}

.detail-product-owner {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.contact-info {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

:deep(.el-empty) {
  padding: 60px 0;
}

:deep(.el-empty__description) {
  color: #909399;
  font-size: 14px;
}

@media (max-width: 768px) {
  .exchange-container {
    padding: 12px;
  }
  
  .exchange-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .product-box {
    width: 100%;
  }

  .exchange-middle {
    width: 100%;
    flex-direction: row;
    justify-content: center;
    padding: 16px 0;
    
    &::before,
    &::after {
      display: none;
    }
  }

  .exchange-actions {
    width: 100%;
    min-width: auto;
  }
  
  .action-buttons {
    flex-direction: row;
    flex-wrap: wrap;
    width: 100%;
  }
  
  .action-btn {
    flex: 1 1 calc(50% - 5px);
    min-width: 100px;
    max-width: 100%;
  }

  .detail-products {
    flex-direction: column;
  }
}
</style>

