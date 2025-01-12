# ======================================================================================
# 1) Nginx 2024-08 version + GeoIP2 모듈 빌드 단계
# ======================================================================================
ARG NGINX_VERSION=1.27.1
FROM nginx:$NGINX_VERSION AS builder

# 빌드에 필요한 도구 및 라이브러리 설치
RUN apt-get update \
    && apt-get install -y \
        build-essential \
        libpcre3-dev \
        zlib1g-dev \
        libgeoip-dev \
        libmaxminddb-dev \
        wget \
        git

# GeoIP2 모듈 다운로드 및 Nginx 빌드
RUN cd /opt \
    && git clone --depth 1 https://github.com/leev/ngx_http_geoip2_module.git \
    && wget -O - http://nginx.org/download/nginx-$NGINX_VERSION.tar.gz | tar zxfv - \
    && mv /opt/nginx-$NGINX_VERSION /opt/nginx \
    && cd /opt/nginx \
    && ./configure --with-compat --add-dynamic-module=/opt/ngx_http_geoip2_module \
    && make modules

# ======================================================================================
# 2) React (Vite+TS) 빌드 단계
# ======================================================================================
FROM node:20.15.0 AS react_builder
WORKDIR /app

# package.json, lock 파일 복사
# ==> FrontEnd/package*.json
COPY FrontEnd/package*.json ./

RUN npm install

# 소스코드 전체 복사
# ==> FrontEnd/...(src, vite.config 등)
COPY FrontEnd/ ./

# 서브모듈에서 환경변수 파일 복사
COPY FrontEnd/frontend-env/.env-react-vite-dev .env

# 프론트엔드 코드 빌드
RUN npm run build

# ======================================================================================
# 3) 최종 Nginx 이미지 생성 단계
# ======================================================================================

# 최종 이미지 - 필요 없는 빌드 도구 제거하고 경량화
FROM nginx:${NGINX_VERSION} AS final

# 빌드한 GeoIP2 모듈을 복사
COPY --from=builder /opt/nginx/objs/ngx_http_geoip2_module.so /usr/lib/nginx/modules/

# 필요한 라이브러리 설치 (GeoIP 모듈 동작 위해)
RUN apt-get update \
    && apt-get install -y --no-install-recommends libmaxminddb0 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && chmod -R 644 /usr/lib/nginx/modules/ngx_http_geoip2_module.so

# GeoIP2 모듈 로드 설정 추가
RUN sed -i '1iload_module /usr/lib/nginx/modules/ngx_http_geoip2_module.so;' /etc/nginx/nginx.conf

# Vite 빌드 결과물(dist)을 /usr/share/nginx/html 에 복사
COPY --from=react_builder /app/dist /usr/share/nginx/html

# nginx 설정 파일을 컨테이너의 적절한 위치에 복사
COPY nginx.conf /etc/nginx/nginx.conf
COPY default.conf /etc/nginx/conf.d/default.conf