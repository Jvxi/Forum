<!-- 编辑帖子页面 -->
<template>
    <div class="create-container">
        <!-- 顶部页头（模仿创建/详情页） -->
        <div class="page-header">
            <div class="header-bg"></div>
            <div class="header-content">
                <h1 class="page-title">✨ 编辑帖子</h1>
                <p class="page-subtitle">更新你的内容，让它更精彩</p>
            </div>
        </div>

        <div class="content-wrapper">
        <el-card class="form-card" shadow="never">
            <template #header>
            <div class="card-header">
                <el-button 
                class="fixed-back-btn"
                :icon="Back" 
                circle 
                size="large"
                @click="goBack"
                />
                <h2>✏️ 编辑帖子</h2>
                <div class="autosave-status" :class="autoSaveState">
                    <el-icon v-if="autoSaveState === 'saving'" class="spin"><Loading /></el-icon>
                    <el-icon v-else-if="autoSaveState === 'saved'"><CircleCheck /></el-icon>
                    <span v-if="autoSaveState === 'saving'">正在保存…</span>
                    <span v-else-if="autoSaveState === 'saved'">已保存到草稿</span>
                </div>
            </div>
            </template>
    
            <el-form :model="form" label-width="90px">
            <el-form-item label="🏷️ 分类" required>
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
            <el-form-item label="📌 标题" required>
                <el-input 
                v-model="form.title" 
                placeholder="输入新的标题"
                maxlength="50"
                show-word-limit
                clearable
                class="title-input"
                />
            </el-form-item>
    
            <el-form-item label="📄 内容" required>
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
                    maxlength="1000"
                    show-word-limit
                />
                </div>
            </el-form-item>
    
            <el-form-item class="submit-btn">
                <div class="submit-actions">
                    <el-button 
                        class="update-btn"
                        type="primary" 
                        size="large" 
                        @click="submitForm"
                        :loading="submitting"
                    >
                        🚀 更新帖子
                    </el-button>
                </div>
            </el-form-item>
            </el-form>
        </el-card>
        </div>
    </div>
</template>

