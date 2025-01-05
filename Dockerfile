# 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# 작업 디렉토리 생성
WORKDIR /app

# 수정된 내용
COPY build/libs/NBE2-3-2-team9-0.0.1-SNAPSHOT.jar app.jar

# 포트 설정
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
