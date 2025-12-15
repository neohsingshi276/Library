<template>
  <div class="sidebar-logo-container" :class="{ 'collapse': collapse }" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <h1 v-else class="sidebar-title" :style="{ color: sideTheme === 'theme-dark' ? variables.logoTitleColor : variables.logoLightTitleColor }">{{ title }}</h1>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <h1 class="sidebar-title" :style="{ color: sideTheme === 'theme-dark' ? variables.logoTitleColor : variables.logoLightTitleColor }">{{ title }}</h1>
      </router-link>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import variables from '@/assets/styles/variables.module.scss'
import defaultLogo from '@/assets/logo/logo.png'
import useSettingsStore from '@/store/modules/settings'
import { getResourceBySceneAndType } from '@/api/system/resourceConfig'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

const title = import.meta.env.VITE_APP_TITLE;
const settingsStore = useSettingsStore();
const sideTheme = computed(() => settingsStore.sideTheme);
const logo = ref(defaultLogo);

// 处理资源URL
function processResourceUrl(url) {
  if (!url) return defaultLogo;
  
  url = String(url).trim();
  
  // 如果是完整URL（http/https），直接使用（阿里云OSS）
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  
  // 如果是相对路径（以/开头），拼接基础API路径（本地存储）
  if (url.startsWith('/')) {
    const baseUrl = import.meta.env.VITE_APP_BASE_API || '';
    return baseUrl + url;
  }
  
  // 其他情况，拼接基础API路径
  const baseUrl = import.meta.env.VITE_APP_BASE_API || '';
  return baseUrl + '/' + url;
}

// 获取Logo资源
async function getLogoResource() {
  try {
    const res = await getResourceBySceneAndType('logo', 'image');
    if (res && res.code === 200) {
      let resource = null;
      
      // 处理返回的数据结构
      if (Array.isArray(res.data)) {
        if (res.data.length > 0) {
          resource = res.data[0];
        }
      } else if (res.data && typeof res.data === 'object') {
        if (res.data.resourceType) {
          resource = res.data;
        } else if (Array.isArray(res.data.rows)) {
          resource = res.data.rows.length > 0 ? res.data.rows[0] : null;
        }
      }
      
      if (resource && resource.resourceUrl) {
        logo.value = processResourceUrl(resource.resourceUrl);
      }
    }
  } catch (error) {
    console.error('获取Logo资源失败:', error);
    // 失败时使用默认logo
    logo.value = defaultLogo;
  }
}

onMounted(() => {
  getLogoResource();
});
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  background: #2b2f3a;
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      width: 32px;
      height: 32px;
      vertical-align: middle;
      margin-right: 12px;
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: #fff;
      font-weight: 600;
      line-height: 50px;
      font-size: 14px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }
}
</style>