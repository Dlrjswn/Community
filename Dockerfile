# build stage
FROM gradle:8.7-jdk17 AS build
WORKDIR /src
COPY . .
RUN gradle clean bootJar --no-daemon

# 부트 JAR만 집어서 표준 이름으로 복사 (plain 제외, 스냅샷/릴리스 모두 OK)
RUN mkdir -p /out && \
    BOOT_JAR="$(find /src -path '*/build/libs/*.jar' -type f ! -name '*-plain.jar' | head -n 1)" && \
    echo "Using boot jar: $BOOT_JAR" && \
    cp "$BOOT_JAR" /out/app.jar

# run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /out/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
