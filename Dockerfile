FROM gradle:7.6-jdk17 as builder
WORKDIR /app

# Gradle 관련 파일만 먼저 복사
COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY settings.gradle /app/settings.gradle
COPY build.gradle /app/build.gradle

# Gradle 캐시를 활용해 의존성 설치
RUN ./gradlew dependencies --no-daemon

# 이후 전체 소스 복사
COPY . .

# 빌드 실행
RUN ./gradlew build -x test --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY . .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

