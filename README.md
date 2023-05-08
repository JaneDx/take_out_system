# 外卖点餐系统

本项目是专门为餐饮企业（餐厅、饭店）定制的一款软件产品，包括系统管理后台和移动端应用两部分。其中系统管理后台主要提供给餐饮企业内部员工使用，可以对餐厅的分类、菜品、套餐、订单、员工等进行管理维护。移动端应用主要提供给消费者使用，可以在线浏览菜品、添加购物车、下单等。

- 后台管理：http://localhost:8080/backend/index.html

  ![1](images\1.png)

  ![1](images\3.png)

- 前端页面：http://localhost:8080/front/index.html

  ![2](images\2.png)



## 功能介绍

- 后台管理

  餐饮企业内部员工使用，主要功能有:

  | 模块      | 描述                                                         |
  | --------- | ------------------------------------------------------------ |
  | 登录/退出 | 内部员工通过账号密码登录后,才可以访问系统管理后台            |
  | 员工管理  | 管理员可以在系统后台对员工信息进行管理，包含查询、新增、编辑、禁用等功能 |
  | 分类管理  | 主要对当前餐厅经营的 菜品分类 或 套餐分类 进行管理维护， 包含查询、新增、修改、删除等功能 |
  | 菜品管理  | 主要维护各个分类下的菜品信息，包含查询、新增、修改、删除、启售、停售等功能 |
  | 套餐管理  | 主要维护当前餐厅中的套餐信息，包含查询、新增、修改、删除、启售、停售等功能 |
  | 订单明细  | 主要维护用户在移动端下的订单信息，包含查询、取消、派送、完成，以及订单报表下载等功能 |

- 移动端

  移动端应用主要提供给消费者使用，主要功能有:

  | 模块        | 描述                                                         |
  | ----------- | ------------------------------------------------------------ |
  | 登录/退出   | 在移动端, 用户需要通过手机号验证码登录后使用APP进行点餐      |
  | 点餐-菜单   | 在点餐界面展示出菜品分类/套餐分类, 并根据当前选择的分类加载其中的菜品信息, 供用户查询选择 |
  | 点餐-购物车 | 用户可以将选中的菜品及其口味加入购物车, 主要包含 查询购物车、加入购物车、删除购物车、清空购物车等功能 |
  | 订单支付    | 用户选完菜品/套餐后, 对购物车菜品进行结算支付                |
  | 个人信息    | 在个人中心页面中展示当前用户的基本信息, 用户可以管理收货地址, 也可以查询历史订单数据 |



## 技术栈

- **应用层**
  - SpringBoot： 快速构建 Spring 项目, 采用 "约定优于配置" 的思想, 简化 Spring 项目的配置开发。
  - Spring：统一管理项目中的各种资源 ( bean )，在 web 开发的各层中都会用到。
  - SpringMVC：SpringMVC是 Spring 框架的一个模块，SpringMVC 和 Spring 无需通过中间整合层进行整合，可以无缝集成。
  - lombok：能以简单的注解形式来简化代码，提高开发人员的开发效率。例如开发中经常需要写的javabean，都需要花时间去添加相应的 getter/setter，也许还要去写构造器、equals 等方法。
  - Swagger： 可以自动的帮助开发人员生成接口文档，并对接口进行测试。
- **数据层**
  - MySQL： 关系型数据库，本项目的核心业务数据都会采用 MySQL 进行存储。
  - MybatisPlus： 本项目持久层将使用 MybatisPlus 来简化开发，基本的单表增删改查直接调用框架提供的方法。
  - Redis： 基于 key-value 格式存储的内存数据库， 访问速度快，本项目使用 Redis 做缓存，降低数据库访问压力, 提供访问效率。

- **工具**
  - git：版本控制工具，使用该工具对项目中的代码进行管理。
  - maven： 项目构建工具。
  - junit：单元测试工具，通过junit对功能进行单元测试。