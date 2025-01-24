# offline_training.py

import pandas as pd
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, when, lit
from pyspark.ml.recommendation import ALS
import mongoDB  # MongoDB 접근 모듈 (동기)
from pyspark.sql.utils import AnalysisException

CSV_PATH = "hdfs://master1:9000/data/commercial_data.csv"
PARQUET_PATH = "hdfs://master1:9000/data/commercial_data.parquet"
MODEL_PATH = "hdfs://master1:9000/models/recommendation_model"

ACTION_WEIGHTS = {"click": 2, "search": 4, "analysis": 7, "save": 10}

def model_exists(spark):
    """
    모델 경로 존재 여부를 Spark로 체크.
    모델 저장 시 metadata/ 디렉토리 등이 생겨야 하므로,
    'MODEL_PATH/metadata' 등을 직접 읽어보거나
    Spark 내장 기능을 써도 됨.
    """
    try:
        spark.read.parquet(MODEL_PATH + "/metadata").limit(1).collect()
        return True
    except AnalysisException as e:
        if "Path does not exist" in str(e):
            return False
        raise e

def offline_train_job():
    """
    매일 새벽 4시에 호출되는 오프라인 학습 함수.
    별도의 SparkSession -> ALS 모델 학습 -> model.save() -> spark.stop()
    """
    print("[offline_train_job] 시작")

    spark = (
        SparkSession.builder
        .appName("OfflineTrainingJob")
        .master("spark://master1:7077")
        .config("spark.driver.memory", "1g")
        .config("spark.executor.memory", "1g")
        .config("spark.executor.cores", "1")
        .config("spark.sql.shuffle.partitions", "8")
        .config("spark.sql.broadcastTimeout", "7200")
        .getOrCreate()
    )

    # 1) CSV -> Parquet (최초 1회)
    if not path_exists(spark, PARQUET_PATH):
        print(f"[offline_train_job] Parquet 파일이 없어 CSV({CSV_PATH})에서 변환합니다.")
        df_csv = spark.read.csv(CSV_PATH, header=True, inferSchema=True)
        df_csv.write.parquet(PARQUET_PATH)
        print("[offline_train_job] CSV -> Parquet 변환 완료.")

    # 2) MongoDB에서 사용자 로그 로드
    mongo_data = mongoDB.get_mongodb_data_sync()
    if not mongo_data:
        print("[offline_train_job] MongoDB 사용자 데이터 없음. 학습 중단.")
        # spark.stop()
        return

    # 3) Spark DataFrame
    user_df = spark.createDataFrame(pd.DataFrame(mongo_data))

    # 4) 가중치 컬럼
    user_df = user_df.withColumn(
        "weight",
        when(col("action") == "click", lit(ACTION_WEIGHTS["click"]))
        .when(col("action") == "search", lit(ACTION_WEIGHTS["search"]))
        .when(col("action") == "analysis", lit(ACTION_WEIGHTS["analysis"]))
        .when(col("action") == "save", lit(ACTION_WEIGHTS["save"]))
        .otherwise(lit(1))
    ).select("userId", "commercialCode", "weight")

    print("[offline_train_job] ALS 모델 학습 시작...")
    als = ALS(
        maxIter=5,
        regParam=0.1,
        userCol="userId",
        itemCol="commercialCode",
        ratingCol="weight",
        coldStartStrategy="drop"
    )
    model = als.fit(user_df)
    print("[offline_train_job] ALS 모델 학습 완료")

    # 5) 모델 저장
    model.save(MODEL_PATH)
    print(f"[offline_train_job] 모델 저장 완료 -> {MODEL_PATH}")

    # spark.stop()
    print("[offline_train_job] 종료")

def path_exists(spark, path):
    """
    Parquet 경로 존재 여부를 체크
    """
    try:
        spark.read.parquet(path).limit(1).collect()
        return True
    except AnalysisException as ae:
        if "Path does not exist" in str(ae):
            return False
        raise ae
