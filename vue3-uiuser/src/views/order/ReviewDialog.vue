<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="600px"
    :before-close="handleClose"
  >
    <el-form
      ref="reviewFormRef"
      :model="reviewForm"
      :rules="reviewRules"
      label-width="100px"
    >
      <el-form-item label="Rating" prop="rating" required>
        <el-rate
          v-model="reviewForm.rating"
          :max="5"
          :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
          show-score
          text-color="#ff9900"
          score-template="{value} points"
        />
      </el-form-item>

      <el-form-item label="Review Content" prop="content">
        <el-input
          v-model="reviewForm.content"
          type="textarea"
          :rows="5"
          placeholder="Please enter review content (optional)"
          maxlength="1000"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="Review Images">
        <ImageUpload
          v-model="reviewForm.reviewImages"
          :limit="6"
          :fileSize="5"
          :fileType="['png', 'jpg', 'jpeg']"
          :isShowTip="true"
        />
        <div class="upload-tip">Upload up to 6 images, each not exceeding 5MB</div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          Submit Review
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { createReview } from '@/api/order'
import ImageUpload from '@/components/ImageUpload'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  orderId: {
    type: Number,
    default: null
  },
  reviewerType: {
    type: String,
    default: 'buyer' // 'buyer' 或 'seller'
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const submitting = ref(false)
const reviewFormRef = ref(null)

const reviewForm = reactive({
  rating: 5,
  content: '',
  reviewImages: ''
})

const reviewRules = {
  rating: [
    { required: true, message: 'Please select rating', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => {
  return props.reviewerType === 'buyer' ? 'Review Seller' : 'Review Buyer'
})

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    resetForm()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

function resetForm() {
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewForm.reviewImages = ''
  if (reviewFormRef.value) {
    reviewFormRef.value.resetFields()
  }
}

function handleClose() {
  visible.value = false
  resetForm()
}

function handleSubmit() {
  if (!reviewFormRef.value) {
    return
  }

  reviewFormRef.value.validate((valid) => {
    if (!valid) {
      return false
    }

    if (!props.orderId) {
      ElMessage.error('Order ID does not exist')
      return
    }

    submitting.value = true
    createReview({
      orderId: props.orderId,
      rating: reviewForm.rating,
      content: reviewForm.content || null,
      reviewImages: reviewForm.reviewImages || null
    })
      .then(response => {
        ElMessage.success('Review submitted successfully')
        emit('success')
        handleClose()
      })
      .catch(error => {
        ElMessage.error(error.msg || 'Review submission failed')
      })
      .finally(() => {
        submitting.value = false
      })
  })
}
</script>

<style scoped>
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.dialog-footer {
  text-align: right;
}
</style>

