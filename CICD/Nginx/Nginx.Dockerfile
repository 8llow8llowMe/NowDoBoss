# nginx의 최신 이미지를 기반으로 합니다.
FROM nginx:latest

# 필요한 패키지 설치 (GeoIP2 라이브러리 및 업데이트 도구 설치)
RUN apt-get update && \
    apt-get install -y libmaxminddb0 libmaxminddb-dev mmdb-bin geoipupdate && \
    apt-get clean

# MaxMind 라이센스 키 설정 (GeoIP.conf 파일을 설정)
COPY GeoIP.conf /etc/GeoIP.conf

# GeoIP 업데이트 실행 (라이센스 키 기반으로 자동 다운로드)
RUN geoipupdate

# nginx 설정 파일을 컨테이너의 적절한 위치에 복사합니다.
COPY nginx.conf /etc/nginx/conf.d/default.conf