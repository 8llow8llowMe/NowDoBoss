from pyspark.sql.functions import col, explode, desc, lit, when
from pyspark.ml.recommendation import ALS, ALSModel
import mongoDB
import pandas as pd

# HDFS 상권 데이터 경로 및 ALS 모델 경로
MODEL_PATH = "hdfs://master1:9000/models/recommendation_model"
COMMERCIAL_DATA_PATH = "hdfs://master1:9000/data/commercial_data.csv"

# 사용자 행동 가중치 설정
ACTION_WEIGHTS = {"click": 2, "search": 4, "analysis": 7, "save": 10}

# 전역 캐시 변수
cached_commercial_data = None
cached_als_model = None  # ALS 모델 캐싱


def load_commercial_data(spark):
    """
    HDFS 상권 데이터를 한 번 로드한 후 Spark DataFrame으로 캐싱합니다.
    """
    global cached_commercial_data
    if cached_commercial_data is None:
        print("HDFS 상권 데이터 로드 및 캐싱 중...")
        # HDFS 데이터 로드 및 Spark DataFrame 유지
        cached_commercial_data = spark.read.csv(COMMERCIAL_DATA_PATH, header=True, inferSchema=True)
        cached_commercial_data.cache()
        print("HDFS 상권 데이터 캐싱 완료")
    return cached_commercial_data


async def recommend(spark, user_id, background_tasks):
    """
    사용자 ID 기반으로 추천 데이터를 생성합니다.

    Args:
        spark: SparkSession 객체
        user_id: 추천 요청을 보낸 사용자 ID
        background_tasks: FastAPI BackgroundTasks 객체

    Returns:
        추천 결과 데이터 (JSON 형식)
    """
    print("추천 로직 시작...")

    # MongoDB에서 사용자 데이터 로드
    mongo_data = await mongoDB.get_mongodb_data()
    if not mongo_data:
        print("MongoDB에 사용자 데이터 없음.")
        return {"message": "No user data found in MongoDB."}

    # MongoDB 데이터를 Spark DataFrame으로 변환
    user_df = spark.createDataFrame(pd.DataFrame(mongo_data))

    # 사용자 행동 데이터를 가중치로 변환
    user_df = user_df.withColumn(
        "weight",
        when(col("action") == "click", lit(ACTION_WEIGHTS["click"]))
        .when(col("action") == "search", lit(ACTION_WEIGHTS["search"]))
        .when(col("action") == "analysis", lit(ACTION_WEIGHTS["analysis"]))
        .when(col("action") == "save", lit(ACTION_WEIGHTS["save"]))
        .otherwise(lit(1))
    ).select("userId", "commercialCode", "weight")

    # 캐싱된 HDFS 상권 데이터 가져오기
    commercial_df = load_commercial_data(spark)

    try:
        # ALS 모델 불러오기 또는 학습
        model = await load_or_train_model(spark, user_df)

        # 특정 사용자에 대한 추천 데이터 생성
        user_subset = user_df.filter(user_df["userId"] == user_id)
        recommendations = model.recommendForUserSubset(user_subset, numItems=10)

        # 추천 데이터를 상권 데이터와 조합
        recommendations_df = recommendations.withColumn("recommendation", explode("recommendations")).select(
            col("userId"),
            col("recommendation.commercialCode").alias("commercialCode"),
            col("recommendation.rating").alias("rating")
        )
        # commercial_df는 Spark DataFrame이므로 join 가능
        result_df = recommendations_df.join(commercial_df, on="commercialCode", how="inner").orderBy(desc("rating"))

        # 결과를 JSON 형식으로 반환
        result = result_df.toPandas().to_dict(orient="records")
        return {"status": "success", "data": result}

    finally:
        print("추천 로직 완료")


async def load_or_train_model(spark, user_df):
    """
    ALS 모델을 불러오거나, 없을 경우 학습 후 저장합니다.

    Args:
        spark: SparkSession 객체
        user_df: 사용자 데이터 (Spark DataFrame)

    Returns:
        ALS 모델 객체
    """
    global cached_als_model
    if cached_als_model is None:
        try:
            print("모델 로드 시도...")
            # 기존 모델 로드
            cached_als_model = ALSModel.load(MODEL_PATH)
            print("모델 로드 성공")
        except Exception as e:
            print(f"모델 로드 실패: {e}. 새로 학습 시작...")
            # ALS 모델 학습
            als = ALS(
                maxIter=5,  # 반복 횟수
                regParam=0.1,  # 정규화 파라미터
                userCol="userId",  # 사용자 컬럼
                itemCol="commercialCode",  # 아이템 컬럼
                ratingCol="weight",  # 가중치 컬럼
                coldStartStrategy="drop"  # Cold Start 처리 전략
            )
            cached_als_model = als.fit(user_df)
            # 학습된 모델 저장
            cached_als_model.save(MODEL_PATH)
            print("모델 학습 완료 및 저장")
    return cached_als_model
