# 🎓 校园论坛 + 💌 表白墙

> 一个轻量、现代、上手快的校园交流平台。
> 
> ✨ 支持发帖/评论/收藏/表白墙、用户资料与安全设置、头像上传与默认头像（按性别）等。

---

## 🧩 项目简介
前后端分离的校园社区应用，聚焦“好用、好看、好维护”。已实现本地头像存储与静态映射、邮件验证码注册、记住我登录和会话拦截。

## 🧰 技术栈
- ⚙️ 后端：Spring Boot 3.0.5、Spring Security、MyBatis、JPA、Redis、JavaMail、Lombok、Log4j2（Java 17）
- 🖥️ 前端：Vue 3、Vite 4、Element Plus、Pinia、Vue Router、Axios
- 🗄️ 数据库：MySQL 8（DDL/示例数据见 `数据库/forum.sql`）

## ✅ 功能总览
- 📝 发帖（普通/表白）与公开展示
- 💬 多级评论
- 👍 点赞与 ⭐ 收藏
- ✏️ 帖主编辑、🛡️ 管理员审核/隐藏/删除
- 👤 用户资料：头像上传/个签/背景/博客等
- 🔐 安全设置：改密、隐私选项、邮箱修改
- 🗂️ 我的收藏、帖子管理

---

## 📁 目录结构
- 后端：`project-backend/`
  - 入口：`com.xiaoRed.ProjectBackendApplication`
  - 配置：`com.xiaoRed.config`（`SecurityConfiguration`、`WebConfiguration`）
  - 控制器：`com.xiaoRed.controller`
  - 拦截器：`com.xiaoRed.interceptor.AuthorizeInterceptor`
  - 持久层：`com.xiaoRed.mapper`
  - 业务层：`com.xiaoRed.service`/`impl`
  - 实体：`com.xiaoRed.entity`（`RestBean` 统一响应）
  - 资源：`src/main/resources/application.yml`
  - 静态上传根：`project-backend/uploads/`（头像：`uploads/avatars/{uid}/...`）
- 前端：`project-frontend/`
  - 入口：`src/main.js`、`App.vue`
  - 路由：`src/router/index.js`
  - 状态：`src/stores/index.js`
  - 网络：`src/net/index.js`
  - 页面：`src/views`（首页 `indexView.vue`，设置页 `index/Settings.vue`）
  - 组件：`src/components/settings/infoSettings.vue` 等
  - 静态资源：`public/images`（默认头像：`男.png`、`女.png`、`未.png`）

---

## 🚀 快速开始
### 📦 先决条件
- JDK 17、Maven 3.8+
- Node.js 16+（建议 18 LTS）
- MySQL 8、Redis（默认 6379）

### 🗃️ 初始化数据库
1) 创建数据库并导入脚本：
```sql
CREATE DATABASE IF NOT EXISTS forum CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE forum;
-- 导入仓库中的 `数据库/forum.sql`
```
2) 确认存在表：`db_account`、`db_account_info`（含 `avatar`、`sex`）、`db_account_user`（含 `avatar`）、`persistent_logins` 等。

### ⚙️ 配置后端
- `project-backend/src/main/resources/application.yml`
  - 数据库：`spring.datasource.*`（默认 `root/root`，数据库名 `forum`）
  - Redis：默认 `localhost:6379`
  - 文件上传：`file.upload-dir: ./uploads`（可改为绝对路径）
  - 邮件：`spring.mail.*`（建议使用环境变量覆盖）

### ▶️ 启动
- 后端（默认 8080）：
```bash
cd project-backend
mvn spring-boot:run
```
- 前端（开发 5173）：
```bash
cd project-frontend
npm i
npm run dev
```
- 代理说明：开发模式已在 `vite.config.js` 将 `/uploads` 代理到 `http://localhost:8080`，前端直接访问 `/uploads/...` 即可。

> 💡 提示：首次上传头像会自动创建目录 `uploads/avatars/{uid}/`，请确保该目录可写。

---

