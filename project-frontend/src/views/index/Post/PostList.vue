<!-- 帖子列表页面 -->
<template>
  <div class="post-list-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">📚 帖子列表</h1>
        <p class="page-subtitle">发现精彩内容，分享你的想法</p>
      </div>
    </div>

    <!-- 搜索和过滤区域 -->
    <div class="filter-section">
      <!-- 搜索框 -->
      <div class="search-container">
        <el-input
          v-model="searchQuery"
          placeholder="🔍 搜索帖子标题或内容..."
          clearable
          @clear="searchQuery = ''"
          class="search-input"
          size="large"
        >
          <template #prefix>
            <el-icon class="search-icon"><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <!-- 分类过滤 -->
      <div class="category-container">
        <div class="category-label">📂 分类筛选：</div>
        <el-radio-group v-model="selectedCategory" class="category-group">
          <el-radio-button 
            v-for="category in categories" 
            :key="category" 
            :value="category"
            class="category-btn"
          >
            {{ category }}
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-section">
      <div class="stats-card">
        <div class="stats-icon">📊</div>
        <div class="stats-info">
          <div class="stats-number">{{ filteredPosts.length }}</div>
          <div class="stats-label">当前帖子</div>
        </div>
      </div>
      <div class="stats-card">
        <div class="stats-icon">🏷️</div>
        <div class="stats-info">
          <div class="stats-number">{{ categories.length - 1 }}</div>
          <div class="stats-label">分类数量</div>
        </div>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div class="posts-grid">
      <transition-group name="post-list" tag="div" class="posts-container">
        <el-card 
          v-for="(post, index) in filteredPosts" 
          :key="post.id"
          class="post-card"
          :style="{ animationDelay: `${index * 0.1}s` }"
          shadow="hover"
          @click="navigateToDetail(post.id)"
        >
          <template #header>
            <div class="post-header">
              <div class="post-title-section">
                <h3 class="post-title">{{ post.title }}</h3>
                <el-tag 
                  :type="getTagType(post.tagName)" 
                  size="small" 
                  class="post-tag"
                  effect="light"
                >
                  {{ post.tagName }}
                </el-tag>
              </div>
              <div class="post-meta">
                <div class="author-info">
                  <el-avatar :size="24" class="author-avatar" :src="resolvePostAvatar(post)" />
                  <span class="author-name">{{ post.authorName }}</span>
                </div>
                <div class="time-info">
                  <el-icon class="time-icon"><Clock /></el-icon>
                  <span class="time-text">{{ formatTime(post.createTime) }}</span>
                </div>
              </div>
            </div>
          </template>
          
          <div class="post-content">
            <div class="content-preview">{{ post.content }}</div>
            <div class="content-overlay">
              <div class="overlay-text">点击查看详情</div>
            </div>
          </div>
          
          <!-- 帖子操作按钮 -->
          <div class="post-actions">
            <el-button 
              type="primary" 
              size="small" 
              class="action-btn"
              @click.stop="navigateToDetail(post.id)"
            >
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
          </div>
        </el-card>
      </transition-group>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredPosts.length === 0" class="empty-state">
      <div class="empty-icon">📭</div>
      <h3 class="empty-title">暂无相关帖子</h3>
      <p class="empty-description">尝试调整搜索条件或分类筛选</p>
      <el-button type="primary" @click="clearFilters">清除筛选</el-button>
    </div>

    <!-- 添加帖子浮动按钮 -->
    <el-button 
      type="primary" 
      :icon="Plus" 
      circle 
      class="add-post-btn"
      @click="navigateToCreate"
    >
      <el-tooltip content="发布新帖子" placement="left">
        <span></span>
      </el-tooltip>
    </el-button>

    <!-- 回到顶部按钮 -->
    <el-button 
      v-show="showBackToTop"
      type="info" 
      :icon="Top" 
      circle 
      class="back-to-top-btn"
      @click="scrollToTop"
    />
  </div>
</template>

