<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="title">AI Recognition Publishing · Second-hand Clothing</div>
        <div class="sub">Upload Image → AI Auto Recognition → Quick Publish</div>
      </div>
      <div class="header-actions">
        <el-button 
          v-if="!publishSuccess" 
          type="primary" 
          :loading="submitting" 
          :disabled="!canPublish"
          @click="handleSubmit"
          size="large"
        >
          Submit Publish
        </el-button>
        <el-button 
          v-else 
          type="success" 
          @click="handleContinuePublish"
          size="large"
          :icon="Refresh"
        >
          Continue Publishing
        </el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 左侧：表单输入区域 -->
      <el-col :span="14">
        <el-card shadow="never" class="card">
          <template #header>
            <div class="card-header">
              <span>Product Information</span>
            </div>
          </template>
          <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
            <el-form-item label="Product Image" required>
              <ImageUpload v-model="images" :limit="1" :file-size="5" />
              <div class="upload-tip">Only 1 image is supported, image size should not exceed 5MB</div>
            </el-form-item>

            <el-form-item label="Product Title" prop="title">
              <el-input 
                v-model="form.title" 
                placeholder="e.g.: Japanese Cotton Shirt / Outdoor Jacket"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>

            <!-- 价格信息 -->
            <el-divider content-position="left">
              <span style="color: #606266; font-size: 14px; font-weight: 500">Price Information</span>
            </el-divider>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="Original Price(¥)" prop="originalPrice">
                  <el-input-number 
                    v-model="form.originalPrice" 
                    :min="0" 
                    :precision="2" 
                    :step="10" 
                    placeholder="Please enter original price"
                    style="width: 100%"
                    controls-position="right"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Sale Price(¥)" prop="salePrice">
                  <el-input-number 
                    v-model="form.salePrice" 
                    :min="0" 
                    :precision="2" 
                    :step="1" 
                    placeholder="Please enter sale price"
                    style="width: 100%"
                    controls-position="right"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 商品属性 -->
            <el-divider content-position="left">
              <span style="color: #606266; font-size: 14px; font-weight: 500">Product Attributes</span>
            </el-divider>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="Condition">
                  <el-select 
                    v-model="form.conditionLevel" 
                    placeholder="Select condition" 
                    style="width: 100%"
                    clearable
                  >
                    <el-option label="Brand New" value="全新" />
                    <el-option label="90% New" value="9成新" />
                    <el-option label="80% New" value="8成新" />
                    <el-option label="70% New" value="7成新" />
                    <el-option label="60% New or Below" value="6成新及以下" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Brand">
                  <el-input 
                    v-model="form.brand" 
                    placeholder="e.g.: Uniqlo / Nike"
                    maxlength="100"
                    show-word-limit
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="Size">
                  <el-input 
                    v-model="form.size" 
                    placeholder="e.g.: M / 42 / 27"
                    maxlength="20"
                    show-word-limit
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="Product Description">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="4"
                :disabled="true"
                placeholder="Product description is automatically generated by AI"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：AI识别结果展示 -->
      <el-col :span="10">
        <el-card shadow="never" class="card result-card">
          <template #header>
            <div class="card-header">
              <el-icon><Check /></el-icon>
              <span>AI Recognition Result</span>
            </div>
          </template>
          <div v-if="aiResult" class="ai-result">
            <div class="result-item">
              <el-tag v-if="aiResultData.isClothing" type="success" size="large">
                <el-icon style="margin-right: 4px"><CircleCheck /></el-icon>
                Recognized as Clothing
              </el-tag>
              <el-tag v-else type="warning" size="large">
                <el-icon style="margin-right: 4px"><Warning /></el-icon>
                Not Recognized as Clothing
              </el-tag>
            </div>
            
            <el-divider v-if="aiResultData.isClothing" />
            
            <!-- 只有识别为衣物时才显示类别、颜色、风格 -->
            <template v-if="aiResultData.isClothing">
              <div v-if="aiResultData.category" class="result-item">
                <div class="result-label">Category</div>
                <div class="result-value">
                  <el-tag>{{ aiResultData.category }}</el-tag>
                  <el-tag v-if="aiResultData.subCategory" type="info" style="margin-left: 8px">
                    {{ aiResultData.subCategory }}
                  </el-tag>
                </div>
              </div>
              
              <div v-if="aiResultData.color" class="result-item">
                <div class="result-label">Color</div>
                <div class="result-value">{{ aiResultData.color }}</div>
              </div>
              
              <div v-if="aiResultData.style" class="result-item">
                <div class="result-label">Style</div>
                <div class="result-value">{{ aiResultData.style }}</div>
              </div>
            </template>
            
            <!-- AI描述始终显示 -->
            <el-divider />
            <div v-if="aiResultData.description" class="result-item">
              <div class="result-label">AI Analysis</div>
              <div class="description-text">{{ aiResultData.description }}</div>
            </div>
            <div v-else-if="aiResult" class="result-item">
              <div class="result-label">AI Analysis</div>
              <div class="description-text">{{ aiResult }}</div>
            </div>
          </div>
          <div v-else class="empty-result">
            <el-empty description="Will automatically recognize after uploading image" :image-size="100" />
          </div>
        </el-card>

        <!-- 发布成功提示 -->
        <el-card v-if="publishSuccess" shadow="never" class="card success-card" style="margin-top: 16px">
          <el-result icon="success" title="Published Successfully" sub-title="Product has been published successfully, you can continue to publish other products">
            <template #extra>
              <el-button type="primary" @click="handleContinuePublish">Continue Publishing</el-button>
            </template>
          </el-result>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, Refresh, CircleCheck, Warning } from '@element-plus/icons-vue'
