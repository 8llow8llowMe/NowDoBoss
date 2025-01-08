#!/bin/bash
#
#   Blue-Green Deploy Script
#   by NowDoBoss
#
#   1) 현재 실행 중인 컨테이너 색상 파악 (blue or green)
#   2) 새 컨테이너 up (--build)
#   3) Health Check
#   4) 성공 시 Nginx(또는 alias) 전환
#   5) 이전 컨테이너 stop & rm
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

# Health Check 설정
HEALTH_ENDPOINT="/actuator/health"   # Spring Boot 기준
HEALTH_MAX_RETRIES=10
HEALTH_INTERVAL=5   # 초

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
echo "[1/5] 신규 ${NEW_ENV} 컨테이너를 빌드 및 실행합니다..."
docker-compose -f "$DOCKER_COMPOSE_FILE" --env-file "$ENV_FILE" up --build -d "$NEW_SERVICE"

# ---------------------------------------------------------
# Health Check (신규 컨테이너)
# ---------------------------------------------------------
echo "[2/5] 신규 ${NEW_ENV} 컨테이너 Health Check를 진행합니다..."

RETRY_COUNT=0
CHECK_SUCCESS=false

while [ $RETRY_COUNT -lt $HEALTH_MAX_RETRIES ]
do
  # docker-compose exec [서비스명] curl -f http://localhost:${SPRING_BOOT_PORT}/actuator/health
  docker-compose -f "$DOCKER_COMPOSE_FILE" exec -T "$NEW_SERVICE" \
    curl -sf "http://localhost:${SPRING_BOOT_PORT}${HEALTH_ENDPOINT}" >/dev/null 2>&1

  if [ $? -eq 0 ]; then
    CHECK_SUCCESS=true
    echo "  > 성공: ${NEW_ENV} 컨테이너 정상 동작 확인!"
    break
  else
    echo "  > [재시도: $((RETRY_COUNT+1))/${HEALTH_MAX_RETRIES}] 아직 준비되지 않음..."
    ((RETRY_COUNT++))
    sleep $HEALTH_INTERVAL
  fi
done

if [ "$CHECK_SUCCESS" != "true" ]; then
  echo "  > 실패: ${NEW_ENV} 컨테이너가 Health Check에 통과하지 못했습니다."
  echo "    기존 환경(${CURRENT_ENV})을 유지하고 ${NEW_ENV} 컨테이너는 중단합니다."
  docker stop "$NEW_CONTAINER" || true
  docker rm "$NEW_CONTAINER" || true
  exit 1
fi

# ---------------------------------------------------------
# (선택) Nginx / Network alias 전환
# ---------------------------------------------------------
echo "[3/5] 트래픽 전환을 위해 alias/Nginx 설정을 변경합니다..."

# 3-1) Docker Network Alias 전환 (예시)
#     - Blue에서 alias(nowdoboss-backend-springboot) 제거,
#       Green에 alias 부여 (or 반대)
NETWORK_NAME="nowdoboss-net"
ALIAS_NAME="nowdoboss-backend-springboot"

# 우선 현재 컨테이너(CURRENT_CONTAINER)에서 alias 제거
echo "  > ${CURRENT_ENV} 컨테이너의 alias 제거..."
docker network disconnect "$NETWORK_NAME" "$CURRENT_CONTAINER" 2>/dev/null || true
docker network connect --alias "$ALIAS_NAME" "$NETWORK_NAME" "$CURRENT_CONTAINER" 2>/dev/null || true
docker network disconnect "$NETWORK_NAME" "$CURRENT_CONTAINER" 2>/dev/null || true

# 신규 컨테이너(NEW_CONTAINER)에 alias 부여
echo "  > 신규 ${NEW_ENV} 컨테이너에 alias 추가..."
docker network connect --alias "$ALIAS_NAME" "$NETWORK_NAME" "$NEW_CONTAINER" 2>/dev/null || true

# 3-2) 혹은 Nginx 설정 파일 치환 & reload (예시)
# sed -i 's/8081/8082/' /etc/nginx/conf.d/default.conf
# docker exec <nginx-container> nginx -s reload

# ---------------------------------------------------------
# 이전 컨테이너 중지 & 제거
# ---------------------------------------------------------
echo "[4/5] 기존 ${CURRENT_ENV} 컨테이너를 중지 및 제거합니다..."
docker stop "$CURRENT_CONTAINER" || true
docker rm "$CURRENT_CONTAINER" || true

# ---------------------------------------------------------
# 완료
# ---------------------------------------------------------
echo "[5/5] 무중단 배포 완료: ${NEW_ENV} 컨테이너가 활성화되었습니다."
echo "============================================="
echo " [기존 환경] ${CURRENT_ENV} -> [신규 환경] ${NEW_ENV}"
echo "============================================="
exit 0
