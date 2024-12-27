from pymongo import MongoClient
import pandas as pd
from pyspark.sql import Row
from motor.motor_asyncio import AsyncIOMotorClient

# 연결에 필요한 정보
authenticationDatabase = "admin"
username = "8llow8llowme"
password = "8llow8llowme"
host = "192.168.0.25"
port = 27017

# MongoDB 연결 URL 설정
url = f"mongodb://{username}:{password}@{host}:{port}/{authenticationDatabase}"

# AsyncIOMotorClient를 사용하여 MongoDB에 연결
client = AsyncIOMotorClient(url)
db = client.nowdoboss  # 데이터베이스 선택

async def get_mongodb_data():
    collection = db['data']
    cursor = collection.find()
    documents = await cursor.to_list(length=1000)  # 비동기 커서에서 데이터를 리스트로 변환
    return documents

async def find_weights(userId):
    # 컬렉션 선택
    collection = db['weights']
    
    # 주어진 ID로 문서 조회
    document = await collection.find_one({"userId": userId})
    
    # 문서가 없을 경우 새로운 문서 생성
    if not document:
        # 새 문서 생성 (userId 제외 모든 필드를 0으로 설정)
        # 문서 구성
        document = {
            "userId": userId,
            "totalTrafficFootValue": 0.0,
            "totalSalesValue": 0.0,
            "openedRateValue": 0.0,
            "closedRateValue": 0.0,
            "totalConsumptionValue": 0.0,
            "finalRating": 0.0
        }
        # 새 문서를 컬렉션에 저장
        await collection.insert_one(document)
        print(f"New document created for userId {userId} with default values.")
    else:
        # _id 필드 제거
        document.pop('_id', None)
        print(f"Document found for userId {userId}.")
    
    # 클라이언트 연결 종료
    # client.close()
    
    return document

async def update_weights(new_record):
    # 컬렉션 선택
    collection = db['weights']

    # new_record가 DataFrame의 row 객체라면 Python 딕셔너리로 변환
    if isinstance(new_record, Row):
        new_record = new_record.asDict()
    
    # userId를 기반으로 문서 업데이트
    result = await collection.update_one(
        {"userId": new_record['userId']}, 
        {"$set": new_record}
    )
    
    # 결과 출력
    if result.matched_count == 0:
        print(f"No document found with userId {new_record['userId']} to update.")
    else:
        print(f"Document with userId {new_record['userId']} updated successfully.")

    # 클라이언트 연결 종료
    # client.close()
