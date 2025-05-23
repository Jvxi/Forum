<template>
    <div class="create-container">
        <el-card class="form-card">
            <template #header>
            <div class="card-header">
                <!-- 返回按钮调整位置 -->
                <el-button 
                    class="fixed-back-btn"
                    type="info" 
                    :icon="ArrowLeftBold" 
                    circle 
                    size="large"
                    @click="goBack"
                />
                <h2>📝 创建新帖子</h2>
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
                <!-- 新增分类选择 -->
                <el-form-item label="🏷️ 分类" required>
                    <el-select 
                    v-model="form.tagId" 
                    placeholder="请选择分类"
                    style="width: 100%"
                    >
                    <el-option
                        v-for="tag in tags"
                        :key="tag.tagId"
                        :label="tag.tagName"
                        :value="tag.tagId"
                    />
                    </el-select>
                </el-form-item>
                <!-- 标题输入 -->
                <el-form-item label="📌 标题" required>
                    <el-input 
                    v-model="form.title" 
                    placeholder="输入吸引人的标题吧～"
                    maxlength="50"
                    show-word-limit
                    clearable
                    />
                </el-form-item>
        
                <!-- 内容编辑器 -->
                <el-form-item label="📄 内容" required>
                    <div class="editor-wrapper">
                        <!-- 表情选择器 -->
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
                            placeholder="分享你的想法吧～可以插入表情和图片哦！"
                            resize="none"
                            class="content-input"
                        />
                    </div>
                </el-form-item>
        
                <!-- 图片上传独立区块 -->
                <!-- <el-form-item label="🖼️ 图片" class="upload-item">
                    <el-upload
                    v-model:file-list="fileList"
                    action="/api/upload"
                    multiple
                    list-type="picture-card"
                    :limit="5"
                    :on-exceed="handleExceed"
                    >
                    <el-icon><Plus /></el-icon>
                    </el-upload>
                    <div class="upload-tip">最多可上传5张图片（支持拖拽）</div>
                </el-form-item> -->
        
                <!-- 提交按钮 -->
                <el-form-item class="submit-btn">
                    <el-button 
                    type="primary" 
                    size="large" 
                    @click="submitForm"
                    :loading="submitting"
                    >
                    🚀 立即发布
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import {get, post} from '@/net'
    import axios from 'axios'
    import { useRouter } from 'vue-router'
    import { ElMessage, ElNotification, ElTooltip } from 'element-plus'
    import { ArrowLeftBold, Plus, Promotion } from '@element-plus/icons-vue'
    
    
    const router = useRouter()
    const tags = ref([]) // 存储标签列表
    const form = ref({
        userId: '',
        title: '',
        content: '',
        tagId: ''
    })

    // 新增：获取分类标签
    const fetchTags = async () => {
        try {
            const response = await axios.get('/api/posts/tags')
            tags.value = response.data
        } catch (error) {
            console.error('获取标签失败:', error)
            ElMessage.error('获取分类标签失败')
        }
    }

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
        '👹','👺','💀','☠️','💩','🤡','👻','💪'
    ]
    
    // 插入表情
    const insertEmoji = (emoji) => {
        form.value.content += emoji
        showEmoji.value = false
    }
    
    // 返回列表
    const goBack = () => {
        router.push('/index/posts')
    }

    // 1. 获取用户信息
    const userString = localStorage.getItem('user');
    if (!userString) {
        alert('请先登录！');
    }

    let user;
    try {
        user = JSON.parse(userString);
        console.log(user);
    } catch (error) {
        console.error('解析用户数据失败:', error);
        alert('用户数据格式错误！');
    }

    if (!user.id) {
        alert('用户ID缺失！');
    }

    // 提交表单
    const submitForm = async () => {
        if (!form.value.title.trim()) {
            ElMessage.warning('标题不能为空哦～')
            return
        }
        if (!form.value.content.trim()) {
            ElMessage.warning('内容不能为空哦～')
            return
        }
        if (!form.value.tagId) {
            ElMessage.warning('请选择分类标签')
            return
        }

        submitting.value = true
        try {
            axios.post('/api/posts/create', {
                userId: user.id,
                title: form.value.title,
                content: form.value.content,
                tagId: form.value.tagId
                }, {
                headers: {
                    'Content-Type': 'application/json', // 明确指定 JSON 格式
                }
            })

            ElNotification.success({
                title: '发布成功 🎉',
                message: '你的帖子已成功发布！',
                duration: 2000
            })
            router.push('/index/posts')
        } catch (error) {
            console.error('提交失败:', error)
            ElMessage.error(`发布失败：${error.response?.data?.message || '未知错误'}`)
        } finally {
            submitting.value = false
        }
    }

    // 初始化获取标签数据
    onMounted(() => {
        fetchTags()
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
</style>