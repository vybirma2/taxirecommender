package domain.actions;

import charging.ChargingStationReader;
import utils.Utils;

import java.util.Objects;

/**
 * Charging action with its parameters.
 */
public class ChargingAction extends MeasurableAction {

    private int connectionId;
    private double cost;


    public ChargingAction(int actionId, int fromNodeId, int toNodeId, int timeStamp, int length, int connectionId, int energyProduction) {
        super(actionId, fromNodeId, toNodeId, timeStamp, length, energyProduction);
        this.connectionId = connectionId;
        this.cost = ChargingStationReader.getChargingConnection(connectionId).getPrizeForKW() * (Utils.BATTERY_CAPACITY*(energyProduction/100.));
    }




    public int getConnectionId() {
        return connectionId;
    }


    public double getChargingCost(){
        return -cost;
    }


    @Override
    public MeasurableAction copy() {
        return new ChargingAction(this.getActionId(), this.getFromNodeId(), this.getToNodeId(), this.getTimeStamp(),
                this.getLength(), connectionId, this.getConsumption());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChargingAction that = (ChargingAction) o;
        return this.getLength() == that.getLength() &&
                this.getFromNodeId() == that.getFromNodeId() &&
                connectionId == that.connectionId &&
                this.getConsumption() == that.getConsumption() &&
                Double.compare(that.cost, cost) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.getActionId(), this.getFromNodeId(), this.getToNodeId(), this.getTimeStamp(),
                this.getLength(), connectionId, this.getConsumption());
    }
}