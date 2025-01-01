CREATE TABLE `member` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '회원 아이디',
    `email` VARCHAR(255) NOT NULL COMMENT '이메일',
    `name` VARCHAR(40) DEFAULT NULL COMMENT '이름',
    `nickname` VARCHAR(60) NOT NULL COMMENT '닉네임',
    `password` VARCHAR(80) DEFAULT NULL COMMENT '비밀번호',
    `profile_image` VARCHAR(255) DEFAULT NULL COMMENT '프로필 이미지 URL',
    `provider` ENUM('KAKAO', 'NAVER', 'GOOGLE') DEFAULT NULL COMMENT '소셜 로그인 제공업체',
    `role` ENUM('USER', 'ADMIN') NOT NULL COMMENT '권한',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 날짜',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 날짜',
    PRIMARY KEY (`id`),
    KEY `idx_email` (`email`)
);

CREATE TABLE `chat_room` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '채팅방 아이디',
    `chat_room_limit` INT UNSIGNED DEFAULT NULL COMMENT '채팅방 제한 인원수',
    `chat_room_introduction` VARCHAR(40) DEFAULT NULL COMMENT '채팅방 소개',
    `chat_room_name` VARCHAR(20) DEFAULT NULL COMMENT '채팅방 이름',
    `category` VARCHAR(20) NOT NULL COMMENT '카테고리',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 날짜',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 날짜',
    PRIMARY KEY (`id`)
);

CREATE TABLE `chat_message` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '채팅 내역 아이디',
    `chat_room_id` INT UNSIGNED NOT NULL COMMENT '채팅방 아이디',
    `member_id` INT UNSIGNED NOT NULL COMMENT '작성자 아이디',
    `content` TEXT NOT NULL COMMENT '채팅 내용',
    `type` ENUM('ENTER', 'EXIT', 'TALK') DEFAULT NULL COMMENT '채팅 내용 종류',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 날짜',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 날짜',
    PRIMARY KEY (`id`),
    KEY `chat_room_id` (`chat_room_id`),
    KEY `member_id` (`member_id`),
    FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
);

CREATE TABLE `chat_room_member` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '채팅방 구성 아이디',
    `chat_room_id` INT UNSIGNED NOT NULL COMMENT '채팅방 아이디',
    `member_id` INT UNSIGNED NOT NULL COMMENT '구성원 아이디',
    PRIMARY KEY (`id`),
    KEY `chat_room_id` (`chat_room_id`),
    KEY `member_id` (`member_id`),
    FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
);

CREATE TABLE `community` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '커뮤니티 아이디',
    `read_count` INT UNSIGNED NOT NULL COMMENT '조회수',
    `member_id` INT UNSIGNED NOT NULL COMMENT '작성자 아이디',
    `content` TEXT NOT NULL COMMENT '커뮤니티 글 내용',
    `title` VARCHAR(65) NOT NULL COMMENT '제목',
    `category` VARCHAR(20) NOT NULL COMMENT '카테고리',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 날짜',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 날짜',
    PRIMARY KEY (`id`),
    KEY `member_id` (`member_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
);

CREATE TABLE `comment` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '댓글 아이디',
    `community_id` INT UNSIGNED NOT NULL COMMENT '커뮤니티 글 아이디',
    `member_id` INT UNSIGNED NOT NULL COMMENT '작성자 아이디',
    `content` TEXT NOT NULL COMMENT '댓글 내용',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 날짜',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 날짜',
    PRIMARY KEY (`id`),
    KEY `community_id` (`community_id`),
    KEY `member_id` (`member_id`),
    FOREIGN KEY (`community_id`) REFERENCES `community` (`id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
);

CREATE TABLE `image` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '커뮤니티 이미지 아이디',
    `community_id` INT UNSIGNED NOT NULL COMMENT '커뮤니티 아이디',
    `url` VARCHAR(255) NOT NULL COMMENT '이미지 url',
    PRIMARY KEY (`id`),
    KEY `community_id` (`community_id`),
    FOREIGN KEY (`community_id`) REFERENCES `community` (`id`)
);

