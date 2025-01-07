#!/bin/bash

DOCKER_COMPOSE_FILE="docker-compose-springboot.yml"
ENV_FILE="src/main/resources/backend-env/.env-springboot"

# 현재 실행 중인 환경 확인
if docker ps --filter "name=nowdoboss-backend-springboot-blue" --filter "status=running" --format "{{.Names}}" | grep -q blue; then
    CURRENT_ENV="blue"
else
    CURRENT_ENV="green"
fi

if [ "$CURRENT_ENV" == "blue" ]; then
    echo "현재 Blue 환경이 작동 중입니다. Green 환경으로 전환합니다."

    # Green 환경 배포
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_green_service

    # Blue 환경 중지
    docker stop nowdoboss-backend-springboot-blue || true
    docker rm nowdoboss-backend-springboot-blue || true

    echo "Green 환경 배포 및 Nginx 설정 업데이트 완료."
else
    echo "현재 Green 환경이 작동 중입니다. Blue 환경으로 전환합니다."

    # Blue 환경 배포
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_blue_service

    # Green 환경 중지
    docker stop nowdoboss-backend-springboot-green || true
    docker rm nowdoboss-backend-springboot-green || true

    echo "Blue 환경 배포 및 Nginx 설정 업데이트 완료."
fi
