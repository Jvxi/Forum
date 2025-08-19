# 🖥️ 前端（project-frontend）

> 基于 Vue 3 + Vite + Element Plus + Pinia 的前端项目，配合后端校园论坛/表白墙服务使用。

## ✨ 特性
- ⚡️ Vite 快速开发与热更新
- 🧩 组件化开发（Element Plus 自动按需加载）
- 🧭 Vue Router 路由与路由守卫
- 🗃️ Pinia 全局状态管理（持久化 `localStorage`）
- 🌐 Axios 请求封装（401 自动跳转登录）
- 🖼️ 头像上传与默认头像（按性别回退）

## 📦 安装与启动
```bash
npm install
npm run dev   # 开发（默认 http://localhost:5173）
```

## 🏗️ 构建
```bash
npm run build # 产物位于 dist/
```

## 📁 目录结构
```
src/
  assets/           # 静态资源
  components/       # 组件（包含设置页组件）
  net/              # Axios 封装
  router/           # 路由
  stores/           # Pinia 状态
  views/            # 页面（index、welcome 等）
  App.vue
  main.js
public/
  images/           # 默认头像：男.png、女.png、未.png
```

## 🔌 开发代理
开发模式下，`vite.config.js` 已配置：
- 将以 `/uploads` 开头的请求代理到后端 `http://localhost:8080`
- 因此前端在开发态可直接使用 `/uploads/avatars/...` 访问后端静态资源

> 注意：生产环境需在 Nginx/网关等做同等代理或改为完整后端域名。

## 👤 头像上传与默认头像
- 上传组件位于：`src/components/settings/infoSettings.vue`
- 上传成功后会更新 `store.auth.user.avatar` 并持久化；页面优先展示上传头像
- 未上传时按 `sex` 回退：
  - `M` → `/images/男.png`
  - `F` → `/images/女.png`
  - 其他/未知 → `/images/未.png`
- 展示位置：
  - 顶部用户头像：`src/views/indexView.vue`
  - 设置页右侧卡片：`src/views/index/Settings.vue`

## 🔐 路由与权限
- 路由定义：`src/router/index.js`
- 路由守卫逻辑：
  - 未登录访问 `/index/*` → 重定向 `/`
  - 已登录访问 `/welcome-*` → 重定向 `/index`

## 🌐 请求封装
- 位置：`src/net/index.js`
- `get`/`post` 默认带上 `withCredentials` 以支持后端会话/记住我
- 收到 401 自动跳转登录并提示

## 🧭 常用脚本
- `npm run dev`：开发模式
- `npm run build`：打包构建

## 📝 开发提示
- 新页面请在 `router/index.js` 注册路由，并适配路由守卫
- 新增需要登录的接口时，确保与后端安全策略匹配（必要时在后端放行路径或增加会话注入）
- 若使用外链图片/资源，请注意同源策略与 HTTPS 混用问题

## 🧪 排错速查
- 头像 404（开发态）：确认前端代理生效（重启 `npm run dev`），并保证后端已映射 `/uploads/avatars/**`
- 成功上传但不显示：检查 `store.auth.user.avatar` 是否已更新，刷新后是否仍存在；确认网络面板头像 URL 200
- 循环提示/重复弹窗：确保上传逻辑仅在自定义上传函数中提示一次