CREATE TABLE `service_type` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '서비스 아이디',
    `key_money` INT NOT NULL COMMENT '권리금 수준 평균, 단위: 만원',
    `key_money_level` DECIMAL(10,2) DEFAULT NULL COMMENT '권리금 수준 ㎡당 평균, 만원/㎡',
    `key_money_ratio` DECIMAL(10,2) DEFAULT NULL COMMENT '권리금 유 비율',
    `large_size` INT NOT NULL COMMENT '대형 크기(m²)',
    `medium_size` INT NOT NULL COMMENT '중형 크기(m²)',
    `small_size` INT NOT NULL COMMENT '소형 크기(m²)',
    `service_code` VARCHAR(255) NOT NULL COMMENT '업종 코드',
    `service_code_name` VARCHAR(255) NOT NULL COMMENT '업종 이름',
    PRIMARY KEY (`id`)
);

CREATE TABLE `rent` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '임대료 아이디',
    `first_floor` INT NOT NULL COMMENT '1층 임대료(단위: 3.3㎡당 월환산임대료, 원)',
    `other_floor` INT NOT NULL COMMENT '1층 외 임대료(단위: 3.3㎡당 월환산임대료, 원)',
    `total` INT NOT NULL COMMENT '전체 층 임대료(단위: 3.3㎡당 월환산임대료, 원)',
    `district_code` VARCHAR(5) NOT NULL COMMENT '자치구 코드',
    `district_code_name` VARCHAR(10) NOT NULL COMMENT '자치구 코드 명',
    PRIMARY KEY (`id`)
);

CREATE TABLE `franchisee` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '프랜차이즈 아이디',
    `area` INT NOT NULL COMMENT '기준점포면적(㎡)',
    `deposit` INT NOT NULL COMMENT '가맹 보증금, 천원',
    `education` INT NOT NULL COMMENT '교육비, 천원',
    `etc` INT NOT NULL COMMENT '기타비용, 천원',
    `interior` INT NOT NULL COMMENT '인테리어 비용, 천원',
    `subscription` INT NOT NULL COMMENT '가입비, 천원',
    `total_levy` INT NOT NULL COMMENT '부담금 합계, 천원',
    `unit_area` INT NOT NULL COMMENT '단위면적(3.3㎡)당 인테리어 비용, 천원',
    `service_type_id` INT UNSIGNED NOT NULL COMMENT '서비스 아이디',
    `brand_name` VARCHAR(255) NOT NULL COMMENT '브랜드 이름',
    PRIMARY KEY (`id`),
    KEY `service_type_id` (`service_type_id`),
    FOREIGN KEY (`service_type_id`) REFERENCES `service_type` (`id`)
);

