package com.ssafy.backend.domain.administration.entity;

import com.ssafy.backend.domain.district.entity.enums.ServiceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Index(name = "idx_period_code", columnList = "periodCode"),
    @Index(name = "idx_administration_code", columnList = "administrationCode"),
    @Index(name = "idx_service_code", columnList = "serviceCode")
})
public class StoreAdministration {

    @Id
    @Comment("점포_행정동_아이디")
    @Column(columnDefinition = "INT UNSIGNED")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("기준 년분기 코드")
    @Column(columnDefinition = "VARCHAR(5)", nullable = false)
    private String periodCode;

    @Comment("행정동 코드")
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String administrationCode;

    @Comment("행정동 코드 명")
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String administrationCodeName;

    @Comment("서비스 업종 코드")
    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    private String serviceCode;

    @Comment("서비스 업종 코드 명")
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String serviceCodeName;

    @Enumerated(EnumType.STRING)
    @Comment("서비스 업종 타입")
    private ServiceType serviceType;

    @Comment("점포 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long totalStore;

    @Comment("유사 업종 점포 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long similarStore;

    @Comment("개업 점포 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long openedStore;

    @Comment("폐업 점포 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long closedStore;

    @Comment("프랜차이즈 점포 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private Long franchiseStore;

    @Comment("개업률")
    @Column(columnDefinition = "Float")
    private Float openedRate;

    @Comment("폐업률")
    @Column(columnDefinition = "Float")
    private Float closedRate;
}
