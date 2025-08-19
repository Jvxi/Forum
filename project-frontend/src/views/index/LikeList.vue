<template>
    <div class="like-page">
        <div class="wall-container">
            <h1>表白墙 💌</h1>
            
            <!-- 新增输入表单 -->
            <div class="post-form">
                <input 
                    v-model="newPost.title" 
                    type="text" 
                    placeholder="输入标题"
                    class="form-input"
                >
                <textarea
                    v-model="newPost.content"
                    placeholder="写下你的心意"
                    class="form-textarea"
                ></textarea>
                <button 
                    @click="submitPost"
                    :disabled="isSubmitting"
                    class="submit-btn"
                >
                    {{ isSubmitting ? '发送中...' : '勇敢表白' }}
                </button>
            </div>
    
            <!-- 加载状态 -->
            <div v-if="loading" class="loading">加载中...</div>
            
            <!-- 表白列表 -->
            <div class="posts-list">
                <div 
                    v-for="post in posts" 
                    :key="post.id" 
                    class="post-item"
                    :style="{
                        gridColumn: post.gridColumn,
                        gridRow: post.gridRow,
                        'font-size': post.fontSize
                    }"
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
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue';
    import axios from 'axios';
    
    const posts = ref([]);
    const loading = ref(true);
    const error = ref(null);
    const isSubmitting = ref(false);
    const newPost = ref({
        title: '',
        content: ''
    });

    // 生成随机布局参数
    const generateLayout = () => {
        const baseSizes = [
            { cols: 1, rows: 1, fontSize: '1rem' },  // 小尺寸
            { cols: 2, rows: 2, fontSize: '1.2rem' }, // 大尺寸
            { cols: 1, rows: 1, fontSize: '1rem' },  // 增加小尺寸出现概率
            { cols: 1, rows: 2, fontSize: '1.1rem' } // 竖长方形
        ];
        
        const randomSize = baseSizes[Math.floor(Math.random() * baseSizes.length)];
        
        return {
            gridColumn: `span ${randomSize.cols}`,
            gridRow: `span ${randomSize.rows}`,
            fontSize: randomSize.fontSize
        }
    }

    // 提交新帖子
    const submitPost = async () => {
        if (!newPost.value.title || !newPost.value.content) return;
        console.log(newPost);
        
        try {
            isSubmitting.value = true;
            const response = await axios.post('/api/likes/create', newPost.value);
            
            // 将新帖子添加到列表开头
            posts.value.unshift({
                ...response.data,
                ...generateLayout()
            });
            
            // 清空输入
            newPost.value = { title: '', content: '' };
        } catch (err) {
            error.value = err.response?.data?.message || '提交失败';
        } finally {
            isSubmitting.value = false;
        }
    };

    // 获取帖子列表
    const fetchPosts = async () => {
        try {
            error.value = null;
            const response = await axios.get('/api/likes/list');
            posts.value = response.data.map(post => ({
                ...post,
                ...generateLayout()
            }));
        } catch (err) {
            error.value = err;
        } finally {
            loading.value = false;
        }
    };
    
    onMounted(fetchPosts);
</script>

<style scoped>
    .wall-container {
        max-width: 1200px;
        margin: 40px auto;
        padding: 30px;
        font-family: 'Helvetica Neue', Arial, sans-serif;
    }

    /* 新增表单样式 */
    .post-form {
        background: #fff5f5;
        border-radius: 12px;
        padding: 25px;
        margin-bottom: 40px;
        box-shadow: 0 2px 8px rgba(255, 107, 107, 0.1);
    }
    
    .form-input,
    .form-textarea {
        width: 100%;
        padding: 12px;
        margin-bottom: 15px;
        border: 2px solid #ffe3e3;
        border-radius: 8px;
        font-size: 1.1rem;
        transition: border-color 0.3s;
    }

    .form-input:focus,
    .form-textarea:focus {
        outline: none;
        border-color: #ff6b6b;
    }

    .form-textarea {
        height: 120px;
        resize: vertical;
    }

    .submit-btn {
        background: #ff6b6b;
        color: white;
        padding: 12px 35px;
        border: none;
        border-radius: 25px;
        font-size: 1.1rem;
        cursor: pointer;
        transition: all 0.3s;
        display: block;
        margin-left: auto;
    }

    .submit-btn:hover {
        background: #ff5252;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
    }

    .submit-btn:disabled {
        background: #ffb3b3;
        cursor: not-allowed;
        transform: none;
        box-shadow: none;
    }

    /* 修改卡片列表布局 */
    .posts-list {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
        grid-auto-rows: 150px; /* 基础网格行高 */
        gap: 20px;
        padding: 20px 0;
        grid-auto-flow: dense;
    }

    .post-item {
        background: white;
        border-radius: 15px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        transition: all 0.3s;
        overflow: hidden;
        display: flex;
        justify-content: center;
        align-items: center;
        position: relative;
    }

    .post-item:hover {
        transform: translateY(-5px);
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
    }

    .post-content {
        padding: 15px;
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        text-align: center;
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
    
    .title {
        margin-bottom: 12px;
        line-height: 1.3;
    }
    
    .content {
        line-height: 1.6;
        word-break: break-word;
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
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            grid-auto-rows: 200px;
            gap: 30px;
        }
        
        .post-item {
            width: 100% !important;
            height: auto !important;
            aspect-ratio: 1/1;
        }

        .post-form {
            padding: 20px;
        }
        
        .submit-btn {
            width: 100%;
            margin: 10px 0;
        }
    }

    @media (min-width: 1200px) {
        .posts-list {
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            grid-auto-rows: 250px;
        }
    }
</style>