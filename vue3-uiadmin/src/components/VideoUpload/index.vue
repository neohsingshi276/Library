<template>
  <div class="component-upload-video">
    <el-upload
      :action="uploadVideoUrl"
      :before-upload="handleBeforeUpload"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      :show-file-list="false"
      :headers="headers"
      accept="video/*"
      ref="videoUpload"
    >
      <el-button type="primary" :icon="Upload">选择视频</el-button>
    </el-upload>
    <div class="el-upload__tip" style="margin-top: 8px;" v-if="showTip">
      仅支持 {{ fileType.join('/') }} 格式，大小不超过 {{ fileSize }}MB
    </div>
    <div v-if="videoUrl" style="margin-top: 12px;" class="video-preview">
      <video :src="fullVideoUrl" controls style="max-width: 100%; max-height: 300px; border-radius: 4px;">
        您的浏览器不支持视频播放
      </video>
      <div style="margin-top: 8px; color: #909399; font-size: 12px;">
        <el-link :href="fullVideoUrl" target="_blank" type="primary" :underline="false">
          {{ videoUrl }}
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, watch } from 'vue';
import { getToken } from '@/utils/auth';
import { Upload } from '@element-plus/icons-vue';

const props = defineProps({
  modelValue: String,
  fileSize: { type: Number, default: 300 }, // MB
  fileType: { type: Array, default: () => ['mp4', 'avi', 'mov', 'wmv', 'flv', 'webm'] },
  isShowTip: { type: Boolean, default: true }
});

const emit = defineEmits(['update:modelValue']);
const { proxy } = getCurrentInstance();
const baseUrl = import.meta.env.VITE_APP_BASE_API;
const uploadVideoUrl = baseUrl + '/common/uploadVideo';
const headers = { Authorization: 'Bearer ' + getToken() };
const videoUrl = computed(() => props.modelValue);

// 处理资源URL，支持阿里云OSS和本地存储
const fullVideoUrl = computed(() => {
  if (!videoUrl.value) return '';
  const url = String(videoUrl.value).trim();
  
  // 如果是完整URL（http/https），直接使用（阿里云OSS）
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  
  // 如果是相对路径（以/开头），拼接基础API路径（本地存储）
  if (url.startsWith('/')) {
    return baseUrl + url;
  }
  
  // 其他情况，拼接基础API路径
  return baseUrl + '/' + url;
});

const showTip = computed(() => props.isShowTip);

// 监听 modelValue 变化，确保视频URL正确显示
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    console.log('VideoUpload - 资源URL更新:', {
      original: newVal,
      fullUrl: fullVideoUrl.value,
      baseUrl: baseUrl
    });
  }
}, { immediate: true });

function handleBeforeUpload(file) {
  // 校验类型
  const ext = file.name.split('.').pop().toLowerCase();
  if (!props.fileType.includes(ext)) {
    proxy.$modal.msgError(`仅支持${props.fileType.join('/')}格式的视频文件！`);
    return false;
  }
  // 校验大小
  if (file.size / 1024 / 1024 > props.fileSize) {
    proxy.$modal.msgError(`视频大小不能超过${props.fileSize}MB！`);
    return false;
  }
  proxy.$modal.loading('正在上传视频，请稍候...');
  return true;
}

function handleUploadSuccess(res) {
  proxy.$modal.closeLoading();
  if (res.code === 200) {
    // 返回的文件名可能是完整URL或相对路径
    const fileName = res.fileName || res.url || '';
    emit('update:modelValue', fileName);
    proxy.$modal.msgSuccess('上传成功');
    console.log('VideoUpload - 上传成功:', {
      fileName: fileName,
      fullUrl: fullVideoUrl.value
    });
  } else {
    proxy.$modal.msgError(res.msg || '上传失败');
  }
}

function handleUploadError() {
  proxy.$modal.closeLoading();
  proxy.$modal.msgError('上传失败');
}
</script>

<style scoped lang="scss">
.component-upload-video {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
  
  .video-preview {
    width: 100%;
    padding: 8px;
    background: #f5f7fa;
    border-radius: 4px;
    border: 1px solid #e4e7ed;
    
    video {
      display: block;
      width: 100%;
      max-width: 600px;
      background: #000;
    }
  }
}
</style>