CREATE TABLE `device_token` (
    `device_token` VARCHAR(255) NOT NULL COMMENT '디바이스 토큰',
    `member_id` INT UNSIGNED NOT NULL COMMENT '회원 아이디',
    PRIMARY KEY (`device_token`),
    KEY `member_id` (`member_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
);

CREATE TABLE `area_district` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '영역 자치구 아이디',햣
    `x` FLOAT NOT NULL COMMENT 'x 좌표 값',
    `y` FLOAT NOT NULL COMMENT 'y 좌표 값',
    `district_code` VARCHAR(5) NOT NULL COMMENT '자치구 코드',
    `district_code_name` VARCHAR(10) NOT NULL COMMENT '자치구 코드 명',
    PRIMARY KEY (`id`),
    KEY `idx_district_code` (`district_code`)
);

CREATE TABLE `change_district` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '상권 변화 지표 자치구 아이디',
    `closed_months` INT NOT NULL COMMENT '폐업 영업 개월 평균',
    `opened_months` INT NOT NULL COMMENT '운영 영업 개월 평균',
    `change_indicator` VARCHAR(5) DEFAULT NULL COMMENT '상권 변화 지표',
    `change_indicator_name` VARCHAR(15) DEFAULT NULL COMMENT '상권 변화 지표 명',
    `district_code` VARCHAR(5) NOT NULL COMMENT '자치구 코드',
    `district_code_name` VARCHAR(10) NOT NULL COMMENT '자치구 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_district_code` (`district_code`)
);

CREATE TABLE `foot_traffic_district` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '유동인구_자치구_아이디',
    `female_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '여성 유동인구 수',
    `male_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '남성 유동인구 수',
    `teen_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 10 유동인구 수',
    `twenty_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 20 유동인구 수',
    `thirty_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 30 유동인구 수',
    `forty_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 40 유동인구 수',
    `fifty_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 50 유동인구 수',
    `sixty_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 60 유동인구 수',
    `total_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '총 유동인구 수',
    `foot_traffic_00` INT UNSIGNED DEFAULT NULL COMMENT '시간대 00 ~ 06 유동인구 수',
    `foot_traffic_06` INT UNSIGNED DEFAULT NULL COMMENT '시간대 06 ~ 11 유동인구 수',
    `foot_traffic_11` INT UNSIGNED DEFAULT NULL COMMENT '시간대 11 ~ 14 유동인구 수',
    `foot_traffic_14` INT UNSIGNED DEFAULT NULL COMMENT '시간대 14 ~ 17 유동인구 수',
    `foot_traffic_17` INT UNSIGNED DEFAULT NULL COMMENT '시간대 17 ~ 21 유동인구 수',
    `foot_traffic_21` INT UNSIGNED DEFAULT NULL COMMENT '시간대 21 ~ 24 유동인구 수',
    `mon_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '월요일 유동인구 수',
    `tue_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '화요일 유동인구 수',
    `wed_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '수요일 유동인구 수',
    `thu_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '목요일 유동인구 수',
    `fri_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '금요일 유동인구 수',
    `sat_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '토요일 유동인구 수',
    `sun_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '일요일 유동인구 수',
    `district_code` VARCHAR(5) NOT NULL COMMENT '자치구 코드',
    `district_code_name` VARCHAR(10) NOT NULL COMMENT '자치구 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_district_code` (`district_code`)
);

CREATE TABLE `income_district` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '소득소비_자치구 아이디',
    `total_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '지출 총금액',
    `district_code` VARCHAR(5) NOT NULL COMMENT '자치구 코드',
    `district_code_name` VARCHAR(10) NOT NULL COMMENT '자치구 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_district_code` (`district_code`)
);

