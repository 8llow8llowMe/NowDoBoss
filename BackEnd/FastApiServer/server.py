from pydantic import BaseModel
from fastapi import FastAPI, HTTPException, BackgroundTasks
from pyspark.sql import SparkSession
import recommendation_engine

app = FastAPI()

# 전역 SparkSession
spark = None

class UserRequest(BaseModel):
    userId: int

@app.on_event("startup")
def startup_event():
    """
    애플리케이션 시작 시 Spark 세션 초기화
    """
    global spark
    spark = initialize_spark_session()
    print("Spark 세션 초기화 완료")

@app.on_event("shutdown")
def shutdown_event():
    """
    애플리케이션 종료 시 Spark 세션 종료
    """
    global spark
    if spark:
        spark.stop()
        print("Spark 세션 종료 완료")

@app.post("/recommend")
async def recommend_commercial_areas(request: UserRequest, background_tasks: BackgroundTasks):
    """
    사용자 ID 기반으로 상권 추천 데이터를 생성합니다.
    """
    print(f"추천 요청 수신: {request}")
    try:
        response = await recommendation_engine.recommend(spark, request.userId, background_tasks)
        print(f"추천 결과 반환: {response}")
        return {"status": "success", "data": response}
    except Exception as e:
        print(f"에러 발생: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/test-hdfs")
async def test_hdfs_connection():
    """
    HDFS 연결 테스트 및 데이터 확인.
    """
    try:
        df = spark.read.csv("hdfs://master1:9000/data/commercial_data.csv", header=True, inferSchema=True)
        df.show()
        return {"status": "success", "data": df.head(10)}
    except Exception as e:
        print(f"에러 발생: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

def initialize_spark_session():
    """
    Spark 세션 초기화
    """
    return SparkSession.builder \
        .appName("RecommendationSystem") \
        .master("spark://master1:7077") \
        .getOrCreate()

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
