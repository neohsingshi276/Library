<template>
  <div class="message-container">
    <el-card class="message-card" shadow="never">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-content">
          <div class="title-section">
            <el-icon class="title-icon" :size="32"><Bell /></el-icon>
            <h2 class="page-title">Message Center</h2>
          </div>
          <p class="page-description">View system notifications and announcements</p>
        </div>
      </div>

      <!-- 标签页 -->
      <el-tabs 
        v-model="activeTab" 
        class="custom-tabs"
        @tab-change="handleTabChange"
      >
        <el-tab-pane name="notifications">
          <template #label>
            <span class="tab-label">
              <el-icon :size="18"><Message /></el-icon>
              <span>Notifications</span>
            </span>
          </template>
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>
          <NoticeList v-else :items="notifications" />
        </el-tab-pane>
        
        <el-tab-pane name="announcements">
          <template #label>
            <span class="tab-label">
              <el-icon :size="18"><Document /></el-icon>
              <span>Announcements</span>
            </span>
          </template>
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>
          <NoticeList v-else :items="announcements" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { noticelist, announcementlist } from '@/api/system/message.js';
import NoticeList from './NoticeList.vue';
import { Bell, Message, Document } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const activeTab = ref('notifications');
const notifications = ref([]);
const announcements = ref([]);
const loading = ref(false);

// 加载消息
const loadMessages = async () => {
  try {
    loading.value = true;
    const [noticesRes, announcementsRes] = await Promise.all([
      noticelist(), 
      announcementlist()
    ]);
    
    if (noticesRes && noticesRes.code === 200) {
      notifications.value = noticesRes.data || [];
    }
    
    if (announcementsRes && announcementsRes.code === 200) {
      announcements.value = announcementsRes.data || [];
    }
  } catch (error) {
    console.error("Failed to load messages", error);
    ElMessage.error('Failed to load messages, please try again later');
  } finally {
    loading.value = false;
  }
};

// 标签切换
const handleTabChange = (name) => {
  // 可以在这里添加切换逻辑，比如记录用户偏好等
};

onMounted(() => {
  loadMessages();
});
</script>

<style scoped lang="scss">
.message-container {
  padding: 24px;
  min-height: calc(100vh - 84px);
  background: linear-gradient(180deg, #f0f2f5 0%, #ffffff 100%);
  
  @media (max-width: 768px) {
    padding: 16px;
  }
}

.message-card {
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: none;
  
  :deep(.el-card__body) {
    padding: 0;
  }
}

.page-header {
  padding: 40px 40px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
    animation: pulse 20s infinite;
  }
  
  .header-content {
    position: relative;
    z-index: 1;
    
    .title-section {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 12px;
      
      .title-icon {
        color: #ffffff;
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
      }
      
      .page-title {
        margin: 0;
        font-size: 28px;
        font-weight: 700;
        color: #ffffff;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
        letter-spacing: 0.5px;
      }
    }
    
    .page-description {
      margin: 0;
      font-size: 15px;
      color: rgba(255, 255, 255, 0.95);
      padding-left: 48px;
      font-weight: 400;
    }
  }
  
  @media (max-width: 768px) {
    padding: 28px 20px 24px;
    
    .header-content {
      .title-section {
        gap: 12px;
        
        .title-icon {
          font-size: 24px;
        }
        
        .page-title {
          font-size: 22px;
        }
      }
      
      .page-description {
        padding-left: 0;
        font-size: 13px;
        margin-top: 8px;
      }
    }
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1) rotate(0deg);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.1) rotate(180deg);
    opacity: 0.5;
  }
}

.custom-tabs {
  padding: 0 40px 32px;
  background: #ffffff;
  
  :deep(.el-tabs__header) {
    margin: 24px 0 0 0;
    border-bottom: 2px solid #f0f2f5;
  }
  
  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }
  
  :deep(.el-tabs__item) {
    font-size: 16px;
    font-weight: 500;
    padding: 0 28px;
    height: 56px;
    line-height: 56px;
    color: #606266;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    
    &:hover {
      color: #409eff;
    }
    
    &.is-active {
      color: #409eff;
      font-weight: 600;
    }
  }
  
  :deep(.el-tabs__active-bar) {
    height: 3px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    border-radius: 2px 2px 0 0;
  }
  
  :deep(.el-tabs__content) {
    padding: 32px 0 0 0;
  }
  
  .tab-label {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: inherit;
    
    .tab-badge {
      margin-left: 4px;
    }
  }
  
  @media (max-width: 768px) {
    padding: 0 20px 24px;
    
    :deep(.el-tabs__item) {
      font-size: 14px;
      padding: 0 16px;
      height: 48px;
      line-height: 48px;
    }
    
    :deep(.el-tabs__content) {
      padding: 24px 0 0 0;
    }
  }
}

.loading-container {
  padding: 40px 20px;
  background: #ffffff;
  
  @media (max-width: 768px) {
    padding: 30px 16px;
  }
}
</style>