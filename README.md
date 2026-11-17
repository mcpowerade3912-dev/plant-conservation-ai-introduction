# 数智保育系统

可直接用 IntelliJ IDEA 运行的前后端分离植物保育管理系统，覆盖数据库、用户登录、JWT、用户管理、引种记录、鉴定审核、植物管理和权限分级。

完整的介绍、安装、运行、接口与常见问题（含乱码排查）请见 [`介绍和使用说明.md`](./介绍和使用说明.md)。

## 技术栈
- 后端：Java 17、Spring Boot 2.7.18、Spring Security、JdbcTemplate、JWT
- 前端：Vue 3、Vite、TypeScript、Element Plus、Axios、Pinia、Vue Router
- 数据库：MySQL 8.x
- AI：OpenAI Responses API、结构化 JSON 输出
- 图片：本地文件上传、静态资源访问、JPG/PNG/WebP 校验
- 设置中心：系统参数持久化、AI 接口动态配置、API Key 加密

## 快速启动
1. 在 MySQL 中执行 `database/plant.sql`。
2. 用 IDEA 打开项目根目录，IDEA 会识别 `backend` 为 Maven 项目。
3. 修改 `backend/src/main/resources/application.yml` 中的数据库用户名和密码。
4. 运行 `com.example.plant.PlantApplication`，后端地址为 `http://127.0.0.1:8081`。
5. 在 IDEA 终端执行：
   ```powershell
   cd frontend
   npm install
   npm run dev
   ```
6. 浏览器打开 `http://localhost:5174`。
7. 测试账号：`admin / 123456`、`reviewer / 123456`、`user1 / 123456`。

## API
- `POST /api/login`：登录并返回 JWT
- `GET/PUT /api/users/me`：查看、修改个人信息
- `GET/POST/PUT/DELETE /api/users`：用户管理
- `GET/POST/PUT/DELETE /api/introductions`：引种记录 CRUD
- `GET /api/reviews/pending`、`POST /api/reviews/{id}`：鉴定审核
- `GET/POST/PUT/DELETE /api/plants`：植物管理
- `GET /api/dashboard/stats`：首页统计
- `POST /api/ai/introduction-assist`：AI 推荐植物学名、科属、来源与养护提示
- `POST /api/files/plant-images`：上传植物图片并返回访问地址
- `GET /api/settings/public`：读取系统名称和客服联系方式
- `GET/PUT /api/admin/settings`：读取和更新管理员设置

## 系统设置

管理员可以在“系统设置”中配置：

- 系统名称
- 客服联系方式
- 默认植物分类
- 图片上传上限
- 是否启用 AI
- AI 接口地址、模型、超时和每分钟调用上限
- AI API Key

设置保存后立即影响现有功能，不需要重启后端。API Key 使用 AES-GCM
加密保存，接口不会返回明文。`reviewer` 和 `user1` 无法访问管理员设置接口。

## 植物图片

引种记录和植物档案均支持图片：

- 新增、编辑时上传 JPG、PNG 或 WebP 图片。
- 单张图片最大 5MB。
- 图片上传后显示预览，可替换或移除。
- 列表显示缩略图，点击后可放大预览。
- 引种审核通过时，图片会同步写入正式植物档案。

图片默认保存在后端运行目录下的 `uploads/plant-images`。可通过
`UPLOAD_DIR` 环境变量修改存储目录：

```powershell
$env:UPLOAD_DIR='D:\plant-uploads'
```

## AI 引种助手

在“引种记录 → 新增引种记录”中填写植物名称后，点击“AI 补全”。
系统会返回最多三个候选物种，并展示科属、别名、来源地、置信度和养护提示。
只有点击“采用此候选”后才会填入学名和来源地，最终仍需人工核对并保存。

配置环境变量后启用：

```powershell
$env:OPENAI_API_KEY='你的 API Key'
$env:OPENAI_BASE_URL='https://api.openai.com/v1'
$env:OPENAI_MODEL='gpt-4.1-mini'
$env:SETTINGS_ENCRYPTION_KEY='用于加密 API Key 的独立密钥'
```

未配置 API Key 时，引种记录的其他功能不受影响，AI 补全接口会返回 503 提示。

## 角色
- `ADMIN`：用户管理、全量引种记录、鉴定、植物管理
- `REVIEWER`：查看全量引种记录、鉴定审核、植物管理
- `USER`：维护自己的引种记录、查看植物

## 交付物
- 实践报告：`2023级专业综合实践报告-完成版.docx`
- 报告预览 PDF：`report-render.pdf`
- 介绍与使用说明：`介绍和使用说明.md`
