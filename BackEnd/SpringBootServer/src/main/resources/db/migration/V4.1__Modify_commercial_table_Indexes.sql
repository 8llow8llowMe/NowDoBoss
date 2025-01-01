-- V4.1__Modify_commercial_table_Indexes.sql

-- 상권 도메인 테이블 관련 단일 인덱스 삭제 및 복합 인덱스 추가

-- facility_commercial 테이블 단일 인덱스 삭제
DROP INDEX idx_period_code ON facility_commercial;
DROP INDEX idx_commercial_code ON facility_commercial;

-- foot_traffic_commercial 테이블 단일 인덱스 삭제
DROP INDEX idx_period_code ON foot_traffic_commercial;
DROP INDEX idx_commercial_code ON foot_traffic_commercial;

-- income_commercial 테이블 단일 인덱스 삭제
DROP INDEX idx_period_code ON income_commercial;
DROP INDEX idx_commercial_code ON income_commercial;

-- population_commercial 테이블 단일 인덱스 삭제
DROP INDEX idx_period_code ON population_commercial;
DROP INDEX idx_commercial_code ON population_commercial;

-- sales_commercial 테이블 단일 인덱스 삭제
DROP INDEX idx_period_code ON sales_commercial;
DROP INDEX idx_commercial_code ON sales_commercial;
DROP INDEX idx_service_code ON sales_commercial;

-- store_commercial 테이블 단일 인덱스 삭제
DROP INDEX idx_period_code ON store_commercial;
DROP INDEX idx_commercial_code ON store_commercial;
DROP INDEX idx_service_code ON store_commercial;


-- facility_commercial 테이블 복합 인덱스 추가
CREATE INDEX idx_period_commercial
    ON facility_commercial (period_code, commercial_code);

-- foot_traffic_commercial 테이블 복합 인덱스 추가
CREATE INDEX idx_period_commercial
    ON foot_traffic_commercial (period_code, commercial_code);

-- income_commercial 테이블 복합 인덱스 추가
CREATE INDEX idx_period_commercial
    ON income_commercial (period_code, commercial_code);

-- population_commercial 테이블 복합 인덱스 추가
CREATE INDEX idx_period_commercial
    ON population_commercial (period_code, commercial_code);

-- sales_commercial 테이블 복합 인덱스 추가
CREATE INDEX idx_commercial_service
    ON sales_commercial (period_code, commercial_code, service_code);

-- StoreCommercial 테이블 복합 인덱스 추가
CREATE INDEX idx_period_commercial_service
    ON store_commercial (period_code, commercial_code, service_code);
CREATE INDEX idx_period_commercial_service_type
    ON store_commercial (period_code, commercial_code, service_type);
