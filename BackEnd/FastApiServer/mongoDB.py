# mongoDB.py
from pymongo import MongoClient
import os
import pandas as pd
from motor.motor_asyncio import AsyncIOMotorClient

# 환경변수로 MongoDB 연결 정보 가져오기
MONGO_DB_USERNAME = os.getenv("MONGO_DB_USERNAME")
MONGO_DB_PASSWORD = os.getenv("MONGO_DB_PASSWORD")
MONGO_DB_HOST = os.getenv("MONGO_DB_HOST")
MONGO_DB_PORT = os.getenv("MONGO_DB_PORT")
MONGO_DB_AUTHENTICATION_DATABASE = os.getenv("MONGO_DB_AUTHENTICATION_DATABASE")
MONGO_DB_DATABASE = os.getenv("MONGO_DB_DATABASE")

# 동기 MongoClient -> 오프라인 학습용
def get_mongodb_data_sync():
    """
    오프라인 학습에서 사용하는 동기 함수.
    여기서 _id 필드를 문자열로 변환해서
    Spark DataFrame이 ObjectId 타입을 처리 못하는 문제를 해결한다.
    """
    url = f"mongodb://{MONGO_DB_USERNAME}:{MONGO_DB_PASSWORD}@{MONGO_DB_HOST}:{MONGO_DB_PORT}/{MONGO_DB_AUTHENTICATION_DATABASE}"
    client = MongoClient(url)
    db = client[MONGO_DB_DATABASE]
    collection = db["data"]  # collection 이름
    data = list(collection.find({}))
    client.close()

    # >>> _id를 문자열로 변환 <<<
    for doc in data:
        if "_id" in doc:
            doc["_id"] = str(doc["_id"])

    return data

# 비동기 MotorClient -> 실시간 API용
client_async = None
def get_async_client():
    global client_async
    if client_async is None:
        url = f"mongodb://{MONGO_DB_USERNAME}:{MONGO_DB_PASSWORD}@{MONGO_DB_HOST}:{MONGO_DB_PORT}/{MONGO_DB_AUTHENTICATION_DATABASE}"
        client_async = AsyncIOMotorClient(url)
    return client_async

async def get_mongodb_data():
    """
    실시간 API에서 사용하는 비동기 함수.
    이미 _id -> str 변환 로직이 있음.
    """
    client = get_async_client()
    db = client[MONGO_DB_DATABASE]
    collection = db["data"]
    cursor = collection.find({})
    documents = await cursor.to_list(length=1000)  # 필요에 따라 제한 조정
    # _id 필드 문자열 변환
    for doc in documents:
        doc["_id"] = str(doc["_id"])
    return documents

# 추가 예시 함수들
async def find_weights(userId: int):
    client = get_async_client()
    db = client[MONGO_DB_DATABASE]
    collection = db["weights"]
    document = await collection.find_one({"userId": userId})
    if not document:
        # userId 문서가 없으면 기본값 생성
        document = {
            "userId": userId,
            "totalTrafficFootValue": 0.0,
            "totalSalesValue": 0.0,
            "openedRateValue": 0.0,
            "closedRateValue": 0.0,
            "totalConsumptionValue": 0.0,
            "finalRating": 0.0
        }
        await collection.insert_one(document)
        print(f"New document created for userId {userId} with default values.")
    else:
        document.pop("_id", None)
        print(f"Document found for userId {userId}.")
    return document

async def update_weights(new_record: dict):
    client = get_async_client()
    db = client[MONGO_DB_DATABASE]
    collection = db["weights"]
    userId = new_record.get("userId")
    if userId is None:
        print("[update_weights] No userId in new_record. Cannot update.")
        return
    result = await collection.update_one({"userId": userId}, {"$set": new_record}, upsert=True)
    if result.matched_count == 0:
        print(f"No document found with userId {userId} to update. Inserted a new one maybe.")
    else:
        print(f"Document with userId {userId} updated successfully.")
