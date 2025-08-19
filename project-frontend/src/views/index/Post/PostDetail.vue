<!-- 帖子详情页面 -->
<template>
    <div class="post-detail-page">
        <!-- 页面背景装饰 -->
        <div class="page-bg">
            <div class="bg-gradient"></div>
            <div class="floating-shapes">
                <div class="shape shape-1"></div>
                <div class="shape shape-2"></div>
                <div class="shape shape-3"></div>
                <div class="shape shape-4"></div>
            </div>
        </div>

        <!-- 加载状态 -->
        <el-skeleton :loading="loading" animated>
            <template #template>
                <div class="skeleton-container">
                    <el-skeleton-item variant="h1" style="width: 60%; height: 40px; margin-bottom: 20px;" />
                    <div style="margin: 20px 0">
                        <el-skeleton-item variant="text" style="width: 30%; margin-bottom: 10px;" />
                        <el-skeleton-item variant="text" style="width: 25%; margin-bottom: 10px;" />
                        <el-skeleton-item variant="text" style="width: 20%;" />
                    </div>
                    <el-skeleton-item variant="text" :rows="10" style="margin-top: 30px;" />
                </div>
            </template>
    
            <!-- 正式内容 -->
            <template #default>
                <div class="content-container">
                    <!-- 返回按钮 -->
                    <div class="back-section">
                        <el-button 
                            class="back-btn"
                            type="info" 
                            :icon="ArrowLeftBold" 
                            circle 
                            size="large"
                            @click="goBack"
                        />
                        <div class="breadcrumb">
                            <span class="breadcrumb-item">📝 帖子列表</span>
                            <el-icon class="breadcrumb-separator"><ArrowRight /></el-icon>
                            <span class="breadcrumb-item active">📄 帖子详情</span>
                        </div>
                    </div>

                    <!-- 帖子卡片 -->
                    <el-card v-if="post" class="post-card" shadow="never">
                        <!-- 帖子头部 -->
                        <div class="post-header">
                            <!-- 标题区域 -->
                            <div class="title-section">
                                <h1 class="post-title">{{ post.title }}</h1>
                                <div class="title-decoration"></div>
                            </div>
                            
                            <!-- 元信息区域 -->
                            <div class="meta-section">
                                <div class="meta-info">
                                    <div class="meta-item">
                                        <div class="meta-icon">
                                            <el-icon><User /></el-icon>
                                        </div>
                                        <div class="meta-content">
                                            <span class="meta-label">作者</span>
                                            <span class="meta-value">{{ post.authorName }}</span>
                                        </div>
                                    </div>
                                    
                                    <div class="meta-item">
                                        <div class="meta-icon">
                                            <el-icon><Clock /></el-icon>
                                        </div>
                                        <div class="meta-content">
                                            <span class="meta-label">发布时间</span>
                                            <span class="meta-value">{{ formatTime(post.createTime) }}</span>
                                        </div>
                                    </div>
                                    
                                    <div v-if="post.updateTime" class="meta-item">
                                        <div class="meta-icon">
                                            <el-icon><Refresh /></el-icon>
                                        </div>
                                        <div class="meta-content">
                                            <span class="meta-label">更新时间</span>
                                            <span class="meta-value">{{ formatTime(post.updateTime) }}</span>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- 操作按钮 -->
                                <div v-if="showEditButton" class="action-section">
                                    <el-button 
                                        type="primary" 
                                        size="large"
                                        @click="editPost"
                                        class="action-btn edit-btn"
                                    >
                                        <el-icon><Edit /></el-icon>
                                        编辑帖子
                                    </el-button>
                                    <el-button 
                                        type="danger"
                                        size="large"
                                        @click="deletePost"
                                        class="action-btn delete-btn"
                                    >
                                        <el-icon><Delete /></el-icon>
                                        删除帖子
                                    </el-button>
                                </div>
                            </div>
                        </div>

                        <!-- 标签区域 -->
                        <div class="tags-section">
                            <el-tag
                                type="info"
                                size="large"
                                class="post-tag"
                                effect="light"
                            >
                                <el-icon><Link /></el-icon>
                                {{ post.tagName }}
                            </el-tag>
                        </div>

                        <!-- 内容区域 -->
                        <div class="content-section">
                            <div class="content-wrapper">
                                <pre class="post-content">{{ post.content }}</pre>
                            </div>
                        </div>
                    </el-card>

                    <!-- 数据不存在提示 -->
                    <div v-else class="empty-state">
                        <el-card class="empty-card">
                            <div class="empty-content">
                                <div class="empty-icon">📭</div>
                                <h3 class="empty-title">帖子不存在</h3>
                                <p class="empty-description">该帖子可能已被删除或不存在</p>
                                <el-button type="primary" @click="goBack" class="empty-btn">
                                    <el-icon><Back /></el-icon>
                                    返回列表
                                </el-button>
                            </div>
                        </el-card>
                    </div>

                    <!-- 评论区 -->
                    <div class="comment-section">
                        <!-- 评论表单 -->
                        <el-card class="comment-form-card">
                            <div class="form-header">
                                <h3 class="form-title">💬 发表评论</h3>
                                <div class="form-subtitle">分享你的想法和观点</div>
                            </div>
                            
                            <div class="form-content">
                                <el-input
                                    v-model="newComment"
                                    type="textarea"
                                    :rows="4"
                                    placeholder="请输入你的评论内容..."
                                    maxlength="300"
                                    show-word-limit
                                    class="comment-input"
                                />
                                <div class="form-actions">
                                    <el-button 
                                        type="primary"
                                        size="large"
                                        @click="submitComment"
                                        :disabled="newComment.length < 5"
                                        :loading="submitting"
                                        class="submit-comment-btn"
                                    >
                                        <el-icon><ChatDotRound /></el-icon>
                                        {{ submitting ? '发表中...' : '发表评论' }}
                                    </el-button>
                                </div>
                            </div>
                        </el-card>

                        <!-- 评论列表 -->
                        <div class="comments-list">
                            <div class="list-header">
                                <h3 class="list-title">💭 评论列表</h3>
                                <span class="comment-count">({{ comments.length }}条评论)</span>
                            </div>
                            
                            <div v-if="comments.length === 0" class="empty-comments">
                                <div class="empty-comment-content">
                                    <div class="empty-comment-icon">💭</div>
                                    <p class="empty-comment-text">暂无评论，快来发表第一条评论吧！</p>
                                </div>
                            </div>
                            
                            <transition-group name="comment-list" tag="div" class="comments-container">
                                <el-card 
                                    v-for="comment in comments" 
                                    :key="comment.id"
                                    class="comment-item"
                                    shadow="hover"
                                >
                                    <div class="comment-header">
                                        <div class="comment-user">
                                            <el-avatar :size="36" :src="resolveCommentAvatar(comment)" class="user-avatar" />
                                            <div class="user-info">
                                                <span class="username">{{ comment.authorName }}</span>
                                                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                                            </div>
                                        </div>
                                        
                                        <div v-if="comment.userId === user.id" class="comment-actions">
                                            <el-button
                                                type="danger"
                                                size="small"
                                                @click="deleteComment(comment.id)"
                                                class="delete-comment-btn"
                                            >
                                                <el-icon><Delete /></el-icon>
                                                删除
                                            </el-button>
                                        </div>
                                    </div>
                                    
                                    <div class="comment-body">
                                        <p class="comment-content">{{ comment.content }}</p>
                                    </div>
                                </el-card>
                            </transition-group>
                        </div>
                    </div>
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
    import { ElMessage, ElMessageBox } from 'element-plus'
    import { User, Clock, Refresh, Back, Edit, Link, Delete, ArrowLeftBold, ArrowRight, ChatDotRound } from '@element-plus/icons-vue' // 新增图标导入，用于meta-item中的图标显示
    
    const route = useRoute()
    const router = useRouter()
    
    // 数据状态
    const post = ref(null)
    const loading = ref(true)
    const errorMessage = ref('')
    const showEditButton = ref(false)

    // 评论相关数据
    const comments = ref([])
    const newComment = ref('')
    const submitting = ref(false) // 新增评论提交状态

    // 添加watch监听
    watch(
        () => route.params.id,
        (newVal) => {
            if(newVal) fetchPostDetail()
    })

    // 用户信息
    let user = null;

    // 获取帖子详情
    const fetchPostDetail = async () => {
        // 验证用户信息
        if (!user) {
            ElMessage.error('请先登录！')
            router.push('/')
            return
        }

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
        router.push({name: 'index-posts'}) // 返回帖子列表页
    }

    // 新增编辑方法
    const editPost = () => {
        if (!user) {
            ElMessage.error('请先登录！')
            router.push('/')
            return
        }
        router.push({name: 'index-post-edit', params: { id: route.params.id }})
    }
    
    // 删除帖子方法
    const deletePost = async () => {
        if (!user) {
            ElMessage.error('请先登录！')
            router.push('/')
            return
        }

        try {
            // 确认对话框
            await ElMessageBox.confirm(
                '确定要删除这篇帖子吗？此操作不可恢复！',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            )
            
            // 发送删除请求
            const response = await axios.post(`/api/posts/delete/${route.params.id}`, {
                    userId: user.id
                }, {
                headers: {
                    'Content-Type': 'application/json', // 明确指定 JSON 格式
                }
            })
            
            if (response.status === 204) {
                ElMessage.success('帖子删除成功')
                router.push({ name: 'index-posts' }) // 跳转回帖子列表页
            }
        } catch (error) {
            // 错误处理（排除取消操作的情况）
            if (error !== 'cancel') {
                console.error('删除失败:', error)
                ElMessage.error(error.response?.data?.message || '删除帖子失败')
            }
        }
    }
    
    // 获取评论列表
    const fetchComments = async () => {
        if (!user) {
            return
        }

        try {
            const response = await axios.get(`/api/comments/post/${route.params.id}`)
            comments.value = response.data
        } catch (error) {
            ElMessage.error('获取评论失败')
        }
    }

    // 提交评论
    const submitComment = async () => {
        if (!user) {
            ElMessage.error('请先登录！')
            router.push('/')
            return
        }

        submitting.value = true; // 开始提交
        try {
            const response = await axios.post('/api/comments/create', {
                postId: route.params.id,
                content: newComment.value,
                userId: user.id
            })
            if (response.status === 200) {
                ElMessage.success('评论成功')
                newComment.value = ''
                await fetchComments()
            }
        } catch (error) {
            ElMessage.error(error.response?.data?.message || '评论失败')
        } finally {
            submitting.value = false; // 结束提交
        }
    }

    // 评论头像解析：优先使用返回的 avatar，缺失则回退到性别默认或通用默认
    const resolveCommentAvatar = (comment) => {
        const url = (comment && comment.avatar) ? String(comment.avatar) : ''
        if (url && url.length > 0) return url
        // 可根据 comment.sex 区分，若没有则使用通用
        switch (comment?.sex) {
            case 'M': return '/images/男.png'
            case 'F': return '/images/女.png'
            default: return '/images/未.png'
        }
    }

    // 删除评论
    const deleteComment = async (commentId) => {
        if (!user) {
            ElMessage.error('请先登录！')
            router.push('/')
            return
        }

        try {
            await ElMessageBox.confirm('确认删除该评论？', '警告', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            
            await axios.delete(`/api/comments/${commentId}`, {
                userId: user.id
            }, {
                headers: {
                    'Content-Type': 'application/json', // 明确指定 JSON 格式
                }
            })
            
            ElMessage.success('删除成功')
            comments.value = comments.value.filter(c => c.id !== commentId)
        } catch (error) {
            if (error !== 'cancel') {
                ElMessage.error('删除失败')
            }
        }
    }

    onMounted(async () => {
        // 1. 获取用户信息
        const userString = localStorage.getItem('user');
        if (!userString) {
            ElMessage.error('请先登录！')
            router.push('/')
            return
        }

        try {
            user = JSON.parse(userString);
        } catch (error) {
            console.error('解析用户数据失败:', error);
            ElMessage.error('用户数据格式错误！');
            return
        }

        if (!user.id) {
            ElMessage.error('用户ID缺失！');
            return
        }
        await fetchPostDetail()
        await fetchComments()
    })
</script>


<style scoped>
    .post-detail-page {
        width: 100%;
        min-height: 100vh;
        position: relative; /* 用于定位背景装饰 */
        overflow: hidden; /* 隐藏背景装饰溢出 */
    }

    .page-bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: -1; /* 背景装饰在内容下方 */
    }

    .bg-gradient {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, var(--bg-start) 0%, var(--bg-end) 100%);
        opacity: 0.8;
    }

    .floating-shapes {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        pointer-events: none; /* 允许背景装饰点击穿透 */
    }

    .shape {
        position: absolute;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 50%;
        filter: blur(5px);
        animation: float 10s infinite ease-in-out;
    }

    .shape-1 {
        width: 100px;
        height: 100px;
        top: 10%;
        left: 10%;
        animation-delay: -2s;
    }

    .shape-2 {
        width: 150px;
        height: 150px;
        top: 70%;
        right: 10%;
        animation-delay: -5s;
    }

    .shape-3 {
        width: 80px;
        height: 80px;
        bottom: 20%;
        left: 30%;
        animation-delay: -3s;
    }

    .shape-4 {
        width: 120px;
        height: 120px;
        bottom: 50%;
        right: 20%;
        animation-delay: -7s;
    }

    @keyframes float {
        0% {
            transform: translateY(0) translateX(0) scale(1);
            opacity: 0.5;
        }
        25% {
            transform: translateY(-20px) translateX(10px) scale(1.1);
            opacity: 0.7;
        }
        50% {
            transform: translateY(10px) translateX(-10px) scale(1.2);
            opacity: 0.9;
        }
        75% {
            transform: translateY(-10px) translateX(5px) scale(1.1);
            opacity: 0.7;
        }
        100% {
            transform: translateY(0) translateX(0) scale(1);
            opacity: 0.5;
        }
    }

    .content-container {
        max-width: 1280px;
        margin: 0 auto;
        padding: 1.5rem 1%;
        position: relative; /* 确保内容在背景上方 */
    }

    .back-section {
        display: flex;
        align-items: center;
        margin-bottom: 1.5rem;
        position: absolute; /* 固定在内容容器顶部 */
        top: 0;
        left: 1%;
        z-index: 10; /* 确保在背景上方 */
    }

    .back-btn {
        margin-right: 1rem;
        transition: all 0.25s var(--motion-ease);
        background: transparent;
        border: 1.5px solid var(--brand-end);
        color: var(--brand-end);
    }

    .back-btn:hover {
        transform: translateX(-3px);
        background: var(--brand-hover);
        box-shadow: 0 6px 16px rgba(18,191,160,0.25);
    }

    .back-btn:focus-visible {
        outline: none;
        box-shadow: 0 0 0 3px rgba(18,191,160,0.25);
    }

    .breadcrumb {
        display: flex;
        align-items: center;
        font-size: 0.95rem;
        color: #6b7280;
    }

    .breadcrumb-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .breadcrumb-item.active {
        color: #374151;
        font-weight: 600;
    }

    .breadcrumb-separator {
        font-size: 1rem;
    }

    .post-card {
        border-radius: 15px;
        box-shadow: 0 10px 20px -5px rgba(0, 0, 0, 0.1), 0 5px 10px -3px rgba(0, 0, 0, 0.05);
        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }

    .post-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 15px 25px -5px rgba(0, 0, 0, 0.15), 0 10px 10px -3px rgba(0, 0, 0, 0.08);
    }

    .post-header {
        padding: 2rem 2rem 1.5rem;
        background: linear-gradient(135deg, #ffffff 0%, #f1f5f9 100%);
        border-radius: 15px 15px 0 0;
        position: relative;
        overflow: hidden;
    }

    .post-header::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, rgba(34, 211, 169, 0.06) 0%, rgba(18, 191, 160, 0.06) 100%);
        opacity: 0.6;
    }

    .title-section {
        margin-bottom: 1.5rem;
        position: relative;
        z-index: 2;
    }

    .post-title {
        margin: 0 0 0.8rem 0;
        color: #1f2937;
        font-size: 2.2rem;
        line-height: 1.2;
        font-weight: 700;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .title-decoration {
        position: absolute;
        bottom: -6px;
        left: 0;
        width: 60px;
        height: 5px;
        background: linear-gradient(to right, #4f46e5, #10b981);
        border-radius: 5px;
        opacity: 0.8;
        animation: titleGlow 2s ease-in-out infinite alternate;
    }

    @keyframes titleGlow {
        0% { opacity: 0.6; }
        100% { opacity: 1; }
    }

    .meta-section {
        display: grid;
        grid-template-columns: 1fr auto;
        gap: 1.5rem;
        align-items: start;
        position: relative;
        z-index: 2;
    }

    .meta-info {
        display: flex;
        flex-direction: column;
        gap: 0.8rem;
    }

    .meta-item {
        display: flex;
        align-items: center;
        gap: 1rem;
        color: #6b7280;
        font-size: 1rem;
        padding: 0.6rem 1rem;
        background: rgba(255, 255, 255, 0.7);
        border-radius: 12px;
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.3);
        transition: all 0.3s ease;
    }

    .meta-item:hover {
        background: rgba(255, 255, 255, 0.9);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .meta-icon {
        font-size: 1.3rem;
        color: #4f46e5;
        background: linear-gradient(135deg, #4f46e5, #7c3aed);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
    }

    .meta-content {
        display: flex;
        flex-direction: column;
        gap: 0.2rem;
    }

    .meta-label {
        font-size: 0.8rem;
        color: #9ca3af;
        font-weight: 500;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }

    .meta-value {
        font-size: 1.1rem;
        font-weight: 600;
        color: #374151;
        line-height: 1.4;
    }

    .action-section {
        display: flex;
        flex-direction: column;
        gap: 0.6rem;
        min-width: 140px;
    }

    .action-btn {
        padding: 0.7rem 1rem;
        border-radius: 12px;
        font-weight: 600;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        font-size: 0.9rem;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .edit-btn {
        background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
        border: none;
        color: white;
    }

    .edit-btn:hover {
        background: linear-gradient(135deg, #4338ca 0%, #6b46c1 100%);
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(79, 70, 229, 0.3);
    }

    .delete-btn {
        background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
        border: none;
        color: white;
    }

    .delete-btn:hover {
        background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(239, 68, 68, 0.3);
    }

    .tags-section {
        margin: 0;
        padding: 0.8rem 2rem;
        display: flex;
        gap: 1rem;
        border-bottom: 1px solid #e5e7eb;
        background: #ffffff;
    }

    .post-tag {
        border-radius: 20px;
        padding: 0.5rem 1rem;
        height: auto;
        font-size: 1rem;
        font-weight: 600;
        color: #7c3aed;
        background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
        border: 2px solid #e5e7eb;
        transition: all 0.3s ease;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }

    .post-tag:hover {
        background: linear-gradient(135deg, #e5e7eb 0%, #d1d5db 100%);
        border-color: #7c3aed;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(124, 58, 237, 0.2);
    }

    .content-section {
        padding: 1.5rem;
        background: #ffffff;
    }

    .content-wrapper {
        background: linear-gradient(135deg, #fafbfc 0%, #f8fafc 100%);
        border-radius: 20px;
        box-shadow: 0 8px 25px -5px rgba(0, 0, 0, 0.1);
        padding: 1.8rem;
        font-size: 1.1rem;
        line-height: 1.8;
        color: #374151;
        border: 1px solid #e5e7eb;
        position: relative;
        overflow: hidden;
    }

    .content-wrapper::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(to right, var(--sidebar-start), var(--sidebar-end), var(--brand-end));
        border-radius: 20px 20px 0 0;
    }

    .post-content {
        white-space: pre-wrap;
        word-break: break-word;
        font-family: inherit;
        font-size: inherit;
        line-height: inherit;
        background: transparent;
        padding: 0;
        border-radius: 0;
        position: relative;
        z-index: 2;
    }

    .empty-state {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 300px;
        margin-top: 2rem;
    }

    .empty-card {
        width: 90%;
        max-width: 600px;
        border-radius: 15px;
        box-shadow: 0 10px 20px -5px rgba(0, 0, 0, 0.1), 0 5px 10px -3px rgba(0, 0, 0, 0.05);
        text-align: center;
        padding: 3rem 2rem;
        background: #f9fafb;
    }

    .empty-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
    }

    .empty-icon {
        font-size: 4rem;
        color: #e0e7ff;
    }

    .empty-title {
        margin: 0;
        color: #4f46e5;
        font-size: 2rem;
        font-weight: 700;
    }

    .empty-description {
        color: #6b7280;
        font-size: 1rem;
        margin-bottom: 1.5rem;
    }

    .empty-btn {
        padding: 0.8rem 1.5rem;
        border-radius: 10px;
        font-weight: 600;
        background-color: #4f46e5;
        border-color: #4f46e5;
        color: white;
        transition: all 0.3s ease;
    }

    .empty-btn:hover {
        background-color: #4338ca;
        border-color: #4338ca;
    }

    /* 评论样式 */
    .comment-section {
        margin-top: 1.5rem;
        max-width: 1280px;
        margin: 1.5rem auto 0;
    }

    .comment-form-card {
        margin-bottom: 1.2rem;
        border-radius: 15px;
        box-shadow: 0 10px 20px -5px rgba(0, 0, 0, 0.1), 0 5px 10px -3px rgba(0, 0, 0, 0.05);
        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }

    .comment-form-card:hover {
        transform: translateY(-3px);
        box-shadow: 0 15px 25px -5px rgba(0, 0, 0, 0.15), 0 10px 10px -3px rgba(0, 0, 0, 0.08);
    }

    .form-header {
        padding: 1rem 1.5rem;
        border-bottom: 1px solid #e5e7eb;
        background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
        border-radius: 15px 15px 0 0;
    }

    .form-title {
        margin: 0 0 0.2rem 0;
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
    }

    .form-subtitle {
        color: #6b7280;
        font-size: 0.95rem;
    }

    .form-content {
        padding: 1.2rem;
    }

    .comment-input {
        margin-bottom: 0.8rem;
    }

    .comment-input :deep(.el-textarea__inner) {
        border-radius: 12px;
        border: 2px solid #e5e7eb;
        padding: 1rem;
        font-size: 1rem;
        line-height: 1.6;
        transition: all 0.3s ease;
    }

    .comment-input :deep(.el-textarea__inner:focus) {
        border-color: #4f46e5;
        box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
    }

    .form-actions {
        display: flex;
        justify-content: flex-end;
    }

    .submit-comment-btn {
        padding: 0.8rem 1.5rem;
        border-radius: 10px;
        font-weight: 600;
        background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
        border: none;
        color: white;
        transition: all 0.3s ease;
    }

    .submit-comment-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(79, 70, 229, 0.3);
    }

    .submit-comment-btn:disabled {
        background: #cbd5e0;
        color: #718096;
        transform: none;
        box-shadow: none;
    }

    .comments-list {
        margin-top: 1.2rem;
    }

    .list-header {
        display: flex;
        align-items: center;
        gap: 1rem;
        margin-bottom: 0.8rem;
        padding: 1rem 0;
        border-bottom: 2px solid #e5e7eb;
    }

    .list-title {
        margin: 0;
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
    }

    .comment-count {
        color: #6b7280;
        font-size: 1rem;
        font-weight: 500;
    }

    .empty-comments {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 200px;
        background: #f9fafb;
        border-radius: 15px;
        border: 2px dashed #d1d5db;
    }

    .empty-comment-content {
        text-align: center;
        color: #6b7280;
    }

    .empty-comment-icon {
        font-size: 3rem;
        margin-bottom: 1rem;
        opacity: 0.5;
    }

    .empty-comment-text {
        margin: 0;
        font-size: 1rem;
    }

    .comments-container {
        display: flex;
        flex-direction: column;
        gap: 0.6rem;
    }

    .comment-item {
        border-radius: 12px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        animation: commentSlideIn 0.5s ease-out;
    }

    .comment-item:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 15px -3px rgba(0, 0, 0, 0.15);
    }

    @keyframes commentSlideIn {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .comment-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.8rem 1.2rem;
        border-bottom: 1px solid #f3f4f6;
    }

    .comment-user {
        display: flex;
        align-items: center;
        gap: 0.8rem;
    }

    .user-avatar {}

    .user-info {
        display: flex;
        flex-direction: column;
        gap: 0.2rem;
    }

    .username {
        font-weight: 600;
        color: #1f2937;
        font-size: 1rem;
    }

    .comment-time {
        color: #6b7280;
        font-size: 0.85rem;
    }

    .comment-actions {
        display: flex;
        gap: 0.5rem;
    }

    .delete-comment-btn {
        padding: 0.4rem 0.8rem;
        border-radius: 8px;
        font-size: 0.85rem;
        background-color: #ef4444;
        border-color: #ef4444;
        color: white;
        transition: all 0.3s ease;
    }

    .delete-comment-btn:hover {
        background-color: #dc2626;
        border-color: #dc2626;
        transform: scale(1.05);
    }

    .comment-body {
        padding: 0.8rem 1.2rem;
    }

    .comment-content {
        margin: 0;
        line-height: 1.6;
        color: #374151;
        white-space: pre-wrap;
    }

    /* 评论列表动画 */
    .comment-list-enter-active,
    .comment-list-leave-active {
        transition: all 0.5s ease;
    }

    .comment-list-enter-from {
        opacity: 0;
        transform: translateY(30px);
    }

    .comment-list-leave-to {
        opacity: 0;
        transform: translateY(-30px);
    }

    .comment-list-move {
        transition: transform 0.5s ease;
    }

    /* 错误提示样式 */
    .error-alert {
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 1000;
        max-width: 400px;
        border-radius: 12px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
    }

    /* 骨架屏样式 */
    .skeleton-container {
        max-width: 1280px;
        margin: 0 auto;
        padding: 2rem 1%;
    }

    /* 响应式设计 */
    @media (max-width: 768px) {
        .content-container {
            padding: 1rem 0.8rem;
        }

        .back-section {
            position: relative;
            margin-bottom: 0.8rem;
        }

        .post-header {
            padding: 1.5rem 1.2rem 1rem;
        }

        .title-section {
            margin-bottom: 1.2rem;
        }

        .post-title {
            font-size: 2rem;
        }

        .meta-section {
            grid-template-columns: 1fr;
            gap: 1.2rem;
        }

        .meta-info {
            gap: 0.8rem;
        }

        .meta-item {
            padding: 0.5rem 0.8rem;
        }

        .action-section {
            flex-direction: row;
            gap: 0.6rem;
            min-width: auto;
        }

        .action-btn {
            flex: 1;
            padding: 0.6rem 0.8rem;
            font-size: 0.9rem;
        }

        .content-section {
            padding: 1.2rem;
        }

        .content-wrapper {
            padding: 1.5rem;
            border-radius: 15px;
        }

        .tags-section {
            padding: 0.8rem 1.2rem;
        }

        .post-tag {
            padding: 0.5rem 1rem;
            font-size: 1rem;
        }

        .form-header {
            padding: 0.8rem 1.2rem;
        }

        .form-content {
            padding: 1rem;
        }

        .comment-header {
            flex-direction: column;
            align-items: flex-start;
            gap: 0.8rem;
        }

        .comment-user {
            width: 100%;
        }

        .comment-actions {
            align-self: flex-end;
        }
    }

    @media (max-width: 480px) {
        .post-title {
            font-size: 1.8rem;
        }

        .meta-item {
            font-size: 0.9rem;
            padding: 0.4rem 0.6rem;
        }

        .meta-label {
            font-size: 0.7rem;
        }

        .meta-value {
            font-size: 1rem;
        }

        .content-wrapper {
            padding: 1.2rem;
            font-size: 1.05rem;
            border-radius: 12px;
        }

        .action-section {
            flex-direction: column;
            gap: 0.5rem;
        }

        .action-btn {
            width: 100%;
            padding: 0.6rem 0.8rem;
            font-size: 0.85rem;
        }

        .form-title {
            font-size: 1.2rem;
        }

        .list-title {
            font-size: 1.2rem;
        }

        .user-avatar {
            width: 32px;
            height: 32px;
            font-size: 1rem;
        }

        .username {
            font-size: 0.85rem;
        }

        .comment-time {
            font-size: 0.75rem;
        }
    }
</style>