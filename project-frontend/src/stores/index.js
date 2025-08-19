import {ref, computed, reactive} from 'vue'
import { defineStore } from 'pinia'



//把获取到的用户信息存储为全局变量，需要用的时候就很方便
export const useStore = defineStore('store', () => {
  // 从 localStorage 初始化用户状态
  const initialUser = JSON.parse(localStorage.getItem('user') || 'null')
  
  const auth = reactive({
    user: initialUser,
    token: localStorage.getItem('token') || null
  })

  // 登录方法
  const login = (userData, token) => {
    auth.user = userData
    auth.token = token
    localStorage.setItem('user', JSON.stringify(userData))
    localStorage.setItem('token', token)
  }

  // 注销方法
  const logout = () => {
    auth.user = null
    auth.token = null
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  // 检查是否已登录（计算属性）
  const isAuthenticated = computed(() => !!auth.token)

  return { 
    auth,
    login,
    logout,
    isAuthenticated
  }
})

