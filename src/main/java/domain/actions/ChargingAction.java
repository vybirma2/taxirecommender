package domain.actions;


import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.states.TaxiGraphState;

import java.util.Objects;

public class ChargingAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction {

    private int length;

    public ChargingAction(int aId, int length) {
        super(aId);
        this.length = length;
    }

    public int getLength() {
        return length;
    }


    public String actionName() {
        return ActionTypes.CHARGING_IN_CHARGING_STATION.getName();
    }

    public Action copy() {
        return new ChargingAction(this.aId, length);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ChargingAction that = (ChargingAction) o;
            return this.aId == that.aId && this.length == that.length;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLength());
    }


    @Override
    public double getActionTime(TaxiGraphState state) {
        return length;
    }

    // TODO - estimate speed of charging
    @Override
    public double getActionEnergyConsumption(TaxiGraphState state) {
        return length;
    }
}