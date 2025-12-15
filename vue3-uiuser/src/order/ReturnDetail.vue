<template>
  <div class="return-detail" v-loading="loading">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>Return Details</span>
          <el-tag :type="statusType(returnInfo.returnStatus)">
            {{ statusText(returnInfo.returnStatus) }}
          </el-tag>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="Return No.">{{ returnInfo.returnNo }}</el-descriptions-item>
        <el-descriptions-item label="Order No.">{{ returnInfo.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="Return Reason" :span="2">{{ returnInfo.returnReason }}</el-descriptions-item>
        <el-descriptions-item label="Return Description" :span="2">{{ returnInfo.returnDescription || '—' }}</el-descriptions-item>
        <el-descriptions-item label="Refund Amount">{{ returnInfo.returnAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="Application Time">{{ formatDate(returnInfo.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="Order Status">{{ statusText(returnInfo.orderStatus) }}</el-descriptions-item>
        <el-descriptions-item label="Shipping Status">{{ returnInfo.shippingStatus === '1' ? 'Shipped' : 'Not Shipped' }}</el-descriptions-item>
      </el-descriptions>

      <div class="actions">
        <el-button @click="router.back()">Back</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getReturnDetail } from '@/api/order'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const returnInfo = ref({})

function loadDetail() {
  const id = route.params.returnId
  if (!id) {
    ElMessage.error('Return ID is missing')
    router.back()
    return
  }
  loading.value = true
  getReturnDetail(id)
    .then((res) => {
      if (res && res.data) {
        returnInfo.value = res.data
      } else if (res) {
        returnInfo.value = res
      } else {
        ElMessage.error('Return record does not exist')
        router.back()
      }
    })
    .catch((err) => {
      ElMessage.error(err.msg || err.message || 'Failed to get return details')
      router.back()
    })
    .finally(() => {
      loading.value = false
    })
}

function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${dd} ${hh}:${mm}`
}

function statusText(status) {
  const map = {
    pending: 'Pending Review',
    approved: 'Pending Return',
    shipping: 'Pending Receipt Confirmation',
    completed: 'Completed',
    rejected: 'Rejected',
    cancelled: 'Cancelled',
    shipped: 'Pending Receipt'
  }
  return map[status] || status || ''
}

function statusType(status) {
  const map = {
    pending: 'warning',
    approved: 'info',
    shipping: 'primary',
    completed: 'success',
    rejected: 'danger',
    cancelled: 'info',
    shipped: 'primary'
  }
  return map[status] || ''
}

onMounted(loadDetail)
</script>

<style scoped>
.return-detail {
  padding: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.actions {
  margin-top: 16px;
  text-align: right;
}
</style>
