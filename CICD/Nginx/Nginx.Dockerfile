# nginx의 최신 이미지를 기반으로 합니다.
FROM nginx:latest

# 필요한 패키지를 설치하고 GeoIP2 모듈을 추가합니다.
RUN apt-get update && \
    apt-get install -y \
    libmaxminddb0 \
    libmaxminddb-dev \
    mmdb-bin \
    wget \
    build-essential \
    zlib1g-dev \
    libpcre3 \
    libpcre3-dev \
    libssl-dev && \
    wget https://nginx.org/download/nginx-1.21.6.tar.gz && \
    tar -zxvf nginx-1.21.6.tar.gz && \
    wget https://github.com/leev/ngx_http_geoip2_module/archive/refs/heads/master.zip && \
    unzip master.zip && \
    cd nginx-1.21.6 && \
    ./configure --with-compat --add-dynamic-module=../ngx_http_geoip2_module-master && \
    make modules && \
    cp objs/ngx_http_geoip2_module.so /etc/nginx/modules/ && \
    rm -rf /var/lib/apt/lists/*

# Nginx 모듈을 로드할 수 있도록 설정 파일을 업데이트합니다.
RUN echo "load_module /etc/nginx/modules/ngx_http_geoip2_module.so;" >> /etc/nginx/nginx.conf

# nginx 설정 파일을 컨테이너의 적절한 위치에 복사합니다.
COPY nginx.conf /etc/nginx/conf.d/default.conf
