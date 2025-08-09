# 1) 빌드 단계 (Gradle)
FROM gradle:8.7-jdk17 AS build
WORKDIR /src
COPY . .
RUN gradle clean bootJar --no-daemon

# 2) 런타임
FROM eclipse-temurin:17-jre
WORKDIR /app
# plain이 아닌 bootJar만 복사됨
COPY --from=build /src/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
