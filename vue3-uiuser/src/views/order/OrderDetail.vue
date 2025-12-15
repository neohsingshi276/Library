<template>
  <div class="order-detail-container" v-loading="loading">
    <el-card shadow="never" class="detail-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">Order Details</span>
          <el-tag :type="getStatusType(order.orderStatus)" size="default">
            {{ getStatusText(order.orderStatus) }}
          </el-tag>
        </div>
      </template>

      <!-- Order Information -->
      <div class="order-info-section">
        <div class="info-row">
          <span class="label">Order No.:</span>
          <span class="value">{{ order.orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">Order Time:</span>
          <span class="value">{{ formatDateTime(order.createTime) }}</span>
        </div>
        <div class="info-row" v-if="order.payTime">
          <span class="label">Payment Time:</span>
          <span class="value">{{ formatDateTime(order.payTime) }}</span>
        </div>
        <div class="info-row" v-if="order.payMethod">
          <span class="label">Payment Method:</span>
          <span class="value">{{ getPayMethodText(order.payMethod) }}</span>
        </div>
        <div class="info-row" v-if="order.shippingTime">
          <span class="label">Shipping Time:</span>
          <span class="value">{{ formatDateTime(order.shippingTime) }}</span>
        </div>
        <div class="info-row" v-if="order.completeTime">
          <span class="label">Completion Time:</span>
          <span class="value">{{ formatDateTime(order.completeTime) }}</span>
        </div>
      </div>

      <!-- Product Information -->
      <div class="product-section">
        <div class="section-title">Product Information</div>
        <div class="product-card">
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
          <div class="product-info">
            <div class="product-title">{{ order.productTitle || 'Product Title' }}</div>
            <div class="product-price">
              <span class="price-label">Price:</span>
              <span class="price-value">¥{{ order.salePrice || 0 }}</span>
              <span v-if="order.originalPrice" class="original-price">¥{{ order.originalPrice }}</span>
            </div>
          </div>
          <div class="order-amount-info">
            <div class="amount-row">
              <span class="amount-label">Order Amount</span>
              <span class="amount-value">¥{{ order.orderAmount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Shipping Address -->
      <div class="address-section">
        <div class="section-title">Shipping Address</div>
        <div class="address-card">
          <div class="address-line">
            <span class="receiver-name">{{ order.receiverName }}</span>
            <span class="receiver-phone">{{ order.receiverPhone }}</span>
          </div>
          <div class="address-detail">
            {{ order.receiverProvince || '' }}{{ order.receiverCity || '' }}{{ order.receiverDistrict || '' }}{{ order.receiverDetailAddress || '' }}
          </div>
          <div v-if="order.receiverPostalCode" class="postal-code">
            Postal Code: {{ order.receiverPostalCode }}
          </div>
        </div>
      </div>

      <!-- Shipping Information -->
      <div class="shipping-section" v-if="order.shippingStatus === '1'">
        <div class="section-title">Shipping Information</div>
        <div class="shipping-card">
          <div class="shipping-row">
            <span class="label">Express Company:</span>
            <span class="value">{{ order.expressCompany || 'N/A' }}</span>
          </div>
          <div class="shipping-row">
            <span class="label">Tracking Number:</span>
            <span class="value">{{ order.expressNo || 'N/A' }}</span>
          </div>
        </div>
      </div>

      <!-- Order Remarks -->
      <div class="remark-section" v-if="order.remark">
        <div class="section-title">Order Remarks</div>
        <div class="remark-content">{{ order.remark }}</div>
      </div>

      <!-- Review Information (Displayed when order is completed) -->
      <div class="review-section" v-if="order.orderStatus === 'completed'">
        <div class="section-title">Review Information</div>
        <div class="reviews-list" v-if="orderReviews.length > 0">
          <!-- Buyer's review of seller -->
          <div v-if="buyerReview" class="review-item">
            <div class="review-header">
              <div class="reviewer-info">
                <span class="reviewer-label">Buyer Reviews Seller</span>
                <el-rate
                  v-model="buyerReview.rating"
                  :max="5"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value} points"
                />
              </div>
              <span class="review-time">{{ formatDateTime(buyerReview.createTime) }}</span>
            </div>
            <div v-if="buyerReview.content" class="review-content">{{ buyerReview.content }}</div>
            <div v-if="buyerReview.reviewImages" class="review-images">
              <el-image
                v-for="(img, index) in getReviewImages(buyerReview.reviewImages)"
                :key="index"
                :src="img"
                fit="cover"
                class="review-image"
                :preview-src-list="getReviewImages(buyerReview.reviewImages)"
                :initial-index="index"
              />
            </div>
          </div>

          <!-- Seller's review of buyer -->
          <div v-if="sellerReview" class="review-item">
            <div class="review-header">
              <div class="reviewer-info">
                <span class="reviewer-label">Seller Reviews Buyer</span>
                <el-rate
                  v-model="sellerReview.rating"
                  :max="5"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value} points"
                />
              </div>
              <span class="review-time">{{ formatDateTime(sellerReview.createTime) }}</span>
            </div>
            <div v-if="sellerReview.content" class="review-content">{{ sellerReview.content }}</div>
            <div v-if="sellerReview.reviewImages" class="review-images">
              <el-image
                v-for="(img, index) in getReviewImages(sellerReview.reviewImages)"
                :key="index"
                :src="img"
                fit="cover"
                class="review-image"
                :preview-src-list="getReviewImages(sellerReview.reviewImages)"
                :initial-index="index"
              />
            </div>
          </div>
        </div>
        <el-empty v-else description="No reviews" :image-size="80" />
      </div>

      <!-- Action Buttons -->
      <div class="action-section">
        <!-- Buyer Actions -->
        <template v-if="!isSellerView">
          <el-button
            v-if="order.orderStatus === 'pending'"
            type="primary"
            size="default"
            @click="handlePay"
          >
            Pay Now
          </el-button>
          <el-button
            v-if="order.orderStatus === 'pending'"
            type="default"
            size="default"
            @click="handleCancel"
          >
            Cancel Order
          </el-button>
          <el-button
            v-if="order.orderStatus === 'shipped'"
            type="primary"
            size="default"
            @click="handleConfirmReceive"
          >
            Confirm Receipt
          </el-button>
          <!-- Can apply for return/refund in paid, shipped, completed status, but not in refunded/returned status -->
          <el-button
            v-if="['paid', 'shipped', 'completed'].includes(order.orderStatus) && !hasReturnRequest && order.orderStatus !== 'refunded' && order.orderStatus !== 'returned'"
            type="warning"
            size="default"
            @click="showReturnDialog = true"
          >
            Apply Return/Refund
          </el-button>
          <!-- Fill return shipping (when return status is approved) -->
          <el-button
            v-if="canFillReturnShipping"
            type="primary"
            size="default"
            @click="showShippingDialog = true"
          >
            Fill Shipping
          </el-button>
          <!-- Refunded status: View refund details -->
          <el-button
            v-if="order.orderStatus === 'refunded' && order.returnId"
            type="primary"
            size="default"
            @click="handleViewReturnDetail"
          >
            View Refund Details
          </el-button>
          <el-button
            v-else-if="order.orderStatus === 'refunded'"
            type="info"
            size="default"
            disabled
          >
            Refunded
          </el-button>
          <!-- Return/refund status display -->
          <el-button
            v-if="order.orderStatus === 'returned'"
            type="info"
            size="default"
            disabled
          >
            Returned
          </el-button>
          <!-- Completed status: Review seller -->
          <el-button
            v-if="order.orderStatus === 'completed' && canReviewAsBuyer"
            type="primary"
            size="default"
            @click="openReviewDialog('buyer')"
          >
            Review Seller
          </el-button>
          <el-button
            v-if="order.orderStatus === 'completed' && hasBuyerReview && !canReviewAsBuyer"
            type="info"
            size="default"
            disabled
          >
            Reviewed
          </el-button>
        </template>

        <!-- Seller Actions -->
        <template v-if="isSellerView">
          <el-button
            v-if="order.orderStatus === 'paid'"
            type="primary"
            size="default"
            @click="openShipDialog"
          >
            Ship Now
          </el-button>
          <!-- Completed status: Review buyer -->
          <el-button
            v-if="order.orderStatus === 'completed' && canReviewAsSeller"
            type="primary"
            size="default"
            @click="openReviewDialog('seller')"
          >
            Review Buyer
          </el-button>
          <el-button
            v-if="order.orderStatus === 'completed' && hasSellerReview && !canReviewAsSeller"
            type="info"
            size="default"
            disabled
          >
            Reviewed
          </el-button>
        </template>

        <el-button
          type="default"
          size="default"
          @click="router.back()"
        >
          Back
        </el-button>
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

    <!-- Apply Return Dialog -->
    <el-dialog
      v-model="showReturnDialog"
      :title="returnForm.receiveStatus === 'not_received' ? 'Apply Refund' : 'Apply Return/Refund'"
      width="600px"
      append-to-body
      class="return-dialog"
    >
      <div class="return-notice">
        <el-alert
          :title="returnForm.receiveStatus === 'not_received' ? 'Refund Notice' : 'Return/Refund Notice'"
          type="warning"
          :closable="false"
          show-icon
        >
          <template #default>
            <div class="notice-content">
              <template v-if="returnForm.receiveStatus === 'not_received'">
                <p>1. Refund without receiving goods, no need to return items</p>
                <p>2. After seller approval, refund will be directly credited to your account</p>
                <p>3. Refund amount is the actual payment amount of the order</p>
                <p>4. Refund will arrive within 1-3 business days after seller approval</p>
              </template>
              <template v-else>
                <p>1. Returned items must remain in original condition, not affecting resale</p>
                <p>2. Return shipping costs must be borne by yourself</p>
                <p>3. After seller approves return, please fill in return shipping information promptly</p>
                <p>4. Refund will be processed after seller confirms receipt</p>
              </template>
            </div>
          </template>
        </el-alert>
      </div>

      <el-form :model="returnForm" :rules="returnRules" ref="returnFormRef" label-width="100px" class="return-form">
        <el-form-item label="Receipt Status" prop="receiveStatus">
          <el-radio-group v-model="returnForm.receiveStatus">
            <el-radio label="received">Received Goods (Return/Refund)</el-radio>
            <el-radio label="not_received">Not Received Goods (Refund Only)</el-radio>
          </el-radio-group>
          <div class="form-tip">
            <el-text type="info" size="small">
              {{ returnForm.receiveStatus === 'not_received' ? 'Select refund only when goods not received, no need to return items' : 'Select return/refund when goods received, need to return items' }}
            </el-text>
          </div>
        </el-form-item>
        <el-form-item :label="returnForm.receiveStatus === 'not_received' ? 'Refund Reason' : 'Return Reason'" prop="returnReason">
          <el-select v-model="returnForm.returnReason" :placeholder="returnForm.receiveStatus === 'not_received' ? 'Please select refund reason' : 'Please select return reason'" style="width: 100%">
            <el-option v-if="returnForm.receiveStatus === 'not_received'" label="Goods Not Received" value="未收到货" />
            <el-option v-if="returnForm.receiveStatus === 'not_received'" label="Logistics Issue" value="物流问题" />
            <el-option v-if="returnForm.receiveStatus === 'not_received'" label="Seller Not Shipped" value="卖家未发货" />
            <el-option v-if="returnForm.receiveStatus === 'received'" label="Product Quality Issue" value="质量问题" />
            <el-option v-if="returnForm.receiveStatus === 'received'" label="Product Doesn't Match Description" value="与描述不符" />
            <el-option v-if="returnForm.receiveStatus === 'received'" label="Received Damaged Goods" value="商品破损" />
            <el-option v-if="returnForm.receiveStatus === 'received'" label="Size Not Suitable" value="尺寸不合适" />
            <el-option v-if="returnForm.receiveStatus === 'received'" label="Don't Like Color/Style" value="不喜欢" />
            <el-option label="Other Reason" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item :label="returnForm.receiveStatus === 'not_received' ? 'Refund Description' : 'Return Description'" prop="returnDescription">
          <el-input
            v-model="returnForm.returnDescription"
            type="textarea"
            :rows="4"
            :placeholder="returnForm.receiveStatus === 'not_received' ? 'Please describe refund reason in detail (optional)' : 'Please describe return reason in detail (optional)'"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item v-if="returnForm.receiveStatus === 'received'" label="Return Images">
          <ImageUpload
            v-model="returnForm.returnImages"
            :limit="3"
            :fileSize="5"
            :fileType="['png', 'jpg', 'jpeg']"
            :isShowTip="true"
          />
          <div class="upload-tip">Upload up to 3 images, each not exceeding 5MB</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReturnDialog = false">Cancel</el-button>
        <el-button type="primary" @click="handleReturnSubmit" :loading="returnLoading">Submit Application</el-button>
      </template>
    </el-dialog>

    <!-- Fill Return Shipping Dialog -->
    <el-dialog
      v-model="showShippingDialog"
      title="Fill Return Shipping Information"
      width="500px"
      append-to-body
    >
      <el-form :model="shippingForm" :rules="shippingRules" ref="shippingFormRef" label-width="100px">
        <el-form-item label="Express Company" prop="expressCompany">
          <el-input
            v-model="shippingForm.expressCompany"
            placeholder="Please enter express company name"
          />
        </el-form-item>
        <el-form-item label="Tracking Number" prop="expressNo">
          <el-input
            v-model="shippingForm.expressNo"
            placeholder="Please enter tracking number"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showShippingDialog = false">Cancel</el-button>
        <el-button type="primary" @click="handleShippingSubmit" :loading="shippingLoading">Confirm</el-button>
      </template>
    </el-dialog>

    <!-- Seller Ship Dialog -->
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

    <!-- Review Dialog -->
    <ReviewDialog
      v-model="showReviewDialog"
      :order-id="order.orderId"
      :reviewer-type="reviewerType"
      @success="handleReviewSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import ImageUpload from '@/components/ImageUpload'
import ReviewDialog from './ReviewDialog.vue'
import { getOrder, cancelOrder, confirmReceive, applyReturn, fillReturnShipping, shipOrder, approveReturn, rejectReturn, confirmReturnReceive, refundReturn, getOrderReturns, checkCanReview, getReviewsByOrderId } from '@/api/order'
import { getToken } from '@/utils/auth'
import useUserStore from '@/store/modules/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const order = ref({})
// Automatically determine if seller or buyer: prioritize URL parameter, if not then based on order info
const isSellerView = computed(() => {
  // If URL has role parameter, use it first
  if (route.query.role === 'seller') {
    return true
  }
  if (route.query.role === 'buyer') {
    return false
  }
  // If no role parameter, determine based on order info and current logged-in user
  if (order.value && order.value.sellerId && userStore.id) {
    return order.value.sellerId === userStore.id
  }
  // Default return false (buyer view)
  return false
})

// Return information
const returnInfo = ref(null)

// Determine if there is a return request
const hasReturnRequest = computed(() => {
  return order.value.orderStatus === 'return_requested' || 
         order.value.returnId || 
         order.value.returnStatus ||
         returnInfo.value !== null
})

// Determine if can fill shipping (return status is approved and is return_refund type)
const canFillReturnShipping = computed(() => {
  if (!returnInfo.value) return false
  return returnInfo.value.returnStatus === 'approved' && 
         returnInfo.value.returnType === 'return_refund' &&
         !isSellerView.value
})

// Review related
const showReviewDialog = ref(false)
const reviewerType = ref('buyer')
const canReviewAsBuyer = ref(false)
const canReviewAsSeller = ref(false)
const hasBuyerReview = ref(false)
const hasSellerReview = ref(false)
const orderReviews = ref([])
const buyerReview = computed(() => {
  return orderReviews.value.find(r => r.reviewerType === 'buyer')
})
const sellerReview = computed(() => {
  return orderReviews.value.find(r => r.reviewerType === 'seller')
})

// Cancel order related
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

// Return related
const showReturnDialog = ref(false)
const returnLoading = ref(false)
const returnForm = reactive({
  returnReason: '',
  returnDescription: '',
  returnImages: '',
  receiveStatus: 'received'
})
const returnFormRef = ref(null)

const returnRules = {
  receiveStatus: [
    { required: true, message: 'Please select receipt status', trigger: 'change' }
  ],
  returnReason: [
    { required: true, message: 'Please select return reason', trigger: 'change' }
  ]
}

// Return shipping related
const showShippingDialog = ref(false)
const shippingLoading = ref(false)
const shippingForm = reactive({
  expressCompany: '',
  expressNo: ''
})
const shippingFormRef = ref(null)

const shippingRules = {
  expressCompany: [
    { required: true, message: 'Please enter express company', trigger: 'blur' }
  ],
  expressNo: [
    { required: true, message: 'Please enter tracking number', trigger: 'blur' }
  ]
}

// Seller shipping
const shipDialogVisible = ref(false)
const shipLoading = ref(false)
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

// Get order details
function getOrderDetail() {
  const orderId = route.params.orderId
  if (!orderId) {
    ElMessage.error('Order ID does not exist')
    router.back()
    return
  }

  loading.value = true
  getOrder(orderId)
    .then((response) => {
      if (response && response.data) {
        order.value = response.data
        // If URL has no role parameter, automatically set role parameter based on order info
        if (!route.query.role && order.value.sellerId && userStore.id) {
          const role = order.value.sellerId === userStore.id ? 'seller' : 'buyer'
          router.replace({ query: { ...route.query, role } })
        }
        // If order has return/refund request, get return records to get returnId
        // Including: active returns, refunded, returned status
        if (order.value.orderStatus === 'return_requested' || 
            order.value.orderStatus === 'refunded' || 
            order.value.orderStatus === 'returned' ||
            order.value.returnId || 
            order.value.returnStatus) {
          getOrderReturns(orderId)
            .then((returnResponse) => {
              if (returnResponse && returnResponse.data && returnResponse.data.length > 0) {
                // For refunded/returned status, find completed refund record
                if (order.value.orderStatus === 'refunded' || order.value.orderStatus === 'returned') {
                  const completedReturn = returnResponse.data.find(r => 
                    r.returnStatus === 'completed' || 
                    r.returnStatus === 'refunded'
                  )
                  if (completedReturn) {
                    order.value.returnId = completedReturn.returnId
                    returnInfo.value = completedReturn // Save return info for status display
                  }
                } else {
                  // Find active return record
                  const activeReturn = returnResponse.data.find(r => 
                    r.returnStatus === 'requested' || 
                    r.returnStatus === 'approved' || 
                    r.returnStatus === 'shipped' ||
                    r.returnStatus === 'pending' ||
                    r.returnStatus === 'shipping' ||
                    r.returnStatus === 'received'
                  )
                  if (activeReturn) {
                    order.value.returnId = activeReturn.returnId
                    returnInfo.value = activeReturn // Save return info
                  }
                }
              }
            })
            .catch(() => {
              // Failure to get return record does not affect main process
            })
        }
        // If URL has action=return parameter, directly open return dialog
        if (route.query.action === 'return') {
          showReturnDialog.value = true
        }
        // If URL has action=fillShipping parameter, directly open fill shipping dialog
        if (route.query.action === 'fillShipping') {
          showShippingDialog.value = true
        }
        // If order is completed, check if can review and get review info
        if (order.value.orderStatus === 'completed') {
          checkReviewStatus()
          loadOrderReviews()
        }
      } else {
        ElMessage.error('Order does not exist')
        router.back()
      }
      loading.value = false
    })
    .catch((error) => {
      ElMessage.error(error.msg || 'Failed to get order details')
      loading.value = false
      router.back()
    })
}

// Check review status
function checkReviewStatus() {
  if (!order.value.orderId) {
    return
  }
  checkCanReview(order.value.orderId)
    .then((response) => {
      if (response && response.data) {
        canReviewAsBuyer.value = response.data.canReviewAsBuyer || false
        canReviewAsSeller.value = response.data.canReviewAsSeller || false
        hasBuyerReview.value = response.data.hasBuyerReview || false
        hasSellerReview.value = response.data.hasSellerReview || false
      }
    })
    .catch(() => {
      // Check failure does not affect main process
    })
}

// Open review dialog
function openReviewDialog(type) {
  reviewerType.value = type
  showReviewDialog.value = true
}

// Load order review information
function loadOrderReviews() {
  if (!order.value.orderId) {
    return
  }
  getReviewsByOrderId(order.value.orderId)
    .then((response) => {
      if (response && response.data) {
        orderReviews.value = response.data || []
      }
    })
    .catch(() => {
      // Failure to get review does not affect main process
    })
}

// Get review image list
function getReviewImages(imagesStr) {
  if (!imagesStr) {
    return []
  }
  return imagesStr.split(',').filter(img => img && img.trim())
}

// Review success callback
function handleReviewSuccess() {
  // Close dialog first
  showReviewDialog.value = false
  checkReviewStatus()
  loadOrderReviews()
  // Delay reload order details to update status, ensure dialog is closed
  setTimeout(() => {
    getOrderDetail()
  }, 100)
}

// Cancel order
function handleCancel() {
  cancelForm.cancelReason = ''
  cancelDialogVisible.value = true
}

// Confirm cancel order
function handleCancelConfirm() {
  cancelFormRef.value.validate((valid) => {
    if (!valid) return
    
    cancelLoading.value = true
    cancelOrder(order.value.orderNo, cancelForm.cancelReason)
      .then(() => {
        ElMessage.success('Order cancelled')
        // Close dialog first
        cancelDialogVisible.value = false
        // Reset form
        if (cancelFormRef.value) {
          cancelFormRef.value.resetFields()
        }
        cancelForm.cancelReason = ''
        // Delay reload order details, ensure dialog is closed
        setTimeout(() => {
          getOrderDetail()
        }, 100)
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
function handlePay() {
  router.push(`/product/payment/${order.value.orderNo}`)
}

// Seller ship order
function openShipDialog() {
  if (!order.value.orderNo) {
    ElMessage.warning('Order information incomplete')
    return
  }
  shipForm.expressCompany = ''
  shipForm.expressNo = ''
  shipDialogVisible.value = true
}

function handleShipSubmit() {
  shipFormRef.value.validate((valid) => {
    if (!valid) return
    shipLoading.value = true
    shipOrder(order.value.orderNo, shipForm.expressCompany, shipForm.expressNo)
      .then(() => {
        ElMessage.success('Shipment successful')
        // Close dialog first
        shipDialogVisible.value = false
        // Reset form
        if (shipFormRef.value) {
          shipFormRef.value.resetFields()
        }
        shipForm.expressCompany = ''
        shipForm.expressNo = ''
        // Delay reload order details, ensure dialog is closed
        setTimeout(() => {
          getOrderDetail()
        }, 100)
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
function handleConfirmReceive() {
  ElMessageBox.confirm('Confirm receipt of goods? You will not be able to apply for return/refund after confirmation.', 'Confirm Receipt', {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    type: 'warning'
  })
    .then(() => {
      confirmReceive(order.value.orderNo)
        .then(() => {
          ElMessage.success('Receipt confirmed')
          // Delay reload order details
          setTimeout(() => {
            getOrderDetail()
          }, 100)
        })
        .catch((error) => {
          ElMessage.error(error.msg || 'Failed to confirm receipt')
        })
    })
    .catch(() => {})
}

// Submit return application
function handleReturnSubmit() {
  returnFormRef.value.validate((valid) => {
    if (!valid) return
    
    returnLoading.value = true
    const images = returnForm.returnImages || ''
    
    const returnType = returnForm.receiveStatus === 'not_received' ? 'refund' : 'return_refund'
    applyReturn(order.value.orderId, returnType, returnForm.returnReason, returnForm.returnDescription, images, returnForm.receiveStatus)
      .then(() => {
        ElMessage.success('Return application submitted, please wait for seller review')
        // Close dialog first
        showReturnDialog.value = false
        // Reset form
        if (returnFormRef.value) {
          returnFormRef.value.resetFields()
        }
        returnForm.returnReason = ''
        returnForm.returnDescription = ''
        returnForm.returnImages = ''
        returnForm.receiveStatus = 'received'
        // Clear action parameter in URL to avoid reopening dialog on reload
        if (route.query.action === 'return') {
          const newQuery = { ...route.query }
          delete newQuery.action
          router.replace({ query: newQuery })
        }
        // Delay reload order details, ensure dialog is closed
        setTimeout(() => {
          getOrderDetail()
        }, 100)
      })
      .catch((error) => {
        ElMessage.error(error.msg || 'Failed to submit return application')
      })
      .finally(() => {
        returnLoading.value = false
      })
  })
}

// Fill return shipping
function handleShippingSubmit() {
  shippingFormRef.value.validate((valid) => {
    if (!valid) return
    
    // 获取退货记录的returnId
    if (!order.value.returnId) {
      ElMessage.error('Return record not found, please apply for return first')
      return
    }
    
    shippingLoading.value = true
    fillReturnShipping(order.value.returnId, shippingForm.expressCompany, shippingForm.expressNo)
      .then(() => {
        ElMessage.success('Return shipping information submitted')
        // 先关闭对话框
        showShippingDialog.value = false
        // 重置表单
        if (shippingFormRef.value) {
          shippingFormRef.value.resetFields()
        }
        shippingForm.expressCompany = ''
        shippingForm.expressNo = ''
        // 清除URL中的action参数，避免重新加载时再次打开对话框
        if (route.query.action === 'fillShipping') {
          const newQuery = { ...route.query }
          delete newQuery.action
          router.replace({ query: newQuery })
        }
        // 延迟重新加载订单详情，确保对话框已关闭
        setTimeout(() => {
          getOrderDetail()
        }, 100)
      })
      .catch((error) => {
        ElMessage.error(error.msg || 'Submission failed')
      })
      .finally(() => {
        shippingLoading.value = false
      })
  })
}




// View refund details
function handleViewReturnDetail() {
  if (!order.value.returnId) {
    ElMessage.warning('Refund record not found')
    return
  }
  router.push(`/order/return/${order.value.returnId}`)
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
  // If order status is return_requested, display different text based on return status
  if (status === 'return_requested' && returnInfo.value) {
    const returnStatus = returnInfo.value.returnStatus
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

// Get payment method text
function getPayMethodText(method) {
  const methodMap = {
    'alipay': 'Alipay',
    'wechat': 'WeChat Pay',
    'balance': 'Balance Payment'
  }
  return methodMap[method] || method
}

onMounted(() => {
  if (!getToken()) {
    ElMessage.warning('Please login first')
    router.push('/login')
    return
  }
  getOrderDetail()
})
</script>

<style scoped lang="scss">
.order-detail-container {
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

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.order-info-section {
  padding: 20px;
  background: #fafafa;
  border-radius: 6px;
  margin-bottom: 24px;
}

.info-row {
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

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #409eff;
}

.product-section,
.address-section,
.shipping-section,
.remark-section,
.review-section {
  margin-bottom: 24px;
}

.product-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  gap: 20px;
}

.product-image {
  width: 120px;
  height: 120px;
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

.product-info {
  flex: 1;
}

.product-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.product-price {
  font-size: 14px;
  color: #606266;

  .price-label {
    margin-right: 4px;
  }

  .price-value {
    font-size: 18px;
    font-weight: 600;
    color: #f56c6c;
    margin-right: 8px;
  }

  .original-price {
    font-size: 14px;
    color: #909399;
    text-decoration: line-through;
  }
}

.order-amount-info {
  text-align: right;
}

.amount-row {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.amount-label {
  font-size: 13px;
  color: #909399;
}

.amount-value {
  font-size: 20px;
  font-weight: 600;
  color: #f56c6c;
}

.address-card {
  padding: 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}

.address-line {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.receiver-phone {
  font-size: 14px;
  color: #606266;
}

.address-detail {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
}

.postal-code {
  font-size: 13px;
  color: #909399;
}

.shipping-card {
  padding: 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}

.shipping-row {
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

.remark-content {
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  font-size: 14px;
  color: #606266;
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

.return-dialog {
  .return-notice {
    margin-bottom: 20px;
  }

  .notice-content {
    p {
      margin: 4px 0;
      font-size: 13px;
      line-height: 1.6;
    }
  }

  .return-form {
    margin-top: 20px;
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 8px;
  }
}

/* 评价信息样式 */
.review-section {
  margin-bottom: 24px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reviewer-label {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.review-time {
  font-size: 12px;
  color: #909399;
}

.review-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
  white-space: pre-wrap;
  word-break: break-word;
}

.review-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.review-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #ebeef5;
}
</style>

