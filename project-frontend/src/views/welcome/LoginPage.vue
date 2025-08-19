<template>
  <div class="card">
    <div class="brand">
      <div class="brand-logo">✏️</div>
      <div class="brand-text">
        <div class="title">校园论坛</div>
        <div class="subtitle">欢迎回来，开始今天的交流吧</div>
      </div>
    </div>

    <div class="form-area">
      <el-input v-model="form.username" type="text" placeholder="用户名/邮箱" size="large">
        <template #prefix>
          <el-icon><User /></el-icon>
        </template>
      </el-input>
      <el-input v-model="form.password" type="password" placeholder="密码" size="large" class="mt16">
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>

      <div class="actions">
        <el-checkbox v-model="form.remember" label="记住我" size="large" />
        <el-link @click="router.push('/forget')" type="primary">忘记密码？</el-link>
      </div>

      <el-button class="btn-primary" @click="login()" type="primary" size="large">登录</el-button>

      <div class="divider">
        <span>没有账号</span>
      </div>
      <el-button class="btn-secondary" @click="router.push('/register')" type="success" size="large" plain>注册账号</el-button>
    </div>
  </div>
</template>

<script setup>
  import {User,Lock} from '@element-plus/icons-vue'
  import {reactive} from "vue";
  import {ElMessage} from "element-plus";
  import {get, post} from '@/net'
  import router from "@/router";
  import {useStore} from "@/stores";

  const store = useStore()
  const form = reactive({
    userId: '',
    username: '',
    password: '',
    remember: false
  })

  const login = () =>{
    if(!form.username||!form.password){
      ElMessage.warning("请填写用户名和密码")
    }else{
      post('/api/auth/login',{
        userId: form.userId,
        username: form.username,
        password: form.password,
        remember: form.remember
      },(message) =>{
        ElMessage.success(message)
        get('/api/user/me',(message)=>{
          store.auth.user = message
          localStorage.setItem("user", JSON.stringify(message))
          router.push('/index')
        },()=>{
          store.auth.user = null
        })
      })
    }
  }
</script>

<style scoped>
/* 自适应卡片（由外层 WelcomeViews 控制居中与最大宽度） */
.card{
  width: 100%;
  background: rgba(255,255,255,.75);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(99,102,241,.15);
  border-radius: 16px;
  box-shadow: 0 12px 36px rgba(0,0,0,.06);
  padding: 28px 28px 26px;
}
.brand{ display: flex; align-items: center; gap: 14px; margin-bottom: 10px; }
.brand-logo{ font-size: 36px; line-height: 1; }
.brand-text .title{ font-size: 22px; font-weight: 800; letter-spacing: .4px; }
.brand-text .subtitle{ font-size: 13px; color: #6b7280; margin-top: 2px; }

.form-area{ margin-top: 8px; }
.mt16{ margin-top: 16px; }
.actions{ display: flex; justify-content: space-between; align-items: center; margin: 12px 2px 6px; }
.btn-primary{ width: 100%; margin-top: 10px; height: 42px; font-weight: 600; letter-spacing: .5px; }
.btn-secondary{ width: 100%; height: 42px; font-weight: 600; letter-spacing: .5px; }
.divider{ display: flex; align-items: center; gap: 12px; color: #9ca3af; font-size: 12px; margin: 14px 0; }
.divider::before, .divider::after{ content: ""; flex: 1; height: 1px; background: linear-gradient(to right, transparent, #e5e7eb, transparent); }
</style>