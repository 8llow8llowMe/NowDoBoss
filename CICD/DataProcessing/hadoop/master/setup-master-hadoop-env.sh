#!/bin/bash
# setup-master-hadoop-env.sh

# 디렉토리 생성
mkdir -p /usr/local/hadoop/hadoop_tmp/hdfs/namenode
mkdir -p /usr/local/hadoop/hadoop_tmp/hdfs/datanode
chmod 777 /usr/local/hadoop/hadoop_tmp/

echo "master의 환경변수를 설정합니다."
# 환경변수 설정
echo 'export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64' >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh
echo 'export HDFS_NAMENODE_USER=root' >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh
echo 'export HDFS_DATANODE_USER=root' >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh
echo 'export HDFS_SECONDARYNAMENODE_USER=root' >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh
echo 'export YARN_NODEMANAGER_USER=root' >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh
echo 'export YARN_RESOURCEMANAGER_USER=root' >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh

echo "/etc/hadoop 디렉토리를 확인합니다."
# /etc/hadoop 디렉토리가 존재하지 않는다면 생성
mkdir -p $HADOOP_HOME/etc/hadoop

echo "hdfs-site.xml 파일을 복사합니다."
# hdfs-site.xml 파일 복사
cp /usr/local/bin/master/hdfs-site.xml $HADOOP_HOME/etc/hadoop/hdfs-site.xml

echo "설정이 완료되었습니다."

# 다른 hadoop 클러스터들 구성 완료 될때까지 sleep 주기
sleep 3

# 하둡 시작 및 워드 카운트 실행
echo "HDFS 네임노드 포맷을 시작합니다."
hdfs namenode -format

echo "하둡 클러스터를 시작합니다."
start-all.sh

# JobHistoryServer 시작
echo "JobHistoryServer를 시작합니다."
mapred --daemon start historyserver

echo "WordCount 예제를 실행합니다."
hdfs dfs -mkdir /input
hdfs dfs -copyFromLocal /usr/local/hadoop/README.txt /input
hdfs dfs -ls /input
hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.4.jar wordcount /input/README.txt ~/wordcount-output

echo "설정이 완료되었습니다."