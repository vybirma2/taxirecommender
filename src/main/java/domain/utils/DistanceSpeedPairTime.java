package domain.utils;

import java.io.Serializable;

public class DistanceSpeedPairTime implements Serializable {

    private double distance;
    private double speed;
    private int time;

    public DistanceSpeedPairTime(double distance, double speed, int time) {
        this.distance = distance;
        this.speed = speed;
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    public int getTime() {
        return time;
    }
}
