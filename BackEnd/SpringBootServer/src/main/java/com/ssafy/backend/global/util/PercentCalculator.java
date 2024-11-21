package com.ssafy.backend.global.util;

public class PercentCalculator {

    /**
     * Percent 계산 메서드
     *
     * @param ageGroupValue 특정 연령대 값
     * @param genderValue   특정 성별 값
     * @param total         전체 값
     * @return 계산된 퍼센트 (소수점 첫째자리까지)
     */
    public static double calculatePercent(long ageGroupValue, long genderValue, long total) {
        if (total == 0) {
            return 0.0;
        }
        return Math.round((double) ageGroupValue / total * genderValue * 1000) / 10.0;
    }
}
