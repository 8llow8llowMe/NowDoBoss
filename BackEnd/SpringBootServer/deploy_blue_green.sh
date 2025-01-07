#!/bin/bash

DOCKER_COMPOSE_FILE="docker-compose-springboot.yml"
ENV_FILE="src/main/resources/backend-env/.env-springboot"

# 현재 실행 중인 환경 확인
CURRENT_ENV=$(docker ps --filter "name=nowdoboss-backend-springboot-blue" --filter "status=running" --format "{{.Names}}" | grep blue && echo "blue" || echo "green")

if [ "$CURRENT_ENV" == "blue" ]; then
    echo "Blue 환경 작동 중입니다. Green 환경으로 배포합니다."

    # Green 배포
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_green_service

    echo "Green 환경으로 배포 완료. 기존 Blue 환경 종료 중..."
    
    # Blue 환경 종료
    docker stop nowdoboss-backend-springboot-blue || true
    docker rm nowdoboss-backend-springboot-blue || true

    echo "Blue 환경 종료 완료."
else
    echo "Green 환경 작동 중입니다. Blue 환경으로 배포합니다."

    # Blue 배포
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_blue_service


    echo "Blue 환경으로 배포 완료. 기존 Green 환경 종료 중..."
    
    # Green 환경 종료
    docker stop nowdoboss-backend-springboot-green || true
    docker rm nowdoboss-backend-springboot-green || true

    echo "Green 환경 종료 완료."
fi
