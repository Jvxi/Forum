<template>
    <div class="post-detail-container">
        <!-- 加载状态 -->
        <el-skeleton :loading="loading" animated>
            <template #template>
            <el-skeleton-item variant="h1" style="width: 50%" />
            <div style="margin: 20px 0">
                <el-skeleton-item variant="text" style="width: 30%" />
                <el-skeleton-item variant="text" style="width: 20%" />
            </div>
            <el-skeleton-item variant="text" :rows="8" />
            </template>
    
            <!-- 正式内容 -->
            <template #default>
                <el-card v-if="post" class="detail-card">
                    <!-- 头部 -->
                    <template #header>
                        <div class="detail-header">
                            <!-- 操作按钮 -->
                            <div class="header-action">
                                <el-button 
                                    type="primary" 
                                    @click="goBack"
                                    icon="Back"
                                    size="large"
                                ><el-icon><Back /></el-icon>
                                    返回列表
                                </el-button>
                            </div>
                            <h1 class="detail-title">{{ post.title }}</h1>
                            <div class="meta-info">
                                <div class="meta-item">
                                    <el-icon><User /></el-icon>
                                    <span class="text">作者：{{ post.authorName }}</span>
                                </div>
                                <div class="meta-item">
                                    <el-icon><Clock /></el-icon>
                                    <span class="text">发布时间：{{ formatTime(post.createTime) }}</span>
                                </div>
                                <div v-if="post.updateTime" class="meta-item">
                                    <el-icon><Refresh /></el-icon>
                                    <span class="text">{{ formatTime(post.updateTime) }}</span>
                                    <!-- 新增编辑按钮 -->
                                    <el-button 
                                        v-if="showEditButton"
                                        type="primary" 
                                        size="small"
                                        @click="editPost"
                                        class="edit-btn"
                                    >
                                        <el-icon><Edit /></el-icon>
                                        编辑
                                    </el-button>
                                    <!-- <span v-if="post.updateTime" class="time">最后更新：{{ formatTime(post.updateTime) }}</span> -->
                                </div>
                            </div>
                        </div>
                    </template>

                    <!-- 标签区域 -->
                    <div v-if="post" class="tags-container">
                        <el-tag
                            type="info"
                            size="large"
                            class="post-tag"
                        >
                        <el-icon><Link /></el-icon>
                            {{ post.tagName }}
                        </el-tag>
                    </div>

                    <!-- 内容区域 -->
                    <div class="detail-content">
                        <pre>{{ post.content }}</pre> <!-- 使用pre保留格式 -->
                    </div>
                </el-card>

                <!-- 数据不存在提示 -->
                <div v-else class="empty-message">
                    <el-empty description="帖子不存在或已被删除">
                    <el-button type="primary" @click="goBack">返回列表</el-button>
                    </el-empty>
                </div>
            </template>
        </el-skeleton>
    
        <!-- 错误提示 -->
        <el-alert 
            v-if="errorMessage"
            :title="errorMessage"
            type="error"
            show-icon
            closable
            class="error-alert"
        />
    </div>
</template>

