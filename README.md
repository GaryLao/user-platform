# geekbang-lessons
极客时间课程-作业

## 运行前准备
- 需要先在手动创建数据 /db/user-platform 并创建users表

## 运行命令
- mvn clean package -U
- java -jar user-web/target/user-web-v1-SNAPSHOT-war-exec.jar
---
## 抽象 API 实现对象的序列化和反序列化
- org.geektimes.cache.util.SerializationObjUtil
## 通过 Lettuce 实现一套 Redis CacheManager 以及 Cache
- org.geektimes.cache.redis.LettuceCacheManager
- org.geektimes.cache.redis.LettuceCache
---
## 通过 ServletContext 获取 Config 数据的类
- org.geektimes.web.mvc.FrontControllerServlet.service

## 在 doFilter 通过 ThreadLocal 把 Config 传递配置到 jsp
- org.geektimes.projects.user.web.filter.CharsetEncodingFilter.doFilter
- http://localhost:8080/
---
## 查看UserMBean
- http://localhost:8080/jolokia/list/pojo-agent-user
---
## 用户注册页面
- http://localhost:8080/register

