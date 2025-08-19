<template>
  <el-form
      ref="form"
      :rules="rules"
      label-position="top"
      label-width="100px"
      :model="infoFrom"
      style="max-width: 460px"
  >
    <!-- 头像上传 -->
    <el-form-item label="头像">
      <div style="display: flex; align-items: center; gap: 20px">
        <el-avatar :size="80" :src="infoFrom.avatar || defaultAvatar()" />
        <el-upload
            :show-file-list="false"
            :on-error="handleAvatarError"
            :before-upload="beforeAvatarUpload"
            :http-request="customUpload"
        >
          <el-button type="primary" size="small">更换头像</el-button>
          <template #tip>
            <div class="el-upload__tip" style="margin-top: 10px">
              支持 JPG/PNG 格式，大小不超过 5MB
            </div>
          </template>
        </el-upload>
      </div>
    </el-form-item>

    <!-- 用户名 -->
    <el-form-item prop="username" label="用户名">
      <el-input :maxlength="8" v-model="infoFrom.username" />
    </el-form-item>

    <!-- 性别 -->
    <el-form-item label="性别">
      <el-radio-group v-model="infoFrom.sex" class="ml-4">
        <el-radio value="M" size="large">男</el-radio>
        <el-radio value="F" size="large">女</el-radio>
        <el-radio value="U" size="large">未知</el-radio>
      </el-radio-group>
    </el-form-item>

    <!-- 手机号 -->
    <el-form-item prop="phone" label="手机号">
      <el-input :maxlength="11" v-model="infoFrom.phone" />
    </el-form-item>
    <el-form-item label="QQ" prop="qq">
      <el-input :maxlength="11" v-model="infoFrom.qq" />
    </el-form-item>
    <el-form-item label="微信" prop="wx">
      <el-input :maxlength="30" v-model="infoFrom.wx" />
    </el-form-item>

    <!-- 个人简介 -->
    <el-form-item label="个人简介" prop="desc">
      <el-input :maxlength="500" type="textarea" v-model="infoFrom.desc" :rows="6"/>
    </el-form-item>
    <el-button type="success" :icon="Select" @click="save">保存个人信息设置</el-button>
  </el-form>
</template>

<script setup>
import {useStore} from "@/stores";
import {Select} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {post, get} from "@/net";
import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router";


const store = useStore()//用户信息存储在这个全局变量中
const defaultAvatar = () => {
  const sex = store.auth.user?.sex
  switch (sex) {
    case 'M': return '/images/男.png'
    case 'F': return '/images/女.png'
    default: return '/images/未.png'
  }
}
const form = ref()
//校验“用户名输入框”不能有特殊字符
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'));
  } else {
    //用正则表达式判断更方便。（以下正则表达式含义：包含中文英文的用户名，不能有特殊字符）
    if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
      callback(new Error('用户名不能包含特殊字符，只能是中文/英文'));
    }else{
      callback();
    }
  }
};

const validatePhone = (rule, value, callback) => {
  if(value !== '' && !/^1[3-9]\d{9}$/.test(value)){
    callback(new Error('手机号格式错误'));
  } else {
    callback();
  }
};

const validateBlogURL = (rule, value, callback) => {
  if(value !== '' && !/^https?:\/\/((www.)?[\w-]+.[\w-]+.?[\w-])?\/?([\w-])\/?([\w-]*)\/?$/.test(value)){
    callback(new Error('博客地址格式错误'));
  } else {
    callback();
  }
};
//制定校验规则
const rules ={
  username: [
    { validator: validateUsername, trigger: ['blur','change'] },
    { min: 2, max: 8, message: '用户名长度必须在2~8个字符之间', trigger: ['blur','change'] }
  ],
  phone: [
    { validator: validatePhone, trigger: ['blur','change'] },
    { min: 11, max: 11, message: '手机号只能是11位', trigger: ['blur','change'] }
  ],
  qq: [
    { min: 8, max: 11, message: 'qq号只能是8~11位', trigger: ['blur','change'] }
  ],
  wx: [
    { min: 5, max: 30, message: '微信号只能是5~30位', trigger: ['blur','change'] }
  ],
  blog: [
    { validator: validateBlogURL, trigger: ['blur','change'] },
    { max: 50, message: '博客地址过长', trigger: ['blur','change'] }
  ],
  desc: [
    { max: 500, message: '个人简介不能超过500字', trigger: ['blur','change'] }
  ]
}
/*"个人信息设置"的各个参数*/
const infoFrom =reactive({
  username: null,
  desc: '',
  qq: '',
  phone: '',
  wx: '',
  blog: '',
  sex: 'U',
  avatar: store.auth.user.avatar || ''
})

