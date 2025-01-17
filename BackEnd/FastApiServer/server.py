from pyspark.sql import SparkSession
from pydantic import BaseModel
from fastapi import FastAPI, HTTPException, BackgroundTasks
import recommendation_engine

app = FastAPI()

class UserRequest(BaseModel):
    userId: int

@app.post("/recommend")
async def recommend_commercial_areas(request: UserRequest, background_tasks: BackgroundTasks):
    """
    사용자 ID 기반으로 상권 추천 데이터를 생성합니다.
    """
    print(f"추천 요청 수신: {request}")
    spark = None
    try:
        # 요청 시 Spark 세션 초기화
        spark = initialize_spark_session()
        response = await recommendation_engine.recommend(spark, request.userId, background_tasks)
        print(f"추천 결과 반환: {response}")
        return {"status": "success", "data": response}
    except Exception as e:
        print(f"에러 발생: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        # 요청 처리 후 Spark 세션 종료
        if spark:
            spark.stop()
            print("Spark 세션 종료 완료")

def initialize_spark_session():
    """
    Spark 세션 초기화
    """
    return SparkSession.builder \
        .appName("RecommendationSystem") \
        .master("spark://master1:7077") \
        .getOrCreate()
