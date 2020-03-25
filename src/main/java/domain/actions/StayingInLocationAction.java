package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.states.TaxiGraphState;

import java.util.Objects;

public class StayingInLocationAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction {

    private int length;

    public StayingInLocationAction(int aId, int length) {
        super(aId);
        this.length = length;
    }


    public int getLength() {
        return length;
    }


    @Override
    public String actionName() {
        return ActionTypes.STAYING_IN_LOCATION.getName();
    }


    @Override
    public Action copy() {
        return new StayingInLocationAction(this.aId, length);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLength());
    }


    @Override
    public int getActionTime(TaxiGraphState state) {
        return length;
    }


    @Override
    public int getActionEnergyConsumption(TaxiGraphState state) {
        return 0;
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            StayingInLocationAction that = (StayingInLocationAction) o;
            return this.aId == that.aId && this.length == that.length;
        } else {
            return false;
        }
    }
}
