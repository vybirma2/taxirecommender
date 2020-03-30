package domain.actions;


import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;
import parameterestimation.EnergyConsumptionEstimator;

import java.util.Objects;

import static domain.actions.ActionUtils.notGoingToChargingPreviously;
import static domain.actions.ActionUtils.shiftNotOver;
import static utils.DistanceGraphUtils.getTripTime;

/**
 * Action of going to charging station from some node in the Environment
 */
public class GoingToChargingStationAction extends GraphDefinedDomain.GraphActionType.GraphAction implements MeasurableAction  {

    private int toNodeId;
    private int fromNodeId;
    private int timeStamp;


    public GoingToChargingStationAction(int aId, int fromNodeId, int toNodeId, int timeStamp) {
        super(aId);
        this.toNodeId = toNodeId;
        this.fromNodeId = fromNodeId;
        this.timeStamp = timeStamp;
    }


    @Override
    public String actionName() {
        return ActionTypes.GOING_TO_CHARGING_STATION.getName();
    }


    @Override
    public Action copy() {
        return new GoingToChargingStationAction(this.aId, this.fromNodeId, this.toNodeId, this.timeStamp);
    }


    @Override
    public int getActionTime(TaxiGraphState state) {
        return getTripTime(state.getNodeId(), toNodeId) ;
    }


    @Override
    public int getActionId() {
        return this.aId;
    }


    @Override
    public int getActionEnergyConsumption(TaxiGraphState state) {
        return EnergyConsumptionEstimator.getActionEnergyConsumption(state, toNodeId, getActionTime(state));
    }


    @Override
    public int getToNodeId() {
        return toNodeId;
    }


    public boolean applicableInState(TaxiGraphState state){
        return shiftNotOver(state, this.getActionTime(state)) && notGoingToChargingPreviously(state);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fromNodeId, toNodeId, timeStamp);
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            GoingToChargingStationAction that = (GoingToChargingStationAction)o;
            return this.aId == that.aId
                    && this.timeStamp == that.timeStamp
                    && this.fromNodeId == that.fromNodeId
                    && this.toNodeId == that.toNodeId;
        } else {
            return false;
        }
    }
}
