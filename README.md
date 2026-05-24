ecommerce-platform/                          # 项目根目录
│
├── backend/                                  # 后端 (Spring Boot)
│   ├── pom.xml                               # Maven 配置文件
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/ecommerce/
│   │   │   │       │
│   │   │   │       ├── ECommerceApplication.java      # Spring Boot 启动类
│   │   │   │       │
│   │   │   │       ├── common/                         # 通用类
│   │   │   │       │   └── ApiResponse.java           # 统一响应封装
│   │   │   │       │
│   │   │   │       ├── config/                         # 配置类
│   │   │   │       │   ├── CorsConfig.java            # 跨域配置
 |    |     |     |        |
│   │   │   │       ├── admin/                          # 管理员模块
│   │   │   │       │   ├── controller/                # 管理员控制器
│   │   │   │       │   │   ├── AdminAuthController.java   # 管理员登录
│   │   │   │       │   │   ├── AdminStatsController.java   # 统计数据
│   │   │   │       │   │   ├── AdminGoodsController.java   # 商品管理
│   │   │   │       │   │   ├── AdminOrderController.java    # 订单管理
│   │   │   │       │   │   ├── AdminClickLogController.java # 点击量管理
│   │   │   │       │   │   └── AdminSystemLogController.java # 系统日志管理
│   │   │   │       │   ├── entity/                     # 管理员实体
│   │   │   │       │   │   └── Admin.java
│   │   │   │       │   └── repository/                 # 管理员数据访问
│   │   │   │       │       └── AdminRepository.java
│   │   │   │       │
│   │   │   │       ├── controller/                      # 用户端控制器
│   │   │   │       │   ├── UserController.java        # 用户管理
│   │   │   │       │   ├── GoodsController.java       # 商品管理
│   │   │   │       │   ├── OrderController.java       # 订单管理
│   │   │   │       │   ├── CommentController.java     # 评论管理
│   │   │   │       │   ├── CartController.java        # 购物车管理
│   │   │   │       │   └── ClickLogController.java    # 点击日志
│   │   │   │       │
│   │   │   │       ├── entity/                          # 数据实体类
│   │   │   │       │   ├── User.java                  # 用户实体
│   │   │   │       │   ├── Goods.java                 # 商品实体
│   │   │   │       │   ├── Order.java                 # 订单实体
│   │   │   │       │   ├── OrderItem.java             # 订单项实体
│   │   │   │       │   ├── Comment.java               # 评论实体
│   │   │   │       │   ├── Cart.java                  # 购物车实体
│   │   │   │       │   ├── CartItem.java              # 购物车项实体
│   │   │   │       │   ├── ClickLog.java              # 点击日志实体
│   │   │   │       │   └── SystemLog.java             # 系统日志实体
│   │   │   │       │
│   │   │   │       ├── repository/                      # 数据访问层
│   │   │   │       │   ├── UserRepository.java
│   │   │   │       │   ├── GoodsRepository.java
│   │   │   │       │   ├── OrderRepository.java
│   │   │   │       │   ├── CommentRepository.java
│   │   │   │       │   ├── CartRepository.java
│   │   │   │       │   ├── ClickLogRepository.java
│   │   │   │       │   └── SystemLogRepository.java
│   │   │   │       │
│   │   │   │       ├── service/                         # 业务逻辑层
│   │   │   │       │   ├── UserService.java           # 接口
│   │   │   │       │   ├── GoodsService.java
│   │   │   │       │   ├── OrderService.java
│   │   │   │       │   ├── CommentService.java
│   │   │   │       │   ├── CartService.java
│   │   │   │       │   ├── SystemLogService.java
│   │   │   │       │   └── impl/                      # 实现类
│   │   │   │       │       ├── UserServiceImpl.java
│   │   │   │       │       ├── GoodsServiceImpl.java
│   │   │   │       │       ├── OrderServiceImpl.java
│   │   │   │       │       ├── CommentServiceImpl.java
│   │   │   │       │       └── CartServiceImpl.java
│   │   │   │       │
│   │   │   │       └── initializer/                     # 数据初始化
│   │   │   │           └── DataInitializer.java       # 启动时初始化数据
│   │   │   │
│   │   │   └── resources/                               # 配置文件
│   │   │       └── application.properties              # Spring Boot 配置
│   │   │
│   │   └── test/                                        # 测试代码
│   │
│   └── target/                                          # 编译输出
│
├── frontend/                                   # 前端 (Vue 3)
│   ├── index.html
│   ├── package.json                            # npm 依赖配置
│   ├── vite.config.js                          # Vite 配置
│   ├── src/
│   │   ├── main.js                             # 入口文件
│   │   ├── App.vue                             # 根组件
│   │   ├── style.css                           # 全局样式
│   │   │
│   │   ├── api/                                # API 接口层
│   │   │   └── index.js                        # 统一 API 导出
│   │   │
│   │   ├── stores/                             # Pinia 状态管理
│   │   │   ├── user.js                         # 用户状态
│   │   │   └── admin.js                        # 管理员状态
│   │   │
│   │   ├── router/                             # 路由配置
│   │   │   └── index.js                        # 路由定义
│   │   │
│   │   └── views/                              # 页面组件
│   │       │
│   │       ├── Login.vue                       # 统一登录页面
│   │       ├── Register.vue                    # 注册页面
│   │       │
│   │       ├── user/                           # 用户端页面
│   │       │   ├── Layout.vue                  # 用户端布局（顶部导航）
│   │       │   ├── Home.vue                    # 首页（轮播图+推荐）
│   │       │   ├── GoodsList.vue               # 商品列表（搜索/分类）
│   │       │   ├── GoodsDetail.vue             # 商品详情（加购/下单）
│   │       │   ├── Cart.vue                    # 购物车（选品结算）
│   │       │   └── Profile.vue                 # 个人中心（订单/评论）
│   │       │
│   │       └── admin/                          # 管理端页面
│   │           ├── Layout.vue                  # 管理端布局（侧边栏）
│   │           ├── Dashboard.vue               # 数据看板（统计）
│   │           ├── GoodsManage.vue             # 商品管理（CRUD）
│   │           ├── OrdersManage.vue            # 订单管理（发货）
│   │           ├── ClickStats.vue              # 点击量统计
│   │           └── SystemLogs.vue              # 系统日志查看
│   │
│   └── dist/                                   # 打包输出
│
├── mysql/                                       # MySQL 数据库
│   └── ecommerce_admin                         # 管理员数据库
│       ├── admin                               # 管理员表
│       └── completed_order                     # 已完成订单表
│
└── mongodb/                                     # MongoDB 数据库
    └── ECommercePlatform                       # 主数据库
        ├── users                               # 用户集合
        ├── goods                               # 商品集合
        ├── orders                              # 订单集合
        ├── comments                            # 评论集合
        ├── carts                               # 购物车集合
        ├── clicks_logs                         # 点击日志集合
        └── system_logs                         # 系统日志集合

