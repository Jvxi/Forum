import { createRouter, createWebHistory } from 'vue-router'
import {useStore} from "@/stores";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'welcome',
      component: ()=>import('@/views/WelcomeViews.vue'),
      children: [
        { path: '', name: 'welcome-login', component: ()=>import('@/views/welcome/LoginPage.vue') },
        { path: 'register', name: 'welcome-register', component: ()=>import('@/views/welcome/RegisterPage.vue') },
        { path: 'forget', name: 'welcome-forget', component: ()=>import('@/views/welcome/ForgetPawPage.vue') }
      ]
    },
    {
      path: '/index',
      name: 'index',
      component:()=>import('@/views/indexView.vue'),
      redirect: '/index/posts',
      children: [
        {
          // 帖子列表 - 默认页面
          path: 'posts',
          name: 'index-posts',
          component: ()=>import('@/views/index/Post/PostList.vue')
        },
        {
          // 创建帖子
          path: 'posts/create',
          name: 'index-post-create',
          component: ()=>import('@/views/index/Post/PostCreate.vue')
        },
        {
          // 帖子详情
          path: 'posts/:id',
          name: 'index-post-detail',
          component: ()=>import('@/views/index/Post/PostDetail.vue')
        },
        {
          // 编辑帖子
          path: 'posts/edit/:id',
          name: 'index-post-edit',
          component: ()=>import('@/views/index/Post/PostEdit.vue')
        },
        {
          // 帖子管理
          path: 'manage',
          name: 'index-manage',
          component:()=>import('@/views/index/Post/PostManage.vue')
        },
        {
          // 表白墙
          path: 'likes',
          name: 'index-likes',
          component:()=>import('@/views/index/LikeList.vue')
        },
        {
          // 我的收藏
          path: 'favorites',
          name: 'index-favorites',
          component:()=>import('@/views/index/Favorites.vue')
        },
        {
          // 个人设置
          path: 'settings',
          name: 'index-settings',
          component:()=>import('@/views/index/Settings.vue')
        },
        {
          // 开发者信息
          path: 'developer',
          name: 'index-developer',
          component: ()=>import('@/views/index/Developer.vue')
        }
      ]
    }
  ]
})

//路由守卫
router.beforeEach((to, from, next)=>{
  const store = useStore()
  
  if(store.auth.user != null && to.name.startsWith('welcome-')){
    next('/index')
  }else if(store.auth.user == null && to.fullPath.startsWith('/index')){
    next('/')
  }else if(to.matched.length === 0){
    next('/index')
  }else{
    next()
  }
})

export default router