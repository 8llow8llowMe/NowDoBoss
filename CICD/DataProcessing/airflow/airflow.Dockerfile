FROM apache/airflow:latest

# 1) 유지: Airflow 공식 이미지는 기본적으로 'airflow' 유저
USER airflow

# 2) 환경변수: ~/.local/bin 등을 PATH에 추가
ENV PATH="/home/airflow/.local/bin:${PATH}"

# 3) requirements 파일 복사
COPY requirements-airflow.txt /tmp/requirements-airflow.txt

# 4) pip install --user
#    => home 디렉터리에 설치 (airflow 유저 권장)
RUN pip install --no-cache-dir --user -r /tmp/requirements-airflow.txt
