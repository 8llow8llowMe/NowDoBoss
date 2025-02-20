# ./airflow/dags/train_model.py

import pandas as pd
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, when, lit
from pyspark.ml.recommendation import ALS
import mongoDB  # 동기 MongoDB
from pyspark.sql.utils import AnalysisException

CSV_PATH = "hdfs://master1:9000/data/commercial_data.csv"
PARQUET_PATH = "hdfs://master1:9000/data/commercial_data.parquet"
MODEL_PATH = "hdfs://master1:9000/models/recommendation_model"

ACTION_WEIGHTS = {"click": 2, "search": 4, "analysis": 7, "save": 10}

def path_exists(spark, path):
    try:
        spark.read.parquet(path).limit(1).collect()
        return True
    except AnalysisException as ae:
        if "Path does not exist" in str(ae):
            return False
        raise ae

def main():
    print("[train_model] 오프라인 학습 시작")

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

    # 1) CSV -> Parquet (최초 1회만)
    if not path_exists(spark, PARQUET_PATH):
        print(f"[train_model] Parquet 없음 -> CSV({CSV_PATH})에서 변환")
        df_csv = spark.read.csv(CSV_PATH, header=True, inferSchema=True)
        df_csv.write.parquet(PARQUET_PATH)
        print("[train_model] CSV -> Parquet 변환 완료.")

    # 2) MongoDB 동기 호출
    mongo_data = mongoDB.get_mongodb_data_sync()
    if not mongo_data:
        print("[train_model] MongoDB 데이터 없음 -> 학습 중단.")
        spark.stop()
        return

    # 3) Spark DF
    user_df = spark.createDataFrame(pd.DataFrame(mongo_data))

    # 4) 행동 가중치 컬럼
    user_df = user_df.withColumn(
        "weight",
        when(col("action") == "click", lit(ACTION_WEIGHTS["click"]))
        .when(col("action") == "search", lit(ACTION_WEIGHTS["search"]))
        .when(col("action") == "analysis", lit(ACTION_WEIGHTS["analysis"]))
        .when(col("action") == "save", lit(ACTION_WEIGHTS["save"]))
        .otherwise(lit(1))
    ).select("userId", "commercialCode", "weight")

    print("[train_model] ALS 모델 학습 시작...")
    als = ALS(
        maxIter=5,
        regParam=0.1,
        userCol="userId",
        itemCol="commercialCode",
        ratingCol="weight",
        coldStartStrategy="drop"
    )
    model = als.fit(user_df)
    print("[train_model] ALS 모델 학습 완료")

    # 5) 모델 저장
    model.save(MODEL_PATH)
    print(f"[train_model] 모델 저장 완료 -> {MODEL_PATH}")

    spark.stop()
    print("[train_model] 종료")

if __name__ == "__main__":
    main()