<script setup>
    import { ref, onMounted, watch, onUnmounted } from 'vue'
    import { useRoute, useRouter, onBeforeRouteLeave } from 'vue-router'
    import axios from 'axios'
    import { ElMessage, ElNotification, ElMessageBox } from 'element-plus'
    import { ArrowLeftBold, Promotion, Loading, CircleCheck, Back } from '@element-plus/icons-vue'
    
    const route = useRoute()
    const router = useRouter()
    const postId = ref('')
    const form = ref({
        title: '',
        content: '',
        userId: '',
        tagId: ''
    })
    const submitting = ref(false)
    const showEmoji = ref(false)
    const tags = ref([])
    const autoSaveState = ref('idle') // idle | saving | saved
    let autoSaveTimer = null
    const lastSavedSnapshot = ref({ title: '', content: '', tagId: '' })

    const getSnapshot = () => ({
        title: form.value.title || '',
        content: form.value.content || '',
        tagId: form.value.tagId || ''
    })

    const isDirty = () => JSON.stringify(getSnapshot()) !== JSON.stringify(lastSavedSnapshot.value)

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
            console.log(response);
            console.log(user);
            if (response.data.userId !== user.id) {
                ElMessage.error('没有编辑权限')
                router.go(-1)
            }
            form.value = {
                title: response.data.title,
                content: response.data.content,
                userId: user.id,
                tagId: response.data.tagId
            }
            const draft = localStorage.getItem(`edit_draft_${postId.value}`)
            if (draft) {
                try {
                    const parsed = JSON.parse(draft)
                    form.value.title = parsed.title ?? form.value.title
                    form.value.content = parsed.content ?? form.value.content
                    form.value.tagId = parsed.tagId ?? form.value.tagId
                } catch {}
            }
            // 记录初始快照
            lastSavedSnapshot.value = getSnapshot()
        } catch (error) {
            console.error('获取帖子失败:', error)
            ElMessage.error(error.response?.data?.message || '加载失败')
            router.go(-1)
        }
    }

    const fetchTags = async () => {
        try {
            const res = await axios.get('/api/posts/tags')
            tags.value = res.data || []
        } catch (e) {
            console.error('获取标签失败', e)
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
            // 成功后更新快照并清理草稿
            lastSavedSnapshot.value = getSnapshot()
            try { localStorage.removeItem(`edit_draft_${postId.value}`) } catch {}
            autoSaveState.value = 'idle'
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
        fetchTags()
        window.addEventListener('beforeunload', handleBeforeUnload)
    })

    const scheduleAutoSave = () => {
        autoSaveState.value = 'saving'
        if (autoSaveTimer) clearTimeout(autoSaveTimer)
        autoSaveTimer = setTimeout(() => {
            try {
                localStorage.setItem(`edit_draft_${postId.value}` , JSON.stringify({
                    title: form.value.title,
                    content: form.value.content,
                    tagId: form.value.tagId
                }))
                autoSaveState.value = 'saved'
                lastSavedSnapshot.value = getSnapshot()
                ElNotification({
                    title: '已自动保存',
                    message: '草稿已保存到本地',
                    position: 'top-right',
                    duration: 1500,
                    type: 'success'
                })
                setTimeout(() => { autoSaveState.value = 'idle' }, 1200)
            } catch {}
        }, 1000)
    }

    watch(() => [form.value.title, form.value.content, form.value.tagId], scheduleAutoSave)

    // 离开页面前拦截（浏览器刷新/关闭）
    const handleBeforeUnload = (e) => {
        if (isDirty()) {
            e.preventDefault()
            e.returnValue = ''
        }
    }

    onUnmounted(() => {
        window.removeEventListener('beforeunload', handleBeforeUnload)
        if (autoSaveTimer) clearTimeout(autoSaveTimer)
    })

    // 路由离开拦截
    onBeforeRouteLeave((to, from, next) => {
        if (!isDirty()) return next()
        ElMessageBox.confirm('页面有未保存的更改，确认离开？', '提示', {
            confirmButtonText: '离开',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => next()).catch(() => next(false))
    })
</script>

<style scoped>
    .create-container {
        position: relative;
        min-height: 100vh;
        padding: 0 0 1rem 0;
        background: linear-gradient(135deg, var(--bg-start) 0%, var(--bg-end) 100%);
        animation: pageIn 0.5s ease-out both;
    }

    /* 顶部页头，复用创建/详情风格 */
    .page-header {
        position: relative;
        height: 200px;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;
        background: linear-gradient(135deg, var(--sidebar-start) 0%, var(--sidebar-end) 100%);
        margin-bottom: -50px;
    }

    .header-bg {
        position: absolute;
        inset: 0;
        background:
            radial-gradient(60% 80% at 30% 30%, rgba(34, 211, 169, 0.18), transparent 60%),
            radial-gradient(50% 70% at 70% 40%, rgba(107, 138, 161, 0.18), transparent 60%),
            linear-gradient(135deg, rgba(255,255,255,0.06), rgba(255,255,255,0.04));
        backdrop-filter: blur(10px);
    }

    .header-content {
        position: relative;
        z-index: 1;
        text-align: center;
        color: #fff;
    }

    .page-title {
        font-size: 2.2rem;
        font-weight: 700;
        margin: 0 0 8px 0;
        text-shadow: 0 2px 10px rgba(0,0,0,0.25);
        animation: titleFloat 3s ease-in-out infinite;
    }

    .page-subtitle {
        font-size: 1rem;
        opacity: 0.9;
        margin: 0;
        font-weight: 300;
    }

    @keyframes titleFloat {
        0%, 100% { transform: translateY(0); }
        50% { transform: translateY(-5px); }
    }

    .content-wrapper {
        position: relative;
        z-index: 2;
        padding: 0 1rem 1rem;
    }
    
    .form-card {
        width: 98%;
        max-width: 100%;
        margin: 0 auto;
        border-radius: 12px;
        box-shadow: 0 20px 40px rgba(0,0,0,0.12);
        overflow: visible;
        transform-origin: top center;
        animation: cardPop 0.4s var(--motion-ease) both;
        
        :deep(.el-card__header) {
            padding: 1rem 1.5rem;
            background: linear-gradient(135deg, var(--sidebar-start), var(--sidebar-end));
            color: #fff;
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

    /* 头部返回按钮（与创建页一致的风格） */
    .fixed-back-btn {
        background: transparent;
        border: 1.5px solid var(--brand-end);
        color: var(--brand-end);
        transition: all 0.25s var(--motion-ease);
    }
    .fixed-back-btn:hover {
        background: var(--brand-hover);
        transform: translateX(-3px);
        box-shadow: 0 6px 16px rgba(18,191,160,0.25);
    }
    .fixed-back-btn:focus-visible {
        outline: none;
        box-shadow: 0 0 0 3px rgba(18,191,160,0.25);
    }
    
    .editor-wrapper {
        width: 100%;
        position: relative;
    }
    
    .emoji-picker {
        position: absolute;
        top: 56px;
        right: 12px;
        z-index: 2000;
        background: #fff;
        padding: 12px;
        border-radius: 12px;
        box-shadow: var(--shadow-lg);
        border: 1px solid #e2e8f0;
        max-height: 200px;
        overflow-y: auto;
        display: grid;
        grid-template-columns: repeat(8, 1fr);
        gap: 8px;
        
        span {
        cursor: pointer;
        padding: 6px;
        font-size: 1.2rem;
        transition: all 0.2s;
        border-radius: 8px;
        
            &:hover {
                transform: scale(1.15);
                background: var(--brand-hover);
            }
        }
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
    
    .content-input {
        :deep(.el-textarea__inner) {
            min-height: 300px !important;
            width: 100%;
            font-size: 1rem;
            line-height: 1.6;
            padding: 1rem;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            transition: all var(--motion-duration) var(--motion-ease);
        }
        :deep(.el-textarea__inner:focus),
        :deep(.el-textarea__inner:focus-within) {
            border-color: var(--brand-end);
            box-shadow: 0 0 0 3px rgba(18,191,160,0.15);
        }
    }

    /* 标题输入框与创建页一致的交互效果 */
    .title-input :deep(.el-input__wrapper) {
        border-radius: 12px;
        transition: all 0.3s ease;
    }
    .title-input :deep(.el-input__wrapper:hover) {
        box-shadow: 0 4px 15px rgba(18, 191, 160, 0.25);
    }
    .title-input :deep(.el-input__wrapper.is-focus),
    .title-input :deep(.el-input__wrapper:focus-within) {
        box-shadow: 0 0 0 3px rgba(18,191,160,0.15);
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
        
        .submit-actions { display: flex; gap: 12px; justify-content: center; align-items: center; }
        .update-btn, .cancel-icon-btn {
            width: 200px;
            height: 48px;
            font-size: 1.05rem;
            letter-spacing: 0.5px;
            border-radius: 999px; /* 更大圆弧度 */
            transition: all 0.3s;
        }
        .cancel-icon-btn {
            width: 48px;
            min-width: 48px;
            height: 48px;
            border-radius: 50%;
            background: #ffffff;
            border: 1px solid #e2e8f0;
            color: #475569;
        }
        .cancel-icon-btn:hover { box-shadow: var(--shadow-md); transform: translateY(-1px); }

        .update-btn {
            background: linear-gradient(135deg, var(--brand-start), var(--brand-end));
            border: none;
            color: #fff;
        }
        .update-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(18,191,160,0.3);
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

    @keyframes pageIn {
        from { opacity: 0; transform: translateY(10px); }
        to { opacity: 1; transform: translateY(0); }
    }

    @keyframes cardPop {
        from { opacity: 0; transform: scale(0.98); }
        to { opacity: 1; transform: scale(1); }
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