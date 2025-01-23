# recommendation_engine.py

from pyspark.sql.functions import col, explode, desc, lit, when
from pyspark.ml.recommendation import ALSModel
import mongoDB
import pandas as pd
from pyspark.sql.utils import AnalysisException

CSV_PATH = "hdfs://master1:9000/data/commercial_data.csv"
PARQUET_PATH = "hdfs://master1:9000/data/commercial_data.parquet"
MODEL_PATH = "hdfs://master1:9000/models/recommendation_model"

ACTION_WEIGHTS = {"click": 2, "search": 4, "analysis": 7, "save": 10}

# 전역 캐시
cached_commercial_data = None
cached_als_model = None  # 오프라인 학습된 모델

def path_exists(spark, path):
    """
    Parquet 경로 존재 여부 체크
    """
    try:
        spark.read.parquet(path).limit(1).collect()
        return True
    except AnalysisException as ae:
        if "Path does not exist" in str(ae):
            return False
        raise ae

def load_commercial_data(spark):
    """
    실시간 추론 시 상권 데이터를 로드 (Parquet)
    만약 Parquet이 없으면 CSV -> Parquet 변환.
    """
    global cached_commercial_data
    if cached_commercial_data is not None:
        return cached_commercial_data

    if not path_exists(spark, PARQUET_PATH):
        print(f"[load_commercial_data] Parquet 파일이 없어 CSV({CSV_PATH})에서 변환 중...")
        df = spark.read.csv(CSV_PATH, header=True, inferSchema=True)
        df.write.parquet(PARQUET_PATH)
        print("[load_commercial_data] CSV -> Parquet 변환 완료.")

    print("[load_commercial_data] Parquet 상권 데이터 로드 중...")
    cached_commercial_data = spark.read.parquet(PARQUET_PATH).cache()
    print("[load_commercial_data] Parquet 상권 데이터 로드 완료")

    return cached_commercial_data

async def recommend(spark, user_id, background_tasks):
    """
    사용자 ID 기반 추천 로직 (실시간 API용)
    오프라인 학습된 모델을 로드하여 recommendForUserSubset.
    """
    print("추천 로직 시작...")

    # 1) MongoDB 사용자 데이터 (비동기)
    mongo_data = await mongoDB.get_mongodb_data()
    if not mongo_data:
        print("MongoDB 사용자 데이터 없음.")
        return {"message": "No user data found in MongoDB."}

    # 2) Spark DataFrame 변환
    # 비동기 로드된 mongo_data에는 이미 _id -> str 변환 로직 있음
    user_df = spark.createDataFrame(pd.DataFrame(mongo_data))

    # 3) 행동 가중치 컬럼
    user_df = user_df.withColumn(
        "weight",
        when(col("action") == "click", lit(ACTION_WEIGHTS["click"]))
        .when(col("action") == "search", lit(ACTION_WEIGHTS["search"]))
        .when(col("action") == "analysis", lit(ACTION_WEIGHTS["analysis"]))
        .when(col("action") == "save", lit(ACTION_WEIGHTS["save"]))
        .otherwise(lit(1))
    ).select("userId", "commercialCode", "weight")

    # 4) 상권 데이터 로드
    commercial_df = load_commercial_data(spark)

    try:
        # 5) 이미 오프라인 학습된 모델 로드
        model = await load_offline_model()

        # 6) 특정 사용자에 대한 추천
        user_subset = user_df.filter(col("userId") == user_id)
        recommendations = model.recommendForUserSubset(user_subset, numItems=10)

        # 7) 추천 결과 전개 + 상권 데이터 조인
        recommendations_df = (
            recommendations
            .withColumn("recommendation", explode("recommendations"))
            .select(
                col("userId"),
                col("recommendation.commercialCode").alias("commercialCode"),
                col("recommendation.rating").alias("rating")
            )
        )
        result_df = recommendations_df.join(commercial_df, on="commercialCode", how="inner").orderBy(desc("rating"))

        # 8) 결과 -> toPandas 변환
        result = result_df.toPandas().to_dict(orient="records")
        return {"status": "success", "data": result}

    finally:
        print("추천 로직 완료")

async def load_offline_model():
    """
    오프라인에서 학습된 ALS 모델을 전역 캐시에 로드.
    """
    global cached_als_model
    if cached_als_model is not None:
        return cached_als_model

    print("[load_offline_model] 오프라인 학습된 모델 로드 시도...")
    try:
        cached_als_model = ALSModel.load(MODEL_PATH)
        print("[load_offline_model] 모델 로드 성공")
    except Exception as e:
        print("[load_offline_model] 모델 로드 실패:", e)
        raise e  # 모델이 전혀 없으면 추론 불가 -> 500 에러

    return cached_als_model
