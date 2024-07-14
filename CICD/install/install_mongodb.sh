#!/bin/bash

# 기존 MongoDB 제거
sudo apt purge -y mongodb-org*

# 패키지 데이터베이스 업데이트
sudo apt update

# MongoDB 공식 GPG 키 추가
sudo wget -qO - https://www.mongodb.org/static/pgp/server-5.0.asc | sudo apt-key add -

# MongoDB 리포지토리 추가
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu $(lsb_release -cs)/mongodb-org/5.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-5.0.list

# 패키지 데이터베이스 업데이트
sudo apt update

# MongoDB 패키지 설치
sudo apt install -y mongodb-org

# MongoDB 데이터 디렉토리 생성 및 소유권 변경
sudo mkdir -p /data/db
sudo chown `id -u`:`id -g` /data/db

# MongoDB 설정 파일 복사
sudo cp ./mongod.conf /etc/mongod.conf

# MongoDB 서버 시작 (백그라운드 실행)
sudo mongod --config /etc/mongod.conf --dbpath /data/db &

# 기다리기 (MongoDB 서비스 시작을 위해)
sleep 5

# 기존 관리자 계정 삭제 (존재하는 경우)
mongo admin --eval "db.dropUser('8llow8llowme')"

# MongoDB에 관리자 계정 생성 스크립트 실행
mongo admin --eval "db.createUser({user: '8llow8llowme', pwd: '8llow8llowme', roles:[{role:'root', db: 'admin'}]})"

# MongoDB 재시작
sudo systemctl restart mongod

# MongoDB에 데이터베이스 생성 (컬렉션 생성 없음)
mongo --username 8llow8llowme --password 8llow8llowme --authenticationDatabase admin --eval "use nowdoboss;"

# 부팅 시 MongoDB 자동 시작 설정
sudo systemctl enable mongod

echo "MongoDB installation and setup completed successfully."