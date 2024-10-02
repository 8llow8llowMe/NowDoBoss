# 빌드용 이미지 - Nginx 최신 버전 사용
FROM nginx:latest as builder

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
    && wget -O - http://nginx.org/download/nginx.tar.gz | tar zxfv - \
    && mv /opt/nginx* /opt/nginx \
    && cd /opt/nginx \
    && ./configure --with-compat --add-dynamic-module=/opt/ngx_http_geoip2_module \
    && make modules

# 최종 이미지 - 필요 없는 빌드 도구 제거하고 경량화
FROM nginx:latest

# 빌드한 GeoIP2 모듈을 복사
COPY --from=builder /opt/nginx/objs/ngx_http_geoip2_module.so /usr/lib/nginx/modules/

# 필요한 라이브러리 설치
RUN apt-get update \
    && apt-get install -y --no-install-recommends libmaxminddb0 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && chmod -R 644 /usr/lib/nginx/modules/ngx_http_geoip2_module.so

# GeoIP2 모듈 로드 설정 추가
RUN sed -i '1iload_module /usr/lib/nginx/modules/ngx_http_geoip2_module.so;' /etc/nginx/nginx.conf

# nginx 설정 파일을 컨테이너의 적절한 위치에 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf
