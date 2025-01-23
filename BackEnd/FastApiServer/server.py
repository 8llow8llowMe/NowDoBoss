# server.py

from fastapi import FastAPI, HTTPException, BackgroundTasks
from pydantic import BaseModel
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.cron import CronTrigger
from pyspark.sql import SparkSession
import recommendation_engine
from offline_training import offline_train_job

app = FastAPI()

spark = None
scheduler = BackgroundScheduler()

class UserRequest(BaseModel):
    userId: int

@app.on_event("startup")
def startup_event():
    """
    1) 실시간 추론용 Spark 세션 생성
    2) APScheduler 시작 & 매일 새벽 4시 offline_train_job 등록
    """
    global spark
    spark = initialize_spark_session()
    print("Spark 세션 초기화 완료")

    # APScheduler 시작
    scheduler.start()

    # 매일 새벽 4시에 offline_train_job() 실행
    trigger = CronTrigger(hour=4, minute=0)
    # offline_train_job 은 spark://master1:7077 로 새 SparkSession 만드는 함수
    scheduler.add_job(
        offline_train_job,
        trigger=trigger,
        id="offline_train_job",
        replace_existing=True
    )
    print("매일 새벽 4시에 offline_train_job 스케줄링 완료")

@app.on_event("shutdown")
def shutdown_event():
    """
    서버 종료 시점: APScheduler 중지 + Spark 세션 stop
    """
    scheduler.shutdown(wait=False)
    print("스케줄러 중지 완료")

    global spark
    if spark:
        spark.stop()
        print("Spark 세션 종료 완료")

@app.post("/recommend")
async def recommend_commercial_areas(request: UserRequest, background_tasks: BackgroundTasks):
    """
    사용자 ID 기반 상권 추천 API
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
def test_hdfs_connection():
    """
    HDFS 상의 Parquet 파일 테스트
    """
    try:
        df = spark.read.parquet("hdfs://master1:9000/data/commercial_data.parquet")
        df.show(5)
        return {"status": "success", "data": df.head(5)}
    except Exception as e:
        print(f"에러 발생: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

def initialize_spark_session():
    """
    실시간 추론용 SparkSession (작은 메모리/코어 할당)
    - 만약 master1 호스트가 인식 안 되면 IP로 교체
    """
    return (
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

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
