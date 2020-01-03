# Bdis

#### 一个分布式缓存中间件，编写的语言java,运行环境JDK1.8以上，完全兼容redis协议，可以独立运行也可以借助于redis集群运行

#### 架构图

![image](https://github.com/fayechenlong/bdis/blob/master/img/bdis-arc-v1.0.png)

 一、系统模块
1. bdis-server

* bdis主服务
    
    （1）单例模式（完成）
    
    （2）集群模式（未完成）
    
    （3）主从模式（未开始）
    
2. bdis-admin

* bdis后台管理界面（未开始）
  
3. 高可用

     未开始

二、系统配置
 
    bdis.properties 是服务配置文件
    log4j.properties 是日志输出配置文件
    
* bdis.port
  
    服务端口号
 
 * bdis.model
 
   bdis启动模式
   
   bdis.model=single  单机代理模式  
   
   bdis.model=cluster 集群代理模式
        
* bdis.single.redis.host
   
   配置单机代理模式
   
   bdis.single.redis.host=redis地址
   
   bdis.single.redis.port=端口号
   
   bdis.single.redis.auth=密码

* bdis.cluster.redis.hosts

   如果配置的集群代理模式，这个配置填写redis集群地址，每个实例用逗号隔开。
   
   例如：bdis.cluster.redis.hosts=172.16.250.91:7000,172.16.250.91:7001,172.16.250.91:7002,172.16.250.91:7003,172.16.250.91:7004,172.16.250.91:7005
   
三、启动方式

* main函数在com.beeplay.bdis.server.BdisServerStart

* mvn clean install 打包后   

  命令："java -jar bdis-server-1.0-SNAPSHOT-jar-with-dependencies.jar"  