CREATE TABLE `sales_district` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '추정매출_자치구_아이디',
    `female_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '여성 매출 금액',
    `male_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '남성 매출 금액',
    `teen_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '10대 매출 금액',
    `twenty_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '20대 매출 금액',
    `thirty_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '30대 매출 금액',
    `forty_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '40대 매출 금액',
    `fifty_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '50대 매출 금액',
    `sixty_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '60대 매출 금액',
    `month_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '당월 매출 금액',
    `mon_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '월요일 매출 금액',
    `tue_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '화요일 매출 금액',
    `wed_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '수요일 매출 금액',
    `thu_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '목요일 매출 금액',
    `fri_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '금요일 매출 금액',
    `sat_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '토요일 매출 금액',
    `sun_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '일요일 매출 금액',
    `sales_00` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 00 ~ 06 매출 금액',
    `sales_06` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 06 ~ 11 매출 금액',
    `sales_11` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 11 ~ 14 매출 금액',
    `sales_14` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 14 ~ 17 매출 금액',
    `sales_17` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 17 ~ 21 매출 금액',
    `sales_21` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 21 ~ 24 매출 금액',
    `district_code` VARCHAR(5) NOT NULL COMMENT '자치구 코드',
    `district_code_name` VARCHAR(10) NOT NULL COMMENT '자치구 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    `service_code` VARCHAR(8) NOT NULL COMMENT '서비스 업종 코드',
    `service_code_name` VARCHAR(20) NOT NULL COMMENT '서비스 업종 코드 명',
    `service_type` ENUM('RESTAURANT', 'ACADEMY', 'LEISURE', 'SERVICE', 'RETAIL', 'HOUSEHOLDS') DEFAULT NULL COMMENT '서비스 업종 타입',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_district_code` (`district_code`),
    KEY `idx_district_code` (`service_code`)
);

CREATE TABLE `store_district` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '점포_자치구_아이디',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    `district_code` VARCHAR(5) NOT NULL COMMENT '자치구 코드',
    `district_code_name` VARCHAR(10) NOT NULL COMMENT '자치구 코드 명',
    `service_code` VARCHAR(8) NOT NULL COMMENT '서비스 업종 코드',
    `service_code_name` VARCHAR(20) NOT NULL COMMENT '서비스 업종 코드 명',
    `service_type` ENUM('RESTAURANT', 'ACADEMY', 'LEISURE', 'SERVICE', 'RETAIL', 'HOUSEHOLDS') DEFAULT NULL COMMENT '서비스 업종 타입',
    `total_store` INT UNSIGNED DEFAULT NULL COMMENT '점포 수',
    `similar_store` INT UNSIGNED DEFAULT NULL COMMENT '유사 업종 점포 수',
    `opened_store` INT UNSIGNED DEFAULT NULL COMMENT '개업 점포 수',
    `closed_store` INT UNSIGNED DEFAULT NULL COMMENT '폐업 점포 수',
    `franchise_store` INT UNSIGNED DEFAULT NULL COMMENT '프랜차이즈 점포 수',
    `opened_rate` FLOAT DEFAULT NULL COMMENT '개업률',
    `closed_rate` FLOAT DEFAULT NULL COMMENT '폐업률',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_district_code` (`district_code`),
    KEY `idx_service_code` (`service_code`)
);

CREATE TABLE `income_administration` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '소득소비_행정동 아이디',
    `total_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '지출 총금액',
    `administration_code` VARCHAR(10) NOT NULL COMMENT '행정동 코드',
    `administration_code_name` VARCHAR(20) NOT NULL COMMENT '행정동 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_administration_code` (`administration_code`)
);

CREATE TABLE `sales_administration` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '추정매출_행정동_아이디',
    `month_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '당월 매출 금액',
    `weekday_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '주중 매출 금액',
    `weekend_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '주말 매출 금액',
    `administration_code` VARCHAR(10) NOT NULL COMMENT '행정동 코드',
    `administration_code_name` VARCHAR(20) NOT NULL COMMENT '행정동 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    `service_code` VARCHAR(8) NOT NULL COMMENT '서비스 업종 코드',
    `service_code_name` VARCHAR(20) NOT NULL COMMENT '서비스 업종 코드 명',
    `service_type` ENUM('RESTAURANT', 'ACADEMY', 'LEISURE', 'SERVICE', 'RETAIL', 'HOUSEHOLDS') DEFAULT NULL COMMENT '서비스 업종 타입',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_administration_code` (`administration_code`),
    KEY `idx_service_code` (`service_code`)
);

