<template>
    <div class="post-management-container">
        <!-- 过滤区域 -->
        <div class="filter-group">
            <el-input
                v-model="filter.keyword"
                placeholder="搜索帖子标题"
                clearable
                @keyup.enter="fetchData"
            >
                <template #append>
                    <el-button :icon="Search" @click="fetchData" />
                </template>
            </el-input>
    
            <el-select
                v-model="filter.status"
                placeholder="状态筛选"
                @change="fetchData"
            >
                <el-option
                    v-for="item in statusOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
            </el-select>
        </div>
    
        <!-- 数据表格 -->
        <el-table
            :data="tableData"
            v-loading="loading"
            stripe
        >
            <el-table-column prop="id" label="ID" width="80" align="center" />
            <el-table-column prop="title" label="标题" min-width="200" />
            
            <el-table-column label="作者" width="120">
                <template #default="{ row }">
                    <el-tag>{{ row.authorName || '-' }}</el-tag>
                </template>
            </el-table-column>
    
            <el-table-column label="发布时间" width="180">
                <template #default="{ row }">
                    {{ formatDateTime(row.createTime) }}
                </template>
            </el-table-column>
    
            <el-table-column label="状态" width="100" align="center">
                <template #default="{ row }">
                    <el-tag :type="statusConfig[row.status]?.type || 'info'">
                        {{ statusConfig[row.status]?.text || row.status }}
                    </el-tag>
                </template>
            </el-table-column>
    
            <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                    <el-button 
                        size="small" 
                        :type="statusConfig[row.status]?.actionType"
                        :loading="row.status === 'changing'"
                        @click="toggleStatus(row)"
                    >
                        {{ statusConfig[row.status]?.actionText }}
                    </el-button>
                    <el-button
                        size="small"
                        type="danger"
                        @click="handleDelete(row.id)"
                    >删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    
        <!-- 分页组件 -->
        <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            layout="prev, pager, next, total"
            @current-change="fetchData"
            @size-change="fetchData"
        />
    </div>
</template>

