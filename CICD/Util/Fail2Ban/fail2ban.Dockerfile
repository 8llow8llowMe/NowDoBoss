FROM linuxserver/fail2ban:latest

# 프로젝트 디렉토리의 Fail2Ban 설정 파일을 컨테이너 내부로 복사
COPY jail.local /etc/fail2ban/jail.local
COPY filter.d/ /etc/fail2ban/filter.d/