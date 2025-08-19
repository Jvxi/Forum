<template>
  <div>
    <div class="steps">
      <!--搞个步骤条，active绑定进行到第几步-->
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮箱" />
        <el-step title="重新设置密码" />
      </el-steps>
    </div>
    <!--active为0的时候显示验证电子邮箱的界面-->
    <transition name="el-fade-in-linear" mode="out-in">
      <div class="forget-container" v-if="active === 0">
        <div class="header">
          <div class="title">重置密码</div>
          <div class="sub">请输入需要重置密码的电子邮箱地址</div>
        </div>
        <div>
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
            <el-form-item prop="email">
              <el-input v-model="form.email" type="email" class="mt12" placeholder="电子邮箱地址">
                <!--给输入框引入一个图标-->
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="code">
              <div class="mt12">
                <el-row :gutter="10" style="width: 100%">
                  <el-col :span="18">
                    <el-input v-model="form.code" type="text" placeholder="请输入验证码">
                      <!--给输入框引入一个图标-->
                      <template #prefix>
                        <el-icon><EditPen /></el-icon>
                      </template>
                    </el-input>
                  </el-col>
                  <el-col :span="6">
                    <el-button type="success" :maxlength="6" @click="validateEmail"
                              :disabled="!isEmailValid || coldTime > 0" >
                      {{coldTime > 0 ? '请稍后' + coldTime + '秒' : '获取验证码'}}</el-button>
                  </el-col>
                </el-row>
              </div>
            </el-form-item>
          </el-form>
        </div>
        <div class="actions">
          <el-button class="primary-btn" type="danger" plain @click="startReset">开始重置密码</el-button>
        </div>
      </div>
    </transition>
    <!--active为1的时候，显示重置密码界面-->
    <transition name="el-fade-in-linear" mode="out-in">
      <div class="forget-container" v-if="active === 1">
        <div class="header">
          <div class="title">重置密码</div>
          <div class="sub">请填写您的新密码</div>
        </div>
        <div class="mt12">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
            <el-form-item prop="password">
              <el-input v-model="form.password" :maxlength="16" type="password" class="mt12" placeholder="新密码">
                <!--给输入框引入一个图标-->
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password_repeat">
              <el-input v-model="form.password_repeat" :maxlength="16" type="password" class="mt12" placeholder="再次输入新密码">
                <!--给输入框引入一个图标-->
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <div class="actions">
          <el-button class="primary-btn" type="danger" plain @click="doReset">立即重置密码</el-button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import {reactive, ref} from "vue";
import {Lock, Message, EditPen} from "@element-plus/icons-vue";
import {post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const form = reactive({
  email: '',
  code: '',
  password: '',
  password_repeat: ''
})

//步骤条进行到第几步
const active = ref(0)

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

const rules = {
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

//定义对整个表单进行响应的变量
const formRef = ref()
//判断邮箱地址是否有效（默认无效），有效才能激发“获取验证码”按钮
const isEmailValid = ref(false)
//发送验证码的冷却时间
const coldTime = ref(0)

const onValidate= (prop, isValid) =>{
  if(prop === 'email') isEmailValid.value = isValid
}

const validateEmail = ()=>{
  coldTime.value = 60
  post('/api/auth/valid-reset-email',{
    email: form.email
  },(message)=>{
    ElMessage.success(message)
    setInterval(()=>coldTime.value--, 1000)
  },(message)=>{
    ElMessage.warning(message)
    coldTime.value = 0
  })
}

const startReset = ()=>{
  formRef.value.validate((isValid) =>{
    if(isValid){
      post("/api/auth/start-reset",{
        email: form.email,
        code: form.code
      },()=>{
        active.value++
      })
    }else{
      ElMessage.warning('请填写电子邮箱地址和验证码')
    }
  })
}

const doReset = ()=>{
  formRef.value.validate((isValid) =>{
    if(isValid){
      post("/api/auth/do-reset",{
        password: form.password,
      },(message)=>{
        ElMessage.success(message)
        router.push('/')
      })
    }else{
      ElMessage.warning('请填写新的密码并确认两次填写是否一致')
    }
  })
}
</script>

<style scoped>
.steps{ margin:60px 0 0; }
.forget-container{ text-align: center; margin: 0 auto; height: 100%; padding: 0 12px; max-width: 460px; }
.header{ margin-top: 60px; }
.header .title{ font-size: 22px; font-weight: 800; }
.header .sub{ font-size: 13px; color: grey; margin-top: 4px; }
.mt12{ margin-top: 18px; }
.actions{ margin-top: 60px; }
.primary-btn{ width: 260px; }
</style>