# offline_training.py

import pandas as pd
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, when, lit
from pyspark.ml.recommendation import ALS
import mongoDB  # MongoDB 접근 모듈 (동기 가정)
from pyspark.sql.utils import AnalysisException

CSV_PATH = "hdfs://master1:9000/data/commercial_data.csv"
PARQUET_PATH = "hdfs://master1:9000/data/commercial_data.parquet"
MODEL_PATH = "hdfs://master1:9000/models/recommendation_model"

ACTION_WEIGHTS = {"click": 2, "search": 4, "analysis": 7, "save": 10}

def offline_train_job():
    """
    APScheduler가 호출할 학습 함수.
    매일 새벽 4시에 돌도록 설정.
    """
    print("[offline_train_job] 시작")

    # 새로 SparkSession 생성 (실시간 추론 세션과는 별개)
    spark = (
        SparkSession.builder
        .appName("OfflineTrainingJob")
        .master("spark://master1:7077")
        # 라즈베리파이 메모리 고려
        .config("spark.driver.memory", "2g")
        .config("spark.executor.memory", "2g")
        .config("spark.executor.cores", "2")
        .config("spark.sql.shuffle.partitions", "16")
        .config("spark.sql.broadcastTimeout", "7200")
        .getOrCreate()
    )

    # 1) CSV -> Parquet (최초 1회)
    if not path_exists(spark, PARQUET_PATH):
        print(f"[offline_train_job] Parquet 파일이 없어 CSV({CSV_PATH})에서 변환합니다.")
        df_csv = spark.read.csv(CSV_PATH, header=True, inferSchema=True)
        df_csv.write.parquet(PARQUET_PATH)
        print("[offline_train_job] CSV -> Parquet 변환 완료.")

    # 2) MongoDB에서 사용자 로그 로드 (동기)
    mongo_data = mongoDB.get_mongodb_data_sync()
    if not mongo_data:
        print("[offline_train_job] MongoDB 사용자 데이터 없음. 학습 종료.")
        spark.stop()
        return

    user_df = spark.createDataFrame(pd.DataFrame(mongo_data))

    # 3) 가중치 컬럼
    user_df = user_df.withColumn(
        "weight",
        when(col("action") == "click", lit(ACTION_WEIGHTS["click"]))
        .when(col("action") == "search", lit(ACTION_WEIGHTS["search"]))
        .when(col("action") == "analysis", lit(ACTION_WEIGHTS["analysis"]))
        .when(col("action") == "save", lit(ACTION_WEIGHTS["save"]))
        .otherwise(lit(1))
    ).select("userId", "commercialCode", "weight")

    # 4) ALS 학습
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

    spark.stop()
    print("[offline_train_job] 종료")

def path_exists(spark, path):
    try:
        spark.read.parquet(path).limit(1).collect()
        return True
    except AnalysisException as ae:
        if "Path does not exist" in str(ae):
            return False
        raise ae
