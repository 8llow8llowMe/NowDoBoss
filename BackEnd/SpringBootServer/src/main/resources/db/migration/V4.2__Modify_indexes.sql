-- V4.2__Modify_indexes.sql

-- 기존 단일 인덱스 삭제 및 필요한 복합 인덱스 추가

-- FacilityCommercial 테이블
DROP INDEX IF EXISTS idx_period_code ON facility_commercial;
DROP INDEX IF EXISTS idx_commercial_code ON facility_commercial;
-- 복합 인덱스 유지 (이미 추가됨): idx_period_commercial

-- FootTrafficCommercial 테이블
DROP INDEX IF EXISTS idx_period_code ON foot_traffic_commercial;
DROP INDEX IF EXISTS idx_commercial_code ON foot_traffic_commercial;
-- 복합 인덱스 유지 (이미 추가됨): idx_period_commercial

-- IncomeCommercial 테이블
DROP INDEX IF EXISTS idx_period_code ON income_commercial;
DROP INDEX IF EXISTS idx_commercial_code ON income_commercial;
-- 복합 인덱스 유지 (이미 추가됨): idx_period_commercial

-- PopulationCommercial 테이블
DROP INDEX IF EXISTS idx_period_code ON population_commercial;
DROP INDEX IF EXISTS idx_commercial_code ON population_commercial;
-- 복합 인덱스 유지 (이미 추가됨): idx_period_commercial

-- SalesCommercial 테이블
DROP INDEX IF EXISTS idx_period_code ON sales_commercial;
DROP INDEX IF EXISTS idx_commercial_code ON sales_commercial;
DROP INDEX IF EXISTS idx_service_code ON sales_commercial;
-- 복합 인덱스 유지 (이미 추가됨): idx_commercial_service

-- StoreCommercial 테이블
DROP INDEX IF EXISTS idx_period_code ON store_commercial;
DROP INDEX IF EXISTS idx_commercial_code ON store_commercial;
DROP INDEX IF EXISTS idx_service_code ON store_commercial;
-- 복합 인덱스 유지 (이미 추가됨): idx_period_commercial_service, idx_period_commercial_service_type

-- IncomeAdministration 테이블
DROP INDEX IF EXISTS idx_period_code ON income_administration;
DROP INDEX IF EXISTS idx_administration_code ON income_administration;
CREATE INDEX IF NOT EXISTS idx_period_administration ON income_administration (period_code, administration_code);

-- SalesAdministration 테이블
DROP INDEX IF EXISTS idx_period_code ON sales_administration;
DROP INDEX IF EXISTS idx_administration_code ON sales_administration;
DROP INDEX IF EXISTS idx_service_code ON sales_administration;
CREATE INDEX IF NOT EXISTS idx_period_administration_service ON sales_administration (period_code, administration_code, service_code);

-- AreaCommercial 테이블
DROP INDEX IF EXISTS idx_district_code ON area_commercial;
DROP INDEX IF EXISTS idx_administration_code ON area_commercial;
CREATE INDEX IF NOT EXISTS idx_district_code ON area_commercial (district_code);
CREATE INDEX IF NOT EXISTS idx_administration_code ON area_commercial (administration_code);