// 自定义上传方法
const customUpload = async (options) => {
  const formData = new FormData();
  
  // 1. 添加文件
  formData.append('file', options.file);
  
  // 2. 添加用户信息（JSON格式）
  const userInfo = JSON.stringify(store.auth.user);
  formData.append('userInfo', userInfo);
  
  try {
    const response = await axios.post('/api/user/upload-avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    
    if (response.data && response.data.success) {
      const avatarUrl = response.data.message;
      infoFrom.avatar = avatarUrl;
      store.auth.user.avatar = avatarUrl;
      localStorage.setItem('user', JSON.stringify(store.auth.user));
      ElMessage.success('头像上传成功');
      // 通知 el-upload 成功结束
      options.onSuccess(response.data);
    } else {
      throw new Error(response.data?.message || "头像上传失败");
    }
  } catch (error) {
    ElMessage.error('头像上传失败');
    // 通知 el-upload 失败结束
    options.onError(error);
  }
};
 
// 上传错误统一处理（避免组件默认重复提示）
const handleAvatarError = () => {
  // 已在 customUpload 中提示，这里不再重复提示
};

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith("image/");
  const isLt5M = file.size / 1024 / 1024 < 5;
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

  if (!isImage) {
    ElMessage.error("只能上传图片文件!");
    return false;
  }

  if (!validTypes.includes(file.type)) {
    ElMessage.error("只支持 JPG/PNG/GIF/WEBP 格式!");
    return false;
  }

  if (!isLt5M) {
    ElMessage.error("图片大小不能超过 5MB!");
    return false;
  }

  return true;
};

//点击保存按钮，触发这个保存函数
const save = ()=>{
  form.value.validate((isValid)=>{
    if(isValid){
      post('/api/user/save-info', infoFrom, ()=>{
        // 更新全局用户信息并持久化
        store.auth.user = { ...store.auth.user, ...infoFrom }
        localStorage.setItem('user', JSON.stringify(store.auth.user))
        //用户名修改成功后，重新获取用户信息，使得新用户名马上能呈现在页面上
        get('/api/user/me',(message)=>{
          // 获取当前会话用户，合并以保留 infoFrom.desc 等扩展字段
          store.auth.user = { ...store.auth.user, ...message }
          localStorage.setItem("user", JSON.stringify(store.auth.user))//存在localStorage永久存储
        },()=>{
          store.auth.user = null
        })
        ElMessage.success("保存成功！")
      },'json')
    }else{
      ElMessage.warning('表单内容有误，请重新检查表单内容')
    }
  })
}

onMounted(()=>{
  if(infoFrom.username==null){
    get('/api/user/info',(message)=>{
      infoFrom.username=message.username
      infoFrom.desc=message.desc
      infoFrom.qq=message.qq
      infoFrom.wx=message.wx
      infoFrom.sex=message.sex ? message.sex:'U'
      infoFrom.phone=message.phone
      // 若全局用户缺少性别，同步一次并持久化
      if (store.auth.user && !store.auth.user.sex) {
        store.auth.user.sex = infoFrom.sex
        localStorage.setItem('user', JSON.stringify(store.auth.user))
      }
      // 同步简介到全局用户，确保右侧卡片即时展示
      store.auth.user = { ...store.auth.user, desc: infoFrom.desc }
      localStorage.setItem('user', JSON.stringify(store.auth.user))
    })
  }
})
</script>

<style scoped>

</style>