<script setup>
    import { ref, reactive, onMounted } from 'vue'
    import { ElMessage, ElMessageBox } from 'element-plus'
    import { Search } from '@element-plus/icons-vue'
    import axios from 'axios'
    import router from '@/router'
    
    // 响应式状态
    const tableData = ref([])
    const loading = ref(false)
    const filter = reactive({ keyword: '', status: '' })
    const pagination = reactive({ current: 1, size: 10, total: 0 })
    
    // 配置数据
    const statusConfig = {
        visible: { text: '正常', type: 'success', actionText: '隐藏', actionType: 'warning' },
        hidden: { text: '隐藏', type: 'danger', actionText: '显示', actionType: 'success' },
        changing: { text: '修改中', type: 'warning' }
    }

    const statusOptions = [
        { label: '全部', value: '' },
        { label: '正常', value: 'visible' },
        { label: '隐藏', value: 'hidden' }
    ]

    // 获取当前用户信息
    const getCurrentUser = () => {
        try {
            const userData = localStorage.getItem('user')
            if (!userData) return null
            return JSON.parse(userData)
        } catch (error) {
            console.error('用户信息解析失败:', error)
            return null
        }
    }
    
    // 验证管理员权限
    const validateAdmin = () => {
        const user = getCurrentUser()
        if (!user) {
            ElMessage.error('请先登录')
            router.push('/login')
            return false
        }
        if (user.username !== '管理') {
            ElMessage.error('无权访问该页面')
            router.push('/index/posts')
            return false
        }
        return true
    }
    
    // 核心方法
    const fetchData = async () => {
        if (!validateAdmin() || pagination.current < 1) return
        const params = {
            pageNum: pagination.current,
            pageSize: pagination.size,
            keyword: filter.keyword.trim(),
            status: filter.status
        }
        try {
            loading.value = true
            const user = getCurrentUser()
            const { data } = await axios.get('/api/manage/lists', {
                params,
                headers: { Authorization: `Bearer ${user.token}` }
            })

            tableData.value = data
            pagination.total = data.length

            tableData.value = data.map(item => ({
                ...item,
                status: item.status.replace(/=+$/, '') // 去除末尾的等号
            }))

            // 自动校正页码
            if (!tableData.value.length && pagination.current > 1) {
                pagination.current--
                return fetchData()
            }
        } catch (error) {
            handleError(error, '数据获取失败')
        } finally {
            loading.value = false
        }
    }
    
    // 分页有效性检查
    const checkPagination = () => {
        if (pagination.current > 1 && tableData.value.length === 0) {
            pagination.current--
            fetchData()
        }
    }
    
    // 分页大小变化处理
    const handleSizeChange = (newSize) => {
        pagination.size = newSize
        pagination.current = 1
        fetchData()
    }
    
    // 状态切换操作
    const toggleStatus = async (row) => {
        if (!validateAdmin()) return
        console.log(row);
        
        const originalStatus = row.status
        console.log(row.status);
        
        try {
            const user = getCurrentUser()
            row.status = 'changing'
            const newStatus = originalStatus === 'visible' ? 'hidden' : 'visible'
            
            await axios.post(
                `/api/manage/${row.id}/status`, newStatus, {
                    headers: {
                        Authorization: `Bearer ${user.token}`
                    }
                }
            )
            
            row.status = newStatus
            ElMessage.success(`已${statusConfig[newStatus].text}帖子`)
        } catch (error) {
            row.status = originalStatus
            handleError(error, '状态修改失败')
        }
    }
    
    // 删除操作
    const handleDelete = async (id) => {
        if (!validateAdmin()) return
    
        try {
            await ElMessageBox.confirm('确定要永久删除该帖子吗？', '危险操作', {
                confirmButtonText: '确认删除',
                cancelButtonText: '取消',
                type: 'warning',
                confirmButtonClass: 'el-button--danger',
                beforeClose: async (action, instance, done) => {
                    if (action === 'confirm') {
                        instance.confirmButtonLoading = true
                        try {
                            const user = getCurrentUser()
                            await axios.delete(`/api/manage/post/${id}`, {
                                headers: {
                                    Authorization: `Bearer ${user.token}`
                                }
                            })
                            done()
                            ElMessage.success('删除成功')
                            await fetchData()
                        } catch (error) {
                            handleError(error, '删除失败')
                        } finally {
                            instance.confirmButtonLoading = false
                        }
                    } else {
                        done()
                    }
                }
            })
        } catch (error) {
            // 取消删除时不处理
        }
    }
    
    // 统一错误处理
    const handleError = (error, defaultMessage = '操作失败') => {
        if (axios.isCancel(error)) return
        
        let message = defaultMessage
        if (error.response) {
            message = error.response.data?.message || defaultMessage
            if (error.response.status === 401) {
                localStorage.removeItem('user')
                router.push('/login')
                return
            }
        }
        ElMessage.error(`${message} (${error.response?.status || '网络错误'})`)
    }
    
    // 日期格式化函数
    const formatDateTime = (timestamp) => {
        try {
            if (!timestamp) return '-'
            const date = new Date(timestamp)
            return date.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            }).replace(/\//g, '-')
        } catch {
            return '-'
        }
    }
    
    // 初始化加载
    onMounted(() => {
        if (validateAdmin()) {
            fetchData()
        }
    })
</script>

<style scoped>
    .post-management-container {
        padding: 20px;
        display: flex;
        flex-direction: column;
        gap: 20px;
    }

    .filter-group {
        display: flex;
        gap: 15px;
        
        .el-input { width: 300px; }
        .el-select { width: 120px; }
    }

    .el-pagination {
        margin-top: 20px;
        justify-content: center;
    }

    @media (max-width: 768px) {
        .filter-group {
            flex-direction: column;
            
            .el-input, .el-select { width: 100%; }
        }
    }
</style>