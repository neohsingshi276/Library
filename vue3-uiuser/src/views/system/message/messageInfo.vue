<template>
  <div class="message-detail-container">
    <el-card class="detail-card" shadow="never">
      <!-- 返回按钮 -->
      <div class="header-actions">
        <el-button 
          type="primary" 
          :icon="ArrowLeft" 
          @click="handleBack"
          plain
        >
          Back to List
        </el-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="8" animated />
      </div>

      <!-- 内容区域 -->
      <div v-else-if="message.noticeTitle" class="message-content">
        <!-- 标题区域 -->
        <div class="message-header">
          <div class="title-section">
            <el-icon class="title-icon" :size="28"><Document /></el-icon>
            <h1 class="message-title">{{ message.noticeTitle }}</h1>
          </div>
        </div>

        <!-- 元信息区域 -->
        <div class="message-meta">
          <div class="meta-card">
            <div class="meta-item">
              <el-icon class="meta-icon" :size="18"><User /></el-icon>
              <div class="meta-content">
                <span class="meta-label">Publisher</span>
                <span class="meta-value">{{ message.createBy || 'System Administrator' }}</span>
              </div>
            </div>
            <div class="meta-item">
              <el-icon class="meta-icon" :size="18"><Clock /></el-icon>
              <div class="meta-content">
                <span class="meta-label">Publish Time</span>
                <span class="meta-value">{{ formatTime(message.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 内容区域 -->
        <div class="message-body">
          <div class="content-wrapper">
            <div 
              class="notice-content" 
              v-html="message.noticeContent"
            ></div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="Announcement content does not exist" :image-size="120" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getMessageInfo } from '@/api/system/message.js';
import { ArrowLeft, Document, User, Clock } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();
const id = route.params.noticeId;
const message = ref({});
const loading = ref(true);

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 获取详情
const getInfo = async () => {
  try {
    loading.value = true;
    const res = await getMessageInfo(id);
    if (res && res.code === 200 && res.data) {
      message.value = res.data;
    } else {
      ElMessage.error('Failed to get announcement details');
    }
  } catch (error) {
    console.error('Failed to get message details:', error);
    ElMessage.error('Failed to get announcement details, please try again later');
  } finally {
    loading.value = false;
  }
};

// 返回列表
const handleBack = () => {
  router.push('/message');
};

onMounted(() => {
  getInfo();
});
</script>

<style scoped lang="scss">
.message-detail-container {
  padding: 24px;
  min-height: calc(100vh - 84px);
  background: linear-gradient(180deg, #f0f2f5 0%, #ffffff 100%);
  
  @media (max-width: 768px) {
    padding: 16px;
  }
}

.detail-card {
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
  
  :deep(.el-card__body) {
    padding: 32px;
    background: #ffffff;
    
    @media (max-width: 768px) {
      padding: 24px;
    }
  }
}

.header-actions {
  margin-bottom: 28px;
  padding-bottom: 24px;
  border-bottom: 2px solid #f0f2f5;
  
  @media (max-width: 768px) {
    margin-bottom: 24px;
    padding-bottom: 20px;
  }
}

.loading-container {
  padding: 40px 20px;
  
  @media (max-width: 768px) {
    padding: 30px 16px;
  }
}

.message-content {
  .message-header {
    margin-bottom: 28px;
    padding-bottom: 24px;
    border-bottom: 2px solid #f0f2f5;
    
    .title-section {
      display: flex;
      align-items: flex-start;
      gap: 16px;
      
      .title-icon {
        color: #667eea;
        flex-shrink: 0;
        margin-top: 4px;
        filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.2));
      }
      
      .message-title {
        margin: 0;
        font-size: 32px;
        font-weight: 700;
        color: #1a1a1a;
        line-height: 1.4;
        flex: 1;
        letter-spacing: -0.5px;
        
        @media (max-width: 768px) {
          font-size: 24px;
        }
      }
    }
  }

  .message-meta {
    margin-bottom: 32px;
    
    .meta-card {
      display: flex;
      gap: 32px;
      padding: 24px;
      background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
      border-radius: 12px;
      border: 1px solid #e8ecf0;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 14px;
        flex: 1;
        
        .meta-icon {
          color: #667eea;
          flex-shrink: 0;
          background: rgba(102, 126, 234, 0.1);
          padding: 8px;
          border-radius: 8px;
        }
        
        .meta-content {
          display: flex;
          flex-direction: column;
          gap: 6px;
          
          .meta-label {
            font-size: 12px;
            color: #8b8e94;
            font-weight: 500;
            text-transform: uppercase;
            letter-spacing: 0.5px;
          }
          
          .meta-value {
            font-size: 15px;
            color: #1a1a1a;
            font-weight: 600;
          }
        }
      }
      
      @media (max-width: 768px) {
        flex-direction: column;
        gap: 20px;
        padding: 20px;
      }
    }
  }

  .message-body {
    .content-wrapper {
      padding: 32px;
      background: #ffffff;
      border-radius: 12px;
      border: 1px solid #e8ecf0;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
      
      .notice-content {
        font-size: 16px;
        line-height: 1.9;
        color: #2c3e50;
        word-wrap: break-word;
        
        @media (max-width: 768px) {
          font-size: 15px;
          padding: 20px;
        }
        
        // 美化HTML内容样式
        :deep(p) {
          margin: 0 0 16px 0;
          
          &:last-child {
            margin-bottom: 0;
          }
        }
        
        :deep(img) {
          max-width: 100%;
          height: auto;
          border-radius: 8px;
          margin: 16px 0;
        }
        
        :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
          margin: 24px 0 16px 0;
          color: #303133;
          font-weight: 600;
        }
        
        :deep(ul), :deep(ol) {
          padding-left: 24px;
          margin: 16px 0;
        }
        
        :deep(li) {
          margin: 8px 0;
        }
        
        :deep(blockquote) {
          margin: 16px 0;
          padding: 12px 16px;
          background: #f5f7fa;
          border-left: 4px solid #409eff;
          border-radius: 4px;
        }
        
        :deep(code) {
          padding: 2px 6px;
          background: #f5f7fa;
          border-radius: 4px;
          font-family: 'Courier New', monospace;
          font-size: 14px;
        }
        
        :deep(pre) {
          padding: 16px;
          background: #f5f7fa;
          border-radius: 8px;
          overflow-x: auto;
          margin: 16px 0;
          
          code {
            background: transparent;
            padding: 0;
          }
        }
        
        :deep(a) {
          color: #409eff;
          text-decoration: none;
          
          &:hover {
            text-decoration: underline;
          }
        }
        
        :deep(table) {
          width: 100%;
          border-collapse: collapse;
          margin: 16px 0;
          
          th, td {
            padding: 12px;
            border: 1px solid #ebeef5;
            text-align: left;
          }
          
          th {
            background: #f5f7fa;
            font-weight: 600;
          }
        }
      }
    }
  }
}
</style>