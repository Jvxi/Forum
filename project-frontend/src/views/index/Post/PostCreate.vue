<!-- 创建帖子页面 -->
<template>
    <div class="create-page">
        <!-- 页面头部装饰 -->
        <div class="page-header">
            <div class="header-bg"></div>
            <div class="header-content">
                <h1 class="page-title">✨ 创建新帖子</h1>
                <p class="page-subtitle">分享你的想法，让世界听见你的声音</p>
            </div>
        </div>

        <!-- 主要内容区域 -->
        <div class="content-wrapper">
            <el-card class="form-card" shadow="never">
                <!-- 卡片头部 -->
                <template #header>
                    <div class="card-header">
                        <div class="header-left">
                            <el-button 
                                class="back-btn"
                                type="info" 
                                :icon="ArrowLeftBold" 
                                circle 
                                size="large"
                                @click="goBack"
                            />
                            <div class="header-info">
                                <h2>📝 发布新内容</h2>
                                <p>填写以下信息，让你的帖子更吸引人</p>
                            </div>
                        </div>
                        <div class="header-right"></div>
                    </div>
                </template>

                <!-- 表单内容 -->
                <el-form :model="form" label-width="90px" class="post-form">
                    <!-- 分类选择 -->
                    <el-form-item label="🏷️ 分类" required class="form-item">
                        <el-select 
                            v-model="form.tagId" 
                            placeholder="请选择帖子分类"
                            class="category-select"
                            size="large"
                        >
                            <el-option
                                v-for="tag in tags"
                                :key="tag.tagId"
                                :label="tag.tagName"
                                :value="tag.tagId"
                            >
                                <div class="option-content">
                                    <span class="option-icon">📂</span>
                                    <span class="option-text">{{ tag.tagName }}</span>
                                </div>
                            </el-option>
                        </el-select>
                    </el-form-item>

                    <!-- 标题输入 -->
                    <el-form-item label="📌 标题" required class="form-item">
                        <el-input 
                            v-model="form.title" 
                            placeholder="输入一个吸引人的标题吧～"
                            maxlength="50"
                            show-word-limit
                            clearable
                            size="large"
                            class="title-input"
                        />
                    </el-form-item>

                    <!-- 内容编辑器 -->
                    <el-form-item label="📄 内容" required class="form-item">
                        <div class="editor-wrapper">
                            <el-tooltip content="插入表情" placement="top">
                                <el-button 
                                    class="editor-emoji-btn" 
                                    circle 
                                    @click="showEmoji = !showEmoji"
                                >
                                    <el-icon><Promotion /></el-icon>
                                </el-button>
                            </el-tooltip>
                            <!-- 表情选择器 -->
                            <transition name="emoji-slide">
                                <div v-if="showEmoji" class="emoji-picker">
                                    <div class="emoji-header">
                                        <span class="emoji-title">😊 选择表情</span>
                                        <el-button 
                                            type="text" 
                                            size="small"
                                            @click="showEmoji = false"
                                        >
                                            ✕
                                        </el-button>
                                    </div>
                                    <div class="emoji-grid">
                                        <span 
                                            v-for="emoji in emojis" 
                                            :key="emoji"
                                            @click="insertEmoji(emoji)"
                                            class="emoji-item"
                                        >{{ emoji }}</span>
                                    </div>
                                </div>
                            </transition>
                            
                            <el-input
                                v-model="form.content"
                                type="textarea"
                                :rows="12"
                                placeholder="分享你的想法吧～可以插入表情和图片哦！"
                                resize="none"
                                class="content-input"
                                maxlength="1000"
                                show-word-limit
                            />
                        </div>
                    </el-form-item>

                    <!-- 提交按钮 -->
                    <el-form-item class="submit-section">
                        <div class="submit-wrapper">
                            <el-button 
                                type="primary" 
                                size="large" 
                                @click="submitForm"
                                :loading="submitting"
                                class="submit-btn"
                                :disabled="!isFormValid"
                            >
                                <el-icon v-if="!submitting" class="submit-icon"><Promotion /></el-icon>
                                <span class="submit-text">
                                    {{ submitting ? '发布中...' : '🚀 立即发布' }}
                                </span>
                            </el-button>
                            <div class="submit-tips">
                                <p>💡 提示：确保内容真实、有价值，避免重复发布</p>
                            </div>
                        </div>
                    </el-form-item>
                </el-form>
            </el-card>
        </div>
    </div>
</template>

