<template>
    <div class="wall-container">
        <h1>表白墙 💌</h1>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">加载中...</div>
        
        <!-- 表白列表 -->
        <div class="posts-list">
            <div 
            v-for="post in posts" 
            :key="post.id" 
            class="post-item"
            >
            <div class="post-content">
                <h2 class="title">{{ post.title }}</h2>
                <p class="content">{{ post.content }}</p>
            </div>
            </div>
        </div>
    </div>
    <div v-if="error" class="error-message">
        数据加载失败，请稍后重试
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue';
    import axios from 'axios';
    
    const posts = ref([]);
    const loading = ref(true);
    const error = ref(null);
    
    // 从后端获取数据
    const fetchPosts = async () => {
        try {
            error.value = null;
            const response = await axios.get('/api/likes/list');
            posts.value = response.data;
        } catch (err) {
            error.value = err;
        } finally {
            loading.value = false;
        }
    };
    
    // 组件挂载时获取数据
    onMounted(() => {
        fetchPosts();
    });
</script>

<style scoped>
    .wall-container {
        max-width: 1200px;
        margin: 40px auto;
        padding: 30px;
        font-family: 'Helvetica Neue', Arial, sans-serif;
    }
    
    h1 {
        text-align: center;
        color: #ff6b6b;
        margin-bottom: 40px;
        font-size: 2.5rem;
        letter-spacing: 2px;
    }
    
    .loading {
        text-align: center;
        color: #666;
        padding: 30px;
        font-size: 1.2rem;
    }
    
    .posts-list {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
        gap: 30px;
        padding: 20px 0;
    }
    
    .post-item {
        background: white;
        border-radius: 16px;
        padding: 25px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        transition: transform 0.2s;
        min-height: 200px;
        display: flex;
        align-items: center;
    }

    .post-item:hover {
        transform: translateY(-5px);
    }
    
    .title {
        color: #ff6b6b;
        margin-bottom: 15px;
        font-size: 1.5rem;
        font-weight: 600;
        border-bottom: 2px solid #ffe3e3;
        padding-bottom: 8px;
    }
    
    .content {
        line-height: 1.8; 
        color: #444;
        margin: 0;
        font-size: 1.1rem;
    }

    .error-message {
        color: #ff4444;
        text-align: center;
        padding: 25px;
        background: #ffecec;
        border-radius: 12px;
        margin: 30px auto;
        max-width: 1200px;
        font-size: 1.1rem;
        font-weight: 500;
    }

    @media (max-width: 768px) {
        .wall-container {
            padding: 20px;
            margin: 20px auto;
        }
        
        h1 {
            font-size: 2rem;
            margin-bottom: 30px;
        }
        
        .posts-list {
            grid-template-columns: 1fr;
            gap: 20px;
        }
        
        .post-item {
            padding: 20px;
            min-height: auto;
        }
    }
</style>