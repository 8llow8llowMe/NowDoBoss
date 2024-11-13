# 빌드 환경 설정 단계
FROM ibm-semeru-runtimes:open-17-jre-jammy

ARG JAR_FILE=./build/libs/*.jar

# 빌드된 JAR 파일 복사 (빌드 아티팩트를 CI/CD 빌드 단계에서 제공)
COPY ${JAR_FILE} /app/app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/app/app.jar"]

