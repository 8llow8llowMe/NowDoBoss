# server.py

from fastapi import FastAPI, HTTPException, BackgroundTasks
from pydantic import BaseModel
from pyspark.sql import SparkSession
import recommendation_engine

app = FastAPI()

spark = None

class UserRequest(BaseModel):
    userId: int

@app.on_event("startup")
def startup_event():
    """
    서버 시작: 실시간 추론용 SparkSession 생성
    """
    global spark
    spark = (
        SparkSession.builder
        .appName("RecommendationSystem")
        .master("spark://master1:7077")
        .config("spark.executor.memory", "1g")
        .config("spark.driver.memory", "1g")
        .config("spark.executor.cores", "1")
        .config("spark.sql.shuffle.partitions", "8")
        .config("spark.sql.broadcastTimeout", "7200")
        .getOrCreate()
    )
    print("[startup_event] Spark 세션 초기화 완료")

@app.on_event("shutdown")
def shutdown_event():
    """
    서버 종료 시점: Spark 세션 stop
    """
    global spark
    if spark:
        spark.stop()
        print("[shutdown_event] Spark 세션 종료 완료")

@app.post("/recommend")
async def recommend_commercial_areas(request: UserRequest, background_tasks: BackgroundTasks):
    """
    사용자 ID 기반 상권 추천 (실시간 추론)
    """
    print(f"[recommend] 요청 수신: {request}")
    try:
        response = await recommendation_engine.recommend(spark, request.userId, background_tasks)
        return {"status": "success", "data": response}
    except Exception as e:
        print(f"[recommend] 에러 발생: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/test-hdfs")
def test_hdfs_connection():
    """
    간단히 HDFS Parquet 파일 조회 (테스트)
    """
    global spark
    try:
        df = spark.read.parquet("hdfs://master1:9000/data/commercial_data.parquet")
        df.show(5)
        return {"status": "success", "data": df.head(5)}
    except Exception as e:
        print(f"[test_hdfs_connection] 에러: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
