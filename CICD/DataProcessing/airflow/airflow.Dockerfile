FROM apache/airflow:2.10.4

# 1) requirements 복사
COPY requirements-airflow.txt /tmp/requirements-airflow.txt

# 2) 해당 requirements 설치
RUN pip install --no-cache-dir -r /tmp/requirements-airflow.txt