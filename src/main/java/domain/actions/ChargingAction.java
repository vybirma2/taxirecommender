package domain.actions;


import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import charging.ChargingStationReader;
import domain.states.TaxiGraphState;
import utils.Utils;

import java.util.Objects;

/**
 * Charging action with its parameters.
 */
public class ChargingAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction {

    private int length;
    private int stationId;
    private int connectionId;
    private int energyProduction;
    private int nodeId;
    private double cost;


    public ChargingAction(int aId, int length, int stationId, int connectionId, int nodeId) {
        super(aId);
        this.length = length;
        this.stationId = stationId;
        this.connectionId = connectionId;
        this.nodeId = nodeId;
        this.energyProduction = (int)(ChargingStationReader.getChargingConnection(connectionId).getPowerKW()*(length/60));
        this.cost = ChargingStationReader.getChargingConnection(connectionId).getPrizeForKW() * energyProduction;
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
        return new ChargingAction(this.aId, length, stationId, connectionId, nodeId);
    }


    @Override
    public int getActionTime(TaxiGraphState state) {
        return length;
    }


    /**
     * @param state state to which the action is applied
     * @return an amount of energy in percents with respect to the battery  capacity
     */
    @Override
    public int getActionEnergyConsumption(TaxiGraphState state) {
        return (int)((energyProduction/Utils.BATTERY_CAPACITY) * 100);
    }


    @Override
    public int getToNodeId() {
        return this.nodeId;
    }


    @Override
    public int getActionId() {
        return this.aId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ChargingAction that = (ChargingAction) o;
        return length == that.length &&
                stationId == that.stationId &&
                connectionId == that.connectionId &&
                energyProduction == that.energyProduction &&
                Double.compare(that.cost, cost) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), length, stationId, connectionId, energyProduction, cost);
    }
}