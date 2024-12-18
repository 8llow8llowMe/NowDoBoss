# 기본 이미지로 Node.js 버전 20.11.1 사용 -> 20.15.0으로 버전 업그레이드
FROM node:20.15.0 AS build

# 작업 디렉토리 설정
WORKDIR /usr/src/app

# package.json 및 package-lock.json을 복사하여 종속성 설치
COPY package*.json ./

# 종속성 설치
RUN npm install

# 나머지 애플리케이션 코드 복사
COPY . .

# 서브모듈에서 환경변수 파일 복사
COPY frontend-env/.env-frontend-dev .env

# 프론트엔드 코드 빌드
RUN npm run build