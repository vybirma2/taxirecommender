package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import charging.ChargingStationReader;
import charging.TripToChargingStation;
import domain.states.TaxiGraphState;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static domain.actions.ActionUtils.*;
import static utils.DistanceGraphUtils.getTripTime;

/**
 * Class with the main purpose of returning the best available actions of going to charging in given state.
 */
public class GoingToChargingStationActionType extends GraphDefinedDomain.GraphActionType {


    public GoingToChargingStationActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(aId, transitionDynamics);
    }


    @Override
    public String typeName() {
        return ActionTypes.GOING_TO_CHARGING_STATION.getName();
    }


    /**
     * @param state Current state to go to charging station from
     * @return NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO actions of going to charging stations chosen by defined
     * charging station order - distance/prize...
     */
    @Override
    public List<Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();

        int nodeId = (Integer)state.get("node");
        Set<GraphDefinedDomain.NodeTransitionProbability> transitions = this.transitionDynamics.get(nodeId).get(this.aId);

        if (transitions != null){
            List<Integer> stations = chooseBestChargingStation(Utils.NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO,
                    (TaxiGraphState) state, transitions);

            for (Integer chargingStation : stations){
                if (this.applicableInState((TaxiGraphState) state, chargingStation)){
                    actions.add(new GoingToChargingStationAction(this.aId, nodeId, chargingStation, ((TaxiGraphState)state).getTimeStamp()));
                }
            }
        }

        return actions;
    }


    private List<Integer> chooseBestChargingStation(int numOfStations, TaxiGraphState state,
                                                            Set<GraphDefinedDomain.NodeTransitionProbability> transitions){

        if (Utils.CHARGING_STATION_STATE_ORDER == null){
            return transitions
                    .stream()
                    .map(trans -> ChargingStationReader.getChargingStation(trans.transitionTo))
                    .map(station -> new TripToChargingStation(state.getNodeId(), station.getRoadNode().getId()))
                    .sorted(Utils.tripToChargingStationComparator)
                    .limit(numOfStations)
                    .map(TripToChargingStation::getChargingStation)
                    .collect(Collectors.toList());
        } else {
            return Utils.CHARGING_STATION_STATE_ORDER.get(state, numOfStations);
        }

    }


    @Override
    protected boolean applicableInState(State s) {
        return notChargedALot(s) && super.applicableInState(s);
    }


    public int getActionTime(TaxiGraphState state, int toNodeId) {
        return getTripTime(state.getNodeId(), toNodeId);
    }


    private boolean applicableInState(TaxiGraphState state, int toNodeId){
        return applicableInState(state) &&  notRunOutOfBattery(state, toNodeId, getActionTime(state, toNodeId))
                && shiftNotOver(state, this.getActionTime(state, toNodeId));
    }

}