import { aiCreateProduct, aiDetectProduct } from '@/api/product'
import ImageUpload from '@/components/ImageUpload'

const formRef = ref()
const submitting = ref(false)
const publishSuccess = ref(false)
const images = ref('')
const aiResult = ref('')
const aiResultData = ref({
  isClothing: false,
  category: '',
  subCategory: '',
  color: '',
  style: '',
  description: '',
  brand: '',
  size: ''
})

const form = ref({
  title: '',
  originalPrice: null,
  salePrice: null,
  description: '',
  brand: '',
  size: '',
  conditionLevel: ''
})

const rules = {
  title: [{ required: true, message: 'Please enter title', trigger: 'blur' }],
  salePrice: [{ required: true, message: 'Please enter sale price', trigger: 'change' }]
}

// 计算是否可以发布：只有识别为衣物时才允许发布
const canPublish = computed(() => {
  return aiResult.value && aiResultData.value.isClothing === true
})

// 解析AI识别结果
function parseAIResult(aiResponse) {
  try {
    // 尝试从JSON字符串中提取JSON对象
    let jsonStr = aiResponse
    if (jsonStr.includes('```json')) {
      const start = jsonStr.indexOf('```json') + 7
      const end = jsonStr.indexOf('```', start)
      if (end > start) {
        jsonStr = jsonStr.substring(start, end).trim()
      }
    } else if (jsonStr.includes('```')) {
      const start = jsonStr.indexOf('```') + 3
      const end = jsonStr.indexOf('```', start)
      if (end > start) {
        jsonStr = jsonStr.substring(start, end).trim()
      }
    }
    
    // 提取JSON对象
    const jsonStart = jsonStr.indexOf('{')
    const jsonEnd = jsonStr.lastIndexOf('}')
    if (jsonStart >= 0 && jsonEnd > jsonStart) {
      jsonStr = jsonStr.substring(jsonStart, jsonEnd + 1)
    }
    
    const data = JSON.parse(jsonStr)
    return {
      isClothing: data.isClothing || false,
      category: data.category || '',
      subCategory: data.subCategory || '',
      color: data.color || '',
      style: data.style || '',
      description: data.description || '',
      brand: data.brand || '',
      size: data.size || ''
    }
  } catch (e) {
    console.warn('解析AI结果失败:', e)
    return {
      isClothing: false,
      category: '',
      subCategory: '',
      color: '',
      style: '',
      description: '',
      brand: '',
      size: ''
    }
  }
}

// 监听图片上传，自动触发AI识别（防抖处理）
let detectTimer = null
watch(images, (newVal) => {
  if (detectTimer) {
    clearTimeout(detectTimer)
  }
  if (newVal && newVal.split(',').filter(Boolean).length > 0) {
    detectTimer = setTimeout(() => {
      handleAIDetect()
    }, 500) // 延迟500ms，避免频繁调用
  }
})

// AI识别（仅识别，不发布）
async function handleAIDetect() {
  if (!images.value) {
    return
  }
  
  const imageList = images.value.split(',').filter(Boolean)
  if (imageList.length === 0) {
    return
  }
  
  // 如果已经发布成功，不自动识别
  if (publishSuccess.value) {
    return
  }
  
  try {
    submitting.value = true
    // 使用仅识别接口，不创建商品
    const payload = {
      imageUrls: imageList
    }
    const { data } = await aiDetectProduct(payload)
    
    // 解析AI识别结果
    if (data.aiResponse) {
      aiResult.value = data.aiResponse
      aiResultData.value = parseAIResult(data.aiResponse)
      
      // 自动填充表单（仅在字段为空时填充）
      if (!form.value.title && aiResultData.value.category) {
        form.value.title = aiResultData.value.category + (aiResultData.value.subCategory ? '-' + aiResultData.value.subCategory : '')
      }
      if (!form.value.brand && aiResultData.value.brand) {
        form.value.brand = aiResultData.value.brand
      }
      if (!form.value.size && aiResultData.value.size) {
        form.value.size = aiResultData.value.size
      }
      // 商品描述始终使用AI生成的描述（字段禁用，由AI自动填充）
      if (aiResultData.value.description) {
        form.value.description = aiResultData.value.description
      }
      
      // 根据识别结果显示提示
      if (aiResultData.value.isClothing) {
        ElMessage.success('AI recognition completed: Recognized as clothing, can publish')
      } else {
        ElMessage.warning('AI recognition completed: Not recognized as clothing, cannot publish')
      }
    } else {
      ElMessage.warning('AI recognition completed, but no result returned')
    }
  } catch (e) {
    ElMessage.error(e.msg || 'AI recognition failed')
    // 即使识别失败，也尝试显示错误信息
    if (e.msg && e.msg.includes('未识别为衣物')) {
      aiResultData.value.isClothing = false
      aiResultData.value.description = e.msg
      aiResult.value = e.msg
    }
  } finally {
    submitting.value = false
  }
}

