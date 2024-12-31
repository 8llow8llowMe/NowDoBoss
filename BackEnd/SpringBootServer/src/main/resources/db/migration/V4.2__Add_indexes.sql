-- V4.2__Add_indexes.sql

-- IncomeAdministration 테이블 복합 인덱스 추가
CREATE INDEX idx_period_administration
    ON income_administration (period_code, administration_code);

-- SalesAdministration 테이블 복합 인덱스 추가
CREATE INDEX idx_period_administration_service
    ON sales_administration (period_code, administration_code, service_code);

-- StoreAdministration 테이블 단일 인덱스 추가
CREATE INDEX idx_period_code
    ON store_administration (period_code);
CREATE INDEX idx_service_code
    ON store_administration (service_code);
CREATE INDEX idx_administration_code
    ON store_administration (administration_code);

