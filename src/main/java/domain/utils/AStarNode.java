package domain.utils;

/**
 * Node used in AStar algorithm
 */
public class AStarNode implements Comparable<AStarNode> {

    private final int nodeId;
    private final double distanceToStart;
    private final double predictedDistanceToGoal;

    public AStarNode(int nodeId, double distanceToStart, double predictedDistanceToGoal) {
        this.nodeId = nodeId;
        this.distanceToStart = distanceToStart;
        this.predictedDistanceToGoal = predictedDistanceToGoal;
    }

    public int getNodeId() {
        return nodeId;
    }

    public double getDistanceToStart() {
        return distanceToStart;
    }

    public double getPredictedDistanceToGoal() {
        return predictedDistanceToGoal;
    }

    @Override
    public int compareTo(AStarNode o) {
        return Double.compare(this.getDistanceToStart() +
                this.predictedDistanceToGoal, o.getDistanceToStart() +
                o.getPredictedDistanceToGoal());
    }
}
