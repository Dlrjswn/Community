# 베이스 이미지 (Java 17 환경)
FROM openjdk:17-jdk-slim

# 빌드된 JAR 파일 경로 (build/libs/*.jar)
ARG JAR_FILE=build/libs/*.jar

# 복사해서 컨테이너 안에 app.jar로 저장
COPY ${JAR_FILE} app.jar

# 앱 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]