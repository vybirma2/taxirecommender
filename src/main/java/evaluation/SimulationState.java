package evaluation;

public class SimulationState {
    private int nodeId;
    private int timeStamp;
    private int stateOfCharge;


    public SimulationState(int nodeId, int timeStamp, int stateOfCharge) {
        this.nodeId = nodeId;
        this.timeStamp = timeStamp;
        this.stateOfCharge = stateOfCharge;
    }


    public int getNodeId() {
        return nodeId;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public int getStateOfCharge() {
        return stateOfCharge;
    }


    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setStateOfCharge(int stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }

    @Override
    public String toString() {
        return "SimulationState{" +
                "nodeId=" + nodeId +
                ", timeStamp=" + timeStamp +
                ", stateOfCharge=" + stateOfCharge +
                '}';
    }
}
