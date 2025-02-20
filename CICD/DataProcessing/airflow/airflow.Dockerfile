FROM apache/airflow:latest

# 1) 루트 권한으로 pip install 가능하도록 전환
USER root

# 2) 필요한 패키지 설치: pymongo, motor, pandas 등
RUN pip install --no-cache-dir pymongo motor pandas

# 3) 유저를 다시 airflow로 전환
USER airflow
