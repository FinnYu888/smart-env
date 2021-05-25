# 智慧环卫应用后台

## 工程结构

``` 
smartenv-app-platform
├── blade-common -- 框架全局配置
├── smartenv-common -- 工程全局常量,工具类等
├── smartenv-datascope -- 接口权限管理
├── smartenv-gateway -- API网关(80)
├── smartenv-auth -- 授权管理(8100)
├── smartenv-alarm -- 告警管理(8101)
├── smartenv-user -- 操作员管理(8102)
├── smartenv-log -- 日志管理(8103)
├── smartenv-statistic -- 统计分析(8104)
├── smartenv-inventory -- 库存管理(8105)
├── smartenv-system -- 系统管理(8106)
├    ├── smartenv-system-api -- 系统管理API 
├    ├── smartenv-system-servie -- 系统管理业务实现 
├── smartenv-event -- 事件管理(8107)
├── smartenv-device -- 设备(传感器)管理(8108)
├── smartenv-workarea -- 工作区域管理(8109)
├── smartenv-facility -- 设施管理(8110)
├── smartenv-oss -- 分布式对象存储(8111)
├── smartenv-assessment -- 考核管理(8112)
├── smartenv-omnic -- 集成域(8113)
├── smartenv-person -- 人员管理(8114)
├── smartenv-arrange -- 排班管理(8115)
├── smartenv-vehicle -- 车辆管理(8116)
├── smartenv-cache -- 缓存管理(8118)
├── smartenv-websocket -- Websocket(8117)
├── smartenv-job -- Job(8122)
├── smartenv-wechat -- wechat(8123)
├── smartenv-pushc -- pushc(8124)
└── smartenv-flow -- 流程引擎(8120)

```

## 相关文档
* [开发指导手册：http://10.21.14.199:3001/springblade](http://10.21.14.199:3001/springblade)
* [低保真地址：http://10.21.14.199:3001/ue/smartenv-web](http://10.21.14.199:3001/ue/smartenv-web)
* [高保真地址：http://10.21.14.199:3001/ui/smartenv-web/](http://10.21.14.199:3001/ui/smartenv-web/)
* [SVN文档地址：http://10.21.20.166:8080/nj/apac_smartenv/DOC/](http://10.21.20.166:8080/nj/apac_smartenv/DOC)
* [后台管理访问地址：http://10.21.33.235:28201](http://10.21.33.235:28201)
* [前后端远程联调指导说明](http://10.21.14.155/apac-smartenv/smartenv-app-platform/blob/develop/doc/guide/debug-guide.md)
* [小程序低保真](http://10.21.14.199:3001/ue/smartenv-mobile/#g=1&p=%E6%88%91%E7%9A%84%E7%94%B3%E8%AF%B7-%E8%AF%A6%E6%83%85)

## RESTFUL规范
### 除了框架原有的功能外，我们的业务功能开发时不要在请求URL中加上具体的动作,如save、update、delete，而是使用HTTP METHOD来区分，示例如下
```
http://127.0.0.1/user/1 GET  根据用户id查询用户数据
http://127.0.0.1/user?userId=1&status=1  GET 根据多种条件查询用户数据
http://127.0.0.1/user?userId=1&status=1&current=1&size=10  GET 根据多种条件分页查询用户数据
http://127.0.0.1/user  POST 新增用户
http://127.0.0.1/user  PUT 修改用户信息
http://127.0.0.1/user  DELETE 删除用户信息
```

![restful](http://img.51bluecoffee.net/restful.png)

### HTTP相应状态码
![httpcode](http://img.51bluecoffee.net/httpcode.png)


###  环境信息
#### 开发环境

+ mysql:
 ```yaml
  datasource:
    dev:
      # MySql
      url: jdbc:mysql://10.21.33.235:3306/smartenv?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&tinyInt1isBit=false&allowMultiQueries=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
      username: root
      password: 123456
    log:
      dev:
        # MySql
        url: jdbc:mysql://10.21.33.235:3306/smartenv_log?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&tinyInt1isBit=false&allowMultiQueries=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
        username: root
        password: 123456
```  
+ DB

```yaml
 mongodb:
      host: 10.21.33.235
      port: 27017
      database: smartenv
      password:
      user:

```

+ jenkins  
地址：
[http://10.21.33.235:18080/jenkins/](http://10.21.33.235:18080/jenkins/)   
用户名： admin   
密码：  admin!@#$

+ nacos   
[http://10.21.33.235:8848/nacos/#/login](http://10.21.33.235:8848/nacos/#/login)  
用户名： nacos   
密码：nacos

+ doc  
[http://10.21.33.235/doc.html#/home](http://10.21.33.235/doc.html#/home)

    




#### 生产环境


 ```yaml
  datasource:
    dev:
      # MySql
      url: jdbc:mysql://39.98.127.65:31306/smartenv?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&tinyInt1isBit=false&allowMultiQueries=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
      username: smartenv
      password: smartenv@123
```  


