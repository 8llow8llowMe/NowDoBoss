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

# 현재 실행중인 환경 확인 (blue 또는 green)
if docker ps --filter "name=nowdoboss-backend-springboot-blue" --filter "status=running" --format "{{.Names}}" | grep -q blue; then
    CURRENT_ENV="blue"
    NEXT_PORT=8082
    NEXT_NAME="nowdoboss-backend-springboot-green"
    NEXT_SERVICE="nowdoboss_springboot_green_service"
else
    CURRENT_ENV="green"
    NEXT_PORT=8081
    NEXT_NAME="nowdoboss-backend-springboot-blue"
    NEXT_SERVICE="nowdoboss_springboot_blue_service"
fi

echo "[INFO] 현재 활성화된 환경: ${CURRENT_ENV} -> 신규 배포 환경 포트: ${NEXT_PORT}"

# 1. 신규 컨테이너 실행
docker-compose -f $DOCKER_COMPOSE_FILE --env-file $ENV_FILE up --build -d $NEXT_SERVICE

# 2. 신규 컨테이너 alias 연결 (기존과 동시에 존재 가능)
docker network connect --alias nowdoboss-backend-springboot nowdoboss-net $NEXT_NAME || true

# 3. 헬스체크 (alias 붙인 뒤 API 정상 작동 확인)
wait_for_container_health $NEXT_PORT

# 4. 기존 컨테이너 alias 제거
docker network disconnect nowdoboss-net nowdoboss-backend-springboot-${CURRENT_ENV} || true

# 5. 기존 컨테이너 종료 및 제거
docker stop -t 30 nowdoboss-backend-springboot-${CURRENT_ENV} || true
docker rm nowdoboss-backend-springboot-${CURRENT_ENV} || true

echo "[SUCCESS] ${CURRENT_ENV} -> ${NEXT_NAME##*-} 배포 완료. nowdoboss-backend-springboot alias 전환 완료."
