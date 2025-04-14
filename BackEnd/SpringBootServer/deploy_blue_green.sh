#!/bin/bash

# 설정 파일 및 환경 변수 정의
DOCKER_COMPOSE_FILE="docker-compose-springboot.yml"
ENV_FILE="src/main/resources/backend-env/.env-springboot"

# 헬스체크 함수 (사설 IP + 호스트 포트 사용) -> 추후에 SSH 연결을 이용한 다른 서버에 배포 시 사설 IP를 localhost로 변경
# 지정한 포트에서 actuator/health 엔드포인트를 검사하여 서비스가 정상적으로 실행 중인지 확인
wait_for_container_health() {
  local port=$1
  local health_url="http://192.168.0.25:${port}/actuator/health"
  local max_retries=12
  local i=1

  echo "[HEALTH CHECK] ${health_url} 에 대한 헬스체크 시작..."
  until curl -s $health_url | grep -q '"status":"UP"'; do
    if [ $i -ge $max_retries ]; then
      echo "[ERROR] 헬스체크 실패: 최대 재시도 횟수(${max_retries}) 초과. 배포 중단."
      exit 1
    fi
    echo "[HEALTH CHECK] 아직 준비되지 않음... 재시도 (${i}/${max_retries})"
    sleep 5
    ((i++))
  done
  echo "[SUCCESS] 서비스가 정상적으로 실행됨 (UP 상태 확인)."
}

# 현재 활성화된 환경(blue 또는 green) 확인
if docker ps --filter "name=nowdoboss-backend-springboot-blue" --filter "status=running" --format "{{.Names}}" | grep -q blue; then
    CURRENT_ENV="blue"
    SPRING_BOOT_INTERNAL_PORT=8082  # Green 컨테이너의 포트
else
    CURRENT_ENV="green"
    SPRING_BOOT_INTERNAL_PORT=8081  # Blue 컨테이너의 포트
fi

echo "[INFO] 현재 활성화된 환경: ${CURRENT_ENV} -> 신규 배포 환경 포트: ${SPRING_BOOT_INTERNAL_PORT}"

# Blue -> Green 전환
if [ "$CURRENT_ENV" == "blue" ]; then
    echo "[DEPLOY] Blue -> Green 전환 시작..."

    # 1) Green 컨테이너 실행 (이때 컨테이너 이름은 nowdoboss-backend-springboot-green)
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_green_service

    # 1-1) 새로운 Green 컨테이너의 헬스체크
    wait_for_container_health $SPRING_BOOT_INTERNAL_PORT

    # 2) Blue 컨테이너 중지 및 제거
    echo "[CLEANUP] 기존 Blue 컨테이너 종료 및 제거..."
    docker stop -t 30 nowdoboss-backend-springboot-blue || true
    docker rm nowdoboss-backend-springboot-blue || true
    
    # 3) 새로 띄워진 Green 컨테이너의 기존 네트워크 연결을 해제하고, alias를 사용해 도메인 이름(nowdoboss-backend-springboot)으로 네트워크에 다시 연결
    echo "[NETWORK] Green 컨테이너 네트워크 alias 변경 중..."
    docker network disconnect nowdoboss-net nowdoboss-backend-springboot-green
    docker network connect --alias nowdoboss-backend-springboot nowdoboss-net nowdoboss-backend-springboot-green

    echo "[SUCCESS] Green 환경 전환 완료! (alias nowdoboss-backend-springboot -> Green)"

# Green -> Blue 전환
else
    echo "[DEPLOY] Green -> Blue 전환 시작..."

    # 1) Blue 컨테이너 실행 (이때 컨테이너 이름은 nowdoboss-backend-springboot-blue)
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d nowdoboss_springboot_blue_service

    # 1-1) 새로운 Blue 컨테이너의 헬스체크
    wait_for_container_health $SPRING_BOOT_INTERNAL_PORT

    # 2) Green 컨테이너 중지 및 제거
    echo "[CLEANUP] 기존 Green 컨테이너 종료 및 제거..."
    docker stop -t 30 nowdoboss-backend-springboot-green || true
    docker rm nowdoboss-backend-springboot-green || true

    # 3) 새로 띄워진 Blue 컨테이너의 기존 네트워크 연결을 해제하고, alias를 사용해 도메인 이름(nowdoboss-backend-springboot)으로 네트워크에 다시 연결
    echo "[NETWORK] Blue 컨테이너 네트워크 alias 변경 중..."
    docker network disconnect nowdoboss-net nowdoboss-backend-springboot-blue
    docker network connect --alias nowdoboss-backend-springboot nowdoboss-net nowdoboss-backend-springboot-blue

    echo "[SUCCESS] Blue 환경 전환 완료! (alias nowdoboss-backend-springboot -> Blue)"
fi
