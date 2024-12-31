package com.ssafy.backend.domain.commercial.service;

import com.ssafy.backend.domain.administration.entity.IncomeAdministration;
import com.ssafy.backend.domain.administration.entity.SalesAdministration;
import com.ssafy.backend.domain.administration.exception.AdministrationErrorCode;
import com.ssafy.backend.domain.administration.exception.AdministrationException;
import com.ssafy.backend.domain.administration.repository.IncomeAdministrationRepository;
import com.ssafy.backend.domain.administration.repository.SalesAdministrationRepository;
import com.ssafy.backend.domain.commercial.document.CommercialAnalysis;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentFootTrafficInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAgeGenderPercentSalesInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialAnnualQuarterIncomeInfo;
import com.ssafy.backend.domain.commercial.dto.info.CommercialSameStoreInfo;
import com.ssafy.backend.domain.commercial.dto.request.CommercialAnalysisKafkaRequest;
import com.ssafy.backend.domain.commercial.dto.request.CommercialAnalysisSaveRequest;
import com.ssafy.backend.domain.commercial.dto.request.ConversionCodeNameRequest;
import com.ssafy.backend.domain.commercial.dto.response.AllIncomeResponse;
import com.ssafy.backend.domain.commercial.dto.response.AllSalesResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialAdministrationAreaResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialAdministrationResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialAnalysisResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialAreaResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialFacilityResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialFootTrafficResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialIncomeResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialPopulationResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialSalesResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialServiceResponse;
import com.ssafy.backend.domain.commercial.dto.response.CommercialStoreResponse;
import com.ssafy.backend.domain.commercial.dto.response.ConversionCodeResponse;
import com.ssafy.backend.domain.commercial.entity.AreaCommercial;
import com.ssafy.backend.domain.commercial.entity.FacilityCommercial;
import com.ssafy.backend.domain.commercial.entity.FootTrafficCommercial;
import com.ssafy.backend.domain.commercial.entity.IncomeCommercial;
import com.ssafy.backend.domain.commercial.entity.PopulationCommercial;
import com.ssafy.backend.domain.commercial.entity.SalesCommercial;
import com.ssafy.backend.domain.commercial.entity.StoreCommercial;
import com.ssafy.backend.domain.commercial.exception.CommercialErrorCode;
import com.ssafy.backend.domain.commercial.exception.CommercialException;
import com.ssafy.backend.domain.commercial.exception.CoordinateTransformationException;
import com.ssafy.backend.domain.commercial.mapper.FacilityCommercialMapper;
import com.ssafy.backend.domain.commercial.mapper.FootTrafficCommercialMapper;
import com.ssafy.backend.domain.commercial.mapper.IncomeCommercialMapper;
import com.ssafy.backend.domain.commercial.mapper.PopulationCommercialMapper;
import com.ssafy.backend.domain.commercial.mapper.SalesCommercialMapper;
import com.ssafy.backend.domain.commercial.mapper.StoreCommercialMapper;
import com.ssafy.backend.domain.commercial.repository.AreaCommercialRepository;
import com.ssafy.backend.domain.commercial.repository.CommercialAnalysisRepository;
import com.ssafy.backend.domain.commercial.repository.FacilityCommercialRepository;
import com.ssafy.backend.domain.commercial.repository.FootTrafficCommercialRepository;
import com.ssafy.backend.domain.commercial.repository.IncomeCommercialRepository;
import com.ssafy.backend.domain.commercial.repository.PopulationCommercialRepository;
import com.ssafy.backend.domain.commercial.repository.SalesCommercialRepository;
import com.ssafy.backend.domain.commercial.repository.ServiceCodeProjection;
import com.ssafy.backend.domain.commercial.repository.StoreCommercialRepository;
import com.ssafy.backend.domain.district.entity.IncomeDistrict;
import com.ssafy.backend.domain.district.entity.SalesDistrict;
import com.ssafy.backend.domain.district.entity.enums.ServiceType;
import com.ssafy.backend.domain.district.exception.DistrictErrorCode;
import com.ssafy.backend.domain.district.exception.DistrictException;
import com.ssafy.backend.domain.district.repository.IncomeDistrictRepository;
import com.ssafy.backend.domain.district.repository.SalesDistrictRepository;
import com.ssafy.backend.global.common.document.DataDocument;
import com.ssafy.backend.global.common.dto.PageResponse;
import com.ssafy.backend.global.common.repository.DataRepository;
import com.ssafy.backend.global.component.geotools.CoordinateConverter;
import com.ssafy.backend.global.component.kafka.KafkaConstants;
import com.ssafy.backend.global.component.kafka.dto.info.DataInfo;
import com.ssafy.backend.global.component.kafka.producer.KafkaProducer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommercialServiceImpl implements CommercialService {

    private static final String DISTRICT = "district";
    private static final String ADMINISTRATION = "administration";

    private final AreaCommercialRepository areaCommercialRepository;
    private final FootTrafficCommercialRepository footTrafficCommercialRepository;
    private final SalesCommercialRepository salesCommercialRepository;
    private final PopulationCommercialRepository populationCommercialRepository;
    private final FacilityCommercialRepository facilityCommercialRepository;
    private final StoreCommercialRepository storeCommercialRepository;
    private final IncomeCommercialRepository incomeCommercialRepository;
    private final SalesDistrictRepository salesDistrictRepository;
    private final SalesAdministrationRepository salesAdministrationRepository;
    private final IncomeDistrictRepository incomeDistrictRepository;
    private final IncomeAdministrationRepository incomeAdministrationRepository;
    private final CommercialAnalysisRepository commercialAnalysisRepository;
    private final KafkaProducer kafkaProducer;
    private final DataRepository dataRepository;
    private final CoordinateConverter coordinateConverter;

    private final SalesCommercialMapper salesCommercialMapper;
    private final FootTrafficCommercialMapper footTrafficCommercialMapper;
    private final PopulationCommercialMapper populationCommercialMapper;
    private final FacilityCommercialMapper facilityCommercialMapper;
    private final IncomeCommercialMapper incomeCommercialMapper;
    private final StoreCommercialMapper storeCommercialMapper;

    @Override
    @Transactional(readOnly = true)
    public ConversionCodeResponse conversionCodeNameToCode(ConversionCodeNameRequest request) {
        // 자치구
        if (DISTRICT.equals(request.type())) {
            return areaCommercialRepository.findDistrictInfoByDistrictCodeName(request.codeName());
        }

        // 행정동
        if (ADMINISTRATION.equals(request.type())) {
            return areaCommercialRepository.findAdministrationInfoByAdministrationCodeName(
                request.codeName());
        }

        // 상권
        return areaCommercialRepository.findCommercialInfoByCommercialCodeName(request.codeName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommercialAdministrationResponse> getAdministrativeAreasByDistrict(
        String districtCode) {
        List<AreaCommercial> areaCommercialList = areaCommercialRepository.findAllByDistrictCode(
            districtCode);
        Set<String> seenAdministrationCodes = new HashSet<>();

        return areaCommercialList.stream()
            .filter(ac -> seenAdministrationCodes.add(ac.getAdministrationCode()))
            .map(ac -> {
                Point transformedPoint = transformCoordinates(ac.getX(), ac.getY());
                return new CommercialAdministrationResponse(
                    ac.getAdministrationCodeName(),
                    ac.getAdministrationCode(),
                    transformedPoint.getX(),
                    transformedPoint.getY()
                );
            })
            .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<CommercialAreaResponse> getCommercialAreasByAdministrationCode(
        String administrationCode) {
        return areaCommercialRepository.findByAdministrationCode(administrationCode).stream()
            .map(ac -> {
                Point transformedPoint = transformCoordinates(ac.getX().doubleValue(),
                    ac.getY().doubleValue());

                return CommercialAreaResponse.builder()
                    .commercialCode(ac.getCommercialCode())
                    .commercialCodeName(ac.getCommercialCodeName())
                    .commercialClassificationCode(ac.getCommercialClassificationCode())
                    .commercialClassificationCodeName(ac.getCommercialClassificationCodeName())
                    .centerLat(transformedPoint.getX())
                    .centerLng(transformedPoint.getY())
                    .build();
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommercialFootTrafficResponse getFootTrafficByPeriodAndCommercialCode(String periodCode,
        String commercialCode) {

        FootTrafficCommercial footTrafficCommercial = footTrafficCommercialRepository.findByPeriodCodeAndCommercialCode(
                periodCode, commercialCode)
            .orElseThrow(() -> new CommercialException(CommercialErrorCode.NOT_FOOT_TRAFFIC));

        CommercialAgeGenderPercentFootTrafficInfo ageGenderPercentFootTraffic = footTrafficCommercialMapper.toCommercialAgeGenderPercentFootTrafficInfo(
            footTrafficCommercial);

        return footTrafficCommercialMapper.toCommercialFootTrafficResponse(
            footTrafficCommercial, ageGenderPercentFootTraffic);
    }

    @Override
    public List<CommercialServiceResponse> getServiceByCommercialCode(String commercialCode) {
        List<ServiceCodeProjection> serviceCodeProjectionList = salesCommercialRepository.findDistinctServiceCodesByCommercialCode(
            commercialCode);

        return serviceCodeProjectionList.stream()
            .map(projection -> CommercialServiceResponse.builder()
                .serviceCode(projection.getServiceCode())
                .serviceCodeName(projection.getServiceCodeName())
                .serviceType(projection.getServiceType())
                .build())
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommercialSalesResponse getSalesByPeriodAndCommercialCodeAndServiceCode(
        String periodCode, String commercialCode, String serviceCode) {

        SalesCommercial salesCommercial = salesCommercialRepository.findByPeriodCodeAndCommercialCodeAndServiceCode(
                periodCode, commercialCode, serviceCode)
            .orElseThrow(() -> new CommercialException(CommercialErrorCode.NOT_SALES));

        List<String> periodCodes = calculateLastFourQuarters(periodCode);

        List<SalesCommercial> salesCommercialList = salesCommercialRepository.findByCommercialCodeAndServiceCodeAndPeriodCodeIn(
            commercialCode, serviceCode, periodCodes);

        CommercialAgeGenderPercentSalesInfo ageGenderPercentSalesInfo = salesCommercialMapper.toCommercialAgeGenderPercentSalesInfo(
            salesCommercial);

        return salesCommercialMapper.toCommercialSalesResponse(salesCommercial, salesCommercialList,
            ageGenderPercentSalesInfo);
    }

    @Override
    public AllSalesResponse getAllSalesByPeriodAndDistrictCodeAndAdministrationCodeAndCommercialCodeAndServiceCode(
        Long memberId, String periodCode, String districtCode, String administrationCode,
        String commercialCode, String serviceCode) {
        SalesDistrict salesDistrict = salesDistrictRepository.findByPeriodCodeAndDistrictCodeAndServiceCode(
                periodCode, districtCode, serviceCode)
            .orElseThrow(() -> new DistrictException(DistrictErrorCode.NOT_SALES));

        SalesAdministration salesAdministration = salesAdministrationRepository.findByPeriodCodeAndAdministrationCodeAndServiceCode(
                periodCode, administrationCode, serviceCode)
            .orElseThrow(() -> new AdministrationException(AdministrationErrorCode.NOT_SALES));

        SalesCommercial salesCommercial = salesCommercialRepository.findByPeriodCodeAndCommercialCodeAndServiceCode(
                periodCode, commercialCode, serviceCode)
            .orElseThrow(() -> new CommercialException(CommercialErrorCode.NOT_SALES));

        // 카프카 토픽에 메시지 저장하기 위해 변환
        CommercialAnalysisKafkaRequest analysisKafkaRequest = new CommercialAnalysisKafkaRequest(
            salesDistrict.getDistrictCodeName(),
            salesAdministration.getAdministrationCodeName(),
            salesCommercial.getCommercialCodeName(),
            salesDistrict.getServiceCodeName()
        );

        kafkaProducer.publish(KafkaConstants.KAFKA_TOPIC_ANALYSIS, analysisKafkaRequest);

        // 추천용 데이터 카프카 토픽으로
        DataInfo dataInfo = new DataInfo(memberId, commercialCode, "analysis");
        DataDocument dataDocument = DataDocument.builder()
            .userId(dataInfo.userId())
            .commercialCode(Long.parseLong(dataInfo.commercialCode()))
            .action(dataInfo.action())
            .build();
        dataRepository.save(dataDocument);

        return salesCommercialMapper.mapToAllSalesResponse(
            salesDistrict, salesAdministration, salesCommercial);
    }

    @Override
    @Transactional(readOnly = true)
    public CommercialPopulationResponse getPopulationByPeriodAndCommercialCode(String periodCode,
        String commercialCode) {

        PopulationCommercial populationCommercial = populationCommercialRepository.findByPeriodCodeAndCommercialCode(
                periodCode, commercialCode)
            .orElseThrow(() -> new CommercialException(CommercialErrorCode.NOT_POPULATION));

        return populationCommercialMapper.toCommercialPopulationResponse(populationCommercial);
    }

    @Override
    @Transactional(readOnly = true)
    public CommercialFacilityResponse getFacilityByPeriodAndCommercialCode(String periodCode,
        String commercialCode) {
        FacilityCommercial facilityCommercial = facilityCommercialRepository.findByPeriodCodeAndCommercialCode(
                periodCode, commercialCode)
            .orElseThrow(() -> new CommercialException(CommercialErrorCode.NOT_FACILITY));

        return facilityCommercialMapper.toCommercialFacilityResponse(facilityCommercial);
    }

    @Override
    @Transactional(readOnly = true)
    public CommercialAdministrationAreaResponse getAdministrationInfoByCommercialCode(Long memberId,
        String commercialCode) {
        return areaCommercialRepository.findByCommercialCode(commercialCode);
    }

    @Override
    @Transactional(readOnly = true)
    public CommercialStoreResponse getStoreByPeriodAndCommercialCodeAndServiceCode(
        String periodCode, String commercialCode, String serviceCode) {

        ServiceType serviceType = storeCommercialRepository.findServiceTypeByPeriodCodeAndCommercialCodeAndServiceCode(
            periodCode, commercialCode, serviceCode);

        List<StoreCommercial> otherStores = storeCommercialRepository.findOtherServicesInSameCategory(
            periodCode, commercialCode, serviceType);

        List<CommercialSameStoreInfo> sameStoreInfos = otherStores.stream()
            .map(storeCommercialMapper::entityToSameStoreInfo)
            .toList();

        StoreCommercial storeCommercial = storeCommercialRepository.findByPeriodCodeAndCommercialCodeAndServiceCode(
                periodCode, commercialCode, serviceCode)
            .orElseThrow(() -> new CommercialException(CommercialErrorCode.NOT_STORE));

        return storeCommercialMapper.toCommercialStoreResponse(sameStoreInfos, storeCommercial);
    }

    @Override
    @Transactional(readOnly = true)
    public CommercialIncomeResponse getIncomeByPeriodCodeAndCommercialCode(String periodCode,
        String commercialCode) {

        IncomeCommercial incomeCommercial = incomeCommercialRepository.findByPeriodCodeAndCommercialCode(
                periodCode, commercialCode)
            .orElseThrow(() -> new CommercialException(CommercialErrorCode.NOT_INCOME));

        // 최근 4분기의 기간 코드를 계산
        List<String> periodCodes = calculateLastFourQuarters(periodCode);

        // 최근 4분기 데이터 조회 및 매핑
        List<CommercialAnnualQuarterIncomeInfo> annualQuarterIncomeInfos = incomeCommercialRepository
            .findByCommercialCodeAndPeriodCodeInOrderByPeriodCode(commercialCode, periodCodes)
            .stream()
            .map(incomeCommercialMapper::entityToCommercialAnnualQuarterIncomeInfo)
            .toList();

        return incomeCommercialMapper.toCommercialIncomeResponse(
            incomeCommercial, annualQuarterIncomeInfos);
    }

    @Override
    @Transactional(readOnly = true)
    public AllIncomeResponse getAllIncomeByPeriodCodeAndDistrictCodeAndAdministrationCodeAndCommercialCode(
        String periodCode, String districtCode, String administrationCode, String commercialCode) {
        IncomeDistrict incomeDistrict = incomeDistrictRepository.findByPeriodCodeAndDistrictCode(
                periodCode, districtCode)
            .orElseThrow(() -> new DistrictException(DistrictErrorCode.NOT_INCOME));

        IncomeAdministration incomeAdministration = incomeAdministrationRepository.findByPeriodCodeAndAdministrationCode(
                periodCode, administrationCode)
            .orElseThrow(() -> new AdministrationException(AdministrationErrorCode.NOT_INCOME));

        IncomeCommercial incomeCommercial = incomeCommercialRepository.findByPeriodCodeAndCommercialCode(
                periodCode, commercialCode)
            .orElseThrow(() -> new CommercialException(CommercialErrorCode.NOT_INCOME));

        return incomeCommercialMapper.toAllIncomeResponse(
            incomeDistrict, incomeAdministration, incomeCommercial);
    }

    @Override
    public void saveAnalysis(Long memberId, CommercialAnalysisSaveRequest analysisSaveRequest) {
        boolean existAnalysis = commercialAnalysisRepository.existsByDistrictCodeAndAdministrationCodeAndCommercialCodeAndServiceCode(
            analysisSaveRequest.districtCode(), analysisSaveRequest.administrationCode(),
            analysisSaveRequest.commercialCode(), analysisSaveRequest.serviceCode());

        if (existAnalysis) {
            throw new CommercialException(CommercialErrorCode.EXIST_ANALYSIS);
        }

        CommercialAnalysis commercialAnalysis = CommercialAnalysis.builder()
            .memberId(memberId)
            .districtCode(analysisSaveRequest.districtCode())
            .districtCodeName(analysisSaveRequest.districtCodeName())
            .administrationCode(analysisSaveRequest.administrationCode())
            .administrationCodeName(analysisSaveRequest.administrationCodeName())
            .commercialCode(analysisSaveRequest.commercialCode())
            .commercialCodeName(analysisSaveRequest.commercialCodeName())
            .serviceType(analysisSaveRequest.serviceType())
            .serviceCode(analysisSaveRequest.serviceCode())
            .serviceCodeName(analysisSaveRequest.serviceCodeName())
            .createdAt(LocalDateTime.now())
            .build();
        commercialAnalysisRepository.save(commercialAnalysis);

        // 카프카 토픽에 메시지 저장하기 위해 변환
        CommercialAnalysisKafkaRequest analysisKafkaRequest = new CommercialAnalysisKafkaRequest(
            analysisSaveRequest.districtCodeName(),
            analysisSaveRequest.administrationCodeName(),
            analysisSaveRequest.commercialCodeName(),
            analysisSaveRequest.serviceCodeName()
        );

        kafkaProducer.publish(KafkaConstants.KAFKA_TOPIC_ANALYSIS, analysisKafkaRequest);

        // 추천용 데이터 카프카 토픽으로
        DataInfo dataInfo = new DataInfo(memberId, analysisSaveRequest.commercialCode(), "save");
        if (!dataInfo.commercialCode().equals("0")) {
            DataDocument dataDocument = DataDocument.builder()
                .userId(dataInfo.userId())
                .commercialCode(Long.parseLong(dataInfo.commercialCode()))
                .action(dataInfo.action())
                .build();
            dataRepository.save(dataDocument);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommercialAnalysisResponse> getMyAnalysisListByMemberId(Long memberId,
        int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommercialAnalysis> commercialAnalysisPage = commercialAnalysisRepository.findByMemberIdOrderByCreatedAt(
            memberId, pageable);

        Page<CommercialAnalysisResponse> responsePage = commercialAnalysisPage.map(
            ca -> new CommercialAnalysisResponse(
                ca.getDistrictCode(),
                ca.getDistrictCodeName(),
                ca.getAdministrationCode(),
                ca.getAdministrationCodeName(),
                ca.getCommercialCode(),
                ca.getCommercialCodeName(),
                ca.getServiceType(),
                ca.getServiceCode(),
                ca.getServiceCodeName(),
                ca.getCreatedAt()
            ));

        return PageResponse.of(responsePage);
    }

    private List<String> calculateLastFourQuarters(String currentPeriod) {
        List<String> periods = new ArrayList<>();
        int year = Integer.parseInt(currentPeriod.substring(0, 4));
        int quarter = Integer.parseInt(currentPeriod.substring(4));

        for (int i = 0; i < 5; i++) {
            periods.add(year + "" + quarter);
            if (quarter == 1) {
                quarter = 4;
                year--;
            } else {
                quarter--;
            }
        }

        return periods;
    }

    private Point transformCoordinates(double x, double y) {
        try {
            Point result = coordinateConverter.transform(x, y);
            CRS.reset("all");
            return result;
        } catch (Exception e) {
            throw new CoordinateTransformationException("좌표 변환에 실패하였습니다.", e);
        }
    }

}
