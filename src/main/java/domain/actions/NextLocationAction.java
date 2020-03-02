package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;

import static domain.actions.ActionUtils.notReturningBack;
import static domain.actions.ActionUtils.shiftNotOver;

public class NextLocationAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction {

    private int toNodeId;


    public NextLocationAction(int aId) {
        super(aId);
    }


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
        return TaxiRecommenderDomainGenerator.getTripTime(state.getNodeId(), toNodeId);
    }


    // TODO - estimate energy consumption on the trip
    @Override
    public double getActionEnergyConsumption(TaxiGraphState state) {
        return -TaxiRecommenderDomainGenerator.getDistanceBetweenNodes(state.getNodeId(), toNodeId);
    }


    public void setToNodeId(int toNodeId) {
        this.toNodeId = toNodeId;
    }


    public boolean applicableInState(TaxiGraphState state){
        return notReturningBack(state, toNodeId) && shiftNotOver(state, this.getActionTime(state));
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
