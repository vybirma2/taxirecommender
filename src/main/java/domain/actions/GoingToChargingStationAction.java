package domain.actions;


import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;

public class GoingToChargingStationAction extends GraphDefinedDomain.GraphActionType.GraphAction {

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

}
