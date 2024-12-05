package com.ssafy.backend.domain.district.service;

import com.ssafy.backend.domain.administration.dto.info.ClosedStoreAdministrationTopFiveInfo;
import com.ssafy.backend.domain.administration.dto.info.OpenedStoreAdministrationTopFiveInfo;
import com.ssafy.backend.domain.administration.dto.info.SalesAdministrationTopFiveInfo;
import com.ssafy.backend.domain.administration.repository.SalesAdministrationRepository;
import com.ssafy.backend.domain.administration.repository.StoreAdministrationRepository;
import com.ssafy.backend.domain.commercial.dto.response.CommercialAdministrationAreaResponse;
import com.ssafy.backend.domain.commercial.entity.AreaCommercial;
import com.ssafy.backend.domain.commercial.repository.AreaCommercialRepository;
import com.ssafy.backend.domain.district.dto.info.FootTrafficDistrictListByInfo;
import com.ssafy.backend.domain.district.dto.info.SalesDistrictMonthSalesTopFiveInfo;
import com.ssafy.backend.domain.district.dto.info.StoreDistrictTotalTopEightInfo;
import com.ssafy.backend.domain.district.dto.response.ChangeIndicatorDistrictResponse;
import com.ssafy.backend.domain.district.dto.response.ClosedStoreDistrictTopTenResponse;
import com.ssafy.backend.domain.district.dto.response.DistrictAreaResponse;
import com.ssafy.backend.domain.district.dto.response.DistrictDetailResponse;
import com.ssafy.backend.domain.district.dto.response.DistrictTopTenResponse;
import com.ssafy.backend.domain.district.dto.response.FootTrafficDistrictDetailResponse;
import com.ssafy.backend.domain.district.dto.response.FootTrafficDistrictTopTenResponse;
import com.ssafy.backend.domain.district.dto.response.OpenedStoreDistrictTopTenResponse;
import com.ssafy.backend.domain.district.dto.response.SalesDistrictDetailResponse;
import com.ssafy.backend.domain.district.dto.response.SalesDistrictTopTenResponse;
import com.ssafy.backend.domain.district.dto.response.StoreDistrictDetailResponse;
import com.ssafy.backend.domain.district.entity.ChangeDistrict;
import com.ssafy.backend.domain.district.entity.FootTrafficDistrict;
import com.ssafy.backend.domain.district.repository.AreaDistrictRepository;
import com.ssafy.backend.domain.district.repository.ChangeDistrictRepository;
import com.ssafy.backend.domain.district.repository.FootTrafficDistrictRepository;
import com.ssafy.backend.domain.district.repository.SalesDistrictRepository;
import com.ssafy.backend.domain.district.repository.StoreDistrictRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private final FootTrafficDistrictRepository footTrafficDistrictRepository;
    private final SalesDistrictRepository salesDistrictRepository;
    private final StoreDistrictRepository storeDistrictRepository;
    private final ChangeDistrictRepository changeDistrictRepository;
    private final AreaDistrictRepository areaDistrictRepository;
    private final StoreAdministrationRepository storeAdministrationRepository;
    private final SalesAdministrationRepository salesAdministrationRepository;
    private final AreaCommercialRepository areaCommercialRepository;

    @Override
    @Cacheable(value = "Contents", key = "'districts:top10'", cacheManager = "contentCacheManager")
    public DistrictTopTenResponse getTopTenDistricts() {
        // 유동 인구
        List<FootTrafficDistrictTopTenResponse> footTrafficInfoList = footTrafficDistrictRepository.getTopTenFootTrafficDistrictByPeriodCode();
        // 매출
        List<SalesDistrictTopTenResponse> salesInfoList = salesDistrictRepository.getTopTenSalesDistrictByPeriodCode();
        // 개업률
        List<OpenedStoreDistrictTopTenResponse> openedStoreInfoList = storeDistrictRepository.getTopTenOpenedStoreDistrictByPeriodCode();
        // 폐업률
        List<ClosedStoreDistrictTopTenResponse> closedStoreInfoList = storeDistrictRepository.getTopTenClosedStoreDistrictByPeriodCode();

        log.info("========================= 자치구 Top 10");

        return new DistrictTopTenResponse(footTrafficInfoList, salesInfoList, openedStoreInfoList,
            closedStoreInfoList);
    }

    @Override
    public DistrictDetailResponse getDistrictDetail(String districtCode) {
        String periodCode = "20233";
        // 상권 변화 지표 관련
        ChangeIndicatorDistrictResponse changeIndicatorDistrictResponse = getChangeIndicatorDistrict(
            periodCode, districtCode);
        // 유동인구 관련
        FootTrafficDistrictDetailResponse footTrafficDistrictDetailResponse = getFootTrafficDetails(
            districtCode, periodCode);
        // 점포 관련
        StoreDistrictDetailResponse storeDistrictDetailResponse = getStoreDetails(districtCode,
            periodCode);
        // 매출 관련 상세 분석
        SalesDistrictDetailResponse salesDistrictDetailResponse = getSalesDetails(districtCode,
            periodCode);

        return new DistrictDetailResponse(changeIndicatorDistrictResponse,
            footTrafficDistrictDetailResponse, storeDistrictDetailResponse,
            salesDistrictDetailResponse);
    }

    @Override
    public List<DistrictAreaResponse> getAllDistricts() {
        return areaDistrictRepository.findAll().stream()
            .map(ad -> new DistrictAreaResponse(ad.getDistrictCode(), ad.getDistrictCodeName()))
            .toList();
    }

    @Override
    public List<CommercialAdministrationAreaResponse> getAdministrativeAreasByDistrict(
        String districtCode) {
        List<AreaCommercial> areaCommercialList = areaCommercialRepository.findAllByDistrictCode(
            districtCode);
        return areaCommercialList.stream()
            .map(ac -> new CommercialAdministrationAreaResponse(
                ac.getAdministrationCodeName(),
                ac.getAdministrationCode())
            )
            .distinct() // 중복 제거
            .toList();
    }

    @Override
    public ChangeIndicatorDistrictResponse getDistrictChangeIndicatorDetail(String districtCode) {
        return getChangeIndicatorDistrict("20233", districtCode);
    }

    @Override
    public FootTrafficDistrictDetailResponse getDistrictFootTrafficDetail(String districtCode) {
        return getFootTrafficDetails(districtCode, "20233");
    }

    @Override
    public List<StoreDistrictTotalTopEightInfo> getDistrictTotalStoreDetail(String districtCode) {
        return storeDistrictRepository.getTopEightTotalStoreByServiceCode("20233", districtCode);
    }

    @Override
    public List<ClosedStoreAdministrationTopFiveInfo> getDistrictClosedStoreDetail(
        String districtCode) {
        // 지역구 코드로 해당 지역구에 속하는 행정동 코드 리스트 가져오기
        List<String> allAdministrationCodes = getAdministrationCodes(districtCode);
        return storeAdministrationRepository.getTopFiveClosedRateAdministration(
            allAdministrationCodes, "20233");
    }

    @Override
    public List<OpenedStoreAdministrationTopFiveInfo> getDistrictOpenedStoreDetail(
        String districtCode) {
        // 지역구 코드로 해당 지역구에 속하는 행정동 코드 리스트 가져오기
        List<String> allAdministrationCodes = getAdministrationCodes(districtCode);
        return storeAdministrationRepository.getTopFiveOpenedRateAdministration(
            allAdministrationCodes, "20233");
    }

    @Override
    public List<SalesDistrictMonthSalesTopFiveInfo> getDistrictSalesDetailByServiceCode(
        String districtCode) {
        return salesDistrictRepository.getTopFiveMonthSalesByServiceCode(districtCode, "20233");
    }

    @Override
    public List<SalesAdministrationTopFiveInfo> getDistrictSalesDetailByAdministrationCode(
        String districtCode) {
        // 지역구 코드로 해당 지역구에 속하는 행정동 코드 리스트 가져오기
        List<String> allAdministrationCodes = getAdministrationCodes(districtCode);
        return salesAdministrationRepository.getTopFiveSalesAdministrationByAdministrationCode(
            allAdministrationCodes, "20233");
    }

    private ChangeIndicatorDistrictResponse getChangeIndicatorDistrict(String periodCode,
        String districtCode) {
        ChangeDistrict changeDistrict = changeDistrictRepository.findByPeriodCodeAndDistrictCode(
            periodCode, districtCode);
        return new ChangeIndicatorDistrictResponse(changeDistrict.getChangeIndicator(),
            changeDistrict.getChangeIndicatorName(), changeDistrict.getOpenedMonths(),
            changeDistrict.getClosedMonths());
    }

    private FootTrafficDistrictDetailResponse getFootTrafficDetails(String districtCode,
        String periodCode) {
        List<String> periodCodes = Arrays.asList("20224", "20231", "20232", "20233");
        List<FootTrafficDistrict> footTrafficDetailList = footTrafficDistrictRepository.findByPeriodCodeInAndDistrictCodeOrderByPeriodCode(
            periodCodes, districtCode);
        return getFootTrafficDataDetail(footTrafficDetailList, periodCode);
    }

    private FootTrafficDistrictDetailResponse getFootTrafficDataDetail(
        List<FootTrafficDistrict> footTrafficDetailList, String periodCode) {
        Map<String, Long> periodData = new LinkedHashMap<>();

        FootTrafficDistrictListByInfo time = null;
        FootTrafficDistrictListByInfo gender = null;
        FootTrafficDistrictListByInfo age = null;
        FootTrafficDistrictListByInfo day = null;

        for (FootTrafficDistrict footTrafficDistrict : footTrafficDetailList) {
            // 총 유동인구
            periodData.put(footTrafficDistrict.getPeriodCode(),
                footTrafficDistrict.getTotalFootTraffic());

            if (footTrafficDistrict.getPeriodCode().equals(periodCode)) {

                // 시간대별
                Map<String, Long> timeData = new LinkedHashMap<>();
                timeData.put("time0to6", footTrafficDistrict.getFootTraffic00());
                timeData.put("time6to11", footTrafficDistrict.getFootTraffic06());
                timeData.put("time11to14", footTrafficDistrict.getFootTraffic11());
                timeData.put("tim14to17", footTrafficDistrict.getFootTraffic14());
                timeData.put("time17to21", footTrafficDistrict.getFootTraffic17());
                timeData.put("time21to24", footTrafficDistrict.getFootTraffic21());

                time = findMaxEntry(timeData);

                // 남녀
                Map<String, Long> genderData = new LinkedHashMap<>();
                genderData.put("male", footTrafficDistrict.getMaleFootTraffic());
                genderData.put("female", footTrafficDistrict.getFemaleFootTraffic());
                String maxKey = null;
                if (genderData.get("male") > genderData.get("female")) {
                    maxKey = "male";
                } else {
                    maxKey = "female";
                }
                gender = new FootTrafficDistrictListByInfo(maxKey, genderData);

                // 연령대별
                Map<String, Long> ageData = new LinkedHashMap<>();
                ageData.put("age10", footTrafficDistrict.getTeenFootTraffic());
                ageData.put("age20", footTrafficDistrict.getTwentyFootTraffic());
                ageData.put("age30", footTrafficDistrict.getThirtyFootTraffic());
                ageData.put("age40", footTrafficDistrict.getFortyFootTraffic());
                ageData.put("age50", footTrafficDistrict.getFiftyFootTraffic());
                ageData.put("age60", footTrafficDistrict.getSixtyFootTraffic());

                age = findMaxEntry(ageData);

                // 요일별
                Map<String, Long> dayData = new LinkedHashMap<>();
                dayData.put("monday", footTrafficDistrict.getMonFootTraffic());
                dayData.put("tuesday", footTrafficDistrict.getTueFootTraffic());
                dayData.put("wednesday", footTrafficDistrict.getWedFootTraffic());
                dayData.put("thursday", footTrafficDistrict.getThuFootTraffic());
                dayData.put("friday", footTrafficDistrict.getFriFootTraffic());
                dayData.put("saturday", footTrafficDistrict.getSatFootTraffic());
                dayData.put("sunday", footTrafficDistrict.getSunFootTraffic());

                day = findMaxEntry(dayData);
            }
        }

        String maxKey = null;
        if (periodData.get("20232") > periodData.get("20233")) {
            maxKey = "감소";
        } else if (periodData.get("20232") < periodData.get("20233")) {
            maxKey = "증가";
        } else {
            maxKey = "정체";
        }
        FootTrafficDistrictListByInfo period = new FootTrafficDistrictListByInfo(maxKey,
            periodData);

        return new FootTrafficDistrictDetailResponse(period, time, gender, age, day);
    }

    private StoreDistrictDetailResponse getStoreDetails(String districtCode, String periodCode) {
        // 점포 관련
        // 점포 수 Top 8 서비스 업종, 업종 코드명, 점포 개수
        List<StoreDistrictTotalTopEightInfo> storeDistrictTotalTopEightList = storeDistrictRepository.getTopEightTotalStoreByServiceCode(
            periodCode, districtCode);
        // 지역구 코드로 해당 지역구에 속하는 행정동 코드 리스트 가져오기
        List<String> allAdministrationCodes = getAdministrationCodes(districtCode);
        // 개업률 top 5 행정동
        List<OpenedStoreAdministrationTopFiveInfo> openedStoreAdministrationTopFiveList = storeAdministrationRepository.getTopFiveOpenedRateAdministration(
            allAdministrationCodes, periodCode);
        // 폐업률 top 5 행정동
        List<ClosedStoreAdministrationTopFiveInfo> closedStoreAdministrationTopFiveList = storeAdministrationRepository.getTopFiveClosedRateAdministration(
            allAdministrationCodes, periodCode);

        return new StoreDistrictDetailResponse(storeDistrictTotalTopEightList,
            openedStoreAdministrationTopFiveList, closedStoreAdministrationTopFiveList);
    }

    private SalesDistrictDetailResponse getSalesDetails(String districtCode, String periodCode) {
        // 매출 관련 상세 분석
        // 서비스 업종별 매출 Top 5
        List<SalesDistrictMonthSalesTopFiveInfo> salesDistrictMonthSalesTopFiveInfoList = salesDistrictRepository.getTopFiveMonthSalesByServiceCode(
            districtCode, periodCode);
        // 지역구 코드로 해당 지역구에 속하는 행정동 코드 리스트 가져오기
        List<String> allAdministrationCodes = getAdministrationCodes(districtCode);
        // 해당 자치구 행정동 매출 Top 5
        List<SalesAdministrationTopFiveInfo> salesAdministrationTopFiveList = salesAdministrationRepository.getTopFiveSalesAdministrationByAdministrationCode(
            allAdministrationCodes, periodCode);

        return new SalesDistrictDetailResponse(salesDistrictMonthSalesTopFiveInfoList,
            salesAdministrationTopFiveList);
    }

    private FootTrafficDistrictListByInfo findMaxEntry(Map<String, Long> data) {
        String maxKey = null;
        Long maxValue = Long.MIN_VALUE;
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxKey = entry.getKey();
                maxValue = entry.getValue();
            }
        }
        return new FootTrafficDistrictListByInfo(maxKey, data);
    }

    private List<String> getAdministrationCodes(String districtCode) {
        // 지역구 코드로 해당 지역구에 속하는 행정동 코드 리스트 가져오기
        List<String> allAdministrationCodes = new ArrayList<>();
        List<CommercialAdministrationAreaResponse> adminResponses = getAdministrativeAreasByDistrict(
            districtCode);
        for (CommercialAdministrationAreaResponse dto : adminResponses) {
            allAdministrationCodes.add(dto.administrationCode());
        }
        return allAdministrationCodes;
    }
}
