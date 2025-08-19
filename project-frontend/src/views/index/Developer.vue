<template>
  <div class="dev-page">
    <div class="dev-card">
      <div class="dev-left">
        <el-avatar :size="88" :src="avatar" class="dev-avatar" />
        <div class="dev-info">
          <h2 class="dev-name">开发者信息</h2>
          <p class="dev-desc">本项目基于 Vue 3 + Vite + Element Plus（前端）与 Spring Boot（后端）。</p>
          <ul class="dev-list">
            <li>
              <span class="label">作者</span>
              <span class="value">{{ author }}</span>
            </li>
            <li>
              <span class="label">仓库</span>
              <a class="link large" :href="repoUrl" target="_blank" rel="noopener noreferrer">{{ repoUrl }}</a>
            </li>
            <li>
              <span class="label">邮箱</span>
              <a class="link large" :href="`mailto:${email}`">{{ email }}</a>
            </li>
          </ul>
        </div>
      </div>

      <div class="dev-right" v-if="qrImage">
        <div class="qr-title">联系方式 / 公众号</div>
        <img :src="qrImage" alt="联系二维码" class="qr-img" />
        <div class="qr-tip">扫一扫获取更多信息</div>
      </div>
    </div>

    <!-- 合并介绍为一张卡片 -->
    <div class="intro-card">
      <h3 class="intro-title">项目介绍</h3>
      <p class="intro-text">
        <span class="highlight-main">校园论坛 + 表白墙</span>的轻量化社区项目，涵盖<span class="highlight-feature">发帖、评论、收藏</span>与<span class="highlight-feature">用户资料设置</span>，提供<span class="highlight-tech">头像上传与默认回退</span>、<span class="highlight-tech">登录会话与 记住我</span>等能力，并在前端统一<span class="highlight-ui">动画与交互风格</span>，带来更自然的使用体验。
      </p>

      <div class="intro-grid">
        <div>
          <h4 class="intro-subtitle">功能特性</h4>
          <ul class="intro-list">
            <li><span class="highlight-category">帖子</span>：<span class="highlight-action">列表/详情/创建/编辑/删除</span>、<span class="highlight-action">标签筛选</span>、<span class="highlight-action">图片上传</span>（预留）</li>
            <li><span class="highlight-category">评论</span>：<span class="highlight-action">创建</span>、<span class="highlight-action">按帖子查询</span>、<span class="highlight-action">删除</span>，展示<span class="highlight-ui">用户头像与昵称</span></li>
            <li><span class="highlight-category">用户</span>：<span class="highlight-action">资料编辑</span>、<span class="highlight-action">头像上传</span>、<span class="highlight-tech">默认头像回退</span>、<span class="highlight-action">隐私设置</span></li>
            <li><span class="highlight-category">安全</span>：<span class="highlight-action">登录/退出</span>、<span class="highlight-tech">记住我</span>、<span class="highlight-action">基础权限校验</span></li>
            <li><span class="highlight-category">体验</span>：<span class="highlight-ui">页面过渡</span>、<span class="highlight-ui">滚动显隐</span>、<span class="highlight-ui">统一悬浮点击反馈</span></li>
          </ul>
        </div>
        <div>
          <h4 class="intro-subtitle">技术栈</h4>
          <ul class="intro-list">
            <li><span class="highlight-category">前端</span>：<span class="highlight-tech">Vue 3</span> + <span class="highlight-tech">Vite</span> + <span class="highlight-tech">Element Plus</span> + <span class="highlight-tech">Pinia</span> + <span class="highlight-tech">Vue Router</span></li>
            <li><span class="highlight-category">后端</span>：<span class="highlight-tech">Spring Boot 3</span> + <span class="highlight-tech">Spring Security</span> + <span class="highlight-tech">MyBatis</span></li>
            <li><span class="highlight-category">数据库</span>：<span class="highlight-tech">MySQL 8</span>（脚本见 <span class="highlight-file">数据库/forum.sql</span>）</li>
            <li><span class="highlight-category">静态映射</span>：本地头像 → <span class="highlight-path">/uploads/avatars/**</span></li>
          </ul>
        </div>
      </div>

      <div class="intro-grid">
        <div>
          <h4 class="intro-subtitle">目录结构（摘）</h4>
          <pre class="code small">
project-frontend/
  src/views/index/       # 业务页面
  src/components/        # 设置页子组件
  src/net/               # Axios 封装
  src/stores/            # Pinia
project-backend/
  src/main/java/com/xiaoRed/
    controller/          # 控制器
    mapper/              # MyBatis Mapper
    service/impl/        # 业务实现
    config/              # 安全/静态映射配置
          </pre>
        </div>
        <div>
          <h4 class="intro-subtitle">快速开始</h4>
          <ol class="intro-steps">
            <li>导入数据库：执行 <code class="highlight-code">数据库/forum.sql</code></li>
            <li>启动后端：<code class="highlight-code">cd project-backend && mvn spring-boot:run</code></li>
            <li>启动前端：<code class="highlight-code">cd project-frontend && npm i && npm run dev</code></li>
            <li>开发代理：<code class="highlight-code">/uploads</code> → <code class="highlight-code">http://localhost:8080</code></li>
          </ol>
        </div>
      </div>

      <!-- 版本历史 / 更新日志（时间轴） -->
      <div class="intro-divider"></div>
      <h4 class="intro-subtitle">版本历史 / 更新日志</h4>
      <ul class="timeline">
        <li v-for="item in processedTimeline" :key="item.version" class="timeline-item">
          <div v-if="item.isLatest" class="timeline-chip latest">最新</div>
          <div class="timeline-date">
            <span class="version-badge">{{ item.version }}</span>
            <span class="date-separator">·</span>
            <span class="date-text">{{ item.date }}</span>
          </div>
          <div class="timeline-content" v-html="item.processedContent"></div>
        </li>
      </ul>

      <!-- 致谢 / 许可证 -->
      <div class="intro-divider"></div>
      <h4 class="intro-subtitle">致谢 / 许可证</h4>
      <ul class="intro-list">
        <li>感谢开源生态：Vue、Vite、Element Plus、Pinia、Spring Boot、MyBatis、MySQL</li>
        <li>本项目仅用于学习与示例，遵循 MIT License（可自由使用、修改、分发，需保留许可与版权声明）。</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const author = '开发者'
const avatar = '/images/author.png'
const repoUrl = 'https://github.com/Jvxi'
const email = '2806949963@qq.com'
// 请将二维码图片放置在 public/images/ 目录下
const qrImage = '/images/qrcode.png'

// Release dates from env (fallback to defaults)
const env = import.meta.env
const r = (k, d) => env[k] || d
const rel = {
  v19: r('VITE_RELEASE_V1_9','2025-08-20'),
  v18: r('VITE_RELEASE_V1_8','2025-08-17'),
  v17: r('VITE_RELEASE_V1_7','2025-08-05'),
  v16: r('VITE_RELEASE_V1_6','2025-07-20'),
  v15: r('VITE_RELEASE_V1_5','2025-07-02'),
  v14: r('VITE_RELEASE_V1_4','2025-06-15'),
  v13: r('VITE_RELEASE_V1_3','2025-05-28'),
  v12: r('VITE_RELEASE_V1_2','2025-05-12'),
  v11: r('VITE_RELEASE_V1_1','2025-04-25'),
  v10: r('VITE_RELEASE_V1_0','2025-04-13'),
}

// 自动解析高亮标签的函数
const parseHighlight = (text) => {
  if (!text) return text
  
  // 定义关键词映射
  const keywords = {
    add: ['新增', '完成', '实现', '添加', '创建', '构建', '搭建'],
    mod: ['修改', '修复', '优化', '调整', '升级', '完善', '改进', '更新', '重构']
  }
  
  let result = text
  
  // 为每个关键词类型添加高亮
  Object.entries(keywords).forEach(([type, words]) => {
    words.forEach(word => {
      try {
        // 转义特殊字符
        const escapedWord = word.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
        const regex = new RegExp(`(${escapedWord})`, 'g')
        result = result.replace(regex, `<span class="hl ${type}">$1</span>`)
      } catch (error) {
        console.error('Error in parseHighlight:', error)
      }
    })
  })
  
  return result
}

// 时间轴数据
const timelineData = [
  { version: 'v1.9', date: rel.v19, content: `
    <span class="hl add">新增</span>全局鼠标拖尾动画效果，跟随鼠标移动，悬停交互元素时增强显示
    <span class="hl add">新增</span>鼠标停止3秒后拖尾自动消失，重新移动时立即出现
    <span class="hl mod">修改</span>左侧菜单栏高亮逻辑，修复了页面切换时高亮状态不正确的问题
    <span class="hl mod">修改</span>使用路由名称匹配替代路径匹配，提高菜单高亮准确性
    <span class="hl mod">修改</span>优化页面切换动画，消除白屏闪烁问题
    <span class="hl mod">修改</span>简化页面过渡效果，提升用户体验流畅度
  `, isLatest: true },
  { version: 'v1.8', date: rel.v18, content: '新增"开发者信息"页面并接入侧边栏入口。' },
  { version: 'v1.7', date: rel.v17, content: '修改多处布局调整与细节优化。' },
  { version: 'v1.6', date: rel.v16, content: '新增帖子列表卡片显示用户头像；帖子评论显示用户头像。' },
  { version: 'v1.5', date: rel.v15, content: '修改整体布局与UI样式升级，更换颜色搭配。' },
  { version: 'v1.4', date: rel.v14, content: '新增用户上传头像功能。' },
  { version: 'v1.3', date: rel.v13, content: '新增帖子评论功能。' },
  { version: 'v1.2', date: rel.v12, content: '新增编辑帖子部分；修改修复个人设置中的若干问题。' },
  { version: 'v1.1', date: rel.v11, content: '新增表白墙部分与帖子管理。' },
  { version: 'v1.0', date: rel.v10, content: '初始版本完成帖子相关展示：帖子列表、添加帖子、帖子详情页。' }
]

// 计算属性：处理后的时间轴数据
const processedTimeline = computed(() => {
  return timelineData.map(item => ({
    ...item,
    processedContent: parseHighlight(item.content)
  }))
})
</script>

<style scoped>
.dev-page{ padding: 28px; display: grid; gap: 24px; }
.dev-card{
  max-width: 980px;
  margin: 20px auto 0;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 28px;
  align-items: start;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 28px 32px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.06);
  animation: cardIn .4s ease-out;
}
.dev-left{ display: grid; grid-template-columns: auto 1fr; gap: 16px; align-items: center; }
.dev-name{ margin: 0 0 8px 0; font-size: 22px; letter-spacing: .2px; }
.dev-desc{ margin: 6px 0; color: #475569; line-height: 1.7; }
.dev-list{ list-style: none; padding: 0; margin: 8px 0 0 0; display: grid; gap: 6px; }
.dev-list .label{ display: inline-block; width: 56px; color: #64748b; }
.dev-list .value, .link{ color: #334155; }
.link{ color: var(--brand-end); text-decoration: none; }
.link:hover{ text-decoration: underline; }
.dev-right{ text-align: center; }
.qr-title{ font-weight: 700; margin-bottom: 12px; font-size: 20px; }
.qr-img{ width: 280px; height: 280px; object-fit: cover; border-radius: 12px; border: 1px solid #e2e8f0; }
.qr-tip{ color: #64748b; font-size: 14px; margin-top: 10px; }
.link.large{ font-size: 15px; }

@keyframes cardIn{ from{ opacity:0; transform: translateY(6px);} to{ opacity:1; transform:none;} }

/* 响应式：窄屏下上下布局 */
@media (max-width: 820px){
  .dev-card{ grid-template-columns: 1fr; }
  .dev-right{ order: 2; }
  .dev-left{ order: 1; grid-template-columns: auto 1fr; }
  .qr-img{ width: 220px; height: 220px; }
}

