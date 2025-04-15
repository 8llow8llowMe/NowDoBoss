package com.ssafy.backend.domain.district.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.backend.domain.district.dto.info.StoreDistrictTotalTopEightInfo;
import com.ssafy.backend.domain.district.dto.response.ClosedStoreDistrictTopTenResponse;
import com.ssafy.backend.domain.district.dto.response.OpenedStoreDistrictTopTenResponse;
import com.ssafy.backend.domain.district.entity.QStoreDistrict;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreDistrictCustomRepositoryImpl implements StoreDistrictCustomRepository {

    private static final int GROUP_SIZE = 5;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<OpenedStoreDistrictTopTenResponse> getTopTenOpenedStoreDistrictByPeriodCode() {
        QStoreDistrict sd = QStoreDistrict.storeDistrict;

        // 1. 분석 기준이 되는 년분기 코드 정의
        // TODO: 하드코딩된 분기 값은 향후 파라미터로 받아서 동적으로 처리하는 방식으로 개선 가능
        String currentPeriod = "20233"; // 기준 분기 (최근)
        String previousPeriod = "20232"; // 비교 분기 (이전)

        // 2. 현재 분기 개업률 계산: (개업 / 전체 점포) * 100
        NumberExpression<Double> currentOpenedSum = new CaseBuilder()
            .when(sd.periodCode.eq(currentPeriod)).then(sd.openedStore)
            .otherwise(0L).sum().doubleValue();

        NumberExpression<Double> currentTotalSum = new CaseBuilder()
            .when(sd.periodCode.eq(currentPeriod)).then(sd.totalStore)
            .otherwise(0L).sum().doubleValue();

        NumberExpression<Double> currentRate = currentOpenedSum.divide(currentTotalSum)
            .multiply(100.0); // 현재 개업률 (백분율)

        // 3. 이전 분기 개업률 계산: (개업 점포 수 / 전체 점포 수) * 100
        NumberExpression<Double> prevOpenedSum = new CaseBuilder()
            .when(sd.periodCode.eq(previousPeriod)).then(sd.openedStore)
            .otherwise(0L).sum().doubleValue();

        NumberExpression<Double> prevTotalSum = new CaseBuilder()
            .when(sd.periodCode.eq(previousPeriod)).then(sd.totalStore)
            .otherwise(0L).sum().doubleValue();

        NumberExpression<Double> prevRate = prevOpenedSum.divide(prevTotalSum)
            .multiply(100.0); // 이전 개업률 (백분율)

        // 4. 변화율 계산: ((현재 개업률 - 이전 개업률) / 이전 개업률) * 100
        NumberExpression<Double> changeRate = currentRate.subtract(prevRate)
            .divide(prevRate)
            .multiply(100.0); // 개업률 변화율

        // 5. 최종 쿼리 실행 - DTO 생성자 기반 Projection
        // 개업률 및 변화율을 계산하여 record 구조 DTO로 반환
        List<OpenedStoreDistrictTopTenResponse> rawResults = queryFactory
            .select(Projections.constructor(
                OpenedStoreDistrictTopTenResponse.class,
                sd.districtCode, // 자치구 코드
                sd.districtCodeName, // 자치구 이름
                currentRate, // 최신 분기 개업률
                changeRate, // 개업률 변화율
                Expressions.constant(0) // level은 후처리로 설정
            ))
            .from(sd)
            .groupBy(sd.districtCode, sd.districtCodeName)
            .orderBy(currentRate.desc()) // 정렬 기준: 최신 개업률 높은 순
            .fetch();

        // 6. Stream을 활용하여 각 자치구에 레벨 부여
        // 5개 단위로 레벨 설정 (예: 1~5위 -> level 1, 6~10위 -> level 2 ...)
        return IntStream.range(0, rawResults.size())
            .mapToObj(i -> {
                OpenedStoreDistrictTopTenResponse res = rawResults.get(i);
                int level = (i / GROUP_SIZE) + 1;

                // 새 인스턴스로 레벨 반영하여 재생성 (빌더 패턴 이용)
                return OpenedStoreDistrictTopTenResponse.builder()
                    .districtCode(res.districtCode())
                    .districtCodeName(res.districtCodeName())
                    .total(res.total()) // 최신 개업률
                    .totalRate(res.totalRate()) // 변화율
                    .level(level)
                    .build();
            })
            .toList();
    }

    @Override
    public List<ClosedStoreDistrictTopTenResponse> getTopTenClosedStoreDistrictByPeriodCode() {
        QStoreDistrict sd = QStoreDistrict.storeDistrict;

        List<Tuple> districtData = queryFactory
            .select(sd.districtCode,
                sd.districtCodeName,
                new CaseBuilder().when(sd.periodCode.eq("20233")).then(sd.closedStore).otherwise(0L)
                    .sum().doubleValue()
                    .divide(new CaseBuilder().when(sd.periodCode.eq("20233")).then(sd.totalStore)
                        .otherwise(0L).sum().doubleValue())
                    .multiply(100).as("total"),
                new CaseBuilder().when(sd.periodCode.eq("20233")).then(sd.closedStore).otherwise(0L)
                    .sum().doubleValue()
                    .divide(new CaseBuilder().when(sd.periodCode.eq("20233")).then(sd.totalStore)
                        .otherwise(0L).sum().doubleValue())
                    .multiply(100)
                    .subtract(new CaseBuilder().when(sd.periodCode.eq("20232")).then(sd.closedStore)
                        .otherwise(0L).sum().doubleValue()
                        .divide(
                            new CaseBuilder().when(sd.periodCode.eq("20232")).then(sd.totalStore)
                                .otherwise(0L).sum().doubleValue())
                        .multiply(100))
                    .divide(new CaseBuilder().when(sd.periodCode.eq("20232")).then(sd.closedStore)
                        .otherwise(0L).sum().doubleValue()
                        .divide(
                            new CaseBuilder().when(sd.periodCode.eq("20232")).then(sd.totalStore)
                                .otherwise(0L).sum().doubleValue())
                        .multiply(100)).multiply(100).as("totalRate")
            )
            .from(sd)
//                .where(sd.districtCodeName.in(topDistrictNames))
            .groupBy(sd.districtCode, sd.districtCodeName)
            .orderBy(
                new CaseBuilder().when(sd.periodCode.eq("20233")).then(sd.closedStore).otherwise(0L)
                    .sum()
                    .divide(new CaseBuilder().when(sd.periodCode.eq("20233")).then(sd.totalStore)
                        .otherwise(0L).sum())
                    .desc())
            .fetch();

        List<ClosedStoreDistrictTopTenResponse> responses = new ArrayList<>();
        int level = 0;
        for (int i = 0; i < districtData.size(); i++) {
            if (i % 5 == 0) {
                level++; // 10개 단위로 level 증가
            }
            ClosedStoreDistrictTopTenResponse response = new ClosedStoreDistrictTopTenResponse(
                districtData.get(i).get(sd.districtCode),
                districtData.get(i).get(sd.districtCodeName),
                districtData.get(i).get(Expressions.numberPath(Double.class, "total")),
                districtData.get(i).get(Expressions.numberPath(Double.class, "totalRate")),
                level
            );
            responses.add(response);
        }
        return responses;
    }

    @Override
    public List<StoreDistrictTotalTopEightInfo> getTopEightTotalStoreByServiceCode(
        String periodCode, String districtCode) {
        QStoreDistrict storeDistrict = QStoreDistrict.storeDistrict;

        return queryFactory
            .select(Projections.constructor(
                StoreDistrictTotalTopEightInfo.class,
                storeDistrict.serviceCode,
                storeDistrict.serviceCodeName,
//                        new CaseBuilder()
//                                .when(storeDistrict.periodCode.eq("20233"))
//                                .then(storeDistrict.totalStore)
//                                .otherwise(0L)
//                                .doubleValue()
//                                .subtract(periodCode20232Query)
//                                .divide(periodCode20232Query)
                storeDistrict.totalStore.sum()
            ))
            .from(storeDistrict)
            .groupBy(storeDistrict.serviceCode, storeDistrict.serviceCodeName)
            .where(storeDistrict.periodCode.eq(periodCode)
                .and(storeDistrict.districtCode.eq(districtCode))
                .and(storeDistrict.serviceType.isNotNull()))
            //.orderBy(new CaseBuilder().when(storeDistrict.periodCode.eq("20233")).then(storeDistrict.totalStore).otherwise(0L).desc())
            .orderBy(storeDistrict.totalStore.sum().desc())
            .limit(8)
            .fetch();
    }


}

