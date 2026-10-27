# 体态与健康管理系统（Servlet + JSP 版）

基于 **Servlet + JSP + JDBC + MySQL** 的体态与健康管理系统。功能与 `D:\code\作业2`（Spring Boot 版）对齐，但**不使用 Spring 框架**，改用传统 Java Web 技术栈：JSP 页面（HTML + CSS + JSTL/EL 服务端渲染）、Servlet 控制器、JDBC 数据访问、Session 会话鉴权。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 视图 | JSP + JSTL 3.0 + EL + HTML + CSS + ECharts（本地引入） |
| 控制器 | Servlet（`@WebServlet`） |
| 数据访问 | JDBC（原生 `PreparedStatement`，无 ORM） |
| 数据库 | MySQL 8（自动建表 `t_*` 7 张） |
| 鉴权 | HttpSession 会话 + `AuthFilter` 过滤器 |
| 密码 | BCrypt（`org.mindrot:jbcrypt`） |
| 构建/部署 | Maven WAR · Tomcat 10.1（Jakarta EE 10） |

## 功能清单

- **通用用户**：注册/登录、个人资料、修改密码、身体指标记录（BMI 自动计算）、健康趋势折线图（ECharts）、健康建议（规则引擎）、健身记录 + 累计统计。
- **有健身经验的用户（初学者只读）**：发布经验文章、点赞、评论。
- **管理员**：数据统计看板（用户增长/分类分布图表）、用户管理（启用禁用/角色/删除）、内容审核（通过/驳回）、公告管理。

## 目录结构

```
大作业/
├── pom.xml
├── sql/init.sql                      # 手动建库脚本（可选）
└── src/main/
    ├── java/com/fitness/
    │   ├── util/        DBUtil / WebUtil
    │   ├── common/      BusinessException
    │   ├── entity/      User / HealthRecord / FitnessRecord / Article / Comment / Announcement / VO
    │   ├── dao/         7 个 JDBC 数据访问类
    │   ├── service/     业务逻辑（含健康建议规则引擎、后台统计）
    │   ├── web/         15 个 Servlet 控制器
    │   ├── filter/      EncodingFilter / AuthFilter
    │   └── listener/    AppInitializer（启动建表 + 演示数据）
    ├── resources/db.properties        # 数据库连接配置
    └── webapp/
        ├── index.jsp                 # 入口（按登录态跳转）
        ├── static/                   # css/style.css + js/echarts.min.js
        └── WEB-INF/views/            # 所有 JSP 页面（含 common/ 公共头尾与分页）
```

## 运行步骤

### 1. 准备数据库

确保本机 MySQL 已启动。默认连接 `fitness_db`（用户名 `root`，密码见 `src/main/resources/db.properties`，当前为 `200300`）。

- 首次启动应用时，`AppInitializer` 会**自动建库建表**并写入演示账号与示例文章；也可手动执行 `sql/init.sql`。
- 若本机 MySQL 密码不同，请先改 `db.properties` 里的 `jdbc.password`。

### 2. 打包

```bash
cd D:\code\大作业
mvn clean package -DskipTests
```

生成 `target/fitness-system.war`。

### 3. 部署到 Tomcat 10.1

把 `target/fitness-system.war` 复制到 Tomcat 的 `webapps/` 目录，启动 Tomcat 后访问：

```
http://localhost:8080/fitness-system/
```

（上下文路径 = war 文件名，即 `/fitness-system`。）

### 4. 测试账号

| 用户名 | 密码 | 角色 | 说明 |
| --- | --- | --- | --- |
| admin | admin123 | 管理员 | 可进后台管理 |
| demo | 123456 | 普通用户（有健身经验） | 可发布文章 |
| beginner | 123456 | 普通用户（初学者） | 只记录数据、浏览文章 |

## 说明

- 页面采用 **PRG（Post-Redirect-Get）** 模式，表单 POST 到 Servlet 处理后重定向回列表页，避免刷新重复提交。
- 登录态用 **HttpSession** 保存；`AuthFilter` 统一拦截未登录请求，并对 `/admin/*` 做管理员校验。
- 图表数据由 Servlet 计算后经 **Gson** 序列化，嵌入 JSP 页面供 ECharts 渲染，全程服务端渲染、离线可用。
