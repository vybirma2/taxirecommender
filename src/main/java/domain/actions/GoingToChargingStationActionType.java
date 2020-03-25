package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import charging.ChargingStationUtils;
import charging.TripToChargingStation;
import domain.TaxiRecommenderDomainGenerator;
import domain.environmentrepresentation.EnvironmentNode;
import domain.states.TaxiGraphState;
import charging.ChargingStation;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static domain.actions.ActionUtils.*;
import static utils.DistanceGraphUtils.getChargingStations;
import static utils.DistanceGraphUtils.getTripTime;

public class GoingToChargingStationActionType extends GraphDefinedDomain.GraphActionType {


    public GoingToChargingStationActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(aId, transitionDynamics);
    }


    @Override
    public String typeName() {
        return ActionTypes.GOING_TO_CHARGING_STATION.getName();
    }



    @Override
    public List<Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();

        int nodeId = (Integer)state.get("node");
        Set<GraphDefinedDomain.NodeTransitionProbability> transitions = this.transitionDynamics.get(nodeId).get(this.aId);

        if (transitions != null){
            List<ChargingStation> stations = chooseBestChargingStation(Utils.NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO,
                    (TaxiGraphState) state, transitions);

            for (ChargingStation chargingStation : stations){
                if (this.applicableInState((TaxiGraphState) state, chargingStation.getRoadNode().getId())){
                    actions.add(new GoingToChargingStationAction(this.aId, chargingStation.getRoadNode().getId()));
                }
            }
        }

        return actions;
    }


    private List<ChargingStation> chooseBestChargingStation(int numOfStations, TaxiGraphState state,
                                                            Set<GraphDefinedDomain.NodeTransitionProbability> transitions){

        return transitions
                .stream()
                .map(trans -> ChargingStationUtils.getChargingStation(trans.transitionTo))
                .map(station -> new TripToChargingStation(state, station))
                .sorted(Utils.tripToChargingStationComparator)
                .limit(numOfStations)
                .map(TripToChargingStation::getChargingStation)
                .collect(Collectors.toList());
    }


    @Override
    protected boolean applicableInState(State s) {
        return notChargingInARow(s) && notGoingToChargingPreviously(s) && notChargedALot(s) && super.applicableInState(s);
    }


    public int getActionTime(TaxiGraphState state, int toNodeId) {
        return getTripTime(state.getNodeId(), toNodeId);
    }


    private boolean applicableInState(TaxiGraphState state, int toNodeId){
        return applicableInState(state) && notReturningBack(state, toNodeId) &&
                notRunOutOfBattery(state, toNodeId, getActionTime(state, toNodeId))
                && shiftNotOver(state, this.getActionTime(state, toNodeId));
    }

}
