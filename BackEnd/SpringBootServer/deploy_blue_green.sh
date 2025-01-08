#!/bin/bash

DOCKER_COMPOSE_FILE="docker-compose-springboot.yml"
ENV_FILE="src/main/resources/backend-env/.env-springboot"

# 현재 활성화된 환경 확인
if docker ps --filter "name=nowdoboss-backend-springboot-blue" --filter "status=running" --format "{{.Names}}" | grep -q blue; then
    CURRENT_ENV="blue"
else
    CURRENT_ENV="green"
fi

echo "현재 동작 중인 환경: $CURRENT_ENV"

if [ "$CURRENT_ENV" == "blue" ]; then
    echo "Blue -> Green 전환을 진행합니다."

    # 1) Green 컨테이너 실행
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_green_service

    # 2) 기존 Blue alias 해제 및 Green alias 연결
    echo "Green 컨테이너에 네트워크 alias 연결 중..."
    docker network disconnect nowdoboss-net nowdoboss-backend-springboot-blue || true
    
    # 3) 새로 띄워진 Green 컨테이너의 기존 네트워크 연결을 해제하고, alias를 사용해 도메인 이름(nowdoboss-backend-springboot)으로 네트워크에 다시 연결
    docker network disconnect nowdoboss-net nowdoboss-backend-springboot-green
    docker network connect --alias nowdoboss-backend-springboot nowdoboss-net nowdoboss-backend-springboot-green

    # 4) Blue 컨테이너 중지 및 제거
    docker stop nowdoboss-backend-springboot-blue || true
    docker rm nowdoboss-backend-springboot-blue || true

    echo "Green 환경 전환 완료. (alias nowdoboss-backend-springboot -> Green)"

else
    echo "Green -> Blue 전환을 진행합니다."

    # 1) Blue 컨테이너 실행
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_blue_service

    # 2) 기존 Green alias 해제 및 Blue alias 연결
    echo "Blue 컨테이너에 네트워크 alias 연결 중..."
    docker network disconnect nowdoboss-net nowdoboss-backend-springboot-green || true

    # 3) 새로 띄워진 Blue 컨테이너의 기존 네트워크 연결을 해제하고, alias를 사용해 도메인 이름(nowdoboss-backend-springboot)으로 네트워크에 다시 연결
    docker network disconnect nowdoboss-net nowdoboss-backend-springboot-blue
    docker network connect --alias nowdoboss-backend-springboot nowdoboss-net nowdoboss-backend-springboot-blue

    # 3) Green 컨테이너 중지 및 제거
    docker stop nowdoboss-backend-springboot-green || true
    docker rm nowdoboss-backend-springboot-green || true

    echo "Blue 환경 전환 완료. (alias nowdoboss-backend-springboot -> Blue)"
fi
