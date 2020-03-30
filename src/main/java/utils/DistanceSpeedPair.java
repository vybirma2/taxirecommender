package utils;

import java.io.Serializable;

public class DistanceSpeedPair implements Serializable {

    private double distance;
    private double speed;


    public DistanceSpeedPair(double distance, double speed) {
        this.distance = distance;
        this.speed = speed;
    }


    public double getDistance() {
        return distance;
    }


    public double getSpeed() {
        return speed;
    }
}
