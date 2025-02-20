# ./airflow/dags/offline_training_dag.py

from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from datetime import datetime, timedelta

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2025, 1, 1),
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

with DAG(
    'offline_training_dag',
    default_args=default_args,
    schedule_interval='0 4 * * *',  # 매일 새벽 4시
    catchup=False
) as dag:

    # train_model.py가 /opt/airflow/dags/ 에 위치한다고 가정
    # spark-submit 호출
    spark_submit_task = BashOperator(
        task_id='spark_submit_train_model',
        bash_command='spark-submit /opt/airflow/dags/train_model.py'
    )

    spark_submit_task
