package domain.actions;

import domain.charging.ChargingConnection;
import domain.charging.ChargingRateType;
import domain.charging.ChargingStationReader;
import domain.utils.Utils;

import java.util.Objects;

/**
 * Charging action with its parameters.
 */
public class ChargingAction extends MeasurableAction {

    private final int connectionId;
    private int restConsumption;
    private int consumption;

    public ChargingAction(int actionId, int fromNodeId, int toNodeId, int length, int connectionId) {
        super(actionId, fromNodeId, toNodeId, length);
        this.connectionId = connectionId;
        restConsumption = (int)(((ChargingStationReader.getChargingConnection(connectionId).getPowerKW()*(getTimeToFinish()/60.))/Utils.BATTERY_CAPACITY)*100.);
        consumption= restConsumption;
    }


    private double getChargingCost(){
        ChargingConnection connection = ChargingStationReader.getChargingConnection(connectionId);
        if (connection.getPowerKW() < ChargingRateType.SLOW_CHARGING.getKWMax()){
            return -this.getActionTime() * ChargingRateType.SLOW_CHARGING.getRate();
        } else if (connection.getPowerKW() < ChargingRateType.STANDARD_CHARGING.getKWMax()){
            return -this.getActionTime()  * ChargingRateType.STANDARD_CHARGING.getRate();
        } else {
            return -this.getActionTime()  * ChargingRateType.SPEED_CHARGING.getRate();
        }
    }

    @Override
    public boolean equals(Object o) {
        ChargingAction that = (ChargingAction) o;
        return this.getTimeToFinish() == that.getTimeToFinish() &&
                this.getFromNodeId() == that.getFromNodeId() &&
                connectionId == that.connectionId;
    }

    @Override
    public int getRestConsumption() {
        return restConsumption;
    }

    @Override
    public double getReward() {
        return getChargingCost();
    }

    @Override
    public void setRestConsumption(int restConsumption) {
        this.restConsumption = restConsumption;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getActionId(), this.getFromNodeId(), this.getToNodeId(), this.getTimeToFinish(), connectionId);
    }

    @Override
    public String toString() {
        return "ChargingAction: " + super.toString();
    }
}