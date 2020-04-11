package domain.actions;

import java.util.Objects;

public abstract class MeasurableAction {

    public static int ids;

    private int id;
    private int actionId;
    private int fromNodeId;
    private int toNodeId;
    private int length;


    public MeasurableAction(int actionId, int fromNodeId, int toNodeId, int length) {
        this.id = ids++;
        this.actionId = actionId;
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.length = length;
    }


    public int getFromNodeId() {
        return fromNodeId;
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


    public int getId(){
        return id;
    }


    public abstract int getConsumption();



    @Override
    public int hashCode() {
        return Objects.hash(this.fromNodeId, this.toNodeId, this.actionId);
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            MeasurableAction that = (MeasurableAction) o;
            return this.actionId == that.getActionId()
                    && this.fromNodeId == that.getFromNodeId()
                    && this.toNodeId == that.getToNodeId();
        } else {
            return false;
        }
    }


    abstract MeasurableAction copy();
}
