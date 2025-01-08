#!/bin/bash

DOCKER_COMPOSE_FILE="docker-compose-springboot.yml"
ENV_FILE="src/main/resources/backend-env/.env-springboot"

# "현재 누구(Blue/Green)가 살아있는지" 간단히 판별
if docker ps --filter "name=nowdoboss-backend-springboot-blue" --filter "status=running" --format "{{.Names}}" | grep -q blue; then
    CURRENT_ENV="blue"
else
    CURRENT_ENV="green"
fi

echo "현재 동작 중인 환경: $CURRENT_ENV"

if [ "$CURRENT_ENV" == "blue" ]; then
    echo "Blue -> Green 전환을 진행합니다."

    # 1) Green 컨테이너를 빌드/실행
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_green_service

    # 2) Blue에서 alias 떼기
    # 혹시 몰라서 disconnect 시도 (이미 alias 없으면 에러무시)
    docker network disconnect nowdoboss-net nowdoboss-backend-springboot-blue || true

    # 3) Green에 alias 붙이기
    docker network connect --alias nowdoboss-backend-springboot nowdoboss-net nowdoboss-backend-springboot-green

    # 4) Blue 중지 & 제거
    docker stop nowdoboss-backend-springboot-blue || true
    docker rm nowdoboss-backend-springboot-blue || true

    echo "Green 환경 전환 완료. (alias nowdoboss-backend-springboot -> Green)"

else
    echo "Green -> Blue 전환을 진행합니다."

    # 1) Blue 컨테이너 실행
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_blue_service

    # 2) Green에서 alias 떼기
    docker network disconnect nowdoboss-net nowdoboss-backend-springboot-green || true

    # 3) Blue에 alias 붙이기
    docker network connect --alias nowdoboss-backend-springboot nowdoboss-net nowdoboss-backend-springboot-blue

    # 4) Green 중지 & 제거
    docker stop nowdoboss-backend-springboot-green || true
    docker rm nowdoboss-backend-springboot-green || true

    echo "Blue 환경 전환 완료. (alias nowdoboss-backend-springboot -> Blue)"
fi
