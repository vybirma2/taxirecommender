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

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void increaseTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStateOfCharge() {
        return stateOfCharge;
    }

    public void increaseStateOfCharge(int stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }
}
