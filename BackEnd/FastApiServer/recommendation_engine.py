from pyspark.sql.functions import col, explode, desc, lit, when
from pyspark.ml.recommendation import ALS, ALSModel
import mongoDB
import pandas as pd
from pyspark.sql.utils import AnalysisException

CSV_PATH = "hdfs://master1:9000/data/commercial_data.csv"     # CSV 파일
PARQUET_PATH = "hdfs://master1:9000/data/commercial_data.parquet"
MODEL_PATH = "hdfs://master1:9000/models/recommendation_model"

ACTION_WEIGHTS = {"click": 2, "search": 4, "analysis": 7, "save": 10}

# 전역 캐시 변수
cached_commercial_data = None
cached_als_model = None  # ALS 모델 캐싱

def path_exists(spark, path):
    try:
        spark.read.parquet(path).limit(1).collect()
        return True
    except AnalysisException as ae:
        if "Path does not exist" in str(ae):
            return False
        raise ae

def load_commercial_data(spark):
    global cached_commercial_data
    if cached_commercial_data is not None:
        return cached_commercial_data

    # Parquet 파일 유무 확인
    if not path_exists(spark, PARQUET_PATH):
        print(f"[load_commercial_data] Parquet 파일이 없어 CSV({CSV_PATH})에서 변환을 시작합니다.")
        df = spark.read.csv(CSV_PATH, header=True, inferSchema=True)
        df.write.parquet(PARQUET_PATH)
        print("[load_commercial_data] CSV -> Parquet 변환 완료.")

    print("[load_commercial_data] Parquet 상권 데이터 로드 및 캐싱 중...")
    cached_commercial_data = spark.read.parquet(PARQUET_PATH).cache()
    print("[load_commercial_data] Parquet 상권 데이터 캐싱 완료")

    return cached_commercial_data

async def recommend(spark, user_id, background_tasks):
    print("추천 로직 시작...")

    # MongoDB에서 사용자 데이터 로드
    mongo_data = await mongoDB.get_mongodb_data()
    if not mongo_data:
        print("MongoDB에 사용자 데이터 없음.")
        return {"message": "No user data found in MongoDB."}

    # MongoDB -> Spark DataFrame 변환
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

    # 캐싱된 상권 데이터 가져오기
    commercial_df = load_commercial_data(spark)

    try:
        model = await load_or_train_model(spark, user_df)

        # 특정 사용자에 대한 추천
        user_subset = user_df.filter(col("userId") == user_id)
        recommendations = model.recommendForUserSubset(user_subset, numItems=10)

        # 추천 결과 전개
        recommendations_df = (
            recommendations
            .withColumn("recommendation", explode("recommendations"))
            .select(
                col("userId"),
                col("recommendation.commercialCode").alias("commercialCode"),
                col("recommendation.rating").alias("rating")
            )
        )
        # 상권 데이터와 조인
        result_df = recommendations_df.join(commercial_df, on="commercialCode", how="inner").orderBy(desc("rating"))

        # 결과가 많지 않을 것이므로 toPandas() 변환
        result = result_df.toPandas().to_dict(orient="records")
        return {"status": "success", "data": result}
    finally:
        print("추천 로직 완료")

async def load_or_train_model(spark, user_df):
    global cached_als_model
    if cached_als_model is None:
        try:
            print("모델 로드 시도...")
            cached_als_model = ALSModel.load(MODEL_PATH)
            print("모델 로드 성공")
        except Exception as e:
            print(f"모델 로드 실패: {e}. 새로 학습 시작...")
            als = ALS(
                maxIter=5,
                regParam=0.1,
                userCol="userId",
                itemCol="commercialCode",
                ratingCol="weight",
                coldStartStrategy="drop"
            )
            cached_als_model = als.fit(user_df)
            cached_als_model.save(MODEL_PATH)
            print("모델 학습 완료 및 저장")
    return cached_als_model
