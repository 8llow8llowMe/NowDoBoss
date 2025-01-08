#!/bin/bash
#
#   Blue-Green Deploy Script (No Health Check)
#   by NowDoBoss
#
#   1) 현재 실행 중인 컨테이너 색상 파악 (blue or green)
#   2) 새 컨테이너 up (--build)
#   3) 트래픽 전환(alias or Nginx 설정)
#   4) 이전 컨테이너 stop & rm
#

# ---------------------------------------------------------
# 설정
# ---------------------------------------------------------
DOCKER_COMPOSE_FILE="docker-compose-springboot.yml"
ENV_FILE="src/main/resources/backend-env/.env-springboot"
SPRING_BOOT_PORT=8080

BLUE_CONTAINER="nowdoboss-backend-springboot-blue"
GREEN_CONTAINER="nowdoboss-backend-springboot-green"

BLUE_SERVICE="nowdoboss_springboot_blue_service"
GREEN_SERVICE="nowdoboss_springboot_green_service"

# Docker Network & Alias (Nginx에서 alias로 연결 시 사용)
NETWORK_NAME="nowdoboss-net"
ALIAS_NAME="nowdoboss-backend-springboot"

# ---------------------------------------------------------
# 현재 동작 중인 환경 파악
# ---------------------------------------------------------
if docker ps --filter "name=${BLUE_CONTAINER}" --filter "status=running" --format "{{.Names}}" | grep -q "$BLUE_CONTAINER"; then
    CURRENT_ENV="blue"
    CURRENT_CONTAINER="$BLUE_CONTAINER"
    CURRENT_SERVICE="$BLUE_SERVICE"
    NEW_ENV="green"
    NEW_CONTAINER="$GREEN_CONTAINER"
    NEW_SERVICE="$GREEN_SERVICE"
else
    CURRENT_ENV="green"
    CURRENT_CONTAINER="$GREEN_CONTAINER"
    CURRENT_SERVICE="$GREEN_SERVICE"
    NEW_ENV="blue"
    NEW_CONTAINER="$BLUE_CONTAINER"
    NEW_SERVICE="$BLUE_SERVICE"
fi

echo "============================================="
echo "[배포 스크립트 시작]"
echo " 현재 동작 중인 환경: ${CURRENT_ENV}"
echo "============================================="

# ---------------------------------------------------------
# 신규 컨테이너(BLUE->GREEN or GREEN->BLUE) 기동
# ---------------------------------------------------------
echo "[1/4] 신규 ${NEW_ENV} 컨테이너를 빌드 및 실행합니다..."
docker-compose -f "$DOCKER_COMPOSE_FILE" --env-file "$ENV_FILE" up --build -d "$NEW_SERVICE"

# ---------------------------------------------------------
# 트래픽 전환(Nginx 설정 or Alias 전환)
# ---------------------------------------------------------
echo "[2/4] 트래픽 전환을 진행합니다..."

echo "  > ${CURRENT_ENV} 컨테이너의 alias 제거..."
docker network disconnect "$NETWORK_NAME" "$CURRENT_CONTAINER" 2>/dev/null || true
docker network connect --alias "$ALIAS_NAME" "$NETWORK_NAME" "$CURRENT_CONTAINER" 2>/dev/null || true
docker network disconnect "$NETWORK_NAME" "$CURRENT_CONTAINER" 2>/dev/null || true

echo "  > 신규 ${NEW_ENV} 컨테이너에 alias 추가..."
docker network connect --alias "$ALIAS_NAME" "$NETWORK_NAME" "$NEW_CONTAINER" 2>/dev/null || true

# (만약 포트 전환 방식을 쓰신다면)
# sed -i 's/8081/8082/' /etc/nginx/conf.d/default.conf
# docker exec <nginx-container> nginx -s reload

# ---------------------------------------------------------
# 기존 컨테이너 중지 & 제거
# ---------------------------------------------------------
echo "[3/4] 기존 ${CURRENT_ENV} 컨테이너를 중지 및 제거합니다..."
docker stop "$CURRENT_CONTAINER" || true
docker rm "$CURRENT_CONTAINER" || true

# ---------------------------------------------------------
# 완료
# ---------------------------------------------------------
echo "[4/4] 무중단(?) 배포 완료: ${NEW_ENV} 컨테이너가 활성화되었습니다."
echo "============================================="
echo " [기존 환경] ${CURRENT_ENV} -> [신규 환경] ${NEW_ENV}"
echo "============================================="
exit 0
