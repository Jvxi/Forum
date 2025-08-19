<template>
  <div class="welcome">
    <!-- 左侧壁纸 -->
    <div class="wallpaper">
      <div class="wallpaper-gradient"></div>
      <div class="bg-blob b1"></div>
      <div class="bg-blob b2"></div>
      <div class="bg-blob b3"></div>
      <!-- 浮动粒子 -->
      <div class="floating-particles">
        <div class="particle p1"></div>
        <div class="particle p2"></div>
        <div class="particle p3"></div>
        <div class="particle p4"></div>
        <div class="particle p5"></div>
        <div class="particle p6"></div>
        <div class="particle p7"></div>
        <div class="particle p8"></div>
      </div>
      <!-- 几何装饰 -->
      <div class="geometric-shapes">
        <div class="shape shape1"></div>
        <div class="shape shape2"></div>
        <div class="shape shape3"></div>
        <div class="shape shape4"></div>
      </div>
      <!-- 光晕效果 -->
      <div class="light-rings">
        <div class="ring r1"></div>
        <div class="ring r2"></div>
        <div class="ring r3"></div>
      </div>
      <div class="wallpaper-pattern"></div>
    </div>

    <!-- 书本式双页容器 -->
    <div class="center-card animate-fade-in">
      <div class="auth-book">
        <!-- 左页：登录/忘记密码（根据路由切换） -->
        <div class="page left animate-slide-in-left" :class="{ 'page-left-login': isLogin }">
          <el-button v-if="showBack" class="back-button" text circle @click="goLogin">
            <el-icon class="back-icon"><ArrowLeft /></el-icon>
          </el-button>
          <component :is="leftComponent" />
        </div>
        <!-- 右页：注册 -->
        <div class="page right animate-slide-in-right">
          <RegisterPage />
        </div>

        <!-- 介绍遮罩页：左右滑动覆盖对应页面 -->
        <div class="intro-cover animate-fade-in-delayed" :class="coverSide">
          <div class="intro-inner">
            <div class="welcome-header animate-text-slide-up">
              <h1 class="welcome-title">欢迎来到校园吧</h1>
              <p class="welcome-subtitle">发现与分享校园生活</p>
            </div>
            <div class="features-grid animate-stagger-in">
              <div class="feature-card animate-card-in" style="animation-delay: 0.1s;">
                <div class="feature-icon">💬</div>
                <h3>动态交流</h3>
                <p>发布动态、交流想法，构建有温度的校园圈子</p>
              </div>
              
              <div class="feature-card animate-card-in" style="animation-delay: 0.2s;">
                <div class="feature-icon">❤️</div>
                <h3>表白墙</h3>
                <p>记录心动瞬间，让美好不被错过</p>
              </div>
              
              <div class="feature-card animate-card-in" style="animation-delay: 0.3s;">
                <div class="feature-icon">🏷️</div>
                <h3>话题标签</h3>
                <p>快速找到兴趣同好，参与热门讨论</p>
              </div>
              
              <div class="feature-card animate-card-in" style="animation-delay: 0.4s;">
                <div class="feature-icon">🔒</div>
                <h3>隐私保护</h3>
                <p>邮箱验证注册、会话保护，使用更安心</p>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import {useRoute, useRouter} from 'vue-router'
import {onMounted, ref, watch, computed} from 'vue'
import { ArrowLeft } from '@element-plus/icons-vue'
import LoginPage from '@/views/welcome/LoginPage.vue'
import RegisterPage from '@/views/welcome/RegisterPage.vue'
import ForgetPawPage from '@/views/welcome/ForgetPawPage.vue'

const route = useRoute()
const router = useRouter()
const coverSide = ref('right')

const syncCoverWithRoute = (path) => {
  if (path === '/register') coverSide.value = 'left'
  else coverSide.value = 'right'
}

onMounted(() => syncCoverWithRoute(route.path))
watch(() => route.path, (p) => syncCoverWithRoute(p))

