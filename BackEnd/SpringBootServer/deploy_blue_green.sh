#!/bin/bash

DOCKER_COMPOSE_FILE="docker-compose-springboot.yml"
ENV_FILE="src/main/resources/backend-env/.env-springboot"

# 헬스체크 함수: 컨테이너 이름과 포트를 받아서 /actuator/health를 호출
wait_for_container_health() {
  local container_name=$1
  local port=$2
  local health_url="http://${container_name}:${port}/actuator/health"
  local max_retries=36
  local i=1

  echo "[$container_name] 헬스체크를 시작합니다... ($health_url)"
  until curl -s $health_url | grep -q '"status":"UP"'; do
    if [ $i -ge $max_retries ]; then
      echo "[$container_name] 헬스체크 실패: 최대 재시도 횟수($max_retries) 초과."
      exit 1
    fi
    echo "[$container_name] 아직 준비되지 않았습니다. 재시도 $i/$max_retries..."
    sleep 5
    ((i++))
  done
  echo "[$container_name] 헬스체크 성공: 상태 UP."
}

# 현재 활성화된 환경 확인
if docker ps --filter "name=nowdoboss-backend-springboot-blue" --filter "status=running" --format "{{.Names}}" | grep -q blue; then
    CURRENT_ENV="blue"
else
    CURRENT_ENV="green"
fi

echo "현재 동작 중인 환경: $CURRENT_ENV"

# 기본 Spring Boot 내부 포트
SPRING_BOOT_INTERNAL_PORT=8080

if [ "$CURRENT_ENV" == "blue" ]; then
    echo "Blue -> Green 전환을 진행합니다."

    # 1) Green 컨테이너 실행 (이때 컨테이너 이름은 nowdoboss-backend-springboot-green)
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_green_service

    # 1-1) 새로운 Green 컨테이너가 완전히 기동할 때까지 헬스체크
    wait_for_container_health "nowdoboss-backend-springboot-green" $SPRING_BOOT_INTERNAL_PORT

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

    # 1) Blue 컨테이너 실행 (이때 컨테이너 이름은 nowdoboss-backend-springboot-blue)
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_blue_service

    # 1-1) 새로운 Blue 컨테이너의 헬스체크
    wait_for_container_health "nowdoboss-backend-springboot-blue" $SPRING_BOOT_INTERNAL_PORT

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
