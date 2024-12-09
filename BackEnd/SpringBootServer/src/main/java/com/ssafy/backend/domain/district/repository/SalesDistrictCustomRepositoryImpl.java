package com.ssafy.backend.domain.district.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.backend.domain.district.dto.info.SalesDistrictMonthSalesTopFiveInfo;
import com.ssafy.backend.domain.district.dto.response.SalesDistrictTopTenResponse;
import com.ssafy.backend.domain.district.entity.QSalesDistrict;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SalesDistrictCustomRepositoryImpl implements SalesDistrictCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SalesDistrictTopTenResponse> getTopTenSalesDistrictByPeriodCode() {
        QSalesDistrict sd = QSalesDistrict.salesDistrict;

        // 상위 판매 구역 정보와 추가 계산을 포함하여 가져오는 쿼리
        List<Tuple> districtData = queryFactory
            .select(sd.districtCode,
                sd.districtCodeName,
                new CaseBuilder().when(sd.periodCode.eq("20233")).then(sd.monthSales).otherwise(0L)
                    .sum().as("total"),
                new CaseBuilder().when(sd.periodCode.eq("20233")).then(sd.monthSales).otherwise(0L)
                    .sum().doubleValue()
                    .subtract(new CaseBuilder().when(sd.periodCode.eq("20232")).then(sd.monthSales)
                        .otherwise(0L).sum().doubleValue())
                    .divide(new CaseBuilder().when(sd.periodCode.eq("20232")).then(sd.monthSales)
                        .otherwise(0L).sum().doubleValue())
                    .multiply(100).as("totalRate")
            )
            .from(sd)
//                .where(sd.districtCodeName.in(topDistrictNames))
            .groupBy(sd.districtCode, sd.districtCodeName)
            .orderBy(
                new CaseBuilder().when(sd.periodCode.eq("20233")).then(sd.monthSales).otherwise(0L)
                    .sum().desc())
            .fetch();

        List<SalesDistrictTopTenResponse> responses = new ArrayList<>();
        int level = 0;
        for (int i = 0; i < districtData.size(); i++) {
            if (i % 5 == 0) {
                level++; // 10개 단위로 level 증가
            }
            SalesDistrictTopTenResponse response = new SalesDistrictTopTenResponse(
                districtData.get(i).get(sd.districtCode),
                districtData.get(i).get(sd.districtCodeName),
                districtData.get(i).get(Expressions.numberPath(Long.class, "total")),
                districtData.get(i).get(Expressions.numberPath(Double.class, "totalRate")),
                level
            );
            responses.add(response);
        }
        return responses;
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
