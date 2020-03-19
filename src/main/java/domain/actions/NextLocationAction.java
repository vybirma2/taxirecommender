package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;

import static utils.DistanceGraphUtils.getTripTime;

public class NextLocationAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction {

    private int toNodeId;


    public NextLocationAction(int aId, int toNodeId) {
        super(aId);
        this.toNodeId = toNodeId;
    }


    @Override
    public String actionName() {
        return ActionTypes.TO_NEXT_LOCATION.getName();
    }


    @Override
    public Action copy() {
        return new NextLocationAction(this.aId, toNodeId);
    }


    @Override
    public double getActionTime(TaxiGraphState state) {
        return getTripTime(state.getNodeId(), toNodeId);
    }


    @Override
    public double getActionEnergyConsumption(TaxiGraphState state) {
        return ActionUtils.getActionEnergyConsumption(state, toNodeId, getActionTime(state)/60);
    }


    public int getToNodeId() {
        return toNodeId;
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            NextLocationAction that = (NextLocationAction)o;
            return this.aId == that.aId;
        } else {
            return false;
        }
    }
}
