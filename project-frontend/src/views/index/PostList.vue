<template>
  <div class="post-container">
    <!-- 分类过滤 -->
    <div class="category-filter">
      <el-radio-group v-model="selectedCategory">
        <el-radio-button 
          v-for="category in categories" 
          :key="category" 
          :value="category"
        >
          {{ category }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 帖子列表 -->
    <el-card 
      v-for="post in filteredPosts" 
      :key="post.id"
      class="post-card"
      shadow="hover"
      @click="navigateToDetail(post.id)"
    >
      <template #header>
        <div class="post-header">
          <div class="title-wrapper">
            <h3 class="post-title">{{ post.title }}</h3>
            <el-tag type="info" size="small">{{ post.tagName }}</el-tag>
          </div>
          <div class="post-meta">
            <span class="author">{{ post.authorName }}</span>
            <span class="time">{{ formatTime(post.createTime) }}</span>
          </div>
        </div>
      </template>
      <div class="post-content">
        {{ post.content }}
      </div>
    </el-card>

    <!-- 添加帖子浮动按钮 -->
    <el-button 
      type="primary" 
      :icon="Promotion" 
      circle 
      class="add-button"
      @click="navigateToCreate"
    />
  </div>
</template>

<script setup>
  import { ref, onMounted, computed } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  import { ElMessage } from 'element-plus';
  import { Plus, Promotion } from '@element-plus/icons-vue';

  const router = useRouter();
  const posts = ref([]);
  const categories = ref(['全部']);
  const selectedCategory = ref('全部');


  // 获取帖子数据
  const fetchPosts = async () => {
    try {
      const response = await axios.get('/api/posts/list');
      posts.value = response.data;
      console.log(posts);
      // 从帖子数据中提取所有唯一分类（使用name字段）
      const allCategories = [...new Set(posts.value.map(post => post.tagName))];
      categories.value = ['全部', ...allCategories];
    } catch (error) {
      console.error('获取帖子列表失败:', error);
      ElMessage.error('获取帖子列表失败');
    }
  };
  // 过滤帖子列表
  const filteredPosts = computed(() => {
    if (selectedCategory.value === '全部') {
      return posts.value;
    }
    return posts.value.filter(post => post.tagName === selectedCategory.value);
  });

  // 导航方法
  const navigateToDetail = (postId) => {
    router.push({ name: 'index-post-detail', params: { id: postId }});
  };

  const navigateToCreate = () => {
    router.push({ name: 'index-post-create' });
  };

  // 时间格式化
  const formatTime = (timeStr) => {
    return new Date(timeStr).toLocaleString();
  };

  onMounted(() => {
    fetchPosts();
  });
</script>

<style scoped>
  .post-container {
    margin: 2px 1%;
    padding: 20px;
    background: white;
    position: relative;
  }

  .category-filter {
    margin-bottom: 20px;
    padding: 10px;
    background: #f5f7fa;
    border-radius: 4px;
    position: sticky;
    top: 0;
    z-index: 10;
  }

  .el-radio-group {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
  }

  .post-card {
    margin-bottom: 20px;
    transition: transform 0.2s;
    cursor: pointer;
  }

  .post-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  }

  .post-header {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .title-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .post-title {
    margin: 0;
    color: #303133;
    flex-grow: 1;
  }

  .post-meta {
    font-size: 0.9em;
    color: #909399;
    display: flex;
    gap: 15px;
  }

  .post-content {
    color: #606266;
    line-height: 1.6;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
  }

  .add-button {
    position: fixed;
    right: 40px;
    bottom: 40px;
    width: 56px;
    height: 56px;
    font-size: 24px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.15);
    transition: all 0.2s;
  }

  .add-button:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 16px rgba(0,0,0,0.2);
  }

  /* 响应式调整 */
  @media (max-width: 768px) {
    .category-filter {
      padding: 8px;
    }
    
    .el-radio-group {
      gap: 6px;
    }
    
    .post-card {
      margin-bottom: 15px;
    }
    
    .add-button {
      right: 20px;
      bottom: 20px;
      width: 48px;
      height: 48px;
    }
  }
</style>