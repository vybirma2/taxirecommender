package domain;

import utils.DistanceSpeedPairTime;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Pair of distances and speeds between given set of nodes
 */
public class AllDistancesSpeedsPair implements Serializable {

    private HashMap<Integer, HashMap<Integer, DistanceSpeedPairTime>> distanceSpeedTime;


    public AllDistancesSpeedsPair(HashMap<Integer, HashMap<Integer, DistanceSpeedPairTime>> distanceSpeedTime) {
        this.distanceSpeedTime = distanceSpeedTime;
    }

    public HashMap<Integer, HashMap<Integer, DistanceSpeedPairTime>> getDistanceSpeedTime() {
        return distanceSpeedTime;
    }
}
