<template>
  <div class="card">
    <div class="brand">
      <div class="brand-logo">📝</div>
      <div class="brand-text">
        <div class="title">注册新用户</div>
        <div class="subtitle">欢迎注册，请在下方填写相关信息</div>
      </div>
    </div>

    <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
      <el-form-item prop="username">
        <el-input v-model="form.username" :maxlength="8" type="text" placeholder="用户名" size="large">
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item prop="password">
        <el-input v-model="form.password" :maxlength="16" type="password" placeholder="密码" size="large">
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item prop="password_repeat">
        <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="再次输入密码" size="large">
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item prop="email">
        <el-input v-model="form.email" type="email" placeholder="电子邮箱地址" size="large">
          <template #prefix>
            <el-icon><Message /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item prop="code" class="code-item">
        <el-row :gutter="10" style="width: 100%">
          <el-col :span="16">
            <el-input v-model="form.code" type="text" placeholder="请输入验证码" size="large">
              <template #prefix>
                <el-icon><EditPen /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="8">
            <el-button type="success" size="large" :maxlength="6" @click="validateEmail"
                       :disabled="!isEmailValid || coldTime > 0">
              {{coldTime > 0 ? '请稍后' + coldTime + '秒' : '获取验证码'}}
            </el-button>
          </el-col>
        </el-row>
      </el-form-item>

      <el-button class="btn-primary" type="warning" size="large" @click="register">注册</el-button>

      <div class="divider"><span>已有帐号？</span></div>
      <el-button class="btn-secondary" type="primary" size="large" plain @click="router.push('/')">立即登录</el-button>
    </el-form>
  </div>
</template>

<script setup>
import {User, Lock, Message, EditPen} from "@element-plus/icons-vue";
import router from "@/router";
import {reactive, ref, onUnmounted } from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net";

const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'));
  } else {
    if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
      callback(new Error('用户名不能包含特殊字符，只能是中文/英文'));
    }else{
      callback();
    }
  }
};

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

const rules ={
  username: [
    { validator: validateUsername, trigger: ['blur','change'] },
    { min: 2, max: 8, message: '用户名长度必须在2~8个字符之间', trigger: ['blur','change'] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度必须在6~16个字符之间', trigger: ['blur','change'] }
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur','change'] },
  ],
  email:[
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入合法的电子邮箱地址', trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' }
  ]
}

const formRef = ref()
const isEmailValid = ref(false)
const coldTime = ref(0)
let intervalId = null

const onValidate= (prop, isValid) =>{
  if(prop === 'email') isEmailValid.value = isValid
}

const register = ()=>{
  formRef.value.validate((isValid) =>{
    if(isValid){
      post("/api/auth/register",{
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.code
      },(message)=>{
        ElMessage.success(message)
        router.push('/')
      })
    }else{
      ElMessage.warning('请完整填写注册表单内容')
    }
  })
}

const validateEmail = ()=>{
  if(intervalId) { clearInterval(intervalId); intervalId = null }
  coldTime.value = 60;
  post("/api/auth/valid-register-email",{ email: form.email }, (message) => {
    ElMessage.success(message)
    intervalId = setInterval(() => { coldTime.value--; }, 1000);
  })
}

onUnmounted(() => { if (intervalId) clearInterval(intervalId) })
</script>

<style scoped>
.card{ width: 100%; background: rgba(255,255,255,.75); backdrop-filter: blur(10px); border: 1px solid rgba(99,102,241,.15); border-radius: 16px; box-shadow: 0 12px 36px rgba(0,0,0,.06); padding: 24px 24px 22px; }
.brand{ display: flex; align-items: center; gap: 14px; margin-bottom: 10px; }
.brand-logo{ font-size: 30px; line-height: 1; }
.brand-text .title{ font-size: 20px; font-weight: 800; letter-spacing: .4px; }
.brand-text .subtitle{ font-size: 12px; color: #6b7280; margin-top: 2px; }
.code-item{ margin-bottom: 8px; }
.btn-primary{ width: 100%; margin-top: 10px; height: 42px; font-weight: 600; letter-spacing: .5px; }
.btn-secondary{ width: 100%; height: 42px; font-weight: 600; letter-spacing: .5px; }
.divider{ display: flex; align-items: center; gap: 12px; color: #9ca3af; font-size: 12px; margin: 14px 0; justify-content: center; }
.divider::before, .divider::after{ content: ""; width: 0; height: 0; }
</style>