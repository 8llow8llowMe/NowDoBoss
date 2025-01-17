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
    - 비동기 백그라운드 작업이 남아있다면 충돌이 날 수 있음.
      서버가 완전히 종료될 타이밍에만 stop() 되도록 주의.
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
        # 이미 Parquet로 변경했다고 가정
        df = spark.read.parquet("hdfs://master1:9000/data/commercial_data.parquet")
        df.show(10)
        return {"status": "success", "data": df.head(10)}
    except Exception as e:
        print(f"에러 발생: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

def initialize_spark_session():
    """
    Spark 세션 초기화
    """
    return (
        SparkSession.builder
        .appName("RecommendationSystem")
        .master("spark://master1:7077")
        .config("spark.executor.memory", "4g")
        .config("spark.driver.memory", "2g")
        .config("spark.executor.cores", "2")
        .config("spark.sql.shuffle.partitions", "16")  # 파티션 수 조정
        .config("spark.sql.broadcastTimeout", "7200")  # 브로드캐스트 타임아웃 (초)
        .getOrCreate()
    )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
