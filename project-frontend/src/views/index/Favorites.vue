<template>
    <div class="page-container">
        <el-card class="box-card">
            <template #header>
            <div class="card-header">
                <span style="font-size: 1.2rem; font-weight: bold">我的收藏</span>
            </div>
            </template>
    
            <el-table 
            :data="favorites"
            style="width: 100%"
            v-loading="loading"
            empty-text="暂无收藏">
            <el-table-column prop="title" label="标题" width="300">
                <template #default="{ row }">
                <el-link type="primary" @click="viewPost(row.id)">{{ row.title }}</el-link>
                </template>
            </el-table-column>
            <el-table-column prop="author" label="作者" width="150" />
            <el-table-column prop="time" label="收藏时间" width="200" />
            <el-table-column label="操作">
                <template #default="{ row }">
                <el-button 
                    type="danger" 
                    size="small"
                    @click="removeFavorite(row.id)"
                    :loading="removeLoading[row.id]">
                    取消收藏
                </el-button>
                </template>
            </el-table-column>
            </el-table>
    
            <div class="pagination-container">
            <el-pagination
                v-model:current-page="currentPage"
                :page-size="pageSize"
                :total="total"
                layout="prev, pager, next"
                @current-change="handlePageChange"
            />
            </div>
        </el-card>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { get, post } from '@/net'
    import { ElMessage } from 'element-plus'
    import { useStore } from '@/stores'
    import { useRouter } from 'vue-router'
    
    const store = useStore()
    const router = useRouter()
    const loading = ref(true)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const favorites = ref([])
    const removeLoading = ref({})
    
    const loadFavorites = () => {
        loading.value = true
        get('/api/favorite/list', {
        page: currentPage.value,
        size: pageSize.value
        }, (data) => {
        favorites.value = data.list
        total.value = data.total
        loading.value = false
        })
    }
    
    const viewPost = (postId) => {
        // 使用正确的应用内路由，避免空白页
        router.push({ name: 'index-post-detail', params: { id: postId } })
    }
    
    const removeFavorite = (postId) => {
        removeLoading.value[postId] = true
        post(`/api/favorite/remove/${postId}`, null, () => {
        ElMessage.success('已取消收藏')
        loadFavorites()
        }, (error) => {
        ElMessage.error(error)
        }).finally(() => {
        removeLoading.value[postId] = false
        })
    }
    
    const handlePageChange = (newPage) => {
        currentPage.value = newPage
        loadFavorites()
    }
    
    onMounted(() => {
        loadFavorites()
    })
</script>

<style scoped>
    .page-container {
        padding: 20px;
    }
    
    .box-card {
        max-width: 1200px;
        margin: 0 auto;
    }
    
    .pagination-container {
        margin-top: 20px;
        display: flex;
        justify-content: center;
    }
    
    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
</style>