package domain.actions;


import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;

import static domain.actions.ActionUtils.notGoingToChargingPreviously;
import static domain.actions.ActionUtils.shiftNotOver;
import static utils.DistanceGraphUtils.getTripTime;

public class GoingToChargingStationAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction  {

    private int toNodeId;


    public GoingToChargingStationAction(int aId, int toNodeId) {
        super(aId);
        this.toNodeId = toNodeId;
    }


    @Override
    public String actionName() {
        return ActionTypes.GOING_TO_CHARGING_STATION.getName();
    }


    @Override
    public Action copy() {
        return new GoingToChargingStationAction(this.aId, this.toNodeId);
    }


    @Override
    public double getActionTime(TaxiGraphState state) {
        return getTripTime(state.getNodeId(), toNodeId) ;
    }


    @Override
    public double getActionEnergyConsumption(TaxiGraphState state) {
        return ActionUtils.getActionEnergyConsumption(state, toNodeId, getActionTime(state));
    }


    public int getToNodeId() {
        return toNodeId;
    }


    public boolean applicableInState(TaxiGraphState state){
        return shiftNotOver(state, this.getActionTime(state)) && notGoingToChargingPreviously(state);
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
