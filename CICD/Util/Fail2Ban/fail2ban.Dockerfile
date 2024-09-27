FROM crazymax/fail2ban:latest

# Fail2Ban 설정 파일 복사
COPY filter.d/sshd.conf /etc/fail2ban/filter.d/sshd.conf
COPY jail.d/sshd.conf /etc/fail2ban/jail.d/sshd.conf