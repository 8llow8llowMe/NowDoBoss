#!/bin/bash

DOCKER_COMPOSE_FILE="docker-compose-springboot.yml"
ENV_FILE="src/main/resources/backend-env/.env-springboot"

# 현재 실행 중인 환경 확인
CURRENT_ENV=$(docker ps --filter "name=nowdoboss-backend-springboot-blue" --filter "status=running" --format "{{.Names}}" | grep blue && echo "blue" || echo "green")

if [ "$CURRENT_ENV" == "blue" ]; then
    echo "Blue 환경 작동 중 입니다. Green 환경으로 배포하겠습니다."
    
    # Green 배포
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_green_service

    # Nginx 설정 변경
    sed -i 's/backend_blue/backend_green/' /etc/nginx/conf.d/default.conf
    nginx -s reload

    # Blue 종료
    docker stop nowdoboss-backend-springboot-blue || true
    docker rm nowdoboss-backend-springboot-blue || true

    echo "Green 환경으로 변경 완료"
else
    echo "Green 환경 작동 중 입니다. Blue 환경으로 배포하겠습니다."

    # Blue 배포
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_blue_service

    # Nginx 설정 변경
    sed -i 's/backend_green/backend_blue/' /etc/nginx/conf.d/default.conf
    nginx -s reload

    # Green 종료
    docker stop nowdoboss-backend-springboot-green || true
    docker rm nowdoboss-backend-springboot-green || true

    echo "Blue 환경으로 변경 완료"
fi
