<template>
  <div class="main-layout" @click="handleClick">
    <div class="layout-container">
      <el-container class="main-container">
        <!-- 左侧导航栏 -->
        <el-aside class="sidebar" :class="{ 'sidebar-collapsed': hideMenu }">
          <!-- 网站Logo和名称 -->
          <div class="sidebar-header" :class="{ 'header-collapsed': hideMenu }">
            <div class="logo-container">
              <div class="logo-icon">✏️</div>
              <h1 class="site-title" v-show="!hideMenu">校园论坛</h1>
            </div>
          </div>
          
          <!-- 导航菜单 -->
          <el-menu
            :default-active="menuActiveIndex"
            class="sidebar-menu"
            :class="{ 'menu-collapsed': hideMenu }"
            router
          >
            <el-menu-item index="/index/posts" class="menu-item">
              <el-icon class="menu-icon"><Menu /></el-icon>
              <span class="menu-text">帖子列表</span>
            </el-menu-item>
            
            <el-menu-item index="/index/likes" class="menu-item">
              <el-icon class="menu-icon"><Document /></el-icon>
              <span class="menu-text">校园表白墙</span>
            </el-menu-item>
            
            <el-menu-item
              index="/index/manage"
              v-if="showEditButton"
              class="menu-item"
            >
              <el-icon class="menu-icon"><Help /></el-icon>
              <span class="menu-text">帖子管理</span>
            </el-menu-item>
            
            <el-menu-item index="/index/favorites" class="menu-item">
              <el-icon class="menu-icon"><Compass/></el-icon>
              <span class="menu-text">我的收藏</span>
            </el-menu-item>
            
            <el-menu-item index="/index/settings" class="menu-item">
              <el-icon class="menu-icon"><Tools /></el-icon>
              <span class="menu-text">个人设置</span>
            </el-menu-item>
          </el-menu>

          <!-- 侧边栏底部用户区域 -->
          <div class="sidebar-footer">
            <el-dropdown trigger="click" placement="top-start" :teleported="false">
              <el-avatar 
                :src="avatarImgSrc" 
                class="sidebar-avatar"
                :size="40"
                @error="onAvatarError"
              />
              <template #dropdown>
                <el-dropdown-menu class="user-dropdown">
                  <el-dropdown-item @click="router.push('/index/developer')">
                    开发者信息
                  </el-dropdown-item>
                  <el-dropdown-item :icon="SwitchButton" @click="logout">
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <div class="sidebar-username" v-show="!hideMenu">{{store.auth.user.username}}</div>
          </div>
        </el-aside>

        <!-- 主内容区域 -->
        <el-container class="main-content">
          <!-- 顶部导航栏 -->
          <el-header class="top-header">
            <!-- 侧边栏切换按钮 -->
            <div class="header-left">
              <el-button 
                class="toggle-btn"
                :icon="hideMenu ? Expand : Fold" 
                text 
                @click="hideMenu = !hideMenu"
              />
            </div>
            
            <div class="header-right"></div>
          </el-header>
          
          <!-- 主内容区域 -->
          <el-main class="content-main">
            <router-view v-slot="{ Component, route: r }">
              <keep-alive>
                <component :is="Component" :key="r.path" />
              </keep-alive>
            </router-view>
          </el-main>
        </el-container>

        <!-- 点击特效容器 -->
        <transition-group 
          name="click-effect"
          tag="div"
          class="click-effects-container"
        >
          <img
            v-for="effect in clickEffects"
            :key="effect.id"
            class="click-effect-icon"
            :src="effect.icon"
            :style="{
              left: `${effect.x - 25}px`,
              top: `${effect.y - 25}px`
            }"
          />
        </transition-group>
      </el-container>
      
      
    </div>
  </div>
</template>

