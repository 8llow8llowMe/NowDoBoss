# nginx의 최신 이미지를 기반으로 합니다.
FROM nginx:latest

# 필요한 패키지 설치 (GeoIP2 라이브러리 및 필요한 도구)
RUN apt-get update && \
    apt-get install -y libmaxminddb0 libmaxminddb-dev mmdb-bin curl && \
    apt-get clean

# MaxMind의 GeoLite2 데이터베이스 직접 다운로드
RUN mkdir -p /usr/share/GeoIP && \
    curl -L https://geolite.maxmind.com/download/geoip/database/GeoLite2-Country.tar.gz -o GeoLite2-Country.tar.gz && \
    tar -xzvf GeoLite2-Country.tar.gz && \
    mv GeoLite2-Country.mmdb /usr/share/GeoIP/GeoLite2-Country.mmdb && \
    rm GeoLite2-Country.tar.gz

# nginx 설정 파일을 컨테이너의 적절한 위치에 복사합니다.
COPY nginx.conf /etc/nginx/conf.d/default.conf
