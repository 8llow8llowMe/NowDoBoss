FROM apache/airflow:2.10.4

# 1) root 권한 허용 환경 변수 설정
ENV AIRFLOW_CONTAINER_RUNAS_ROOT=1

# 2) root로 변경 (이미 기본 user는 airflow지만, 이 ENV로 root 사용이 허용됨)
USER root

# 3) requirements 복사
COPY requirements-airflow.txt /tmp/requirements-airflow.txt

# 4) --user 옵션 없이, 전역 설치
RUN pip install --no-cache-dir -r /tmp/requirements-airflow.txt

# 5) 다시 airflow 유저로 전환 (보안상 권장)
USER airflow