模块解析
一、后端架构 (Spring Boot)
模块	功能	核心文件
Controller	处理 HTTP 请求，返回 JSON	*Controller.java
Service	业务逻辑处理	*Service.java
Repository	数据库操作	*Repository.java
Entity	数据实体映射	*.java
Config	跨域、安全配置	CorsConfig.java
Initializer	启动数据初始化	DataInitializer.java

二、前端架构 (Vue 3)
模块	功能	核心文件
views	页面组件	*.vue
stores	全局状态管理	user.js, admin.js
api	后端接口调用	index.js
router	页面路由	index.js

三、数据库设计
数据库	用途	核心集合/表
MongoDB	主业务数据	users, goods, orders, comments, carts, clicks_logs, system_logs
MySQL	管理员+历史订单	admin, completed_order

核心功能对应关系
功能	前端页面	后端 Controller	数据库集合
用户登录/注册	Login.vue, Register.vue	UserController	users
商品浏览	GoodsList.vue, GoodsDetail.vue	GoodsController	goods
购物车	Cart.vue	CartController	carts
下单/订单	Profile.vue	OrderController	orders
评论	GoodsDetail.vue, Profile.vue	CommentController	comments
管理员登录	Login.vue (admin模式)	AdminAuthController	admin (MySQL)
商品管理	GoodsManage.vue	AdminGoodsController	goods
订单管理	OrdersManage.vue	AdminOrderController	orders
数据统计	Dashboard.vue	AdminStatsController	多个集合
点击量	ClickStats.vue	AdminClickLogController	clicks_logs
系统日志	SystemLogs.vue	AdminSystemLogController	system_logs

技术栈总结
层级	技术
后端框架	Spring Boot 3.x
数据库	MongoDB + MySQL
ORM	Spring Data MongoDB, Spring Data JPA
前端框架	Vue 3
状态管理	Pinia
UI 组件库	Element Plus
HTTP 客户端	Axios
构建工具	Maven (后端), Vite (前端)