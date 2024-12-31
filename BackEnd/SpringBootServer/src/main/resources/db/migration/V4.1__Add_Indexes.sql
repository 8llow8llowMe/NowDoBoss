-- V4.1__Add_Indexes.sql

-- FacilityCommercial 테이블 복합 인덱스 추가
CREATE INDEX idx_period_commercial
    ON facility_commercial (period_code, commercial_code);

-- FootTrafficCommercial 테이블 복합 인덱스 추가
CREATE INDEX idx_period_commercial
    ON foot_traffic_commercial (period_code, commercial_code);

-- IncomeCommercial 테이블 복합 인덱스 추가
CREATE INDEX idx_period_commercial
    ON income_commercial (period_code, commercial_code);

-- PopulationCommercial 테이블 복합 인덱스 추가
CREATE INDEX idx_period_commercial
    ON population_commercial (period_code, commercial_code);

-- SalesCommercial 테이블 복합 인덱스 추가
CREATE INDEX idx_commercial_service
    ON sales_commercial (period_code, commercial_code, service_code);

-- StoreCommercial 테이블 복합 인덱스 추가
CREATE INDEX idx_period_commercial_service
    ON store_commercial (period_code, commercial_code, service_code);

CREATE INDEX idx_period_commercial_service_type
    ON store_commercial (period_code, commercial_code, service_type);
