# TraceGuard 项目开发规范

本文档为 AI 编程助手提供在 TraceGuard（标签防伪溯源系统）代码库中工作的基本规范。

## 项目概述

**TraceGuard** 是基于 RuoYi 框架开发的标签防伪溯源系统，采用多模块 Maven 架构，使用 Java 8、Spring Boot 2.5.15 和 Spring Security 技术栈。

### 模块结构

- **TraceGuard-admin**: Web 服务入口（主应用程序）
- **TraceGuard-framework**: 核心框架（安全、配置、异常处理）
- **TraceGuard-system**: 系统模块（用户、角色、部门、菜单）
- **TraceGuard-feature**: 业务模块（产品、批次、优惠券等）
- **TraceGuard-quartz**: 定时任务模块
- **TraceGuard-generator**: 代码生成模块
- **TraceGuard-common**: 通用工具和共享组件

## 构建命令

```bash
# 编译整个项目
mvn clean compile

# 打包所有模块
mvn clean package

# 安装到本地仓库
mvn clean install

# 跳过测试构建
mvn clean package -DskipTests

# 仅构建指定模块
mvn clean compile -pl TraceGuard-common

# 构建包含依赖的模块
mvn clean package -pl TraceGuard-admin -am
```

## 测试命令

```bash
# 运行所有测试
mvn test

# 运行指定模块的测试
mvn test -pl TraceGuard-framework

# 运行指定测试类
mvn test -Dtest=ClassName

# 运行指定测试方法
mvn test -Dtest=ClassName#methodName

# 跳过测试
mvn clean install -DskipTests
```

**注意**: 本项目目前测试覆盖率较低。添加测试时，请将测试文件放在 `src/test/java` 目录下，并遵循与源码相同的包结构。

## 代码规范

### 包结构

- **基础包**: `com.arsc.traceGuard`
- **控制器**: `com.arsc.traceGuard.web.controller.{module}`
- **服务层**: `com.arsc.traceGuard.{module}.service`（接口带 `I` 前缀）和 `.service.impl`
- **映射层**: `com.arsc.traceGuard.{module}.mapper`
- **领域/实体**: `com.arsc.traceGuard.{module}.domain`
- **配置**: `com.arsc.traceGuard.framework.config`

### 命名规范

- **类名**: 大驼峰（例如 `SysUserController`, `TgProductService`）
- **接口名**: 大驼峰，服务接口带 `I` 前缀（例如 `ISysUserService`）
- **方法名**: 小驼峰（例如 `selectUserList`, `insertTgProduct`）
- **变量名**: 小驼峰
- **常量**: 大写下划线（例如 `MAX_SIZE`, `DEFAULT_TIMEOUT`）
- **数据库实体**: 领域类名与表名匹配，带 `Tg` 或 `Sys` 前缀

### 导入组织

1. 标准 Java 导入（`java.*`, `javax.*`）
2. 第三方库（Spring, Apache Commons 等）
3. 项目内部导入（`com.arsc.traceGuard.*`）
4. 静态导入

仅在静态导入或需要导入同一包下大量类时使用通配符导入。

### 代码格式

- **缩进**: 4 空格（不使用制表符）
- **行长度**: 每行不超过 120 字符
- **大括号**: 左大括号与语句同行（K&R 风格）
- **注释**: 公共 API 使用 Javadoc，区块使用块注释，谨慎使用行注释

示例：
```java
/**
 * 产品信息控制器
 * 
 * @author zhangcj
 * @date 2026-01-06
 */
@RestController
@RequestMapping("/feature/product")
public class TgProductController extends BaseController
{
    @Autowired
    private ITgProductService tgProductService;

    /**
     * 查询产品信息列表
     */
    @PreAuthorize("@ss.hasPermi('feature:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgProduct tgProduct)
    {
        startPage();
        List<TgProduct> list = tgProductService.selectTgProductList(tgProduct);
        return getDataTable(list);
    }
}
```

### 控制器模式

所有控制器必须继承 `BaseController`：
- 使用 `@RestController` 和 `@RequestMapping`
- 安全控制: 使用 `@PreAuthorize("@ss.hasPermi('module:resource:action')")`
- 日志记录: 数据变更操作使用 `@Log(title = "描述", businessType = BusinessType.XXX)`
- CRUD 映射: `@GetMapping`（列表/查询）, `@PostMapping`（创建）, `@PutMapping`（更新）, `@DeleteMapping`（删除）
- 单对象返回使用 `AjaxResult`，分页列表返回 `TableDataInfo`
- 变更操作使用 `toAjax(int rows)` 或 `toAjax(boolean result)` 返回结果

### 服务层

- 接口定义放在 `.service` 包，带 `I` 前缀
- 实现类放在 `.service.impl` 包
- 使用 `@Service` 注解
- 返回领域对象或集合，集合永远不要返回 `null`（应返回空列表）

### 异常处理

- 使用 `GlobalExceptionHandler` 进行集中式异常处理
- 业务逻辑错误抛出 `ServiceException`
- 使用 `HttpStatus` 常量设置适当的 HTTP 状态码
- 错误日志需包含上下文：`log.error("请求地址'{}',发生系统异常.", requestURI, e);`

### 数据库（MyBatis）

- 映射器接口使用 `@Mapper` 注解
- XML 映射文件放在 `src/main/resources/mapper/{module}/`
- 使用动态 SQL 的 `<where>`, `<if>` 标签
- ORDER BY 子句始终使用 `SqlUtil.escapeOrderBySql()` 转义

### 安全

- 基于 JWT 的身份认证
- 权限注解: `@PreAuthorize("@ss.hasPermi('perm')")`
- 角色检查: `@PreAuthorize("@ss.hasRole('role')")`
- 获取当前用户: `getLoginUser()` 或 `SecurityUtils.getLoginUser()`

### 常用工具类

`TraceGuard-common` 中的关键工具类：
- `StringUtils`: 字符串操作
- `DateUtils`: 日期格式化和解析
- `SecurityUtils`: 安全上下文操作
- `ExcelUtil<T>`: Excel 导入导出
- `AjaxResult`: 标准 API 响应包装
- `TableDataInfo`: 分页响应包装

## 应用入口

主类: `TraceGuard-admin` 模块中的 `com.arsc.traceGuard.RuoYiApplication`

## 开发提示

1. **添加新模块**: 遵循现有模块命名规范 `TraceGuard-{name}`
2. **数据库变更**: 更新领域类、映射器接口和 XML 映射文件
3. **新接口**: 始终将权限字符串添加到数据库 `sys_menu` 表
4. **日志**: 使用 `logger`（继承自 `BaseController`）或 `log`（SLF4J）
5. **分页**: 服务调用前执行 `startPage()`，返回 `getDataTable(list)`

## 部署

使用 `ry.sh` 脚本管理应用生命周期：
```bash
./ry.sh start    # 启动应用
./ry.sh stop     # 停止应用
./ry.sh restart  # 重启应用
./ry.sh status   # 查看状态
```

脚本中配置的 JVM 参数为初始堆 512MB / 最大堆 1024MB。

---

详细框架文档请访问: http://doc.ruoyi.vip
