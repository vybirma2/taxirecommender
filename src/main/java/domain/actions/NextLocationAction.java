package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;

import static domain.TaxiRecommenderDomainGenerator.getDistanceBetweenNodes;
import static utils.Utils.*;

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
        return TaxiRecommenderDomainGenerator.getTripTime(state.getNodeId(), toNodeId);
    }


    @Override
    public double getActionEnergyConsumption(TaxiGraphState state) {
        return getMovingEnergyConsumption(state.getNodeId()) + getAuxiliaryEnergyConsumption(state);
    }

    private double getMovingEnergyConsumption(int fromNodeId){
        double speed = TaxiRecommenderDomainGenerator.getSpeedBetweenNodes(fromNodeId, toNodeId);
        double distance = getDistanceBetweenNodes(fromNodeId, toNodeId);
        return - RIDER_AGGRESSIVENESS * (ALPHA_1 * speed * speed + ALPHA_2*speed + ALPHA_3) * distance;
    }


    private double getAuxiliaryEnergyConsumption(TaxiGraphState state){
        return - LOADING * (getActionTime(state)/60);
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
