from pyspark.sql.functions import col, explode, desc, lit, when
from pyspark.ml.recommendation import ALS, ALSModel
import mongoDB
import pandas as pd

MODEL_PATH = "hdfs://master1:9000/models/recommendation_model"
ACTION_WEIGHTS = {"click": 2, "search": 4, "analysis": 7, "save": 10}

# 전역 변수로 HDFS 데이터를 한 번만 로드하여 사용
commercial_df = None

def load_commercial_data(spark):
    """
    HDFS 상권 데이터를 한 번 로드하고 캐싱.
    """
    global commercial_df
    if commercial_df is None:
        print("HDFS 상권 데이터 로드 및 캐싱 중...")
        commercial_data_path = "hdfs://master1:9000/data/commercial_data.csv"
        commercial_df = spark.read.csv(commercial_data_path, header=True, inferSchema=True).select(
            "commercialCode", "상권_코드_명", "상권_구분_코드", "상권_구분_코드_명", "totalTrafficFoot", "totalSales", "totalConsumption"
        )
        commercial_df.cache()
        print("HDFS 상권 데이터 캐싱 완료")
    return commercial_df

async def recommend(spark, user_id, background_tasks):
    """
    MongoDB 사용자 데이터와 캐싱된 HDFS 상권 데이터를 활용한 추천 데이터 생성.
    """
    print("추천 로직 시작...")

    # MongoDB에서 사용자 데이터 가져오기
    mongo_data = await mongoDB.get_mongodb_data()
    if not mongo_data:
        print("MongoDB에 사용자 데이터 없음.")
        return {"message": "No user data found in MongoDB."}

    # MongoDB 데이터를 Spark DataFrame으로 변환
    user_df = spark.createDataFrame(pd.DataFrame(mongo_data))

    # 사용자 행동 데이터를 가중치로 변환
    user_df = user_df.withColumn("weight", 
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
        result_df = recommendations_df.join(commercial_df, on="commercialCode", how="inner").orderBy(desc("rating"))

        # 결과 반환
        result = result_df.toPandas().to_dict(orient="records")
        return result
    finally:
        print("추천 로직 완료")

async def load_or_train_model(spark, user_df):
    """
    ALS 모델을 불러오거나 학습 후 저장합니다.
    """
    try:
        print("모델 로드 시도...")
        model = ALSModel.load(MODEL_PATH)
        print("모델 로드 성공")
    except Exception as e:
        print(f"모델 로드 실패: {e}. 새로 학습 시작...")

        als = ALS(
            maxIter=5,  # 반복 횟수 조정
            regParam=0.1,
            userCol="userId",
            itemCol="commercialCode",
            ratingCol="weight",
            coldStartStrategy="drop"
        )
        model = als.fit(user_df)
        model.save(MODEL_PATH)
        print("모델 학습 완료 및 저장")
    
    return model
