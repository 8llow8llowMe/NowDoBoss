package com.ssafy.backend.domain.district.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.backend.domain.district.dto.info.SalesDistrictMonthSalesTopFiveInfo;
import com.ssafy.backend.domain.district.dto.response.SalesDistrictTopTenResponse;
import com.ssafy.backend.domain.district.entity.QSalesDistrict;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SalesDistrictCustomRepositoryImpl implements SalesDistrictCustomRepository {

    private static final int GROUP_SIZE = 5;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<SalesDistrictTopTenResponse> getTopTenSalesDistrictByPeriodCode() {
        QSalesDistrict s1 = QSalesDistrict.salesDistrict;
        QSalesDistrict s2 = new QSalesDistrict("s2");

        // 1. 최신 분기("20233") 기준으로 매출 상위 10개 자치구 코드 추출
        // TODO: 기준 분기는 추후 파라미터화 필요
        List<String> topTenDistrictCodes = queryFactory
            .select(s2.districtCode)
            .from(s2)
            .where(s2.periodCode.eq("20233")) // 최신 기준
            .groupBy(s2.districtCode)
            .orderBy(s2.monthSales.sum().desc()) // 월 매출 합 기준 내림차순 정렬
            .limit(10)
            .fetch();

        // 2. 이전 분기("20232")와의 비교를 통해 매출 변화율 계산
        // Projections.constructor 방식은 Record와 같이 불변 객체에 적합
        // level 값은 후처리 예정이므로 임시로 Expressions.constant(0) 처리
        List<SalesDistrictTopTenResponse> rawResults = queryFactory
            .select(
                Projections.constructor(
                    SalesDistrictTopTenResponse.class,
                    s1.districtCode, // 자치구 코드
                    s1.districtCodeName, // 자치구 이름
                    s1.monthSales.sum().as("total"), // 최신 분기 매출 총합
                    s1.monthSales.sum().doubleValue() // 매출 변화율 계산
                        .subtract(
                            JPAExpressions
                                .select(s2.monthSales.sum().doubleValue())
                                .from(s2)
                                .where(
                                    s2.districtCode.eq(s1.districtCode),
                                    s2.periodCode.eq("20232")
                                )
                        )
                        .divide(
                            JPAExpressions
                                .select(s2.monthSales.sum().doubleValue())
                                .from(s2)
                                .where(
                                    s2.districtCode.eq(s1.districtCode),
                                    s2.periodCode.eq("20232")
                                )
                        )
                        .multiply(100.0),
                    Expressions.constant(0) // level은 후처리에서 계산
                )
            )
            .from(s1)
            .where(
                s1.periodCode.eq("20233"), // 기준 분기 조건
                s1.districtCode.in(topTenDistrictCodes) // 상위 10개 자치구만 조회
            )
            .groupBy(s1.districtCode, s1.districtCodeName)
            .orderBy(s1.monthSales.sum().desc()) // 매출 기준 정렬
            .fetch();

        // 3. Stream을 활용해 각 항목에 level 부여 후 새 record 인스턴스로 재생성
        // 5개 단위로 레벨이 올라감 (ex. 1~5위 -> level = 1, 6~10위 -> level = 2)
        return IntStream.range(0, rawResults.size())
            .mapToObj(i -> {
                SalesDistrictTopTenResponse r = rawResults.get(i);
                int level = (i / GROUP_SIZE) + 1;

                // 새 인스턴스로 레벨 반영하여 재생성 (빌더 패턴 이용)
                return SalesDistrictTopTenResponse.builder()
                    .districtCode(r.districtCode())
                    .districtCodeName(r.districtCodeName())
                    .total(r.total())
                    .totalRate(r.totalRate())
                    .level(level)
                    .build();
            })
            .toList();
    }


    @Override
    public List<SalesDistrictMonthSalesTopFiveInfo> getTopFiveMonthSalesByServiceCode(
        String districtCode, String periodCode) {
        QSalesDistrict sd = QSalesDistrict.salesDistrict;
        QSalesDistrict s2 = new QSalesDistrict("s2");

        SubQueryExpression<Double> periodCode20232Query = JPAExpressions
            .select(s2.monthSales.doubleValue())
            .from(s2)
            .where(s2.districtCode.eq(districtCode), s2.periodCode.eq("20232"),
                s2.serviceType.isNotNull(), s2.serviceCode.eq(sd.serviceCode));

        return queryFactory
            .select(Projections.constructor(
                SalesDistrictMonthSalesTopFiveInfo.class,
                sd.serviceCode,
                sd.serviceCodeName,
                sd.monthSales.doubleValue().subtract(periodCode20232Query)
                    .divide(periodCode20232Query).multiply(100)
            ))
            .from(sd)
            .where(sd.districtCode.eq(districtCode), sd.periodCode.eq(periodCode),
                sd.serviceType.isNotNull())
            .orderBy(sd.monthSales.desc())
            .limit(5)
            .fetch();
    }

}
