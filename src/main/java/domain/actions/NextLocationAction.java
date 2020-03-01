package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;

public class NextLocationAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction {

    private int toNodeId;

    public NextLocationAction(int aId, int toNodeId) {
        super(aId);
        this.toNodeId = toNodeId;
    }

    public String actionName() {
        return ActionTypes.TO_NEXT_LOCATION.getName();
    }

    public Action copy() {
        return new NextLocationAction(this.aId, toNodeId);
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

    @Override
    public double getActionTime(TaxiGraphState state) {
        return TaxiRecommenderDomainGenerator.getTripTime(state.getNodeId(), toNodeId);
    }

    // TODO - estimate energy consumption on the trip
    @Override
    public double getActionEnergyConsumption(TaxiGraphState state) {
        return -TaxiRecommenderDomainGenerator.getDistanceBetweenNodes(state.getNodeId(), toNodeId)/2;
    }
}
