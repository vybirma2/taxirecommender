package domain;

public class AStarNode implements Comparable {
    private int nodeId;
    private double distanceToStart;
    private double predictedDistanceToGoal;


    public AStarNode(int nodeId, double distanceToStart, double predictedDistanceToGoal) {
        this.nodeId = nodeId;
        this.distanceToStart = distanceToStart;
        this.predictedDistanceToGoal = predictedDistanceToGoal;
    }

    public void increaseDistance(int increment){
        this.distanceToStart += increment;
    }


    public int getNodeId() {
        return nodeId;
    }


    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }


    public double getDistanceToStart() {
        return distanceToStart;
    }


    public void setDistanceToStart(int distanceToStart) {
        this.distanceToStart = distanceToStart;
    }

    public void setDistanceToStart(double distanceToStart) {
        this.distanceToStart = distanceToStart;
    }

    public double getPredictedDistanceToGoal() {
        return predictedDistanceToGoal;
    }

    public void setPredictedDistanceToGoal(double predictedDistanceToGoal) {
        this.predictedDistanceToGoal = predictedDistanceToGoal;
    }

    @Override
    public int compareTo(Object o) {
        AStarNode node = (AStarNode)o;
        return Double.compare(this.getDistanceToStart() + this.predictedDistanceToGoal, node.getDistanceToStart() + node.getPredictedDistanceToGoal());
    }
}
