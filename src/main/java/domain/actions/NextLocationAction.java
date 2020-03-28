package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;

import java.util.Objects;

import static utils.DistanceGraphUtils.getTripTime;

public class NextLocationAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction {

    private int fromNodeId;
    private int toNodeId;
    private int timeStamp;


    public NextLocationAction(int aId, int fromNodeId, int toNodeId, int timeStamp) {
        super(aId);
        this.toNodeId = toNodeId;
        this.fromNodeId = fromNodeId;
        this.timeStamp = timeStamp;
    }


    @Override
    public String actionName() {
        return ActionTypes.TO_NEXT_LOCATION.getName();
    }


    @Override
    public Action copy() {
        return new NextLocationAction(this.aId,fromNodeId, toNodeId, timeStamp);
    }


    @Override
    public int getActionTime(TaxiGraphState state) {
        return getTripTime(state.getNodeId(), toNodeId);
    }


    @Override
    public int getActionEnergyConsumption(TaxiGraphState state) {
        return ActionUtils.getActionEnergyConsumption(state, toNodeId, getActionTime(state));
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),fromNodeId, toNodeId, timeStamp);
    }

    public int getToNodeId() {
        return toNodeId;
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            NextLocationAction that = (NextLocationAction)o;
            return this.aId == that.aId
                    && this.timeStamp == that.timeStamp
                    && this.fromNodeId == that.fromNodeId
                    && this.toNodeId == that.toNodeId;
        } else {
            return false;
        }
    }
}
