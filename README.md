# geekbang-lessons
极客时间课程-作业

## 运行前准备
- 需要先在手动创建数据 /db/user-platform 并创建users表

## 运行命令
- mvn clean package -U
- java -jar user-web/target/user-web-v1-SNAPSHOT-war-exec.jar

## 用户注册页面
- http://localhost:8080/register

## 查看UserMBean
- http://localhost:8080/jolokia/list/pojo-agent-user