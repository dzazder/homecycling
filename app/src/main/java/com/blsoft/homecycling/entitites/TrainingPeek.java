package com.blsoft.homecycling.entitites;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by bartek on 04.02.2018.
 */

public class TrainingPeek {
    private long peekTime;  // time from start training to read peek
    private BigDecimal speed;
    private BigDecimal power;
    private BigDecimal cadence;
    private BigDecimal distance;

    public TrainingPeek() {

    }

    public TrainingPeek(long peekTime, BigDecimal speed, BigDecimal power, BigDecimal cadence, BigDecimal distance) {
        this.peekTime = peekTime;
        this.speed = speed;
        this.power = power;
        this.cadence = cadence;
        this.distance = distance;
    }

    public long getPeekTime() {
        return peekTime;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public BigDecimal getPower() {
        return power;
    }

    public BigDecimal getCadence() {
        return cadence;
    }

    public BigDecimal getDistance() { return distance; }

    public void setPeekTime(long peekTime) {
        this.peekTime = peekTime;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public void setPower(BigDecimal power) {
        this.power = power;
    }

    public void setCadence(BigDecimal cadence) {
        this.cadence = cadence;
    }

    public void setDistance(BigDecimal distance) { this.distance = distance; }
}