## 🔗 接口概要（摘）
- 认证（匿名可访问）：`/api/auth/**`
  - `POST /api/auth/login`（Security 表单登录）
  - `POST /api/auth/logout`
  - `POST /api/auth/valid-register-email`、`/valid-reset-email`
  - `POST /api/auth/register`、`/start-reset`、`/do-reset`
- 用户（需登录，除特殊放行）：`/api/user/**`
  - `GET /api/user/me` 当前会话用户（拦截器注入 `session.accountUser`）
  - `GET /api/user/info`、`GET /api/user/privacy`
  - `POST /api/user/save-info`、`/save-email`、`/change-password`、`/save-privacy`
  - `POST /api/user/upload-avatar`（multipart，放行但做用户合法性校验）
- 帖子：`/api/posts/**`（列表/详情/创建/编辑/删除/标签）
- 评论：`/api/comments/**`（创建、按帖子查询、删除）
- 表白墙：`/api/likes/**`（列表、创建）
- 管理：`/api/manage/**`（帖子管理）

> 🔐 安全策略：`SecurityConfiguration` 放行 `auth/posts/comments/likes/manage` 与 `POST /api/user/upload-avatar`、`GET /uploads/**`；其余需认证。跨域允许携带 Cookie。

---

## 🖼️ 头像上传与默认头像
- 📥 存储：`FileStorageService.storeAvatar` 保存至 `file.upload-dir/avatars/{uid}/`，返回 URL：`/uploads/avatars/{uid}/{filename}`。
- 🌐 静态映射：`WebConfiguration#addResourceHandlers` 将 `/uploads/avatars/**` 映射到本地目录，浏览器可直接访问。
- 🧑‍🤝‍🧑 默认头像回退策略（前端）：

| 条件 | 展示 |
| --- | --- |
| 用户已上传头像 | 使用返回的 `/uploads/avatars/{uid}/{filename}` |
| 未上传且 `sex === 'M'` | `public/images/男.png` |
| 未上传且 `sex === 'F'` | `public/images/女.png` |
| 未上传且未知性别 | `public/images/未.png` |

- 🔁 会话/数据库更新：
  - `UserServiceImpl.updateAvatar` 同步写入 `db_account_user.avatar` 与 `db_account_info.avatar`（存在即更新，不存在则插入）。
  - `UserMapper.findAccountUser*` 通过 `db_account` 左联 `db_account_info`，会话对象可携带最新头像。

> ✅ 前端已在 `indexView.vue`、`Settings.vue`、`components/settings/infoSettings.vue` 统一“优先头像、否则按性别默认”的展示逻辑。

---

## 🧪 常见问题（FAQ）
- ❓ 开发时头像 404：
  - 前端必须使用开发代理（`vite.config.js` 的 `/uploads` → `8080`），修改后重启前端；
  - 后端 `WebConfiguration` 路径拼接确保为 `file:./uploads/avatars/`；
  - 确认实际文件已写入 `project-backend/uploads/avatars/{uid}/`。
- ❓ 上传成功但不展示：
  - 检查页面引用是否以 `/uploads/` 开头；
  - 后端日志应包含“头像存储成功”；
  - 清理浏览器缓存后重试。
- ❓ 反序列化报 `sex` 字段：
  - `AccountUser` 已加 `@JsonIgnoreProperties(ignoreUnknown = true)`。

> ⚠️ 安全建议：生产环境收敛 CORS 允许来源、用环境变量管理敏感配置、为上传路径设置配额与清理策略。

---

## 🛠️ 构建与部署
- 🧱 后端打包：
```bash
cd project-backend
mvn -DskipTests package
```
- 📦 前端构建：
```bash
cd project-frontend
npm run build
```
- 🚢 生产部署建议：
  - 静态站点（Nginx 等）托管 `project-frontend/dist`；
  - 反向代理 `/api/` 到后端、`/uploads/` 到后端；
  - 或直接调用后端域名，注意跨域与 Cookie 策略配置。

---

## 🤝 开发建议
- 将 `spring.mail.*`、数据库密码等敏感项迁移到环境变量/私有配置；
- 为接口与关键服务增加单元测试；
- 生产环境仅放行可信前端域名；
- `file.upload-dir` 使用绝对路径并设置权限与清理任务。