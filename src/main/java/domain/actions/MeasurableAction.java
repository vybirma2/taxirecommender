package domain.actions;

import domain.parameterestimation.EnergyConsumptionEstimator;

import java.io.Serializable;
import java.util.Objects;

public abstract class MeasurableAction implements Serializable {

    public static int ids;

    private int id;
    private int actionId;
    private int fromNodeId;
    private int toNodeId;
    private int actionTime;
    private int timeToFinish;


    public MeasurableAction(int actionId, int fromNodeId, int toNodeId, int actionTime) {
        this.id = ids++;
        this.actionId = actionId;
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.actionTime = actionTime;
        this.timeToFinish = actionTime;
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

    public int getActionTime() {
        return actionTime;
    }

    public int getTimeToFinish() {
        return timeToFinish;
    }

    public void setTimeToFinish(int timeToFinish){this.timeToFinish = timeToFinish;}

    public int getId(){
        return id;
    }



    public abstract int getRestConsumption();
    public abstract double getReward();
    public abstract void setRestConsumption(int restConsumption);



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


    @Override
    public String toString() {
        return "fromNodeId: " + fromNodeId +
                ", toNodeId: " + toNodeId +
                ", length: " + timeToFinish;
    }
}
