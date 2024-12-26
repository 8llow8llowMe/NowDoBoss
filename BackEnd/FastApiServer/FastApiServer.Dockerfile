# Python 3.12 슬림 버전 이미지를 기반으로 합니다
FROM python:3.12-slim

# 시스템 패키지 업데이트 및 Java와 필요한 도구 설치
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    procps \
    build-essential \
    --no-install-recommends \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# 작업 디렉토리를 설정합니다
WORKDIR /app

# 현재 디렉토리의 내용을 컨테이너 내 /app으로 복사합니다
COPY . /app

# pip, setuptools, wheel을 업그레이드하고, requirements.txt의 의존성을 설치합니다
RUN pip install --no-cache-dir --upgrade pip setuptools wheel \
    && pip install --no-cache-dir -r requirements.txt

# 애플리케이션 실행
CMD ["uvicorn", "server:app", "--host", "0.0.0.0", "--port", "8000"]