<script setup>
    import { ref, onMounted, computed } from 'vue'
    import {get, post} from '@/net'
    import axios from 'axios'
    import { useRouter } from 'vue-router'
    import { ElMessage, ElNotification, ElTooltip } from 'element-plus'
    import { ArrowLeftBold, Plus, Promotion } from '@element-plus/icons-vue'
    
    const router = useRouter()
    const tags = ref([])
    const form = ref({
        userId: '',
        title: '',
        content: '',
        tagId: ''
    })

    const submitting = ref(false)
    const showEmoji = ref(false)
    
    // 常用表情列表
    const emojis = [
        '😀','😃','😄','😁','😆','😅','😂','🤣','😊','😇',
        '🙂','🙃','😉','😌','😍','🥰','😘','😗','😙','😚',
        '😋','😛','😝','😜','🤪','🤨','🧐','🤓','😎','🤩',
        '🥳','😏','😒','😞','😔','😟','😕','🙁','😣','😖',
        '😫','😩','🥺','😢','😭','😤','😠','😡','🤬','🤯',
        '😳','🥵','🥶','😱','😨','😰','😥','😓','🤗','🤔',
        '🤭','🤫','🤥','😶','😐','😑','😬','🙄','😯','😦',
        '😧','😮','😲','🥱','😴','🤤','😪','😵','🤐','🥴',
        '🤢','🤮','🤧','😷','🤒','🤕','🤑','🤠','😈','👿',
        '👹','👺','💀','☠️','💩','🤡','👻','💪','👍','👎',
        '👌','✌️','🤞','🤟','🤘','🤙','👈','👉','👆','🖕',
        '👇','☝️','👋','🤚','🖐️','✋','🖖','👌','🤌','🤏',
        '✌️','🤞','🤟','🤘','🤙','👈','👉','👆','🖕','👇'
    ]

    // 计算属性
    const isFormValid = computed(() => {
        return form.value.title.trim() && 
               form.value.content.trim() && 
               form.value.tagId
    })

    // 获取分类标签
    const fetchTags = async () => {
        try {
            const response = await axios.get('/api/posts/tags')
            tags.value = response.data
        } catch (error) {
            console.error('获取标签失败:', error)
            ElMessage.error('获取分类标签失败')
        }
    }
    
    // 插入表情
    const insertEmoji = (emoji) => {
        form.value.content += emoji
        showEmoji.value = false
    }
    
    // 返回列表
    const goBack = () => {
        router.push('/index/posts')
    }

    // 用户信息
    let user = null;

    // 提交表单
    const submitForm = async () => {
        // 验证用户信息
        if (!user) {
            ElMessage.error('请先登录！')
            router.push('/')
            return
        }

        if (!isFormValid.value) {
            ElMessage.warning('请完善所有必填信息')
            return
        }

        submitting.value = true
        try {
            await axios.post('/api/posts/create', {
                userId: user.id,
                title: form.value.title.trim(),
                content: form.value.content.trim(),
                tagId: form.value.tagId
            }, {
                headers: {
                    'Content-Type': 'application/json',
                }
            })

            ElNotification.success({
                title: '发布成功 🎉',
                message: '你的帖子已成功发布！',
                duration: 2000
            })
            
            setTimeout(() => {
                router.push('/index/posts')
            }, 2000)
            
        } catch (error) {
            console.error('提交失败:', error)
            ElMessage.error(`发布失败：${error.response?.data?.message || '未知错误'}`)
        } finally {
            submitting.value = false
        }
    }

    onMounted(() => {
        // 获取用户信息
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
            ElMessage.error('用户数据格式错误！')
            router.push('/')
            return
        }

        if (!user.id) {
            ElMessage.error('用户ID缺失！')
            router.push('/')
            return
        }

        fetchTags()
    })
</script>

<style scoped>
.create-page {
    min-height: 100vh;
    background: linear-gradient(135deg, var(--bg-start) 0%, var(--bg-end) 100%);
    position: relative;
    overflow: hidden;
}

/* 页面头部 */
.page-header {
    position: relative;
    height: 200px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    /* 使用色卡：深蓝灰 → 中蓝灰 渐变，避免纯白背景 */
    background: linear-gradient(135deg, var(--sidebar-start) 0%, var(--sidebar-end) 100%);
}

