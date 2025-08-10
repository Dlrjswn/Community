# build stage
FROM gradle:8.7-jdk17 AS build
WORKDIR /src
COPY . .
RUN gradle clean bootJar --no-daemon

# BOOT-INF가 있는 '실행 가능한' 부트 JAR만 선별
RUN mkdir -p /out && \
    BOOT_JAR="$( \
      find /src -path '*/build/libs/*.jar' -type f ! -name '*-plain.jar' -print0 \
      | xargs -0 -I{} sh -c 'unzip -l "{}" | grep -q "BOOT-INF/" && echo "{}"' \
      | head -n 1 \
    )" && \
    echo "Using boot jar: $BOOT_JAR" && \
    cp "$BOOT_JAR" /out/app.jar

# run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /out/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
