# 远程调试指导说明
## 使用VPN连接测试环境进行联调
1. 提交代码后，登录Jenkins进行相关子模块的发布
Jenkins地址：http://10.21.33.235:18080/jenkins/
用户名/口令: admin/admin!@#$
2. 指定对应的子模块进行构建发布，如下图所示
![Jenkins](http://img.51bluecoffee.net/Jenkins.png)
3. 发布后检查对应模块的服务在NACOS是否正常
![nacos](http://img.51bluecoffee.net/nacos.png)
4. 访问Swagger查看服务并验证
   Swagger地址为：http://10.21.33.235/doc.html#/home
   前端联调地址为：http://10.21.33.235/
5. 登录服务器查看日志
服务器IP: 10.21.33.235
用户名/口令: smartenv/smartenv123 
所有服务都是使用Docker部署，因此需要使用Docker命令来查看日志

```
#查看docker容器
docker ps 
```
   
 ![docker-ps](http://img.51bluecoffee.net/docker-ps.png)
 
```
#查看容器日志
docker logs -f --tail 100 ${容器ID或容器名字}
```
![docker-log](http://img.51bluecoffee.net/docker-log.png)

## 使用NATAPP进行联调
当前后端开发人员希望点对点Debug时(即后端开发人员希望前端调用自己本地服务进行联调)，可使用该方法，该方法本质上是做了一个内网穿透，将自己的服务地址暴露在公网上供别人调用
NATAPP官网地址：https://natapp.cn/
大家可以根据自己的需要选择不同的套餐进行调试，建议选择VIP-1型，因为可以有一个固定的域名，免费通道每次重启后域名都会变
![natapp](http://img.51bluecoffee.net/natapp.png)
具体如何使用可查看[官方教程及文档](https://natapp.cn/article)
![natapp-guide](http://img.51bluecoffee.net/natapp-guide.png)


   


 