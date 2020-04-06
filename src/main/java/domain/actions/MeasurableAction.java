package domain.actions;

import java.util.Objects;

public abstract class MeasurableAction {

    private int actionId;
    private int fromNodeId;
    private int toNodeId;
    private int timeStamp;
    private int length;
    private int consumption;


    public MeasurableAction(int actionId, int fromNodeId, int toNodeId, int timeStamp, int length, int consumption) {
        this.actionId = actionId;
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.timeStamp = timeStamp;
        this.length = length;
        this.consumption = consumption;
    }


    public int getFromNodeId() {
        return fromNodeId;
    }


    public int getTimeStamp() {
        return timeStamp;
    }


    public int getActionId() {
        return actionId;
    }


    public int getToNodeId() {
        return toNodeId;
    }


    public int getLength() {
        return length;
    }

    public int getConsumption() {
        return consumption;
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.getFromNodeId(), this.getToNodeId(), this.getTimeStamp());
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            MeasurableAction that = (MeasurableAction) o;
            return this.actionId == that.getActionId()
                    && this.timeStamp == that.getTimeStamp()
                    && this.fromNodeId == that.getFromNodeId()
                    && this.toNodeId == that.getToNodeId();
        } else {
            return false;
        }
    }


    abstract MeasurableAction copy();
}
