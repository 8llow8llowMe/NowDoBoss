package com.ssafy.backend.domain.map.controller;

import com.ssafy.backend.domain.map.dto.response.MapResponse;
import com.ssafy.backend.domain.map.service.MapService;
import com.ssafy.backend.global.common.dto.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "지도", description = "지도 테두리 좌표 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map")
public class MapController {
    private final MapService mapservice;

    @Operation(
            summary = "상권 영역 좌표",
            description = "주어진 지도 영역 내에 포함된 상권의 테두리 좌표를 조회하는 기능입니다."
    )
    @GetMapping("/commercial")
    public ResponseEntity<Message<MapResponse>> getCommercialAreaCoords (
            @RequestParam Double lngSW, @RequestParam Double latSW, @RequestParam Double lngNE, @RequestParam Double latNE) {
        log.info("상권 영역 좌표: {}, {}, {}, {}", lngSW, latSW, lngNE, latNE);
        return ResponseEntity.ok().body(Message.success(mapservice.getCommercialAreaCoords(lngSW, latSW, lngNE, latNE)));
    }

    @Operation(
            summary = "행정동 영역 좌표",
            description = "주어진 지도 영역 내에 포함된 행정동의 테두리 좌표를 조회하는 기능입니다."
    )
    @GetMapping("/administration")
    public ResponseEntity<Message<MapResponse>> getAdministrationAreaCoords (
            @RequestParam Double lngSW, @RequestParam Double latSW, @RequestParam Double lngNE, @RequestParam Double latNE) {
        log.info("행정동 영역 좌표: {}, {}, {}, {}", lngSW, latSW, lngNE, latNE);
        return ResponseEntity.ok().body(Message.success(mapservice.getAdministrationAreaCoords(lngSW, latSW, lngNE, latNE)));
    }

    @Operation(
            summary = "자치구 영역 좌표",
            description = "주어진 지도 영역 내에 포함된 자치구의 테두리 좌표를 조회하는 기능입니다."
    )
    @GetMapping("/district")
    public ResponseEntity<Message<MapResponse>> getDistrictAreaCoords (
            @RequestParam Double lngSW, @RequestParam Double latSW, @RequestParam Double lngNE, @RequestParam Double latNE) {
        log.info("자치구 영역 좌표: {}, {}, {}, {}", lngSW, latSW, lngNE, latNE);
        return ResponseEntity.ok().body(Message.success(mapservice.getDistrictAreaCoords(lngSW, latSW, lngNE, latNE)));
    }
}