<script setup>
  import { ref, onMounted, computed, onUnmounted } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  import { ElMessage } from 'element-plus';
  import { 
    Plus, 
    Search, 
    Clock, 
    View, 
    Top 
  } from '@element-plus/icons-vue';

  const router = useRouter();
  const posts = ref([]);
  const searchQuery = ref('');
  const categories = ref(['全部']);
  const selectedCategory = ref('全部');
  const showBackToTop = ref(false);

  // 获取帖子数据
  const fetchPosts = async () => {
    try {
      const response = await axios.get('/api/posts/list');
      posts.value = response.data;
      const allCategories = [...new Set(posts.value.map(post => post.tagName))];
      categories.value = ['全部', ...allCategories];
    } catch (error) {
      console.error('获取帖子列表失败:', error);
      ElMessage.error('获取帖子列表失败');
    }
  };

  // 解析帖子作者头像（后端提供 post.avatar）
  const resolvePostAvatar = (post) => {
    const url = post?.avatar ? String(post.avatar) : ''
    if (url && url.length > 0) return url
    switch (post?.sex) {
      case 'M': return '/images/男.png'
      case 'F': return '/images/女.png'
      default: return '/images/未.png'
    }
  }

  // 过滤帖子列表
  const filteredPosts = computed(() => {
    let filtered = posts.value.filter(post => post.status === 'visible=');

    if (selectedCategory.value !== '全部') {
      filtered = filtered.filter(post => post.tagName === selectedCategory.value);
    }
    
    const query = searchQuery.value.toLowerCase().trim();
    if (query) {
      filtered = filtered.filter(post => 
        post.title.toLowerCase().includes(query) ||
        post.content.toLowerCase().includes(query)
      );
    }

    return filtered;
  });

  // 获取标签类型
  const getTagType = (tagName) => {
    const tagTypes = {
      '技术': 'success',
      '生活': 'warning',
      '学习': 'info',
      '娱乐': 'danger'
    };
    return tagTypes[tagName] || 'info';
  };

  // 导航方法
  const navigateToDetail = (postId) => {
    router.push({ name: 'index-post-detail', params: { id: postId }});
  };

  const navigateToCreate = () => {
    router.push({ name: 'index-post-create' });
  };

  // 清除筛选
  const clearFilters = () => {
    searchQuery.value = '';
    selectedCategory.value = '全部';
  };

  // 时间格式化
  const formatTime = (timeStr) => {
    const date = new Date(timeStr);
    const now = new Date();
    const diff = now - date;
    
    if (diff < 60000) return '刚刚';
    if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
    if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
    if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`;
    
    return date.toLocaleDateString();
  };

  // 滚动处理
  const handleScroll = () => {
    showBackToTop.value = window.scrollY > 300;
  };

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  };

  onMounted(() => {
    fetchPosts();
    window.addEventListener('scroll', handleScroll);
  });

  onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll);
  });
</script>

<style scoped>
.post-list-container {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--bg-start) 0%, var(--bg-end) 100%);
  padding: 20px;
  position: relative;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, var(--sidebar-start) 0%, var(--sidebar-end) 100%);
  border-radius: 20px;
  padding: 40px;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.page-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: headerFloat 6s ease-in-out infinite;
}

@keyframes headerFloat {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(-10px, -10px) rotate(1deg); }
}

.header-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 10px rgba(0,0,0,0.3);
  animation: titleGlow 3s ease-in-out infinite alternate;
}

@keyframes titleGlow {
  from { text-shadow: 0 2px 10px rgba(0,0,0,0.3); }
  to { text-shadow: 0 2px 20px rgba(255,255,255,0.5); }
}

.page-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin: 0;
  font-weight: 300;
}

.header-decoration {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 1;
}

.decoration-circle {
  width: 60px;
  height: 60px;
  border: 2px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  animation: circleRotate 10s linear infinite;
}

.decoration-line {
  width: 40px;
  height: 2px;
  background: rgba(255,255,255,0.3);
  margin-top: 10px;
  animation: linePulse 2s ease-in-out infinite;
}

@keyframes circleRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes linePulse {
  0%, 100% { opacity: 0.3; transform: scaleX(1); }
  50% { opacity: 0.6; transform: scaleX(1.2); }
}

/* 筛选区域 */
.filter-section {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  border: 1px solid rgba(255,255,255,0.8);
}

.search-container {
  margin-bottom: 20px;
}

.search-input {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-input:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(18, 191, 160, 0.2);
}

.search-icon {
  font-size: 1.2em;
  color: var(--brand-end);
}

.category-container {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.category-label {
  font-weight: 600;
  color: #374151;
  white-space: nowrap;
}

.category-group {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.category-btn {
  transition: all 0.3s ease;
  border-radius: 20px;
}

.category-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(18, 191, 160, 0.2);
}

/* 统计信息 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stats-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  border: 1px solid rgba(255,255,255,0.8);
  transition: all 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(18, 191, 160, 0.2);
}

.stats-icon {
  font-size: 2rem;
  animation: statsBounce 2s ease-in-out infinite;
}

@keyframes statsBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.stats-number {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--brand-end);
  line-height: 1;
}

.stats-label {
  font-size: 0.9rem;
  color: #64748b;
  margin-top: 2px;
}

/* 帖子网格 */
.posts-grid {
  position: relative;
}

.posts-container {
  display: grid;
  gap: 20px;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
}

/* 帖子卡片 */
.post-card {
  border-radius: 16px;
  border: none;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  background: white;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  cursor: pointer;
  animation: cardSlideIn 0.6s ease-out both;
}

.post-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(18, 191, 160, 0.2);
}

.post-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(18, 191, 160, 0.12), transparent);
  transition: left 0.6s;
}

.post-card:hover::before {
  left: 100%;
}

@keyframes cardSlideIn {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.post-header {
  padding: 0;
}

.post-title-section {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 15px;
}

.post-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  flex: 1;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-tag {
  flex-shrink: 0;
  border-radius: 12px;
  font-weight: 500;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  color: #64748b;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-avatar {
  border: 2px solid #e2e8f0;
}

.author-name {
  font-weight: 500;
  color: #475569;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 4px;
}

.time-icon {
  font-size: 0.8em;
}

.post-content {
  position: relative;
  padding: 20px 0;
  min-height: 80px;
}

.content-preview {
  color: #64748b;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: all 0.3s ease;
}

.content-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(34, 211, 169, 0.92), rgba(18, 191, 160, 0.92));
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  border-radius: 8px;
}

.post-card:hover .content-overlay {
  opacity: 1;
}

.overlay-text {
  color: white;
  font-weight: 600;
  font-size: 1.1rem;
  text-align: center;
}

.post-actions {
  padding: 15px 0 0;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  border-radius: 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(18, 191, 160, 0.3);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  animation: emptyBounce 2s ease-in-out infinite;
}

@keyframes emptyBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.empty-title {
  font-size: 1.5rem;
  color: #374151;
  margin: 0 0 10px 0;
}

.empty-description {
  color: #64748b;
  margin: 0 0 25px 0;
}

/* 浮动按钮 */
.add-post-btn {
  position: fixed;
  right: 40px;
  bottom: 40px;
  width: 60px;
  height: 60px;
  font-size: 24px;
  background: linear-gradient(135deg, var(--brand-start) 0%, var(--brand-end) 100%);
  border: none;
  box-shadow: 0 8px 25px rgba(18, 191, 160, 0.35);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1000;
}

.add-post-btn:hover {
  transform: scale(1.1) rotate(90deg);
  box-shadow: 0 12px 35px rgba(18, 191, 160, 0.4);
}

.back-to-top-btn {
  position: fixed;
  right: 40px;
  bottom: 120px;
  width: 50px;
  height: 50px;
  font-size: 18px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  z-index: 1000;
}

.back-to-top-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

/* 列表动画 */
.post-list-enter-active,
.post-list-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-list-enter-from {
  opacity: 0;
  transform: translateY(30px) scale(0.9);
}

.post-list-leave-to {
  opacity: 0;
  transform: translateY(-30px) scale(0.9);
}

.post-list-move {
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-list-container {
    padding: 15px;
  }
  
  .page-header {
    padding: 30px 20px;
    margin-bottom: 20px;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .filter-section {
    padding: 20px;
  }
  
  .category-container {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .posts-container {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .stats-section {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .add-post-btn {
    right: 20px;
    bottom: 20px;
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
  
  .back-to-top-btn {
    right: 20px;
    bottom: 90px;
    width: 45px;
    height: 45px;
  }
}

@media (max-width: 480px) {
  .page-header {
    padding: 25px 15px;
  }
  
  .page-title {
    font-size: 1.8rem;
  }
  
  .filter-section {
    padding: 15px;
  }
  
  .stats-card {
    padding: 20px;
  }
  
  .post-card {
    margin: 0;
  }
}
</style>