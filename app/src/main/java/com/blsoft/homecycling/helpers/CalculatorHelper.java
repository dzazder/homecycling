package com.blsoft.homecycling.helpers;

import java.math.BigDecimal;

/**
 * Created by bartek on 16.03.2018.
 */

public class CalculatorHelper {
    private static BigDecimal kmhFactor = new BigDecimal(3.6);

    public static BigDecimal calculateAvgSpeed(BigDecimal distance, long timeMillis) {
        BigDecimal timeInSeconds = new BigDecimal(timeMillis / (double)1000);
        BigDecimal avgSpeed = distance.divide(timeInSeconds, 2, BigDecimal.ROUND_HALF_UP);  // speed in meters per second

        return convertToKmH(avgSpeed);
    }

    public static BigDecimal convertToKmH(BigDecimal speed) {
        return speed.multiply(kmhFactor);
    }
}