CREATE TABLE `store_administration` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '점포_행정동_아이디',
    `closed_rate` FLOAT DEFAULT NULL COMMENT '폐업률',
    `opened_rate` FLOAT DEFAULT NULL COMMENT '개업률',
    `closed_store` INT UNSIGNED DEFAULT NULL COMMENT '폐업 점포 수',
    `opened_store` INT UNSIGNED DEFAULT NULL COMMENT '개업 점포 수',
    `franchise_store` INT UNSIGNED DEFAULT NULL COMMENT '프랜차이즈 점포 수',
    `similar_store` INT UNSIGNED DEFAULT NULL COMMENT '유사 업종 점포 수',
    `total_store` INT UNSIGNED DEFAULT NULL COMMENT '점포 수',
    `administration_code` VARCHAR(10) NOT NULL COMMENT '행정동 코드',
    `administration_code_name` VARCHAR(20) NOT NULL COMMENT '행정동 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    `service_code` VARCHAR(8) NOT NULL COMMENT '서비스 업종 코드',
    `service_code_name` VARCHAR(20) NOT NULL COMMENT '서비스 업종 코드 명',
    `service_type` ENUM('RESTAURANT', 'ACADEMY', 'LEISURE', 'SERVICE', 'RETAIL', 'HOUSEHOLDS') DEFAULT NULL COMMENT '서비스 업종 타입',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_administration_code` (`administration_code`),
    KEY `idx_service_code` (`service_code`)
);

CREATE TABLE `area_commercial` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '영역_상권 아이디',
    `x` FLOAT NOT NULL COMMENT 'x 좌표 값',
    `y` FLOAT NOT NULL COMMENT 'y 좌표 값',
    `administration_code` VARCHAR(10) NOT NULL COMMENT '행정동 코드',
    `administration_code_name` VARCHAR(20) NOT NULL COMMENT '행정동 코드 명',
    `commercial_classification_code` VARCHAR(1) NOT NULL COMMENT '상권 구분 코드',
    `commercial_classification_code_name` VARCHAR(4) NOT NULL COMMENT '상권 구분 코드 명',
    `commercial_code` VARCHAR(8) NOT NULL COMMENT '상권 코드',
    `commercial_code_name` VARCHAR(80) NOT NULL COMMENT '상권 코드 명',
    `district_code` VARCHAR(5) NOT NULL COMMENT '자치구 코드',
    `district_code_name` VARCHAR(10) NOT NULL COMMENT '자치구 코드 명',
    PRIMARY KEY (`id`),
    KEY `idx_district_code` (`district_code`),
    KEY `idx_administration_code` (`administration_code`)
);

CREATE TABLE `facility_commercial` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '집객시설_상권 아이디',
    `bus_stop_cnt` INT UNSIGNED DEFAULT NULL COMMENT '버스 정거장 수',
    `elementary_school_cnt` INT UNSIGNED DEFAULT NULL COMMENT '초등학교 수',
    `middle_school_cnt` INT UNSIGNED DEFAULT NULL COMMENT '중학교 수',
    `high_school_cnt` INT UNSIGNED DEFAULT NULL COMMENT '고등학교 수',
    `university_cnt` INT UNSIGNED DEFAULT NULL COMMENT '대학교 수',
    `subway_station_cnt` INT UNSIGNED DEFAULT NULL COMMENT '지하철 역 수',
    `facility_cnt` INT UNSIGNED DEFAULT NULL COMMENT '집객 시설 수',
    `commercial_classification_code` VARCHAR(1) NOT NULL COMMENT '상권 구분 코드',
    `commercial_classification_code_name` VARCHAR(4) NOT NULL COMMENT '상권 구분 코드 명',
    `commercial_code` VARCHAR(8) NOT NULL COMMENT '상권 코드',
    `commercial_code_name` VARCHAR(80) NOT NULL COMMENT '상권 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_commercial_code` (`commercial_code`)
);

CREATE TABLE `foot_traffic_commercial` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '유동인구_상권 아이디',
    `total_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '총 유동인구 수',
    `male_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '남성 유동인구 수',
    `female_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '여성 유동인구 수',
    `teen_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 10 유동인구 수',
    `twenty_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 20 유동인구 수',
    `thirty_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 30 유동인구 수',
    `forty_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 40 유동인구 수',
    `fifty_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 50 유동인구 수',
    `sixty_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '연령대 60 이상 유동인구 수',
    `foot_traffic_00` INT UNSIGNED DEFAULT NULL COMMENT '시간대 00 ~ 06 유동인구 수',
    `foot_traffic_06` INT UNSIGNED DEFAULT NULL COMMENT '시간대 06 ~ 11 유동인구 수',
    `foot_traffic_11` INT UNSIGNED DEFAULT NULL COMMENT '시간대 11 ~ 14 유동인구 수',
    `foot_traffic_14` INT UNSIGNED DEFAULT NULL COMMENT '시간대 14 ~ 17 유동인구 수',
    `foot_traffic_17` INT UNSIGNED DEFAULT NULL COMMENT '시간대 17 ~ 21 유동인구 수',
    `foot_traffic_21` INT UNSIGNED DEFAULT NULL COMMENT '시간대 21 ~ 24 유동인구 수',
    `mon_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '월요일 유동인구 수',
    `tue_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '화요일 유동인구 수',
    `wed_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '수요일 유동인구 수',
    `thu_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '목요일 유동인구 수',
    `fri_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '금요일 유동인구 수',
    `sat_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '토요일 유동인구 수',
    `sun_foot_traffic` INT UNSIGNED DEFAULT NULL COMMENT '일요일 유동인구 수',
    `commercial_classification_code` VARCHAR(1) NOT NULL COMMENT '상권 구분 코드',
    `commercial_classification_code_name` VARCHAR(4) NOT NULL COMMENT '상권 구분 코드 명',
    `commercial_code` VARCHAR(8) NOT NULL COMMENT '상권 코드',
    `commercial_code_name` VARCHAR(80) NOT NULL COMMENT '상권 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_commercial_code` (`commercial_code`)
);

