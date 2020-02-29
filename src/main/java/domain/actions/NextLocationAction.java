package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;

public class NextLocationAction extends GraphDefinedDomain.GraphActionType.GraphAction {

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

}
