package domain.actions;

import charging.ChargingStationReader;
import utils.Utils;

import java.util.Objects;

/**
 * Charging action with its parameters.
 */
public class ChargingAction extends MeasurableAction {

    private final int connectionId;


    public ChargingAction(int actionId, int fromNodeId, int toNodeId, int length, int connectionId) {
        super(actionId, fromNodeId, toNodeId, length);
        this.connectionId = connectionId;
    }


    public int getConnectionId() {
        return connectionId;
    }


    private double getChargingCost(){
        return -Utils.COST_FOR_KW * (Utils.BATTERY_CAPACITY*(this.getConsumption()/100.));
    }


    @Override
    public MeasurableAction copy() {
        return new ChargingAction(this.getActionId(), this.getFromNodeId(), this.getToNodeId(),
                this.getLength(), connectionId);
    }


    @Override
    public boolean equals(Object o) {
        ChargingAction that = (ChargingAction) o;
        return this.getLength() == that.getLength() &&
                this.getFromNodeId() == that.getFromNodeId() &&
                connectionId == that.connectionId;
    }


    @Override
    public int getConsumption() {
        return (int)(((ChargingStationReader.getChargingConnection(connectionId).getPowerKW()*(getLength()/60.))/Utils.BATTERY_CAPACITY)*100.);
    }

    @Override
    public double getReward() {
        return getChargingCost();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getActionId(), this.getFromNodeId(), this.getToNodeId(), this.getLength(), connectionId);
    }

    @Override
    public String toString() {
        return "ChargingAction: " + super.toString();
    }
}