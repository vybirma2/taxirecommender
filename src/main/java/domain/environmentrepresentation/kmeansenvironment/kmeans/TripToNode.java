package domain.environmentrepresentation.kmeansenvironment.kmeans;

import domain.utils.DistanceSpeedPairTime;

import java.io.Serializable;

public class TripToNode implements Serializable, Comparable<TripToNode> {
    private DistanceSpeedPairTime distanceSpeedPairTime;
    private int toNodeId;

    public TripToNode(int toNodeId, DistanceSpeedPairTime distanceSpeedPairTime) {
        this.distanceSpeedPairTime = distanceSpeedPairTime;
        this.toNodeId = toNodeId;
    }


    public DistanceSpeedPairTime getDistanceSpeedPairTime() {
        return distanceSpeedPairTime;
    }

    public int getToNodeId() {
        return toNodeId;
    }

    @Override
    public int compareTo(TripToNode o) {
        return Double.compare(distanceSpeedPairTime.getDistance(), o.distanceSpeedPairTime.getDistance());
    }
}