CREATE TABLE `income_commercial` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '소득소비_상권 아이디',
    `income_section_code` INT DEFAULT NULL COMMENT '소득 구간 코드',
    `month_avg_income` BIGINT UNSIGNED DEFAULT NULL COMMENT '월 평균 소득 금액',
    `total_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '지출 총금액',
    `grocery_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '식료품 지출 총금액',
    `clothes_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '의류 신발 지출 총금액',
    `medical_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '의료비 지출 총금액',
    `life_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '생활용품 지출 총금액',
    `traffic_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '교통 지출 총금액',
    `leisure_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '여가 지출 총금액',
    `culture_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '문화 지출 총금액',
    `education_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '교육 지출 총금액',
    `luxury_price` BIGINT UNSIGNED DEFAULT NULL COMMENT '유흥 총금액',
    `commercial_classification_code` VARCHAR(1) NOT NULL COMMENT '상권 구분 코드',
    `commercial_classification_code_name` VARCHAR(4) NOT NULL COMMENT '상권 구분 코드 명',
    `commercial_code` VARCHAR(8) NOT NULL COMMENT '상권 코드',
    `commercial_code_name` VARCHAR(80) NOT NULL COMMENT '상권 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_commercial_code` (`commercial_code`)
);

CREATE TABLE `population_commercial` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '상주인구_상권 아이디',
    `total_population` INT UNSIGNED DEFAULT NULL COMMENT '총 상주인구 수',
    `male_population` INT UNSIGNED DEFAULT NULL COMMENT '남성 상주인구 수',
    `female_population` INT UNSIGNED DEFAULT NULL COMMENT '여성 상주인구 수',
    `teen_population` INT UNSIGNED DEFAULT NULL COMMENT '연령대 10 상주인구 수',
    `twenty_population` INT UNSIGNED DEFAULT NULL COMMENT '연령대 20 상주인구 수',
    `thirty_population` INT UNSIGNED DEFAULT NULL COMMENT '연령대 30 상주인구 수',
    `forty_population` INT UNSIGNED DEFAULT NULL COMMENT '연령대 40 상주인구 수',
    `fifty_population` INT UNSIGNED DEFAULT NULL COMMENT '연령대 50 상주인구 수',
    `sixty_population` INT UNSIGNED DEFAULT NULL COMMENT '연령대 60 이상 상주인구 수',
    `commercial_classification_code` VARCHAR(1) NOT NULL COMMENT '상권 구분 코드',
    `commercial_classification_code_name` VARCHAR(4) NOT NULL COMMENT '상권 구분 코드 명',
    `commercial_code` VARCHAR(8) NOT NULL COMMENT '상권 코드',
    `commercial_code_name` VARCHAR(80) NOT NULL COMMENT '상권 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_commercial_code` (`commercial_code`)
);

