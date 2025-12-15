<template>
  <div class="notice-list-container">
    <div v-if="items.length === 0" class="empty-state">
      <el-empty description="No announcements" :image-size="120" />
    </div>
    <div v-else class="notice-list">
      <div 
        v-for="(item, index) in items" 
        :key="item.noticeId || index" 
        class="notice-item"
        @click="handleItemClick(item)"
      >
        <div class="notice-item-header">
          <div class="notice-icon">
            <el-icon :size="24"><Bell /></el-icon>
          </div>
          <div class="notice-content">
            <h3 class="notice-title">{{ item.noticeTitle }}</h3>
            <div class="notice-meta">
              <span class="meta-item">
                <el-icon :size="14"><User /></el-icon>
                <span>{{ item.createBy || 'System Administrator' }}</span>
              </span>
              <span class="meta-item">
                <el-icon :size="14"><Clock /></el-icon>
                <span>{{ formatTime(item.createTime) }}</span>
              </span>
            </div>
          </div>
        </div>
        <div class="notice-item-footer">
          <router-link 
            :to="`/user/message/${item.noticeId}`" 
            class="view-more"
            @click.stop
          >
            <span>View Details</span>
            <el-icon :size="14"><ArrowRight /></el-icon>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';
import { Bell, User, Clock, ArrowRight } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 定义props
const props = defineProps({
  items: {
    type: Array,
    required: true,
    default: () => []
  }
});

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (days === 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60));
    if (hours === 0) {
      const minutes = Math.floor(diff / (1000 * 60));
      return minutes <= 0 ? 'Just now' : `${minutes} minutes ago`;
    }
    return `${hours} hours ago`;
  } else if (days === 1) {
    return 'Yesterday';
  } else if (days < 7) {
    return `${days} days ago`;
  } else {
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  }
};

// 处理点击事件
const handleItemClick = (item) => {
  router.push(`/user/message/${item.noticeId}`);
};
</script>

<style scoped lang="scss">
.notice-list-container {
  width: 100%;
}

.empty-state {
  padding: 80px 20px;
  text-align: center;
  background: #ffffff;
  border-radius: 12px;
  
  @media (max-width: 768px) {
    padding: 60px 16px;
  }
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  
  @media (max-width: 768px) {
    gap: 16px;
  }
}

.notice-item {
  background: #ffffff;
  border-radius: 14px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #e8ecf0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
    background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
    transform: scaleY(0);
    transition: transform 0.3s ease;
  }
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
    border-color: #667eea;
    
    &::before {
      transform: scaleY(1);
    }
    
    .notice-icon {
      transform: scale(1.05) rotate(5deg);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    }
    
    .view-more {
      color: #667eea;
      gap: 8px;
    }
  }
  
  .notice-item-header {
    display: flex;
    align-items: flex-start;
    gap: 18px;
    margin-bottom: 16px;
    
    .notice-icon {
      flex-shrink: 0;
      width: 52px;
      height: 52px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 12px;
      color: #ffffff;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
    }
    
    .notice-content {
      flex: 1;
      min-width: 0;
      
      .notice-title {
        margin: 0 0 10px 0;
        font-size: 17px;
        font-weight: 600;
        color: #1a1a1a;
        line-height: 1.6;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        transition: color 0.3s ease;
      }
      
      .notice-meta {
        display: flex;
        flex-wrap: wrap;
        gap: 20px;
        font-size: 13px;
        color: #8b8e94;
        
        .meta-item {
          display: flex;
          align-items: center;
          gap: 6px;
          transition: color 0.3s ease;
          
          .el-icon {
            color: #b4b7bd;
            transition: color 0.3s ease;
          }
          
          &:hover {
            color: #667eea;
            
            .el-icon {
              color: #667eea;
            }
          }
        }
      }
    }
  }
  
  .notice-item-footer {
    display: flex;
    justify-content: flex-end;
    padding-top: 16px;
    border-top: 1px solid #f0f2f5;
    margin-top: 4px;
    
    .view-more {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #606266;
      font-size: 14px;
      font-weight: 500;
      text-decoration: none;
      transition: all 0.3s ease;
      
      &:hover {
        color: #667eea;
        gap: 8px;
      }
    }
  }
  
  @media (max-width: 768px) {
    padding: 18px;
    border-radius: 12px;
    
    .notice-item-header {
      gap: 14px;
      margin-bottom: 14px;
      
      .notice-icon {
        width: 44px;
        height: 44px;
        border-radius: 10px;
      }
      
      .notice-content {
        .notice-title {
          font-size: 16px;
          margin-bottom: 8px;
        }
        
        .notice-meta {
          font-size: 12px;
          gap: 16px;
        }
      }
    }
    
    .notice-item-footer {
      padding-top: 14px;
      
      .view-more {
        font-size: 13px;
      }
    }
  }
}
</style>