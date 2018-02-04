package com.blsoft.homecycling.entitites;

import java.util.Date;

/**
 * Created by bartek on 04.02.2018.
 */

public class TrainingPeek {
    private long peekTime;  // time from start training to read peek
    private double speed;
    private double power;
    private double cadence;

    public TrainingPeek() {

    }

    public TrainingPeek(long peekTime, double speed, double power, double cadence) {
        this.peekTime = peekTime;
        this.speed = speed;
        this.power = power;
        this.cadence = cadence;
    }

    public long getPeekTime() {
        return peekTime;
    }

    public double getSpeed() {
        return speed;
    }

    public double getPower() {
        return power;
    }

    public double getCadence() {
        return cadence;
    }

    public void setPeekTime(long peekTime) {
        this.peekTime = peekTime;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void setCadence(double cadence) {
        this.cadence = cadence;
    }
}
