-- V4.3__Drop_indexes.sql

-- income_administration 테이블 단일 인덱스 삭제
DROP INDEX idx_period_code ON income_administration;
DROP INDEX idx_administration_code ON income_administration;

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