from pyspark.sql.functions import col, explode, desc, lit, when
from pyspark.ml.recommendation import ALS, ALSModel
import mongoDB
import pandas as pd

MODEL_PATH = "hdfs://master1:9000/models/recommendation_model"
ACTION_WEIGHTS = {"click": 2, "search": 4, "analysis": 7, "save": 10}

async def recommend(spark, user_id, background_tasks):
    """
    MongoDB 사용자 데이터와 HDFS 상권 데이터를 활용한 추천 데이터 생성
    """
    print("추천 로직 시작...")

    # MongoDB에서 사용자 데이터 가져오기
    mongo_data = await mongoDB.get_mongodb_data()
    if not mongo_data:
        print("MongoDB에 사용자 데이터 없음.")
        return {"message": "No user data found in MongoDB."}

    # MongoDB 데이터를 Pandas DataFrame으로 변환
    mongo_df = pd.DataFrame(mongo_data)

    # 필요 없는 열 제거
    mongo_df = mongo_df.drop(columns=["_id", "_class"], errors="ignore")

    # Spark DataFrame으로 변환
    user_df = spark.createDataFrame(mongo_df)

    # 사용자 행동 데이터를 가중치로 변환
    user_df = user_df.withColumn("weight", 
        when(col("action") == "click", lit(ACTION_WEIGHTS["click"]))
        .when(col("action") == "search", lit(ACTION_WEIGHTS["search"]))
        .when(col("action") == "analysis", lit(ACTION_WEIGHTS["analysis"]))
        .when(col("action") == "save", lit(ACTION_WEIGHTS["save"]))
        .otherwise(lit(1))
    ).select("userId", "commercialCode", "weight")
    user_df = user_df.repartition(4, "userId")  # 파티션 수 조정
    user_df.cache()  # 캐싱으로 데이터 재활용 속도 개선
    user_df.show()

    # HDFS에서 상권 데이터 가져오기
    commercial_data_path = "hdfs://master1:9000/data/commercial_data.csv"
    commercial_df = spark.read.csv(commercial_data_path, header=True, inferSchema=True) \
        .select("commercialCode", "상권_코드_명", "상권_구분_코드", "상권_구분_코드_명", "totalTrafficFoot", "totalSales", "totalConsumption") \
        .repartition(4, "commercialCode")
    commercial_df.cache()  # 캐싱으로 데이터 재활용 속도 개선
    commercial_df.show()

    try:
        # ALS 모델 불러오기 또는 학습
        model = await load_or_train_model(spark, user_df)

        # 특정 사용자에 대한 추천 데이터 생성
        user_subset = user_df.filter(user_df["userId"] == user_id)
        recommendations = model.recommendForUserSubset(user_subset, numItems=10)

        # 추천 데이터를 상권 데이터와 조합
        recommendations_df = recommendations.withColumn("recommendation", explode("recommendations")).select(
            col("userId"),
            col("recommendation.commercialCode").cast("string").alias("commercialCode"),
            col("recommendation.rating").alias("finalRating")
        )

        result_df = recommendations_df.join(commercial_df, on="commercialCode", how="inner").orderBy(desc("finalRating"))

        # Java가 요구하는 데이터 구조로 변환
        result = result_df.withColumn("openedRate", lit(None).cast("double")) \
                          .withColumn("closedRate", lit(None).cast("double")) \
                          .select("userId", "commercialCode", "totalTrafficFoot", "totalSales", "openedRate", 
                                  "closedRate", "totalConsumption", "finalRating")

        # Pandas DataFrame으로 변환 후 JSON으로 반환
        result_json = result.toPandas().to_dict(orient="records")
        return result_json
    finally:
        # 캐싱 해제
        user_df.unpersist()
        commercial_df.unpersist()
        print("캐싱 해제 완료")

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
            maxIter=5,  # 반복 횟수 감소로 학습 시간 단축
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
