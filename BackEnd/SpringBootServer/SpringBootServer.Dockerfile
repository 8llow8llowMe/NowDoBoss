# OpenJDK 17 이미지를 베이스로 사용
FROM openjdk:17-jdk-slim

# 애플리케이션을 빌드할 소스 코드 및 리소스 복사
COPY . /app

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper에 실행 권한 부여
RUN chmod +x ./gradlew

# Spring Boot 애플리케이션 빌드
RUN ./gradlew clean bootJar

# JAR 파일을 /app 디렉토리로 복사
RUN cp build/libs/*.jar /app/app.jar

# 로그를 기록할 디렉토리 생성
RUN mkdir -p /app/logs

# 로그 디렉토리에 쓰기 권한 부여
RUN chmod -R 777 /app/logs

RUN mkdir /app/videos

# Spring Boot 애플리케이션 실행을 위한 명령 설정
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/app/app.jar"]