<script setup>
  import {get} from "@/net"
  import {ElMessage} from "element-plus";
  import router from "@/router";
  import axios from 'axios'
  import {useStore} from "@/stores";
  import {User, SwitchButton, Fold, 
          Compass, Menu, Expand, Document, Help, 
          Tools, Loading } from "@element-plus/icons-vue";
  import {ref, reactive, onMounted, computed, watch, nextTick } from "vue";

  const store = useStore()
  const showEditButton = ref(false)
  const hideMenu = ref(false)
  const menuActiveIndex = ref('/index/posts')

  // 点击特效相关
  const clickEffects = ref([])
  const localIcons = {
    star: '/images/cat.png',
    heart: '/images/earth.png',
    sparkle: '/images/emoji.png'
  }
  const iconSizes = {
    star: { width: 34, height: 34 },
    heart: { width: 28, height: 28 },
    sparkle: { width: 30, height: 30 }
  }

  // 当前高亮项：将子路由统一归一到父级路径，保证正确高亮
  const activeIndex = computed(() => {
    const route = router.currentRoute.value
    const path = route?.path || '/index/posts'
    const name = route?.name || ''
    
    // 使用路由名称进行更精确的匹配
    if (name === 'index-manage') {
      return '/index/manage'
    }
    if (name === 'index-likes') {
      return '/index/likes'
    }
    if (name === 'index-favorites') {
      return '/index/favorites'
    }
    if (name === 'index-settings') {
      return '/index/settings'
    }
    if (name === 'index-developer') {
      return '' // 开发者页面返回空字符串
    }
    if (name === 'index-posts' || name === 'index-post-create' || name === 'index-post-detail' || name === 'index-post-edit') {
      return '/index/posts'
    }
    
    // 如果路由名称匹配失败，回退到路径匹配
    if (path.startsWith('/index/manage')) return '/index/manage'
    if (path.startsWith('/index/likes')) return '/index/likes'
    if (path.startsWith('/index/favorites')) return '/index/favorites'
    if (path.startsWith('/index/settings')) return '/index/settings'
    if (path.startsWith('/index/developer')) return ''
    if (path.startsWith('/index/posts')) return '/index/posts'
    
    return '/index/posts'
  })

  // 监听路由变化，确保菜单高亮正确更新
  watch(() => router.currentRoute.value, (newRoute) => {
    nextTick(() => {
      menuActiveIndex.value = activeIndex.value
    })
  }, { immediate: true, deep: true })


  // 计算用户头像URL
  const avatarUrl = computed(() => {
    const uploaded = store.auth.user?.avatar
    if (uploaded && uploaded.length > 0) return uploaded

    const sex = store.auth.user?.sex
    switch (sex) {
      case 'M': return '/images/男.png'
      case 'F': return '/images/女.png'
      default: return '/images/未.png'
    }
  });

  // 防缓存参数与错误降级
  const cacheBust = ref(Date.now())
  const avatarImgSrc = computed(() => `${avatarUrl.value}?v=${cacheBust.value}`)
  const onAvatarError = () => {
    // 发生错误时强制回退到性别默认头像并刷新缓存
    const sex = store.auth.user?.sex || 'U'
    const fallback = sex === 'M' ? '/images/男.png' : (sex === 'F' ? '/images/女.png' : '/images/未.png')
    cacheBust.value = Date.now()
    // 若原本就是默认头像，仍通过更新时间戳触发刷新
    setTimeout(() => { cacheBust.value = Date.now() }, 0)
  }

  // 点击事件处理
  const handleClick = (event) => {
    const effectId = Date.now()
    const icons = Object.values(localIcons)
    const iconType = Math.random() > 0.5 ? 'star' : 'heart'
    const duration = Math.random() * 500 + 500

    const chosenIcon = icons[Math.floor(Math.random() * icons.length)]
    const chosenSize = iconSizes[iconType] || { width: 30, height: 30 }
    clickEffects.value.push({
      x: event.clientX + Math.random() * 20 - 10,
      y: event.clientY + Math.random() * 20 - 10,
      id: effectId,
      icon: chosenIcon,
      size: chosenSize,
      animation: `click-effect ${duration}ms ease-out`
    })
    setTimeout((currentId) => {
      clickEffects.value = clickEffects.value.filter(e => e.id !== currentId)
    }, duration - 100 , effectId)
  }

  

  onMounted(() => {
    if (store.auth.user && !store.auth.user.sex) {
      get('/api/user/info', (message) => {
        store.auth.user.sex = message.sex || 'U'
        localStorage.setItem('user', JSON.stringify(store.auth.user))
      })
    }
    Object.values(localIcons).forEach(url => {
      new Image().src = url
    })
  });

  if (localStorage.getItem('user')){
    store.auth.user = JSON.parse(localStorage.getItem('user'))
    showEditButton.value = store.auth.user.username === '管理'
  }

  const logout = ()=>{
    get('/api/auth/logout',(message) =>{
      ElMessage.success(message)
      store.auth.user = null
      localStorage.removeItem('user')
      sessionStorage.clear();
      router.push('/').then(() => location.reload());
    }, (error) => {
      ElMessage.error(error.message || "退出失败，请重试");
    })
  }
</script>

<style scoped>
/* 主布局样式 */
.main-layout {
  height: 100vh;
  background: linear-gradient(135deg, var(--bg-start) 0%, var(--bg-end) 100%);
  overflow: hidden;
  position: relative;
}

.layout-container {
  height: 100%;
  position: relative;
  background: inherit;
}

.main-container {
  height: 100%;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  position: relative;
}

