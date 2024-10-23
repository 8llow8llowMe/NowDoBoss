package com.ssafy.backend.global.component.redis;

import com.ssafy.backend.domain.commercial.service.CommercialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.ssafy.backend.domain.district.service.DistrictService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * Redis 키 만료 이벤트를 처리하는 리스너 클래스입니다.
 * Redis에서 특정 키가 만료될 때 해당 이벤트를 감지하고, 필요한 후속 작업(캐시 재로드 등)을 수행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisKeyExpirationListener implements MessageListener {

    private final DistrictService districtService;
    private final CommercialService commercialService;

    /**
     * Redis 키 만료 이벤트가 발생했을 때 호출되는 메서드입니다.
     * 만료된 키를 감지하고, 필요한 경우 해당 데이터를 다시 로드하여 캐시를 갱신합니다.
     *
     * @param message Redis 메시지 객체, 만료된 키가 포함되어 있음
     * @param pattern 패턴, Redis 메시지 리스너에 전달된 패턴 정보
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        log.info("======================= Received expired key event for key: {}", expiredKey);

        // 특정 키("districts:top10")가 만료되었을 때, 해당 데이터를 다시 로드하여 캐시를 갱신합니다.
        if ("Contents::districts:top10".equals(expiredKey)) {
            log.info("======================= Reloading cache for key: {}", expiredKey);
            districtService.getTopTenDistricts(); // 캐시를 다시 로드하는 메서드 호출
        }
        else if (expiredKey.startsWith("Contents::administrativeAreas:")) {
            String districtCode = expiredKey.split("Contents::administrativeAreas:")[1];
            log.info("======================= Reloading cache for districtCode: {}", districtCode);

            commercialService.getAdministrativeAreasByDistrict(districtCode);
        }
        else if (expiredKey.startsWith("Contents::commercialAreas:")) {
            String administrationCode = expiredKey.split("Contents::commercialAreas:")[1];
            log.info("======================= Reloading cache for administrationCode: {}", administrationCode);

            commercialService.getCommercialAreasByAdministrationCode(administrationCode);
        }
    }
}