const goLogin = () => { coverSide.value = 'right'; if (route.path !== '/') router.push('/') }
const goRegister = () => { coverSide.value = 'left'; if (route.path !== '/register') router.push('/register') }

// 左页组件：登录 或 忘记密码
const leftComponent = computed(() => route.path === '/forget' ? ForgetPawPage : LoginPage)
const showBack = computed(() => route.path === '/forget')
const isLogin = computed(() => route.path === '/')
</script>

<style scoped>
.welcome{ position: relative; width: 100vw; height: 100vh; overflow: hidden; }
.welcome * { box-sizing: border-box; }
.wallpaper{ position: absolute; inset: 0; }
.wallpaper-gradient{ position: absolute; inset: 0; background: 
  linear-gradient(180deg, #c7d2fe 0%, #e0f2fe 35%, #fde68a 65%, #fbcfe8 100%),
  radial-gradient(circle at 20% 80%, rgba(120,119,198,0.3) 0%, transparent 50%),
  radial-gradient(circle at 80% 20%, rgba(255,119,198,0.3) 0%, transparent 50%),
  radial-gradient(circle at 40% 40%, rgba(120,219,255,0.2) 0%, transparent 50%); 
  background-size: 400% 400%, 100% 100%, 100% 100%, 100% 100%; 
  animation: gradientShift 24s ease infinite; }
@keyframes gradientShift { 0% { background-position: 0% 0%; } 50% { background-position: 100% 100%; } 100% { background-position: 0% 0%; } }
/* 漂浮色块与纹理 */
.bg-blob{ position: absolute; border-radius: 50%; filter: blur(60px); opacity: .35; mix-blend-mode: screen; pointer-events: none; }
.bg-blob.b1{ width: 520px; height: 520px; left: -120px; top: -100px; background: radial-gradient(circle at 30% 30%, #a5b4ff 0%, #93c5fd 50%, transparent 70%); animation: blobFloatA 26s ease-in-out infinite; }
.bg-blob.b2{ width: 420px; height: 420px; right: -140px; bottom: -120px; background: radial-gradient(circle at 60% 40%, #fca5a5 0%, #fbcfe8 50%, transparent 70%); animation: blobFloatB 22s ease-in-out infinite; }
.bg-blob.b3{ width: 380px; height: 380px; left: 40%; top: 55%; transform: translateX(-50%); background: radial-gradient(circle at 50% 50%, #fde68a 0%, #fef3c7 45%, transparent 70%); animation: blobFloatC 28s ease-in-out infinite; }
@keyframes blobFloatA{ 0% { transform: translate(0,0); } 50% { transform: translate(40px, 30px); } 100% { transform: translate(0,0); } }
@keyframes blobFloatB{ 0% { transform: translate(0,0); } 50% { transform: translate(-50px, -20px); } 100% { transform: translate(0,0); } }
@keyframes blobFloatC{ 0% { transform: translate(-50%, 0) scale(1); } 50% { transform: translate(calc(-50% + 20px), -20px) scale(1.05); } 100% { transform: translate(-50%, 0) scale(1); } }
.wallpaper-pattern{ position: absolute; inset: 0; pointer-events: none; opacity: .22; background:
  radial-gradient(#ffffff22 1px, transparent 1px) 0 0/24px 24px,
  radial-gradient(#ffffff18 1px, transparent 1px) 12px 12px/24px 24px; }

/* 浮动粒子 */
.floating-particles{ position: absolute; inset: 0; pointer-events: none; }
.particle{ position: absolute; width: 6px; height: 6px; background: rgba(255,255,255,.9); border-radius: 50%; box-shadow: 0 0 12px rgba(255,255,255,.7); }
.particle.p1{ left: 15%; top: 20%; animation: float1 8s ease-in-out infinite; }
.particle.p2{ left: 85%; top: 30%; animation: float2 12s ease-in-out infinite; }
.particle.p3{ left: 25%; top: 70%; animation: float3 10s ease-in-out infinite; }
.particle.p4{ left: 75%; top: 80%; animation: float4 14s ease-in-out infinite; }
.particle.p5{ left: 50%; top: 15%; animation: float5 9s ease-in-out infinite; }
.particle.p6{ left: 10%; top: 60%; animation: float6 11s ease-in-out infinite; }
.particle.p7{ left: 90%; top: 50%; animation: float7 13s ease-in-out infinite; }
.particle.p8{ left: 40%; top: 85%; animation: float8 7s ease-in-out infinite; }
@keyframes float1{ 0%,100% { transform: translate(0,0) scale(1); opacity: .9; } 50% { transform: translate(30px,-20px) scale(1.2); opacity: 1; } }
@keyframes float2{ 0%,100% { transform: translate(0,0) scale(1); opacity: .9; } 50% { transform: translate(-25px,15px) scale(1.1); opacity: 1; } }
@keyframes float3{ 0%,100% { transform: translate(0,0) scale(1); opacity: .9; } 50% { transform: translate(20px,-10px) scale(1.3); opacity: 1; } }
@keyframes float4{ 0%,100% { transform: translate(0,0) scale(1); opacity: .9; } 50% { transform: translate(-15px,-25px) scale(1.1); opacity: 1; } }
@keyframes float5{ 0%,100% { transform: translate(0,0) scale(1); opacity: .9; } 50% { transform: translate(40px,10px) scale(1.2); opacity: 1; } }
@keyframes float6{ 0%,100% { transform: translate(0,0) scale(1); opacity: .9; } 50% { transform: translate(25px,20px) scale(1.1); opacity: 1; } }
@keyframes float7{ 0%,100% { transform: translate(0,0) scale(1); opacity: .9; } 50% { transform: translate(-30px,-15px) scale(1.3); opacity: 1; } }
@keyframes float8{ 0%,100% { transform: translate(0,0) scale(1); opacity: .9; } 50% { transform: translate(-20px,25px) scale(1.2); opacity: 1; } }

/* 几何装饰形状 */
.geometric-shapes{ position: absolute; inset: 0; pointer-events: none; }
.shape{ position: absolute; opacity: .4; border: 3px solid rgba(255,255,255,.6); background: rgba(255,255,255,.1); }
.shape1{ width: 80px; height: 80px; left: 8%; top: 25%; border-radius: 50%; animation: rotate1 20s linear infinite; }
.shape2{ width: 60px; height: 60px; left: 82%; top: 65%; transform: rotate(45deg); animation: rotate2 25s linear infinite reverse; }
.shape3{ width: 40px; height: 40px; left: 70%; top: 20%; border-radius: 8px; animation: rotate3 18s linear infinite; }
.shape4{ width: 100px; height: 50px; left: 15%; top: 75%; border-radius: 25px; animation: rotate4 22s linear infinite reverse; }
@keyframes rotate1{ from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
@keyframes rotate2{ from { transform: rotate(45deg); } to { transform: rotate(-315deg); } }
@keyframes rotate3{ from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
@keyframes rotate4{ from { transform: rotate(0deg); } to { transform: rotate(-360deg); } }

/* 光晕环 */
.light-rings{ position: absolute; inset: 0; pointer-events: none; }
.ring{ position: absolute; border: 2px solid rgba(255,255,255,.4); border-radius: 50%; animation: pulse 6s ease-in-out infinite; }
.ring.r1{ width: 200px; height: 200px; left: -50px; top: -50px; animation-delay: 0s; }
.ring.r2{ width: 300px; height: 300px; right: -100px; bottom: -100px; animation-delay: 2s; }
.ring.r3{ width: 150px; height: 150px; left: 60%; top: 40%; animation-delay: 4s; }
@keyframes pulse{ 0%,100% { transform: scale(1); opacity: .4; } 50% { transform: scale(1.1); opacity: .6; } }

/* 居中书本 */
.center-card{ position: absolute; inset: 0; display: grid; place-items: center; z-index: 2; padding: 16px; animation: fadeInUp 1s ease-out; }
.auth-book{ --page-w: 500px; --page-h: 560px; --gap: 0px; position: relative; width: calc(var(--page-w) * 2 + var(--gap)); height: var(--page-h); max-width: 96vw; display: grid; grid-template-columns: 1fr 1fr; gap: var(--gap); align-items: stretch; overflow: hidden; box-sizing: border-box; margin: 0 auto; }
.page{ width: var(--page-w); height: var(--page-h); overflow: hidden; background: rgba(255,255,255,.92); backdrop-filter: blur(10px); border: 1px solid rgba(99,102,241,.15); border-radius: 16px; box-shadow: 0 12px 36px rgba(0,0,0,.06); padding: 12px 14px; display: flex; position: relative; z-index: 1; box-sizing: border-box; justify-content: center; align-items: flex-start; transition: all 0.3s ease; }
.page:hover{ transform: translateY(-2px); box-shadow: 0 16px 48px rgba(0,0,0,.1); }
.page :deep(.card){ height: 100%; width: 100%; background: transparent; border: 0; box-shadow: none; padding: 0; display: flex; flex-direction: column; box-sizing: border-box; overflow-y: auto; }
.back-button{ position: absolute; top: 8px; left: 8px; z-index: 2; background: transparent !important; transition: all 0.3s ease; }
.back-button:hover{ transform: scale(1.1) translateX(-2px); }
.back-button:hover, .back-button:focus{ background: transparent !important; }
.page-left-login{ padding-top: 60px; }
.back-icon{ font-size: 22px; transition: all 0.3s ease; }
/* optional enlarge clickable area */
.back-button :deep(.el-icon){ width: 28px; height: 28px; display: inline-flex; align-items: center; justify-content: center; }

/* 介绍遮罩页（默认覆盖右页，显示左侧登录/忘记密码） */
.intro-cover{ position: absolute; top: 0; left: 0; width: var(--page-w); height: var(--page-h); pointer-events: auto; transition: transform .45s cubic-bezier(0.4, 0, 0.2, 1); z-index: 2; box-sizing: border-box; background: linear-gradient(135deg, #f8f9ff 0%, #eef2ff 100%); backdrop-filter: blur(10px); border: 1px solid rgba(99,102,241,0.2); border-radius: 16px; box-shadow: 0 15px 40px rgba(99,102,241,0.15); overflow: hidden; }
.intro-cover::after{ content: ""; position: absolute; inset: 0; border-radius: inherit; pointer-events: none; background:
  radial-gradient(#a5b4fc22 1px, transparent 1px) 0 0/16px 16px,
  radial-gradient(#93c5fd1c 1px, transparent 1px) 8px 8px/16px 16px; opacity: .4; mask-image: linear-gradient(to bottom, rgba(0,0,0,0) 0%, rgba(0,0,0,.35) 35%, rgba(0,0,0,0) 100%); }
.intro-cover.right{ transform: translateX(calc(var(--page-w) + var(--gap))); }
.intro-cover.left{ transform: translateX(0); }

/* 遮罩内容样式 */
.intro-inner{ height: 100%; display: flex; flex-direction: column; justify-content: center; align-items: center; background: transparent; padding: 35px 30px 52px; text-align: center; }
.welcome-title { font-size: 28px; font-weight: 700; color: #1e293b; margin: 30px 0 0; letter-spacing: 0.5px; animation: textGlow 3s ease-in-out infinite alternate; }
.welcome-subtitle { font-size: 16px; color: #64748b; margin: 4px 0 12px; animation: textFade 4s ease-in-out infinite; }

/* 特性网格布局 */
.features-grid { display: grid; grid-template-columns: repeat(2, 1fr); grid-template-rows: repeat(2, minmax(150px, 1fr)); gap: 18px; margin: 4px 0 80px; }
.feature-card { background: rgba(255, 255, 255, 0.9); border-radius: 14px; padding: 22px 18px; box-shadow: 0 6px 18px rgba(99, 102, 241, 0.08); border: 1px solid rgba(199, 210, 254, 0.6); transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1); min-height: 160px; display: flex; flex-direction: column; align-items: center; text-align: center; position: relative; overflow: hidden; }
.feature-card::before{ content: ''; position: absolute; top: 0; left: -100%; width: 100%; height: 100%; background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent); transition: left 0.5s; }
.feature-card:hover::before{ left: 100%; }
.feature-card:hover { transform: translateY(-8px) scale(1.02); box-shadow: 0 20px 40px rgba(99, 102, 241, 0.2); }
.feature-icon { width: 54px; height: 54px; border-radius: 14px; display: grid; place-items: center; background: linear-gradient(135deg,#eef2ff,#f8fafc); border: 1px solid #e5e7eb; box-shadow: 0 4px 12px rgba(0,0,0,.06); font-size: 24px; margin-bottom: 12px; transition: all 0.3s ease; animation: iconFloat 4s ease-in-out infinite; }
.feature-card:hover .feature-icon{ transform: scale(1.1) rotate(5deg); box-shadow: 0 8px 20px rgba(99,102,241,0.2); }
.feature-card h3 { font-size: 16px; font-weight: 600; color: #4338ca; margin: 0 0 10px; transition: all 0.3s ease; }
.feature-card:hover h3{ color: #3730a3; transform: scale(1.05); }
.feature-card p { font-size: 13px; color: #475569; line-height: 1.5; margin: 0; transition: all 0.3s ease; }
.feature-card:hover p{ color: #374151; }

/* 页面进入动画 */
@keyframes fadeInUp{ from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
@keyframes slideInLeft{ from { opacity: 0; transform: translateX(-50px); } to { opacity: 1; transform: translateX(0); } }
@keyframes slideInRight{ from { opacity: 0; transform: translateX(50px); } to { opacity: 1; transform: translateX(0); } }
@keyframes fadeInDelayed{ from { opacity: 0; } to { opacity: 1; } }
@keyframes textSlideUp{ from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes staggerIn{ from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
@keyframes cardIn{ from { opacity: 0; transform: translateY(40px) scale(0.9); } to { opacity: 1; transform: translateY(0) scale(1); } }

/* 文字动画 */
@keyframes textGlow{ from { text-shadow: 0 0 5px rgba(99,102,241,0.3); } to { text-shadow: 0 0 20px rgba(99,102,241,0.6); } }
@keyframes textFade{ 0%,100% { opacity: 0.7; } 50% { opacity: 1; } }

/* 图标浮动动画 */
@keyframes iconFloat{ 0%,100% { transform: translateY(0); } 50% { transform: translateY(-3px); } }

/* 应用动画类 */
.animate-fade-in{ animation: fadeInUp 1s ease-out; }
.animate-slide-in-left{ animation: slideInLeft 0.8s ease-out; }
.animate-slide-in-right{ animation: slideInRight 0.8s ease-out; }
.animate-fade-in-delayed{ animation: fadeInDelayed 1.2s ease-out; }
.animate-text-slide-up{ animation: textSlideUp 1s ease-out 0.3s both; }
.animate-stagger-in{ animation: staggerIn 1s ease-out 0.5s both; }
.animate-card-in{ animation: cardIn 0.8s ease-out both; }

/* 响应式优化：窄屏下略微缩小页面宽高 */
@media (max-width: 1180px){ .auth-book{ --page-w: 460px; --page-h: 560px; } }
@media (max-width: 1024px){ .auth-book{ --page-w: 420px; --page-h: 560px; } }

/* 移动端动画优化 */
@media (max-width: 768px){ 
  .feature-card:hover{ transform: translateY(-4px) scale(1.01); }
  .page:hover{ transform: translateY(-1px); }
  .back-button:hover{ transform: scale(1.05) translateX(-1px); }
}
</style>