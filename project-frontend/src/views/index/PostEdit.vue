<template>
    <div class="create-container">
        <el-card class="form-card">
            <template #header>
            <div class="card-header">
                <el-button 
                class="fixed-back-btn"
                type="info" 
                :icon="ArrowLeftBold" 
                circle 
                size="large"
                @click="goBack"
                />
                <h2>✏️ 编辑帖子</h2>
                <el-tooltip content="插入表情" placement="top">
                <el-button 
                    class="emoji-trigger" 
                    type="info" 
                    circle 
                    @click="showEmoji = !showEmoji"
                >
                    <el-icon><Promotion /></el-icon>
                </el-button>
                </el-tooltip>
            </div>
            </template>
    
            <el-form :model="form" label-width="90px">
            <el-form-item label="📌 标题" required>
                <el-input 
                v-model="form.title" 
                placeholder="输入新的标题"
                maxlength="50"
                show-word-limit
                clearable
                />
            </el-form-item>
    
            <el-form-item label="📄 内容" required>
                <div class="editor-wrapper">
                <transition name="fade">
                    <div v-if="showEmoji" class="emoji-picker">
                    <span 
                        v-for="emoji in emojis" 
                        :key="emoji"
                        @click="insertEmoji(emoji)"
                    >{{ emoji }}</span>
                    </div>
                </transition>
                
                <el-input
                    v-model="form.content"
                    type="textarea"
                    :rows="8"
                    placeholder="更新你的内容"
                    resize="none"
                    class="content-input"
                />
                </div>
            </el-form-item>
    
            <el-form-item class="submit-btn">
                <el-button 
                type="primary" 
                size="large" 
                @click="submitForm"
                :loading="submitting"
                >
                🚀 更新帖子
                </el-button>
            </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import axios from 'axios'
    import { ElMessage, ElNotification } from 'element-plus'
    import { ArrowLeftBold, Promotion } from '@element-plus/icons-vue'
    
    const route = useRoute()
    const router = useRouter()
    const postId = ref('')
    const form = ref({
        title: '',
        content: '',
        userId: ''
    })
    const submitting = ref(false)
    const showEmoji = ref(false)

    // 用户信息处理（与PostDetail.vue保持一致）
    let user = null
    try {
        const userString = localStorage.getItem('user')
        if (!userString) {
            ElMessage.error('请先登录')
            router.push('/login')
            throw new Error('未登录')
        }
        user = JSON.parse(userString)
        if (!user?.id) {
            ElMessage.error('用户信息异常')
            router.push('/login')
            throw new Error('无效用户')
        }
    } catch (error) {
        console.error('用户数据处理失败:', error)
    }
    
    // 获取帖子数据
    const fetchPost = async () => {
        try {
            const response = await axios.get(`/api/posts/${postId.value}`)
            if (response.data.id !== user.id) {
                ElMessage.error('没有编辑权限')
                router.go(-1)
            }
            form.value = {
                title: response.data.title,
                content: response.data.content,
                userId: user.id
            }
        } catch (error) {
            console.error('获取帖子失败:', error)
            ElMessage.error(error.response?.data?.message || '加载失败')
            router.go(-1)
        }
    }
    
    // 表情相关逻辑
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
        '👹','👺','💀','☠️','💩','🤡','👻','💪'
    ]
    
    const insertEmoji = (emoji) => {
        form.value.content += emoji
        showEmoji.value = false
    }
    
    // 表单提交
    const submitForm = async () => {
        if (!form.value.title.trim()) {
        ElMessage.warning('标题不能为空')
        return
        }
        if (!form.value.content.trim()) {
        ElMessage.warning('内容不能为空')
        return
        }
    
        submitting.value = true
        try {
        await axios.put(`/api/posts/${postId.value}`, form.value)
        ElNotification.success({
            title: '更新成功',
            message: '帖子已更新',
            duration: 2000
        })
        router.push(`/index/posts/${postId.value}`)
        } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error(error.response?.data?.message || '更新失败')
        } finally {
        submitting.value = false
        }
    }
    
    const goBack = () => {
        router.go(-1)
    }
    
    // 初始化
    onMounted(() => {
        postId.value = route.params.id
        fetchPost()
    })
</script>

<style scoped>
    .create-container {
        position: relative;
        min-height: 100vh;
        padding: 1rem;
        background: linear-gradient(135deg, #f8f9fa 0%, #f1f3f5 100%);
    }
    
    .form-card {
        width: 98%;
        max-width: 100%;
        margin: 0 auto;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        
        :deep(.el-card__header) {
            padding: 1rem 1.5rem;
            background: linear-gradient(135deg, #409eff, #79bbff);
        }
    }
    
    .card-header {
        display: flex;
        align-items: center;
        gap: 1rem;
        
        h2 {
            margin: 0;
            font-size: 1.3rem;
            flex-grow: 1;
        }
        
        .back-btn {
            margin-right: 0.5rem;
            transition: all 0.3s;
            
            &:hover {
            transform: translateX(-3px);
            }
        }
    }
    
    .editor-wrapper {
        width: 100%;
    }
    
    .emoji-picker {
        position: absolute;
        bottom: calc(100% + 10px);
        right: 0;
        z-index: 10;
        background: white;
        padding: 10px;
        border-radius: 12px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        max-height: 200px;
        overflow-y: auto;
        display: grid;
        grid-template-columns: repeat(8, 1fr);
        gap: 8px;
        
        span {
        cursor: pointer;
        padding: 4px;
        font-size: 1.2rem;
        transition: all 0.2s;
        
        &:hover {
            transform: scale(1.3);
            filter: brightness(1.1);
        }
        }
    }
    
    .content-input {
        :deep(.el-textarea__inner) {
            min-height: 300px !important;
            width: 100%;
            font-size: 1rem;
            line-height: 1.6;
            padding: 1rem;
        }
    }
    
    .upload-item {
        :deep(.el-upload) {
            width: 100px;
            height: 100px;
        }
    }
    
    .upload-tip {
        color: #909399;
        font-size: 0.9rem;
        margin-top: 0.5rem;
    }
    
    .submit-btn {
        text-align: center;
        margin-top: 2rem;
        
        button {
        width: 240px;
        height: 48px;
        font-size: 1.1rem;
        letter-spacing: 1px;
        transition: all 0.3s;
        
        &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(64,158,255,0.3);
        }
        }
    }
    
    .fade-enter-active,
    .fade-leave-active {
        transition: opacity 0.3s, transform 0.3s;
    }
    
    .fade-enter-from,
    .fade-leave-to {
        opacity: 0;
        transform: translateY(10px);
    }
    
    @media (max-width: 768px) {
        .create-container {
        padding: 2rem;
        }
        
        .fixed-back-btn {
        top: 1rem;
        left: 1rem;
        }
        
        .form-card {
            width: 90%;
            max-width: 1200px;
            
            :deep(.el-card__header) {
            padding: 1.2rem 2rem;
            }
        }

        .card-header h2 {
            font-size: 1.5rem;
        }
        
        .emoji-picker {
        grid-template-columns: repeat(6, 1fr);
        right: 10px;
        }
        
        .content-input {
            :deep(.el-textarea__inner) {
            min-height: 400px !important;
            }
        }
        
        .upload-item {
            :deep(.el-upload) {
            width: 150px;
            height: 150px;
            }
        }
        
        .submit-btn button {
        width: 100%;
        }
    }
    
    /* 可添加编辑页专属样式 */
    .edit-title {
        color: #67C23A;
    }
</style>