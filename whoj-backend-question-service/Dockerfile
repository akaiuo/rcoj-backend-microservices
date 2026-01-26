# 基础镜像
FROM ringcentral/jdk:17-alpine

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录
ADD target/whoj-backend-question-service-0.0.1-SNAPSHOT.jar .

# 暴露端口
EXPOSE 8102

# 启动命令
ENTRYPOINT ["java","-jar","/app/whoj-backend-question-service-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