/* 通用区块卡片 */
.section-card{
  max-width: 980px;
  margin: 0 auto;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-left: 4px solid var(--brand-end);
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.04);
  animation: cardIn .35s ease-out;
}
.section-card.grid{ display: grid; grid-template-columns: 1fr; gap: 16px; }
@media (min-width: 960px){ .section-card.grid{ grid-template-columns: 1fr 1fr; } }
.section-title{ margin: 0 0 10px 0; font-size: 18px; font-weight: 700; letter-spacing: .3px; }
.section-text{ margin: 6px 0; color: #475569; line-height: 1.75; }
.section-list{ margin: 8px 0 0 0; padding: 0; color: #334155; list-style: none; display: grid; gap: 6px; }
.section-list li{ position: relative; padding-left: 14px; line-height: 1.7; }
.section-list li::before{ content: ''; position: absolute; left: 0; top: .65em; width: 6px; height: 6px; border-radius: 50%; background: var(--brand-end); }
.code{
  margin: 8px 0 0 0; padding: 12px 14px; background: #f8fafc; border-radius: 10px;
  border: 1px solid #e2e8f0; color: #334155; overflow: auto; font-size: 13px; line-height: 1.6;
}
.intro-card{
  max-width: 980px; margin: 0 auto; background: #fff; border: 1px solid #e2e8f0; border-radius: 12px;
  padding: 22px 26px; box-shadow: 0 10px 30px rgba(0,0,0,0.04); animation: cardIn .35s ease-out;
}
.intro-title{ margin: 0 0 10px 0; font-size: 20px; font-weight: 800; letter-spacing: .3px; }
.intro-text{ margin: 6px 0 12px 0; color: #475569; line-height: 1.8; }
.intro-grid{ display: grid; grid-template-columns: 1fr; gap: 16px; margin-top: 4px; }
@media (min-width: 960px){ .intro-grid{ grid-template-columns: 1fr 1fr; } }
.intro-subtitle{ margin: 0 0 8px 0; font-size: 16px; font-weight: 700; }
.intro-list{ margin: 0; padding: 0; list-style: none; display: grid; gap: 6px; color: #334155; }
.intro-list li{ position: relative; padding-left: 14px; line-height: 1.7; }
.intro-list li::before{ content: ''; position: absolute; left: 0; top: .65em; width: 6px; height: 6px; border-radius: 50%; background: var(--brand-end); }
.intro-steps{ margin: 0; padding-left: 18px; color: #334155; display: grid; gap: 6px; }
.code.small{ font-size: 12px; }

.intro-divider{ height: 1px; background: #e2e8f0; margin: 14px 0; border-radius: 1px; }

/* 时间轴样式 */
.timeline{ list-style: none; padding: 0; margin: 8px 0 0 0; position: relative; }
.timeline::before{ content: ''; position: absolute; left: 12px; top: 0; bottom: 0; width: 2px; background: #e2e8f0; }
.timeline-item{ position: relative; padding-left: 36px; margin-bottom: 14px; }
.timeline-item::before{ content: ''; position: absolute; left: 6px; top: 4px; width: 12px; height: 12px; border-radius: 50%; background: var(--brand-end); box-shadow: 0 0 0 3px rgba(18,191,160,0.15); }
.timeline-date{ 
  font-weight: 900; 
  color: #1e293b; 
  margin-bottom: 4px; 
  font-size: 18px; 
  letter-spacing: .4px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.timeline-date:hover{
  color: var(--brand-end);
}

.version-badge{
  background: var(--brand-end);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 16px;
  font-weight: 900;
  letter-spacing: .5px;
  box-shadow: 0 2px 8px rgba(18,191,160,0.25);
  transition: all 0.3s ease;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.version-badge:hover{
  background: var(--brand-start);
  box-shadow: 0 4px 12px rgba(18,191,160,0.35);
  transform: translateY(-1px);
}

.date-separator{
  color: #64748b;
  font-weight: 700;
  font-size: 20px;
  margin: 0 4px;
}

.date-text{
  color: #475569;
  font-weight: 700;
  font-size: 18px;
  letter-spacing: .3px;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.timeline-content{ color: #475569; line-height: 1.7; }
.timeline-chip{ 
  position: absolute; 
  right: 0; 
  top: -2px; 
  background: #e2fdf6; 
  color: #059669; 
  border: 1px solid #99f6e4; 
  font-size: 16px; 
  font-weight: 900;
  padding: 4px 10px; 
  border-radius: 999px; 
  letter-spacing: .3px;
  box-shadow: 0 2px 8px rgba(5,150,105,0.2);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.timeline-chip.latest{ 
  background: #eef2ff; 
  color: #4f46e5; 
  border-color: #c7d2fe; 
  box-shadow: 0 2px 8px rgba(79,70,229,0.2);
}

/* 高亮标签 */
.hl{ 
  display: inline-block; 
  padding: 2px 8px; 
  margin-right: 8px; 
  border-radius: 8px; 
  font-weight: 800; 
  font-size: 15px; 
  letter-spacing: .3px; 
  text-shadow: 0 1px 2px rgba(0,0,0,0.1);
  transform: scale(1.05);
  transition: all 0.2s ease;
}
.hl.add{ 
  background: #ecfeff !important; 
  color: #0284c7 !important; 
  border: 2px solid #a5f3fc !important; 
  box-shadow: 0 2px 8px rgba(2,132,199,0.15) !important;
}
.hl.mod{ 
  background: #fefce8 !important; 
  color: #b45309 !important; 
  border: 2px solid #fde68a !important; 
  box-shadow: 0 2px 8px rgba(180,83,9,0.15) !important;
}

/* 确保高亮样式在v-html中生效 */
:deep(.hl){ 
  display: inline-block; 
  padding: 2px 8px; 
  margin-right: 8px; 
  border-radius: 8px; 
  font-weight: 800; 
  font-size: 15px; 
  letter-spacing: .3px; 
  text-shadow: 0 1px 2px rgba(0,0,0,0.1);
  transform: scale(1.05);
  transition: all 0.2s ease;
}
:deep(.hl.add){ 
  background: #ecfeff !important; 
  color: #0284c7 !important; 
  border: 2px solid #a5f3fc !important; 
  box-shadow: 0 2px 8px rgba(2,132,199,0.15) !important;
}
:deep(.hl.mod){ 
  background: #fefce8 !important; 
  color: #b45309 !important; 
  border: 2px solid #fde68a !important; 
  box-shadow: 0 2px 8px rgba(180,83,9,0.15) !important;
}

/* 高亮文字样式 */
.highlight-main{
  background: linear-gradient(135deg, var(--brand-start), var(--brand-end));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 900;
  font-size: 1.05em;
  animation: glowPulse 2s ease-in-out infinite;
}

.highlight-feature{
  background: #ecfeff;
  color: #0284c7;
  padding: 2px 6px;
  border-radius: 6px;
  font-weight: 700;
  border: 1px solid #a5f3fc;
  transition: all 0.3s ease;
}

.highlight-feature:hover{
  background: #dbeafe;
  color: #1d4ed8;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(2,132,199,0.2);
}

.highlight-tech{
  background: #fefce8;
  color: #b45309;
  padding: 2px 6px;
  border-radius: 6px;
  font-weight: 700;
  border: 1px solid #fde68a;
  transition: all 0.3s ease;
}

.highlight-tech:hover{
  background: #fef3c7;
  color: #92400e;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(180,83,9,0.2);
}

.highlight-ui{
  background: #f3e8ff;
  color: #7c3aed;
  padding: 2px 6px;
  border-radius: 6px;
  font-weight: 700;
  border: 1px solid #ddd6fe;
  transition: all 0.3s ease;
}

.highlight-ui:hover{
  background: #ede9fe;
  color: #5b21b6;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(124,58,237,0.2);
}

.highlight-category{
  background: #f1f5f9;
  color: #475569;
  padding: 2px 8px;
  border-radius: 8px;
  font-weight: 800;
  border: 2px solid #cbd5e1;
  transition: all 0.3s ease;
  display: inline-block;
  margin-right: 4px;
}

.highlight-category:hover{
  background: #e2e8f0;
  color: #334155;
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(71,85,105,0.2);
}

.highlight-action{
  background: #dcfce7;
  color: #166534;
  padding: 1px 4px;
  border-radius: 4px;
  font-weight: 600;
  border: 1px solid #bbf7d0;
  transition: all 0.2s ease;
}

.highlight-action:hover{
  background: #bbf7d0;
  color: #15803d;
  transform: scale(1.02);
}

.highlight-file{
  background: #fef2f2;
  color: #dc2626;
  padding: 1px 4px;
  border-radius: 4px;
  font-weight: 600;
  border: 1px solid #fecaca;
  font-family: 'Courier New', monospace;
  transition: all 0.2s ease;
}

.highlight-file:hover{
  background: #fee2e2;
  color: #b91c1c;
  transform: scale(1.02);
}

.highlight-path{
  background: #f0f9ff;
  color: #0369a1;
  padding: 1px 4px;
  border-radius: 4px;
  font-weight: 600;
  border: 1px solid #bae6fd;
  font-family: 'Courier New', monospace;
  transition: all 0.2s ease;
}

.highlight-path:hover{
  background: #e0f2fe;
  color: #075985;
  transform: scale(1.02);
}

.highlight-code{
  background: #1e293b;
  color: #e2e8f0;
  padding: 2px 6px;
  border-radius: 6px;
  font-weight: 600;
  border: 1px solid #475569;
  font-family: 'Courier New', monospace;
  transition: all 0.3s ease;
}

.highlight-code:hover{
  background: #334155;
  color: #f1f5f9;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(30,41,59,0.3);
}

/* 动画效果 */
@keyframes glowPulse {
  0%, 100% { 
    filter: brightness(1);
    text-shadow: 0 0 5px rgba(18,191,160,0.3);
  }
  50% { 
    filter: brightness(1.1);
    text-shadow: 0 0 15px rgba(18,191,160,0.6);
  }
}

/* 为介绍卡片添加进入动画 */
.intro-card {
  animation: slideInUp 0.6s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 为列表项添加延迟动画 */
.intro-list li, .intro-steps li {
  animation: fadeInLeft 0.5s ease-out;
  animation-fill-mode: both;
}

.intro-list li:nth-child(1) { animation-delay: 0.1s; }
.intro-list li:nth-child(2) { animation-delay: 0.2s; }
.intro-list li:nth-child(3) { animation-delay: 0.3s; }
.intro-list li:nth-child(4) { animation-delay: 0.4s; }
.intro-list li:nth-child(5) { animation-delay: 0.5s; }

.intro-steps li:nth-child(1) { animation-delay: 0.1s; }
.intro-steps li:nth-child(2) { animation-delay: 0.2s; }
.intro-steps li:nth-child(3) { animation-delay: 0.3s; }
.intro-steps li:nth-child(4) { animation-delay: 0.4s; }

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>