// 提交发布
async function handleSubmit() {
  formRef.value.validate(async valid => {
    if (!valid) return
    if (!images.value) {
      ElMessage.error('Please upload image')
      return
    }
    
    // 检查是否识别为衣物
    if (!canPublish.value) {
      ElMessage.warning('Current image is not recognized as clothing, cannot publish. Please upload clothing image and try again.')
      return
    }
    
    submitting.value = true
    try {
      const imageList = images.value.split(',').filter(Boolean)
      const payload = {
        product: {
          title: form.value.title,
          originalPrice: form.value.originalPrice || 0,
          salePrice: form.value.salePrice,
          description: form.value.description,
          brand: form.value.brand,
          size: form.value.size,
          conditionLevel: form.value.conditionLevel,
          images: images.value
        },
        aiRequest: {
          imageUrls: imageList
        }
      }
      const { data } = await aiCreateProduct(payload)
      
      // 更新识别结果
      if (data.aiCheckResult) {
        aiResult.value = data.aiCheckResult
        aiResultData.value = parseAIResult(data.aiCheckResult)
        
        // 确保商品描述使用AI生成的描述
        if (aiResultData.value.description) {
          form.value.description = aiResultData.value.description
        }
      }
      
      // 标记发布成功
      publishSuccess.value = true
      ElMessage.success('Published successfully! You can continue to publish other products')
    } catch (e) {
      ElMessage.error(e.msg || 'Publish failed')
      publishSuccess.value = false
    } finally {
      submitting.value = false
    }
  })
}

// 继续发布（重置表单）
function handleContinuePublish() {
  // 重置表单
  form.value = {
    title: '',
    originalPrice: null,
    salePrice: null,
    description: '',
    brand: '',
    size: '',
    conditionLevel: ''
  }
  images.value = ''
  aiResult.value = ''
  aiResultData.value = {
    isClothing: false,
    category: '',
    subCategory: '',
    color: '',
    style: '',
    description: '',
    brand: '',
    size: ''
  }
  publishSuccess.value = false
  
  // 清除表单验证状态
  if (formRef.value) {
    formRef.value.clearValidate()
  }
  
  ElMessage.info('Form has been reset, you can continue to publish new products')
}
</script>

<style scoped>
.page {
  padding: 20px 24px 32px;
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}
.title {
  font-size: 22px;
  font-weight: 700;
  color: #1f2533;
  margin-bottom: 4px;
}
.sub {
  color: #909399;
  font-size: 14px;
}
.header-actions {
  display: flex;
  gap: 12px;
}
.card {
  border-radius: 12px;
  border: 1px solid #ebeef5;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}
.result-card {
  position: sticky;
  top: 20px;
}
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  line-height: 1.5;
}

/* 表单优化样式 */
:deep(.el-form-item) {
  margin-bottom: 22px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
  padding-right: 12px;
}

:deep(.el-divider) {
  margin: 24px 0 20px;
  border-color: #e4e7ed;
}

:deep(.el-divider__text) {
  background-color: #fff;
  padding: 0 16px;
  font-size: 13px;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-input-number__decrease),
:deep(.el-input-number__increase) {
  background-color: #f5f7fa;
  border-color: #dcdfe6;
}

:deep(.el-input-number__decrease:hover),
:deep(.el-input-number__increase:hover) {
  color: #409eff;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  border-radius: 6px;
  transition: all 0.3s;
}

:deep(.el-input__inner:focus),
:deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-select .el-input__inner) {
  cursor: pointer;
}
.ai-result {
  padding: 4px 0;
}
.result-item {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}
.result-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}
.result-label {
  font-weight: 600;
  color: #606266;
  font-size: 13px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}
.result-label::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 14px;
  background: #409eff;
  border-radius: 2px;
  margin-right: 8px;
}
.result-value {
  color: #303133;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}
.description-text {
  color: #606266;
  line-height: 1.8;
  font-size: 13px;
  background: #f8f9fa;
  padding: 12px;
  border-radius: 6px;
  border-left: 3px solid #409eff;
}
.empty-result {
  padding: 40px 20px;
  text-align: center;
}
.success-card {
  border: 2px solid #67c23a;
  background: linear-gradient(135deg, #f0f9ff 0%, #ffffff 100%);
}
</style>

