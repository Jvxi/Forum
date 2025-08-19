import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import 'element-plus/dist/index.css'
import axios from "axios";
import '@/assets/animations.css'
import scrollReveal from './directives/scrollReveal.js'
import mouseTrail from './directives/mouseTrail.js'

const app = createApp(App)
//配置服务器地址（后端）前缀
axios.defaults.baseURL='http://localhost:8080'
// http://8.148.31.237:6394
// http://localhost:8080

app.use(createPinia())
app.use(router)
app.use(mouseTrail)

// 全局指令注册
app.directive('reveal', scrollReveal)

app.mount('#app')
