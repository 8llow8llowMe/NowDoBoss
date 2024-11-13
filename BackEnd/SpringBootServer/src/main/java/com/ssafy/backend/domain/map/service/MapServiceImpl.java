package com.ssafy.backend.domain.map.service;

import com.ssafy.backend.domain.map.dto.response.MapResponse;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public MapResponse getCommercialAreaCoords(double ax, double ay, double bx, double by)
        throws Exception {
        return getAreaCoords("commercial", ax, ay, bx, by);
    }

    @Override
    public MapResponse getAdministrationAreaCoords(double ax, double ay, double bx, double by)
        throws Exception {
        return getAreaCoords("administration", ax, ay, bx, by);
    }

    @Override
    public MapResponse getDistrictAreaCoords(double ax, double ay, double bx, double by)
        throws Exception {
        return getAreaCoords("district", ax, ay, bx, by);
    }

    private MapResponse getAreaCoords(String type, double ax, double ay, double bx, double by) {
        Map<String, Map<String, Object>> codes = new LinkedHashMap<>();
        Map<String, List<List<Double>>> coordsMap = getCoordsFromCache(type);

        Map<String, List<List<Double>>> res = new LinkedHashMap<>();
        for (Map.Entry<String, List<List<Double>>> entry : coordsMap.entrySet()) {
            String codeName = entry.getKey();
            List<List<Double>> coords = entry.getValue();
            List<Double> center = coords.get(0);
            int code = center.get(2).intValue();
            center.remove(2);
            coords.remove(0);

            List<List<Double>> filteredCoords = filterCoordsByRange(coords, ax, bx, ay, by);
            if (!filteredCoords.isEmpty()) {
                filteredCoords.sort(Comparator.comparingDouble(a -> a.get(2)));
                res.put(codeName, filteredCoords);

                Map<String, Object> metaData = new LinkedHashMap<>();
                metaData.put("code", code);
                metaData.put("center", center);
                codes.put(codeName, metaData);
            }
        }
        return new MapResponse(codes, res);
    }

    private Map<String, List<List<Double>>> getCoordsFromCache(String key) {
        Map<String, List<List<Double>>> coordsMap = (Map<String, List<List<Double>>>) redisTemplate.opsForValue().get(key);
        if (coordsMap == null) {
            loadAndCacheCoords(key);
            coordsMap = (Map<String, List<List<Double>>>) redisTemplate.opsForValue().get(key);
        }
        return coordsMap;
    }

    private void loadAndCacheCoords(String type) {
        JSONParser parser = new JSONParser();
        // 파일 전체를 JSONArray로 읽기
        try (Reader reader = new InputStreamReader(
                new ClassPathResource("area/" + type + ".json").getInputStream(), StandardCharsets.UTF_8)) {
            JSONArray dataArray = (JSONArray) parser.parse(reader);
            Map<String, List<List<Double>>> allCoords = new LinkedHashMap<>();

            for (Object element : dataArray) {
                JSONObject dto = (JSONObject) element;
                String dtoCodeName = (String) dto.get(type + "_code_name");
                JSONArray areaCoords = (JSONArray) dto.get("area_coords");

                List<List<Double>> coords = new ArrayList<>();
                double i = 0;
                for (Object coordObject : areaCoords) {
                    JSONArray coordArray = (JSONArray) coordObject;
                    double x = ((Number) coordArray.get(0)).doubleValue();
                    double y = ((Number) coordArray.get(1)).doubleValue();
                    coords.add(Arrays.asList(x, y, i++));
                }

                JSONArray center = (JSONArray) dto.get("center_coords");
                double x = ((Number) center.get(0)).doubleValue();
                double y = ((Number) center.get(1)).doubleValue();
                double districtCode = Double.parseDouble((String) dto.get(type + "_code"));
                coords.add(0, Arrays.asList(x, y, districtCode));

                allCoords.put(dtoCodeName, coords);
            }

            // 데이터를 Redis에 캐싱
            redisTemplate.opsForValue().set(type, allCoords, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            throw new RuntimeException("파일 읽기 중 오류 발생", e);
        }
    }

    private List<List<Double>> filterCoordsByRange(List<List<Double>> coords, double minLng,
        double maxLng, double minLat, double maxLat) {
        if ((minLng > maxLng) || (minLat > maxLat)) {
            return null;
        }
        // 경도 기준으로 필터링 (이미 정렬되어 있음)
        int lowIndex = Collections.binarySearch(coords, Arrays.asList(minLng, Double.MIN_VALUE),
            Comparator.comparingDouble(a -> a.get(0)));
        int highIndex = Collections.binarySearch(coords, Arrays.asList(maxLng, Double.MAX_VALUE),
            Comparator.comparingDouble(a -> a.get(0)));
        lowIndex = lowIndex < 0 ? -lowIndex - 1 : lowIndex;
        highIndex = highIndex < 0 ? -highIndex - 1 : highIndex;

        List<List<Double>> longitudeFiltered = coords.subList(lowIndex, highIndex);

        // 위도 기준으로 정렬
        longitudeFiltered.sort(Comparator.comparingDouble(a -> a.get(1)));

        // 위도 범위로 필터링
        int lowYIndex = Collections.binarySearch(longitudeFiltered,
            Arrays.asList(Double.MIN_VALUE, minLat), Comparator.comparingDouble(a -> a.get(1)));
        int highYIndex = Collections.binarySearch(longitudeFiltered,
            Arrays.asList(Double.MAX_VALUE, maxLat), Comparator.comparingDouble(a -> a.get(1)));
        lowYIndex = lowYIndex < 0 ? -lowYIndex - 1 : lowYIndex;
        highYIndex = highYIndex < 0 ? -highYIndex - 1 : highYIndex;

        return longitudeFiltered.subList(lowYIndex, highYIndex);
    }
}
