package domain.environmentrepresentation.gridenvironment;

import java.io.Serializable;

public class GridDistanceSpeedPair implements Serializable {

    private double distance;
    private double speed;

    public GridDistanceSpeedPair(double distance, double speed) {
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
