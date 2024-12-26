# 공식 Python 런타임 이미지를 사용합니다
FROM python:3.12-slim

# 설치할 수 있는 Java 버전 확인
RUN apt-get update && apt-cache search openjdk

# Java와 필요한 도구를 설치합니다
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    procps \
    build-essential \
    --fix-missing \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# JAVA_HOME 환경 변수 설정
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64

# 컨테이너 내에서 작업 디렉토리를 설정합니다
WORKDIR /app

# 현재 디렉토리의 내용을 컨테이너 내의 /app 디렉토리로 복사합니다
COPY . /app

# pip, setuptools, wheel을 업그레이드합니다
RUN pip install --upgrade pip setuptools wheel

# requirements.txt에 명시된 필요한 패키지를 설치합니다
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션을 실행하는 명령어를 정의합니다
CMD ["uvicorn", "server:app", "--host", "0.0.0.0", "--port", "8000"]