.header-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    /* 轻微薄荷青与蓝灰叠加光感，替代白色蒙层 */
    background:
      radial-gradient(60% 80% at 30% 30%, rgba(34, 211, 169, 0.18), transparent 60%),
      radial-gradient(50% 70% at 70% 40%, rgba(107, 138, 161, 0.18), transparent 60%),
      linear-gradient(135deg, rgba(255,255,255,0.06), rgba(255,255,255,0.04));
    backdrop-filter: blur(10px);
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
    animation: titleFloat 3s ease-in-out infinite;
}

@keyframes titleFloat {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-5px); }
}

.page-subtitle {
    font-size: 1.1rem;
    opacity: 0.9;
    margin: 0;
    font-weight: 300;
}

/* 内容区域 */
.content-wrapper {
    position: relative;
    z-index: 10;
    margin-top: -50px;
    padding: 0 20px 40px;
}

.form-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: 20px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    animation: cardSlideIn 0.8s ease-out;
}

@keyframes cardSlideIn {
    from {
        opacity: 0;
        transform: translateY(50px) scale(0.95);
    }
    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

/* 卡片头部 */
.card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 30px 40px;
    background: linear-gradient(135deg, var(--sidebar-start) 0%, var(--sidebar-end) 100%);
    color: white;
    position: relative;
    overflow: hidden;
}

.card-header::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
    animation: headerShine 6s ease-in-out infinite;
}

@keyframes headerShine {
    0%, 100% { transform: translate(0, 0) rotate(0deg); }
    50% { transform: translate(-10px, -10px) rotate(1deg); }
}

.header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    position: relative;
    z-index: 2;
}

.back-btn {
    background: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: white;
    transition: all 0.3s ease;
}

.back-btn:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateX(-3px);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.header-info h2 {
    margin: 0 0 5px 0;
    font-size: 1.5rem;
    font-weight: 600;
}

.header-info p {
    margin: 0;
    opacity: 0.8;
    font-size: 0.9rem;
}

.header-right {
    position: relative;
    z-index: 2;
}

.emoji-trigger {
    background: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: white;
    transition: all 0.3s ease;
}

.emoji-trigger:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: scale(1.1);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

/* 表单样式 */
.post-form {
    padding: 40px;
}

.form-item {
    margin-bottom: 30px;
    animation: formItemSlideIn 0.6s ease-out;
    animation-fill-mode: both;
}

.form-item:nth-child(1) { animation-delay: 0.1s; }
.form-item:nth-child(2) { animation-delay: 0.2s; }
.form-item:nth-child(3) { animation-delay: 0.3s; }

@keyframes formItemSlideIn {
    from {
        opacity: 0;
        transform: translateX(-30px);
    }
    to {
        opacity: 1;
        transform: translateX(0);
    }
}

.category-select {
    width: 100%;
}

.option-content {
    display: flex;
    align-items: center;
    gap: 10px;
}

.option-icon {
    font-size: 1.2rem;
}

.option-text {
    font-weight: 500;
}

.title-input {
    width: 100%;
}

.title-input :deep(.el-input__wrapper) {
    border-radius: 12px;
    transition: all 0.3s ease;
}

.title-input :deep(.el-input__wrapper:hover) {
    box-shadow: 0 4px 15px rgba(18, 191, 160, 0.25);
}

/* 编辑器区域 */
.editor-wrapper {
    position: relative;
    width: 100%;
}

.content-input {
    width: 100%;
}

.content-input :deep(.el-textarea__inner) {
    min-height: 300px;
    border-radius: 12px;
    padding: 20px;
    padding-right: 56px; /* 为右上角表情按钮留出空间 */
    font-size: 1rem;
    line-height: 1.6;
    transition: all 0.3s ease;
    border: 2px solid #e2e8f0;
}

.content-input :deep(.el-textarea__inner:focus) {
    border-color: var(--brand-end);
    box-shadow: 0 0 0 3px rgba(18, 191, 160, 0.15);
}

/* 表情选择器 */
.emoji-picker {
    position: absolute;
    bottom: calc(100% + 15px);
    right: 0;
    z-index: 1000;
    background: white;
    border-radius: 16px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
    border: 1px solid #e2e8f0;
    overflow: hidden;
    animation: emojiSlideIn 0.3s ease-out;
}

/* 输入区内的表情触发按钮（无填充描边） */
.editor-emoji-btn {
    position: absolute;
    right: 12px;
    top: 12px;
    z-index: 2;
    background: transparent;
    border: 1.5px solid var(--brand-end);
    color: var(--brand-end);
    transition: all 0.25s var(--motion-ease);
}
.editor-emoji-btn:hover { background: var(--brand-hover); box-shadow: 0 6px 16px rgba(18,191,160,0.25); }
.editor-emoji-btn:focus-visible { outline: none; box-shadow: 0 0 0 3px rgba(18,191,160,0.25); }