CREATE TABLE `sales_commercial` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '추정매출_상권 아이디',
    `month_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '당월 매출 금액',
    `male_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '남성 매출 금액',
    `female_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '여성 매출 금액',
    `teen_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '연령대 10 매출 금액',
    `twenty_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '연령대 20 매출 금액',
    `thirty_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '연령대 30 매출 금액',
    `forty_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '연령대 40 매출 금액',
    `fifty_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '연령대 50 매출 금액',
    `sixty_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '연령대 60 이상 매출 금액',
    `sales_00` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 00 ~ 06 매출 금액',
    `sales_06` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 06 ~ 11 매출 금액',
    `sales_11` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 11 ~ 14 매출 금액',
    `sales_14` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 14 ~ 17 매출 금액',
    `sales_17` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 17 ~ 21 매출 금액',
    `sales_21` BIGINT UNSIGNED DEFAULT NULL COMMENT '시간대 21 ~ 24 매출 금액',
    `mon_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '월요일 매출 금액',
    `tue_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '화요일 매출 금액',
    `wed_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '수요일 매출 금액',
    `thu_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '목요일 매출 금액',
    `fri_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '금요일 매출 금액',
    `sat_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '토요일 매출 금액',
    `sun_sales` BIGINT UNSIGNED DEFAULT NULL COMMENT '일요일 매출 금액',
    `commercial_classification_code` VARCHAR(1) NOT NULL COMMENT '상권 구분 코드',
    `commercial_classification_code_name` VARCHAR(4) NOT NULL COMMENT '상권 구분 코드 명',
    `commercial_code` VARCHAR(8) NOT NULL COMMENT '상권 코드',
    `commercial_code_name` VARCHAR(80) NOT NULL COMMENT '상권 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    `service_code` VARCHAR(8) NOT NULL COMMENT '서비스 업종 코드',
    `service_code_name` VARCHAR(20) NOT NULL COMMENT '서비스 업종 코드명',
    `service_type` ENUM('RESTAURANT', 'ACADEMY', 'LEISURE', 'SERVICE', 'RETAIL', 'HOUSEHOLDS') DEFAULT NULL COMMENT '서비스 업종 타입',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_commercial_code` (`commercial_code`),
    KEY `idx_service_code` (`service_code`)
);

CREATE TABLE `store_commercial` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '점포_상권 아이디',
    `total_store` INT UNSIGNED DEFAULT NULL COMMENT '점포 수',
    `similar_store` INT UNSIGNED DEFAULT NULL COMMENT '유사 업종 점포 수',
    `opened_store` INT UNSIGNED DEFAULT NULL COMMENT '개업 점포 수',
    `closed_store` INT UNSIGNED DEFAULT NULL COMMENT '폐업 점포 수',
    `franchise_store` INT UNSIGNED DEFAULT NULL COMMENT '프랜차이즈 점포 수',
    `opened_rate` FLOAT DEFAULT NULL COMMENT '개업률',
    `closed_rate` FLOAT DEFAULT NULL COMMENT '폐업률',
    `commercial_classification_code` VARCHAR(1) NOT NULL COMMENT '상권 구분 코드',
    `commercial_classification_code_name` VARCHAR(4) NOT NULL COMMENT '상권 구분 코드 명',
    `commercial_code` VARCHAR(8) NOT NULL COMMENT '상권 코드',
    `commercial_code_name` VARCHAR(80) NOT NULL COMMENT '상권 코드 명',
    `period_code` VARCHAR(5) NOT NULL COMMENT '기준 년분기 코드',
    `service_code` VARCHAR(8) NOT NULL COMMENT '서비스 업종 코드',
    `service_code_name` VARCHAR(20) NOT NULL COMMENT '서비스 업종 코드명',
    `service_type` ENUM('RESTAURANT','ACADEMY','LEISURE','SERVICE','RETAIL','HOUSEHOLDS') DEFAULT NULL COMMENT '서비스 업종 타입',
    PRIMARY KEY (`id`),
    KEY `idx_period_code` (`period_code`),
    KEY `idx_commercial_code` (`commercial_code`),
    KEY `idx_service_code` (`service_code`)
);