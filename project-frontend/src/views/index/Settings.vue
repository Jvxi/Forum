<template>
  <div style="display: flex;padding: 20px 30px" v-reveal>
    <!--左侧标签页展示设置细节-->
    <div style="flex: 1">
      <el-tabs v-model="activeName">
        <el-tab-pane label="个人信息设置" name="info">
          <info-settings/>
        </el-tab-pane>
        <el-tab-pane label="账号安全设置" name="security">
          <security-settings/>
        </el-tab-pane>
        <el-tab-pane label="隐私设置" name="privacy">
          <privacy-settings/>
        </el-tab-pane>
      </el-tabs>
    </div>
    <!--右侧用一个类似卡片的形式显示一些个人信息-->
    <div style="width: 300px">
      <div class="user-card" v-reveal>
        <div class="card-header-row">
          <div class="card-username">{{ store.auth.user.username || '未命名' }}</div>
          <el-avatar class="card-avatar" :size="70" :src="store.auth.user.avatar || defaultAvatar()"/>
        </div>
        <div class="card-body">
          <div v-if="store.auth.user.desc" style="white-space: pre-line">
            {{ store.auth.user.desc || '暂无简介' }}
          </div>
          <div v-else style="color: #888; font-style: italic">
            暂无个人简介
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script setup>
import { ref } from "vue";
import {useStore} from "@/stores";
import InfoSettings from "@/components/settings/infoSettings.vue";
import SecuritySettings from "@/components/settings/securitySettings.vue";
import PrivacySettings from "@/components/settings/PrivacySettings.vue";



const store = useStore()//用户信息存储在这个全局变量中
const activeName =ref('info')/*默认在“个人信息设置”标签页*/
const defaultAvatar = () => {
  const sex = store.auth.user?.sex
  switch (sex) {
    case 'M': return '/images/男.png'
    case 'F': return '/images/女.png'
    default: return '/images/未.png'
  }
}


</script>

<style scoped>
/* 右侧个人明信片样式（美化 + 动画） */
.user-card{
  height: 300px;
  width: 260px;
  margin: 15px 0 0 30px;
  border-radius: 14px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  box-shadow: 0 10px 30px rgba(0,0,0,0.08);
  overflow: hidden;
  position: relative;
  transform: translateZ(0);
  animation: cardFadeIn 480ms cubic-bezier(0.4, 0, 0.2, 1);
}
/*右侧个人明信片样式用灰色线上下分隔*/
.user-card > div{
  border-bottom: solid 1px lightgrey;
}

/* 简介区域样式 */
.user-card > div:last-child {
  min-height: 120px;
  font-size: 14px;
  line-height: 1.6;
}

/* 卡片头像和用户名横排，用户名在左，头像在右 */
.card-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  border-bottom: solid 1px lightgrey;
}
.card-username {
  font-weight: 700;
  font-size: 40px;
  color: #1f2937;
  margin-right: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-body{
  padding: 16px 12px;
  margin-top: 30px; /* 与上方用户名/头像增加间隙 */
  font-size: 16px;
  min-height: 100px;
}

/* 头像互动效果 */
.card-avatar{
  border: 3px solid rgba(18,191,160,0.25);
  transition: transform 240ms cubic-bezier(0.4, 0, 0.2, 1),
              box-shadow 240ms cubic-bezier(0.4, 0, 0.2, 1);
}
.card-avatar:hover{
  transform: scale(1.05) rotate(2deg);
  box-shadow: 0 8px 24px rgba(18,191,160,0.25);
}

/* 卡片装饰动画 */
.user-card::before{
  content: '';
  position: absolute;
  top: -60%;
  left: -60%;
  width: 220%;
  height: 220%;
  background: radial-gradient(closest-side, rgba(34,211,169,0.08), transparent 60%);
  transform: translate3d(0,0,0);
  animation: glowPulse 4s ease-in-out infinite;
}
.user-card::after{ display: none; }

@keyframes cardFadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}
/* sheen 动画已取消 */
@keyframes glowPulse {
  0%,100% { opacity: .5; transform: translate3d(0,0,0) scale(1); }
  50%     { opacity: .9; transform: translate3d(0,-2px,0) scale(1.02); }
}
</style>