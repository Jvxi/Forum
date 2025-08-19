<script setup>
import {get} from "@/net";
import {ElMessage} from "element-plus";
import {useStore} from "@/stores";
import router from "@/router";
import { ref, onMounted } from 'vue'

//存储用户信息的全局变量
const store = useStore()
const isLoading = ref(true)

//把存在localStorage中的user取出来
const user = localStorage.getItem('user')

if(user != null){//如果存储的用户信息不为空，就将用户信息恢复回store里
  store.auth.user=JSON.parse(user)//localStorage中的是json格式，因此还要解析
}

onMounted(() => {
  // 确保页面完全加载后再隐藏加载状态
  setTimeout(() => {
    isLoading.value = false
  }, 100)
})
</script>

<template>
  <div id="app" v-mouse-trail>
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-content">
        <div class="loading-spinner"></div>
        <div class="loading-text">加载中...</div>
      </div>
    </div>
    
    <!-- 主要内容 -->
    <router-view v-slot="{ Component, route }">
      <component :is="Component" :key="route.path" />
    </router-view>
  </div>
</template>

<style scoped>
#app {
  min-height: 100vh;
  background: #f8fafc;
  position: relative;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #f8fafc;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-content {
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #12bfa0;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

.loading-text {
  color: #64748b;
  font-size: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
