package domain.actions;


import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import charging.ChargingStationUtils;
import domain.states.TaxiGraphState;
import utils.DistanceGraphUtils;
import utils.Utils;

import java.util.Objects;

public class ChargingAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction {

    private int length;
    private int stationId;
    private int connectionId;
    private int energyProduction;
    private double cost;


    public ChargingAction(int aId, int length, int stationId, int connectionId) {
        super(aId);
        this.length = length;
        this.stationId = stationId;
        this.connectionId = connectionId;
        this.energyProduction = (int)(ChargingStationUtils.getChargingConnection(connectionId).getPowerKW()*(length/60));
        this.cost = ChargingStationUtils.getChargingConnection(connectionId).getPrizeForKW() * energyProduction;
    }


    public double getLength() {
        return length;
    }

    public int getStationId() {
        return stationId;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public double getChargingCost(){
        return -cost;
    }

    @Override
    public String actionName() {
        return ActionTypes.CHARGING_IN_CHARGING_STATION.getName();
    }


    @Override
    public Action copy() {
        return new ChargingAction(this.aId, length, stationId, connectionId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLength(), getStationId(), getConnectionId(), energyProduction);
    }


    @Override
    public int getActionTime(TaxiGraphState state) {
        return length;
    }


    @Override
    public int getActionEnergyConsumption(TaxiGraphState state) {
        return (int)((energyProduction/Utils.BATTERY_CAPACITY) * 100);
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ChargingAction that = (ChargingAction) o;
            return this.aId == that.aId
                    && this.length == that.length
                    && this.connectionId == that.connectionId
                    && this.stationId == that.stationId;
        } else {
            return false;
        }
    }
}