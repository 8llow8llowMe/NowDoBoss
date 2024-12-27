#!/bin/bash
# setup-worker-hadoop-env.sh

# 디렉토리 생성
mkdir -p /usr/local/hadoop/hadoop_tmp/hdfs/datanode
chmod 777 /usr/local/hadoop/hadoop_tmp/

echo "worker의 환경변수를 설정합니다."
# 환경변수 설정
echo 'export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64' >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh

echo "/etc/hadoop 디렉토리를 확인합니다."
# /etc/hadoop 디렉토리가 존재하지 않는다면 생성
mkdir -p $HADOOP_HOME/etc/hadoop

echo "hdfs-site.xml 파일을 복사합니다."
# hdfs-site.xml 파일 복사
cp /usr/local/bin/worker/hdfs-site.xml $HADOOP_HOME/etc/hadoop/hdfs-site.xml

echo "설정이 완료되었습니다."