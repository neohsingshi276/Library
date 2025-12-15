<template>
  <div :class="classObj" class="app-wrapper" :style="{ '--current-color': theme }">
    <!-- Fixed header container at top -->
    <div class="header-container">
      <sidebar />
      <navbar @setLayout="setLayout" />
    </div>
    
    <!-- Main content area -->
    <div :class="{ hasTagsView: needTagsView, sidebarHide: sidebar.hide }" class="main-container">
      <app-main />
      <settings ref="settingRef" />
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onBeforeUnmount, watchEffect } from 'vue';
import { useWindowSize } from '@vueuse/core';
import Sidebar from './components/Sidebar/index.vue';
import { AppMain, Navbar, Settings } from './components';

import useAppStore from '@/store/modules/app';
import useSettingsStore from '@/store/modules/settings';

const settingsStore = useSettingsStore();
const theme = computed(() => settingsStore.theme);
const sidebar = computed(() => useAppStore().sidebar);
const device = computed(() => useAppStore().device);
const needTagsView = computed(() => settingsStore.tagsView);

const classObj = computed(() => ({
  hideSidebar: !sidebar.value.opened,
  openSidebar: sidebar.value.opened,
  withoutAnimation: sidebar.value.withoutAnimation,
  mobile: device.value === 'mobile'
}));

const { width } = useWindowSize();
const WIDTH = 992; // refer to Bootstrap's responsive design

watchEffect(() => {
  if (width.value - 1 < WIDTH) {
    useAppStore().toggleDevice('mobile');
    // Keep sidebar always expanded
    // do not close sidebar automatically on mobile
  } else {
    useAppStore().toggleDevice('desktop');
  }
});

function handleClickOutside() {
  useAppStore().closeSideBar({ withoutAnimation: false });
}

function isMobile() {
  return device.value === 'mobile';
}

const settingRef = ref(null);
function setLayout() {
  settingRef.value.openSetting();
}

// Listen to window size changes to determine if it's a mobile device
onMounted(() => {
  window.addEventListener('resize', handleResize);
});
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
});
function handleResize() {
  if (window.innerWidth < WIDTH) {
    useAppStore().toggleDevice('mobile');
  } else {
    useAppStore().toggleDevice('desktop');
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/styles/mixin.scss";
@import "@/assets/styles/variables.module.scss";

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.header-container {
  display: flex;
  align-items: center;
  background-color: #fff; /* Set background color according to your design requirements */
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  width: 100%;
  height: 60px; 
  position: fixed; /* Fixed position */
  top: 0;
  left: 0;
  z-index: 1000; /* Ensure it's on top */
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 50px; /* Considering header height */
  right: 0;
  z-index: 9;
  transition: width 0.28s;
}

.sidebar {
  width: $base-sidebar-width;
  background-color: var(--current-color); // Assuming you want the sidebar to have a background color
  height: 100%;
}

.navbar {
  flex-grow: 1;
  background-color: #fff; // Or any color you want
  height: 100%;
}

.main-container {
  padding-top: 50px; /* Ensure content starts below header */
}

/* Other original styles remain unchanged */
.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

.sidebarHide .fixed-header {
  width: 100%;
}

.mobile .fixed-header {
  width: 100%;
}
</style>