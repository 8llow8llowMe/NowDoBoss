# nginx의 최신 이미지를 기반으로 합니다.
FROM nginx:latest

# nginx 설정 파일을 컨테이너의 적절한 위치에 복사합니다.
COPY nginx.conf /etc/nginx/conf.d/default.conf