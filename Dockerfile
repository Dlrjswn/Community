FROM openjdk:17-jdk-slim

# 컨테이너 안으로 JAR 복사
COPY app.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
