package domain.actions;


import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;

public class GoingToChargingStationAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction  {

    private int toNodeId;

    public GoingToChargingStationAction(int aId, int toNodeId) {
        super(aId);
        this.toNodeId = toNodeId;
    }

    public String actionName() {
        return ActionTypes.GOING_TO_CHARGING_STATION.getName();
    }

    public Action copy() {
        return new GoingToChargingStationAction(this.aId, this.toNodeId);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            GoingToChargingStationAction that = (GoingToChargingStationAction)o;
            return this.aId == that.aId;
        } else {
            return false;
        }
    }

    @Override
    public double getActionTime(TaxiGraphState state) {
        return TaxiRecommenderDomainGenerator.getTripTime(state.getNodeId(), toNodeId) ;
    }

    // TODO - estimate energy consumption on the trip
    @Override
    public double getActionEnergyConsumption(TaxiGraphState state) {
        return -TaxiRecommenderDomainGenerator.getDistanceBetweenNodes(state.getNodeId(), toNodeId)/2;
    }
}
