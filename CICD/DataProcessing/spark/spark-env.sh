#!/bin/bash
# spark-env.sh

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export SPARK_WORKER_CORES=1
export SPARK_WORKER_MEMORY=1g
export SPARK_MASTER_HOST=master1
export SPARK_SUBMIT_OPTIONS="--add-opens java.base/sun.nio.ch=ALL-UNNAMED"
export PYSPARK_PYTHON=/opt/venv/bin/python
export PYSPARK_DRIVER_PYTHON=/opt/venv/bin/python