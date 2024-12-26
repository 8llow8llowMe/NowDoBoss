#!/bin/bash
# init-ssh-keys.sh

# SSH 설정 디렉터리 확인 및 생성
mkdir -p /root/.ssh
chmod 700 /root/.ssh

# SSH 키 생성
if [ ! -f /root/.ssh/id_rsa ]; then
    echo "SSH 키가 존재하지 않습니다. 키를 생성합니다."
    ssh-keygen -t rsa -b 4096 -f /root/.ssh/id_rsa -N ''
fi

# 공유 디렉터리에 키 저장
cp /root/.ssh/id_rsa.pub /shared_keys/$(hostname)_id_rsa.pub
