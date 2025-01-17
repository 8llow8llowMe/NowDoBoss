from pymongo import MongoClient
import pandas as pd
from pyspark.sql import Row
from motor.motor_asyncio import AsyncIOMotorClient
import os

# MongoDB 환경변수 읽기
MONGO_DB_USERNAME = os.getenv("MONGO_DB_USERNAME")
MONGO_DB_PASSWORD = os.getenv("MONGO_DB_PASSWORD")
MONGO_DB_HOST = os.getenv("MONGO_DB_HOST")
MONGO_DB_PORT = os.getenv("MONGO_DB_PORT")
MONGO_DB_AUTHENTICATION_DATABASE = os.getenv("MONGO_DB_AUTHENTICATION_DATABASE")
MONGO_DB_DATABASE = os.getenv("MONGO_DB_DATABASE")

# MongoDB 연결 URL 설정
MONGO_URL = f"mongodb://{MONGO_DB_USERNAME}:{MONGO_DB_PASSWORD}@{MONGO_DB_HOST}:{MONGO_DB_PORT}/{MONGO_DB_AUTHENTICATION_DATABASE}"

# AsyncIOMotorClient를 사용하여 MongoDB에 연결
client = AsyncIOMotorClient(MONGO_URL)

db = client[MONGO_DB_DATABASE]  # 환경변수로 설정된 데이터베이스 이름을 사용

async def get_mongodb_data():
    collection = db['data']
    cursor = collection.find()
    documents = await cursor.to_list(length=1000)
    
    # `_id` 필드 문자열 변환
    for doc in documents:
        doc["_id"] = str(doc["_id"])  # ObjectId를 문자열로 변환
    
    return documents

async def find_weights(userId):
    collection = db['weights']
    document = await collection.find_one({"userId": userId})
    
    if not document:
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
        document.pop('_id', None)
        print(f"Document found for userId {userId}.")
    
    return document

async def update_weights(new_record):
    collection = db['weights']
    if isinstance(new_record, Row):
        new_record = new_record.asDict()
    result = await collection.update_one({"userId": new_record['userId']}, {"$set": new_record})
    if result.matched_count == 0:
        print(f"No document found with userId {new_record['userId']} to update.")
    else:
        print(f"Document with userId {new_record['userId']} updated successfully.")