<script setup>
    import { ref, watch, onMounted } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import axios from 'axios'
    import { ElMessage } from 'element-plus'
    import { User, Clock, Refresh, Back, Edit, Link } from '@element-plus/icons-vue' // 新增图标导入，用于meta-item中的图标显示
    
    const route = useRoute()
    const router = useRouter()
    
    // 数据状态
    const post = ref(null)
    const loading = ref(true)
    const errorMessage = ref('')
    const showEditButton = ref(false)
    // 添加watch监听

    watch(
        () => route.params.id,
        (newVal) => {
            if(newVal) fetchPostDetail()
    })

    // 1. 获取用户信息
    const userString = localStorage.getItem('user');
    if (!userString) {
        alert('请先登录！');
    }

    let user;
    try {
        user = JSON.parse(userString);
    } catch (error) {
        console.error('解析用户数据失败:', error);
        alert('用户数据格式错误！');
    }

    if (!user.id) {
        alert('用户ID缺失！');
    }

    // 获取帖子详情
    const fetchPostDetail = async () => {
        try {
            const response = await axios.get(`/api/posts/${route.params.id}`)
            if (response.status === 200) {
                post.value = response.data
                // 检查当前用户是否是作者
                showEditButton.value = user.id === post.value.userId
            }
        } catch (error) {
            console.error('获取帖子详情失败:', error)
            errorMessage.value = error.response?.data?.message || '加载帖子详情失败'
            ElMessage.error(errorMessage.value)
        } finally {
            loading.value = false
        }
    }
    
    // 时间格式化
    const formatTime = (timeStr) => {
        return new Date(timeStr).toLocaleString()
    }
    
    // 返回操作
    const goBack = () => {
        router.push({name: 'index-list'}) // 返回上一页
    }

    // 新增编辑方法
    const editPost = () => {
        router.push({name: 'index-post-edit'})
    }
    
    onMounted(() => {
        fetchPostDetail()
    })
</script>


<style scoped>
    .post-detail-container {
        width: 100%;
        min-height: 100vh;
        padding: 1rem 1%;
        background: #f8f9fa;
    }

    .detail-card {
        margin: 0 auto;
        max-width: 1280px;
        border-radius: 12px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    }

    .detail-header .header-action {
            margin-bottom: 1.5rem;
    }

    .detail-header {
        padding: 1.5rem;
        border-bottom: 1px solid #e5e7eb;
        position: relative; /* 为绝对定位提供参考 */
    }

    .detail-title {
        margin: 0 0 1.5rem 0;
        color: #1f2937;
        font-size: 2.5rem;
        line-height: 1.2;
        font-weight: 600;
    }

    .meta-info {
        display: flex;
        gap: 2rem;
        flex-wrap: wrap;
    }

    .meta-item {
        position: relative;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        color: #6b7280;
        font-size: 0.95rem;
    }

    .meta-item .el-icon {
        font-size: 1.1rem;
    }

    .detail-content {
        padding: 2rem 0;
        font-size: 1.1rem;
        line-height: 1.8;
        color: #374151;
    }

    .detail-content pre {
        white-space: pre-wrap;
        word-break: break-word;
        font-family: inherit;
        font-size: inherit;
        line-height: inherit;
        background: transparent;
        padding: 1rem;
        border-radius: 8px;
    }

    .action-buttons {
        margin-top: 3rem;
        padding-top: 2rem;
        border-top: 1px solid #e5e7eb;
        text-align: center;
    }

    /* 新增编辑按钮样式 */
    .edit-btn {
        margin-left: 15px;
        padding: 5px 12px;
        border-radius: 6px;
    }

    .tags-container {
        margin: 1rem 0 2rem;
        display: flex;
        gap: 0.8rem;
        flex-wrap: wrap;
    }

    .post-tag {
        border-radius: 6px;
        padding: 0 12px;
        height: 32px;
        font-size: 0.95rem;
    }

    /* 响应式设计 */
    @media (max-width: 768px) {
        .header-action .el-button {
            padding: 12px 18px;
            font-size: 14px;
        }

        .post-detail-container {
            padding: 1rem;
        }

        .detail-title {
            font-size: 1.8rem;
        }

        .meta-info {
            gap: 1rem;
        }

        .detail-content {
            font-size: 1rem;
            padding: 1rem 0;
        }

        .tags-container {
            margin: 0.5rem 0 1.5rem;
            gap: 0.6rem;
        }
        .post-tag {
            height: 28px;
            font-size: 0.85rem;
        }
    }

        @media (max-width: 480px) {
        .detail-title {
            font-size: 1.5rem;
        }

        .meta-item {
            font-size: 0.85rem;
        }

        .edit-btn {
            padding: 4px 10px;
            margin-left: 10px;
        }
    }
</style>