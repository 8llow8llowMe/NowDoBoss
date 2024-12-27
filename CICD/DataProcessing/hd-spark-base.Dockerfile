# 우분투 베이스 이미지 사용
FROM ubuntu:latest

# 필수 패키지 설치 및 deadsnakes PPA 추가
RUN apt-get update && apt-get upgrade -y
RUN apt-get install -y software-properties-common
RUN add-apt-repository ppa:deadsnakes/ppa
RUN apt-get update && apt-get install -y curl openssh-server rsync wget vim iputils-ping htop openjdk-8-jdk python3.12 python3.12-venv python3.12-dev python3-pip

# python 명령어를 python3.10로 링크
RUN ln -s /usr/bin/python3.10 /usr/bin/python

# 하둡 다운로드 및 설치
RUN wget http://mirror.navercorp.com/apache/hadoop/common/hadoop-3.2.4/hadoop-3.2.4.tar.gz \
    && tar zxvf hadoop-3.2.4.tar.gz \
    && rm hadoop-3.2.4.tar.gz \
    && mv hadoop-3.2.4 /usr/local/hadoop

# Spark 다운로드 및 설치
RUN wget https://archive.apache.org/dist/spark/spark-3.4.0/spark-3.4.0-bin-hadoop3.tgz \
    && tar zxvf spark-3.4.0-bin-hadoop3.tgz \
    && rm spark-3.4.0-bin-hadoop3.tgz \
    && mv spark-3.4.0-bin-hadoop3 /usr/local/spark

# 가상 환경 생성 및 활성화, PySpark 및 필요 라이브러리 설치
RUN python3.12 -m venv /opt/venv
RUN /opt/venv/bin/pip install --upgrade pip
RUN /opt/venv/bin/pip install pyspark==3.4.0 numpy==1.26.4 pandas==2.0.3

# 환경변수 설정
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64
ENV HADOOP_HOME=/usr/local/hadoop
ENV SPARK_HOME=/usr/local/spark
ENV PYSPARK_PYTHON=/opt/venv/bin/python
ENV PYSPARK_DRIVER_PYTHON=/opt/venv/bin/python
ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$JAVA_HOME/bin:$SPARK_HOME/bin:$SPARK_HOME/sbin

# 디렉토리 생성
RUN mkdir -p /usr/local/bin /usr/local/bin/master /usr/local/bin/worker

# 공통적으로 사용되는 하둡 설정 파일(.xml)과 및 쉘 스크립트 복사
COPY hadoop/common/core-site.xml $HADOOP_HOME/etc/hadoop/core-site.xml
COPY hadoop/common/mapred-stie.xml $HADOOP_HOME/etc/hadoop/mapred-site.xml
COPY hadoop/common/yarn-site.xml $HADOOP_HOME/etc/hadoop/yarn-site.xml
COPY hadoop/common/setup-hadoop.sh /usr/local/bin/setup-hadoop.sh
COPY hadoop/common/init-ssh-keys.sh /usr/local/bin/init-ssh-keys.sh
COPY hadoop/common/collect-ssh-keys.sh /usr/local/bin/collect-ssh-keys.sh
COPY hadoop/common/update-hosts.sh /usr/local/bin/update-hosts.sh

# 하둡 마스터 노드 설정 파일 및 스크립트 복사
COPY hadoop/master/hdfs-site.xml /usr/local/bin/master/hdfs-site.xml
COPY hadoop/master/setup-master-hadoop-env.sh /usr/local/bin/master/setup-master-hadoop-env.sh

# 하둡 워커 노드 설정 파일 및 스크립트 복사
COPY hadoop/worker/hdfs-site.xml /usr/local/bin/worker/hdfs-site.xml
COPY hadoop/worker/setup-worker-hadoop-env.sh /usr/local/bin/worker/setup-worker-hadoop-env.sh

# 각 노드내에 스파크 설정 파일 및 스파크 관련 쉘 스크립트 복사
COPY spark/spark-env.sh $SPARK_HOME/conf/spark-env.sh
COPY spark/spark-defaults.conf $SPARK_HOME/conf/spark-defaults.conf
COPY spark/history-server.conf $SPARK_HOME/conf/history-server.conf
COPY spark/start-master.sh /usr/local/bin/start-master.sh
COPY spark/start-slave.sh /usr/local/bin/start-slave.sh
COPY spark/start-history-server.sh /usr/local/bin/start-history-server.sh
COPY spark/create-hdfs-log-dir.sh /usr/local/bin/create-hdfs-log-dir.sh

# 쉘 스크립트 실행 권한 부여 및 실행
RUN chmod +x /usr/local/bin/setup-hadoop.sh 
RUN chmod +x /usr/local/bin/init-ssh-keys.sh /usr/local/bin/collect-ssh-keys.sh 
RUN chmod +x /usr/local/bin/update-hosts.sh
RUN chmod +x /usr/local/bin/start-master.sh
RUN chmod +x /usr/local/bin/start-slave.sh
RUN chmod +x /usr/local/bin/start-history-server.sh
RUN chmod +x /usr/local/bin/create-hdfs-log-dir.sh
RUN chmod +x /usr/local/bin/master/setup-master-hadoop-env.sh
RUN chmod +x /usr/local/bin/worker/setup-worker-hadoop-env.sh


# SSH 구성
RUN service ssh start

# SSH 데몬 실행
CMD ["/usr/sbin/sshd", "-D"]
