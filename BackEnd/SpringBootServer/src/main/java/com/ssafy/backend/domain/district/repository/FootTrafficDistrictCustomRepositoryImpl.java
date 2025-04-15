package com.ssafy.backend.domain.district.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.backend.domain.district.dto.response.FootTrafficDistrictTopTenResponse;
import com.ssafy.backend.domain.district.entity.QFootTrafficDistrict;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FootTrafficDistrictCustomRepositoryImpl implements
    FootTrafficDistrictCustomRepository {

    private static final int GROUP_SIZE = 5;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<FootTrafficDistrictTopTenResponse> getTopTenFootTrafficDistrictByPeriodCode() {
        QFootTrafficDistrict f1 = QFootTrafficDistrict.footTrafficDistrict;
        QFootTrafficDistrict f2 = new QFootTrafficDistrict("f2");

        // 1. 이전 분기("20232")와 비교하여 유동인구 변화율을 계산한 결과를 record dto 형태로 조회
        // record 구조는 생성자 기반이므로 모든 파라미터를 constructor에 맞춰 넘겨야 함
        // level은 post-processing으로 계산하므로 Expressions.constant(0)으로 임시 값 채움
        List<FootTrafficDistrictTopTenResponse> rawResults = queryFactory
            .select(
                Projections.constructor(
                    FootTrafficDistrictTopTenResponse.class,
                    f1.districtCode, // 자치구 코드
                    f1.districtCodeName, // 자치구 코드명 (자치구 이름)
                    f2.totalFootTraffic, // 최근 분기 유동인구 수
                    f2.totalFootTraffic.doubleValue() // 변화율 계산 공식
                        .subtract(f1.totalFootTraffic.doubleValue())
                        .divide(f1.totalFootTraffic)
                        .multiply(100.0),
                    Expressions.constant(0) // level은 후처리로 설정
                )
            )
            .from(f1)
            .join(f2)
            .on(f1.districtCode.eq(f2.districtCode)) // 자치구 코드 기준으로 self join (과거 vs 현재 비교)
            .where(
                f1.periodCode.eq("20232"), // 비교 대상: 이전 분기
                f2.periodCode.eq("20233") // 기준 대상: 최근 분기
            )
            .orderBy(f2.totalFootTraffic.desc()) // 정렬 기준: 최근 분기 유동인구
            .fetch();

        // 2. Stream을 활용해 각 항목에 level 부여 후 새 record 인스턴스로 재생성
        // 5개 단위로 레벨이 올라감 (ex. 1~5위 -> level = 1, 6~10위 -> level = 2)
        return IntStream.range(0, rawResults.size())
            .mapToObj(i -> {
                FootTrafficDistrictTopTenResponse response = rawResults.get(i);
                int level = (i / GROUP_SIZE) + 1;

                // 새 인스턴스로 레벨 반영하여 재생성 (빌더 패턴 이용)
                return FootTrafficDistrictTopTenResponse.builder()
                    .districtCode(response.districtCode())
                    .districtCodeName(response.districtCodeName())
                    .total(response.total())
                    .totalRate(response.totalRate())
                    .level(level)
                    .build();
            })
            .toList();
    }
}
