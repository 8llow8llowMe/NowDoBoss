package com.ssafy.backend.domain.commercial.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
    @Index(name = "idx_period_commercial", columnList = "periodCode, commercialCode")
})
public class PopulationCommercial {

    @Id
    @Comment("상주인구_상권 아이디")
    @Column(columnDefinition = "INT UNSIGNED")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("기준 년분기 코드")
    @Column(columnDefinition = "VARCHAR(5)", nullable = false)
    private String periodCode;

    @Comment("상권 구분 코드")
    @Column(columnDefinition = "VARCHAR(1)", nullable = false)
    private String commercialClassificationCode;

    @Comment("상권 구분 코드 명")
    @Column(columnDefinition = "VARCHAR(4)", nullable = false)
    private String commercialClassificationCodeName;

    @Comment("상권 코드")
    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    private String commercialCode;

    @Comment("상권 코드 명")
    @Column(columnDefinition = "VARCHAR(80)", nullable = false)
    private String commercialCodeName;

    @Comment("총 상주인구 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long totalPopulation;

    @Comment("남성 상주인구 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long malePopulation;

    @Comment("여성 상주인구 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long femalePopulation;

    @Comment("연령대 10 상주인구 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long teenPopulation;

    @Comment("연령대 20 상주인구 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long twentyPopulation;

    @Comment("연령대 30 상주인구 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long thirtyPopulation;

    @Comment("연령대 40 상주인구 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long fortyPopulation;

    @Comment("연령대 50 상주인구 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long fiftyPopulation;

    @Comment("연령대 60 이상 상주인구 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long sixtyPopulation;
}