/* 侧边栏样式 */
.sidebar {
  width: 280px;
  background: linear-gradient(180deg, var(--sidebar-start) 0%, var(--sidebar-end) 100%);
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.1);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(255,255,255,0.1) 0%, transparent 50%, rgba(255,255,255,0.05) 100%);
  pointer-events: none;
}

.sidebar-collapsed {
  width: 80px !important;
}

/* 侧边栏头部 */
.sidebar-header {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.4s ease;
}

.header-collapsed {
  padding: 0 10px;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.4s ease;
}

.logo-icon {
  font-size: 32px;
  animation: logoFloat 3s ease-in-out infinite;
}

.site-title {
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  white-space: nowrap;
  transition: all 0.4s ease;
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

/* 侧边栏菜单 */
.sidebar-menu {
  height: calc(100% - 80px);
  border: none;
  background: transparent;
  padding: 20px 0;
}

.menu-item {
  margin: 8px 16px;
  border-radius: 12px;
  height: 50px;
  line-height: 50px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s;
}

.menu-item:hover::before {
  left: 100%;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(4px);
  box-shadow: 0 6px 18px rgba(0,0,0,0.18);
}

.menu-item.is-active {
  background: linear-gradient(135deg, var(--brand-start) 0%, var(--brand-end) 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(18, 191, 160, 0.35);
}

.menu-icon {
  font-size: 18px;
  margin-right: 12px;
  transition: all 0.3s ease;
}

.menu-text {
  font-weight: 500;
  transition: all 0.3s ease;
}

.menu-collapsed .menu-text {
  display: none; /* 折叠时不占位，避免图标被文字占位挤偏 */
}

/* 折叠菜单高亮对齐修正 */
.menu-collapsed .menu-item {
  margin: 8px 8px;
}
.menu-collapsed .menu-item:hover {
  transform: none;
}
.menu-collapsed .el-menu-item {
  padding-left: 12px !important;
  padding-right: 12px !important;
  justify-content: center;
  align-items: center;
}
.menu-collapsed .menu-icon {
  margin-right: 0;
}

/* 侧边栏底部用户区域 */
.sidebar-footer {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
}
.sidebar-avatar {
  border: 3px solid rgba(255,255,255,0.25);
}
.sidebar-username {
  color: #ffffff;
  font-weight: 600;
  white-space: nowrap;
}

/* 主内容区域 */
.main-content {
  background: #f8fafc;
  position: relative;
}

/* 顶部导航栏 */
.top-header {
  height: 80px;
  background: white;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-btn {
  font-size: 20px;
  color: #64748b;
  transition: all 0.3s ease;
  border-radius: 8px;
  padding: 8px;
}

.toggle-btn:hover {
  background: #f1f5f9;
  color: #475569;
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  text-align: right;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-weight: 600;
  font-size: 16px;
  color: #1e293b;
}

.user-email {
  font-size: 13px;
  color: #64748b;
}

.user-avatar-container {
  position: relative;
}

.user-avatar {
  border: 3px solid #e2e8f0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.user-avatar:hover {
  border-color: #667eea;
  transform: scale(1.1);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.user-dropdown {
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  border: none;
}

/* 主内容区域 */
.content-main {
  padding: 0;
  background: #f8fafc;
  position: relative;
}

/* 点击特效 */
.click-effects-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 0;
  height: 0;
  pointer-events: none;
  z-index: 9999;
}

.click-effect-icon {
  position: absolute;
  width: 30px;
  height: 30px;
  pointer-events: none;
  animation: clickEffect 0.8s ease-out forwards;
}

@keyframes clickEffect {
  0% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(0.5) rotate(0deg);
  }
  70% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(1.2) rotate(180deg);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.8) rotate(360deg);
  }
}

.click-effect-enter-active,
.click-effect-leave-active {
  transition: all 0.3s ease;
}

.click-effect-enter-from {
  opacity: 0;
  transform: scale(0);
}

.click-effect-leave-to {
  opacity: 0;
  transform: scale(0);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 240px;
  }
  
  .sidebar-collapsed {
    width: 60px !important;
  }
  
  .top-header {
    padding: 0 20px;
  }
  
  .user-info {
    display: none;
  }
  
}

@media (max-width: 480px) {
  .sidebar {
    width: 200px;
  }
  
  .sidebar-collapsed {
    width: 50px !important;
  }
  
  .top-header {
    padding: 0 15px;
  }
  
  .toggle-btn {
    font-size: 18px;
  }
}

/* 加载占位符样式 */
.loading-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #64748b;
  font-size: 16px;
}

.loading-icon {
  font-size: 48px;
  margin-bottom: 20px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>