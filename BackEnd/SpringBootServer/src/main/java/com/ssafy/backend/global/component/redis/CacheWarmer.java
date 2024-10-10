package com.ssafy.backend.global.component.redis;

import com.ssafy.backend.domain.district.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션이 시작될 때 특정 데이터를 미리 로드하여 캐시에 저장하는 클래스입니다.
 * 이 클래스는 Spring Boot의 ApplicationRunner를 구현하여 애플리케이션 초기화 후에 실행됩니다.
 */
@Component
@RequiredArgsConstructor
public class CacheWarmer implements ApplicationRunner {

    private final DistrictService districtService;

    /**
     * 애플리케이션이 실행되면 Spring Boot가 자동으로 호출하는 메서드입니다.
     * DistrictService를 사용하여 "getTopTenDistricts" 데이터를 미리 로드하여 Redis 캐시에 저장합니다.
     *
     * @param args ApplicationArguments 애플리케이션 실행 시 전달된 인자
     */
    @Override
    public void run(ApplicationArguments args) {
        districtService.getTopTenDistricts(); // 데이터를 미리 로드하여 캐시합니다.
    }
}
