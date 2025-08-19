# ⚙️ 后端（project-backend）

> Spring Boot 3 + Security + MyBatis/JPA + Redis 的后端服务，配合前端实现校园论坛/表白墙功能。

## 🧰 技术栈
- ☕ Java 17，Spring Boot 3.0.5
- 🔐 Spring Security（表单登录、RememberMe、会话拦截、CORS）
- 🗂️ MyBatis 注解 Mapper + JPA（按需）
- 📨 JavaMail（邮件验证码）
- ⚡ Redis（验证码/状态）
- 🧱 Log4j2、Lombok、HikariCP

---

## 📁 主要结构
```
src/main/java/com/xiaoRed/
  config/            # 安全与Web配置（CORS、静态资源、拦截器）
  controller/        # 控制器（auth/user/posts/comments/likes/manage）
  interceptor/       # AuthorizeInterceptor（会话注入）
  mapper/            # MyBatis Mapper（User/Post/Comment/Like）
  service/           # 接口与实现（impl）
  entity/            # 统一响应 RestBean、领域实体（auth/user/postings/vindicate）
src/main/resources/
  application.yml    # 数据源/Redis/上传/邮件/日志
uploads/             # 运行时生成（头像：uploads/avatars/{uid}/...）
```

---

## ⚙️ 运行配置（application.yml）
- 数据库（默认）：`jdbc:mysql://localhost:3306/forum` 用户/密码 `root/root`
- Redis：`localhost:6379`
- 文件上传：
  - `spring.servlet.multipart.max-file-size: 5MB`
  - `file.upload-dir: ./uploads`
  - `file.avatar-base-url: /uploads/avatars/`
- 邮件服务：`spring.mail.*`（建议生产用环境变量覆盖）
- 日志与调试：`debug: true`，`logging.level.com.xiaoRed: debug`

> 💡 修改 `file.upload-dir` 后需重启服务；确保该目录对运行用户可写。

---

## 🔐 安全与拦截
- 放行路径：
  - `/api/auth/**`、`/api/posts/**`、`/api/comments/**`、`/api/likes/**`、`/api/manage/**`
  - `POST /api/user/upload-avatar`
  - `GET /uploads/**`（静态头像访问）
- 其余需要登录；表单登录 `/api/auth/login`，退出 `/api/auth/logout`
- CORS：允许任意来源（开发阶段）、允许携带凭证（Cookie）
- RememberMe：JDBC 存储，表 `persistent_logins`
- 会话注入：`AuthorizeInterceptor` 从 SecurityContext 取登录用户，在 `session.accountUser` 注入 `AccountUser`（安全防护：仅在 principal 为登录用户时注入）

---

## 🖼️ 头像上传与静态映射
- API：`POST /api/user/upload-avatar`（`multipart/form-data`）
  - 字段：`file`（图片文件）、`userInfo`（`AccountUser` 的 JSON；多余字段已忽略）
  - 流程：
    1) 校验用户合法性（`UserService.isValidUser` 基于 `db_account`）
    2) 删除旧头像（若存在）
    3) 存储新头像至 `file.upload-dir/avatars/{uid}/`（`FileStorageService.storeAvatar`）
    4) 同步更新 `db_account_user.avatar`、`db_account_info.avatar`（`upsert`）
    5) 返回统一响应 `RestBean.success("/uploads/avatars/{uid}/{filename}")`
- 静态映射：`WebConfiguration.addResourceHandlers`
  - `/uploads/avatars/**` → `file:{file.upload-dir}/avatars/`
  - 已从拦截器与安全策略中放行

---

## 🔗 接口概要（摘）
- 认证：
  - `POST /api/auth/login`、`POST /api/auth/logout`
  - `POST /api/auth/valid-register-email`、`/valid-reset-email`
  - `POST /api/auth/register`、`/start-reset`、`/do-reset`
- 用户：
  - `GET /api/user/me`（从 `session.accountUser` 返回会话用户）
  - `GET /api/user/info`、`GET /api/user/privacy`
  - `POST /api/user/save-info`、`/save-email`、`/change-password`、`/save-privacy`
  - `POST /api/user/upload-avatar`
- 帖子：`/api/posts/**`（列表、详情、创建、编辑、删除、标签）
- 评论：`/api/comments/**`（创建、按帖子查询、删除）
- 表白墙：`/api/likes/**`（列表、创建）
- 管理：`/api/manage/**`（列表、改状态、删除）

> 📦 统一响应体：`RestBean<T>`
```
{
  "status": 200,
  "success": true,
  "message": <T>
}
```

---

## 🗄️ 数据库
- 脚本：`数据库/forum.sql`
- 关键表：
  - `db_account`（基础账号：id/username/password/email）
  - `db_account_info`（扩展资料：uid/username/sex/avatar/...）
  - `db_account_user`（会话/投影：id/username/email/avatar）
  - `db_post`、`db_post_article`、`db_post_comment`、`db_likes`
  - `persistent_logins`（RememberMe）

---

## ▶️ 运行与构建
- 运行（开发）：
```bash
mvn spring-boot:run
```
- 打包：
```bash
mvn -DskipTests package
```
- 环境变量（建议）：
  - `SPRING_DATASOURCE_URL/USERNAME/PASSWORD`
  - `SPRING_MAIL_USERNAME/PASSWORD`（授权码）
  - `FILE_UPLOAD_DIR`（覆盖上传目录）

---

## 🧪 常见问题（FAQ）
- ❓ 上传成功但前端 404：
  - 确认实际文件存在于 `{upload-dir}/avatars/{uid}/`；
  - `WebConfiguration` 静态映射是否为 `file:{dir}/avatars/`（注意末尾斜杠）；
  - 前端开发态需使用 Vite 代理 `/uploads` → `http://localhost:8080`。
- ❓ 上传 500：
  - `userInfo` JSON 含额外字段无妨（`@JsonIgnoreProperties(ignoreUnknown = true)`）；
  - 确认 `multipart` 大小未超 `5MB`；
  - 日志查看 `UserController#uploadAvatar` 打印与堆栈。
- ❓ 会话空或强转异常：
  - 访问是否为放行路径；
  - `AuthorizeInterceptor` 已做 `principal` 类型判断，确保先登录再访问需鉴权接口。
- ❓ RememberMe 失效：
  - 确认存在表 `persistent_logins` 且 `SecurityConfiguration` 已配置 `JdbcTokenRepositoryImpl`。

---

## 🤝 开发建议
- 生产环境收敛 CORS 允许来源，仅放行可信前端域名；
- 将敏感配置迁移至环境变量/密钥管理；
- `file.upload-dir` 使用绝对路径并设置权限与周期清理；
- 为关键接口增加单元/集成测试（鉴权、上传、事务）。 