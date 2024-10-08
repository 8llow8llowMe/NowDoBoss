# Prometheus Dockerfile (prometheus.Dockerfile)
FROM prom/prometheus:latest

# Prometheus 설정 파일을 컨테이너 내부로 복사
COPY prometheus.yml /etc/prometheus/prometheus.yml