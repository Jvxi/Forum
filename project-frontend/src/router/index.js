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
      children: [
        { 
          path: 'posts',
          children:[
            { path: '', name: 'index-list', component: ()=>import('@/views/index/PostList.vue') },
            { path: 'create', name: 'index-post-create', component: ()=>import('@/views/index/PostCreate.vue') },
            { path: ':id', name: 'index-post-detail', component: ()=>import('@/views/index/PostDetail.vue') },
            { path: 'edit/:id', name: 'index-post-edit', component: () => import('@/views/index/PostEdit.vue') }
          ]
        },
        {
          path: 'likes',
          children:[
            { path: '', name: 'index-likes', component: ()=>import('@/views/index/LikeList.vue') }
          ]
        },
        {
          path: 'settings',
          name: 'index-settings',
          component:()=>import('@/views/index/Settings.vue')
        }
      ]
    }
  ]
})
//路由守卫
router.beforeEach((to, from, next)=>{
  const store = useStore()//用户的信息存储在这个全局变量里
  if(store.auth.user != null && to.name.startsWith('welcome-')){//用户已经登录且请求的页面的name以welcome-开头
    next('/index')//丢到index页面，不能回到关于欢迎的页面（登录，注册，重置密码）
  }else if(store.auth.user == null && to.fullPath.startsWith('/index')){//用户没有登录且请求的页面路径以index开头
    next('/')//不能让他访问，丢到登录页面
  }else if(to.matched.length === 0){//请求的页面不存在
    next('/index')//先丢到index页面。对于已登录的用户，就会展现index页面；对于没登录的用户，则会再被丢到登录页面
  }else{//其他情况该去哪去哪
    next()
  }
})
export default router