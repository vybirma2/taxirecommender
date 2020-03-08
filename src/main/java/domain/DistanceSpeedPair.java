package domain;

import java.util.HashMap;

public class DistanceSpeedPair {
    private HashMap<Integer, HashMap<Integer, Double>> distances;
    private HashMap<Integer, HashMap<Integer, Double>> speeds;

    public DistanceSpeedPair(HashMap<Integer, HashMap<Integer, Double>> distances, HashMap<Integer, HashMap<Integer, Double>> speeds) {
        this.distances = distances;
        this.speeds = speeds;
    }

    public HashMap<Integer, HashMap<Integer, Double>> getDistances() {
        return distances;
    }

    public void setDistances(HashMap<Integer, HashMap<Integer, Double>> distances) {
        this.distances = distances;
    }

    public HashMap<Integer, HashMap<Integer, Double>> getSpeeds() {
        return speeds;
    }

    public void setSpeeds(HashMap<Integer, HashMap<Integer, Double>> speeds) {
        this.speeds = speeds;
    }
}
