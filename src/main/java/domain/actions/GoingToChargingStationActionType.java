package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;
import charging.ChargingStation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static domain.actions.ActionUtils.*;

public class GoingToChargingStationActionType extends GraphDefinedDomain.GraphActionType {


    public GoingToChargingStationActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(aId, transitionDynamics);
    }


    @Override
    public String typeName() {
        return ActionTypes.GOING_TO_CHARGING_STATION.getName();
    }


    @Override
    public Action associatedAction(String strRep) {
        return new GoingToChargingStationAction(this.aId, Integer.parseInt(strRep));
    }


    @Override
    public List<Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();

        List<ChargingStation> chargingStations = TaxiRecommenderDomainGenerator.getChargingStations();

        for (ChargingStation chargingStation : chargingStations){
            if (this.applicableInState((TaxiGraphState) state, chargingStation.getRoadNode().getId())){
                actions.add(new GoingToChargingStationAction(this.aId, chargingStation.getRoadNode().getId()));
            }
        }

        return actions;
    }


    @Override
    protected boolean applicableInState(State s) {
        return notChargingInARow(s) && notGoingToChargingPreviously(s) && notFullyCharged(s) && super.applicableInState(s);
    }


    public double getActionTime(TaxiGraphState state, int toNodeId) {
        return TaxiRecommenderDomainGenerator.getTripTime(state.getNodeId(), toNodeId);
    }


    private boolean applicableInState(TaxiGraphState state, int toNodeId){
        return applicableInState(state) && notReturningBack(state, toNodeId) && shiftNotOver(state, this.getActionTime(state, toNodeId));
    }

}
