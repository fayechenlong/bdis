# Bdis

#### 一个分布式缓存中间件，编写的语言java,运行环境JDK1.8以上，完全兼容redis协议，可以独立运行也可以借助于redis集群运行

#### 架构图

![image](https://github.com/fayechenlong/bdis/blob/master/img/bdis-arc-v1.0.png)

## 一、系统模块
 
###1. bdis-server

* bdis主服务
    
    （1）单例代理模式（完成）
    
    （2）集群代理模式（未完成）
    
    （3）独立模式（未完成）
    
###2. bdis-admin

* bdis后台管理界面（未开始）
  
###3. 高可用

  *未开始*
 
###4. bcache

  * bdis内部缓存模块，采用堆内内存+堆外内存+持久化混合存储，也用于代理模式的一级缓存，基于[ehcache3](http://www.ehcache.org)开发

##二、系统配置
 
   * bdis.properties 是服务配置文件

   * log4j.properties 是日志输出配置文件
    
###1.bdis服务端口号
  
   * bdis.port=6999  #端口号自定义
 
###2.bdis启动模式
 
   * bdis.model=single  #单机代理模式  
   
   * bdis.model=cluster #集群代理模式
   
   * bdis.model=bcache #独立运行模式 ,用自有bcache作为存储
        
###3.配置单机代理模式
   
   * bdis.model=single  #单机代理模式
   
   * bdis.single.redis.host= 127.0.0.1  #redis地址
   
   * bdis.single.redis.port=6379  #端口号
   
   * 例：
     
         bdis.port=6789
         bdis.model=single
         bdis.single.redis.host=172.16.249.72
         bdis.single.redis.port=6379
   
###4.配置的集群代理模式

   * bdis.model=cluster #集群代理模式
   
   * bdis.cluster.redis.hosts= #集群节点
   
   *如果配置的集群代理模式，这个配置填写redis集群地址，每个实例用逗号隔开*
   
   *例：
   
         bdis.port=6789
         bdis.model=cluster
         bdis.cluster.redis.hosts=172.16.250.91:7000,172.16.250.91:7001,172.16.250.91:7002,172.16.250.91:7003,172.16.250.91:7004,172.16.250.91:7005
          
###5.配置独立运行模式

   * bdis.model=bcache #独立运行模式,用自有bcache作为存储
   
   * bdis.bcache.heapSize=100 #设置堆内内存大小,单位M
   
   * bdis.bcache.offheapSize=100 #设置堆外内存大小,单位M
   
   * 例：
   
         bdis.port=6789
         bdis.model=bcache
         bdis.bcache.heapSize=100
         bdis.bcache.offheapSize=100
   
##三、启动方式

* main函数在com.beeplay.bdis.server.BdisServerStart

* mvn clean install 打包后   

  命令："java -jar bdis-server-1.0-SNAPSHOT-jar-with-dependencies.jar"  