@keyframes emojiSlideIn {
    from {
        opacity: 0;
        transform: translateY(10px) scale(0.95);
    }
    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

.emoji-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 15px 20px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
}

.emoji-title {
    font-weight: 600;
    color: #374151;
}

.emoji-grid {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    gap: 8px;
    padding: 15px;
    max-height: 200px;
    overflow-y: auto;
}

.emoji-item {
    cursor: pointer;
    padding: 8px;
    font-size: 1.2rem;
    text-align: center;
    border-radius: 8px;
    transition: all 0.2s ease;
    user-select: none;
}

.emoji-item:hover {
    background: #f1f5f9;
    transform: scale(1.2);
}

/* 提交区域 */
.submit-section {
    margin-top: 40px;
    text-align: center;
}

.submit-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
}

.submit-btn {
    min-width: 200px !important;
    height: 50px !important;
    font-size: 1.1rem !important;
    font-weight: 600 !important;
    border-radius: 25px !important;
    background: linear-gradient(135deg, var(--brand-start) 0%, var(--brand-end) 100%) !important;
    border: none !important;
    color: white !important;
    transition: all 0.3s ease !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    gap: 10px !important;
    position: relative !important;
    overflow: hidden !important;
}

.submit-btn:hover {
    transform: translateY(-3px) !important;
    box-shadow: 0 8px 25px rgba(18, 191, 160, 0.35) !important;
    background: linear-gradient(135deg, #27d6b1 0%, #12bfa0 100%) !important;
}

.submit-btn:active {
    transform: translateY(-1px) !important;
}

.submit-btn:disabled {
    background: #cbd5e0 !important;
    color: #718096 !important;
    cursor: not-allowed !important;
    transform: none !important;
    box-shadow: none !important;
}

.submit-icon {
    font-size: 1.2rem !important;
    margin-right: 5px !important;
}

.submit-text {
    font-weight: 600 !important;
    white-space: nowrap !important;
}

/* 确保Element Plus按钮样式不覆盖我们的自定义样式 */
.submit-btn.el-button--primary {
    background: linear-gradient(135deg, var(--brand-start) 0%, var(--brand-end) 100%) !important;
    border-color: transparent !important;
}

.submit-btn.el-button--primary:hover {
    background: linear-gradient(135deg, #27d6b1 0%, #12bfa0 100%) !important;
    border-color: transparent !important;
}

.submit-btn.el-button--primary:focus {
    background: linear-gradient(135deg, #27d6b1 0%, #12bfa0 100%) !important;
    border-color: transparent !important;
    box-shadow: 0 0 0 3px rgba(18, 191, 160, 0.25) !important;
}

.submit-btn.el-button--primary.is-loading {
    background: linear-gradient(135deg, var(--brand-start) 0%, var(--brand-end) 100%) !important;
    border-color: transparent !important;
}

.submit-btn.el-button--primary.is-disabled {
    background: #cbd5e0 !important;
    border-color: #cbd5e0 !important;
    color: #718096 !important;
}

.submit-tips {
    color: #64748b;
    font-size: 0.9rem;
    text-align: center;
}

.submit-tips p {
    margin: 0;
}

/* 动画过渡 */
.emoji-slide-enter-active,
.emoji-slide-leave-active {
    transition: all 0.3s ease;
}

.emoji-slide-enter-from,
.emoji-slide-leave-to {
    opacity: 0;
    transform: translateY(10px) scale(0.95);
}

/* 响应式设计 */
@media (max-width: 768px) {
    .page-title {
        font-size: 2rem;
    }
    
    .content-wrapper {
        padding: 0 15px 30px;
    }
    
    .card-header {
        padding: 20px;
        flex-direction: column;
        gap: 15px;
        text-align: center;
    }
    
    .header-left {
        flex-direction: column;
        gap: 10px;
    }
    
    .post-form {
        padding: 20px;
    }
    
    .emoji-grid {
        grid-template-columns: repeat(6, 1fr);
    }
    
    .submit-btn {
        width: 100% !important;
        min-width: auto !important;
    }
}

@media (max-width: 480px) {
    .page-header {
        height: 150px;
    }
    
    .page-title {
        font-size: 1.8rem;
    }
    
    .emoji-grid {
        grid-template-columns: repeat(5, 1fr);
    }
}
</style>