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
public class IncomeCommercial {

    @Id
    @Comment("소득소비_상권 아이디")
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

    @Comment("월 평균 소득 금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long monthAvgIncome;

    @Comment("소득 구간 코드")
    @Column(columnDefinition = "INT")
    private Integer incomeSectionCode;

    @Comment("지출 총금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long totalPrice;

    @Comment("식료품 지출 총금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long groceryPrice;

    @Comment("의류 신발 지출 총금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long clothesPrice;

    @Comment("의료비 지출 총금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long medicalPrice;

    @Comment("생활용품 지출 총금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long lifePrice;

    @Comment("교통 지출 총금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long trafficPrice;

    @Comment("여가 지출 총금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long leisurePrice;

    @Comment("문화 지출 총금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long culturePrice;

    @Comment("교육 지출 총금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long educationPrice;

    @Comment("유흥 총금액")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long luxuryPrice;
}
