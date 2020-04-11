package domain.actions;

import charging.ChargingStationReader;
import charging.TripToChargingStation;
import domain.states.TaxiGraphState;
import parameterestimation.EnergyConsumptionEstimator;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static domain.actions.ActionUtils.*;
import static utils.DistanceGraphUtils.getTripTime;

/**
 * Class with the main purpose of returning the best available actions of going to charging in given state.
 */
public class GoingToChargingStationActionType extends TaxiActionType {


    public GoingToChargingStationActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions) {
        super(actionId, transitions);
    }

    @Override
    void addPreviousState(TaxiGraphState previousState, int stateId) {
        previousState.addGoingToChargingPreviousState(stateId);
    }


    /**
     * @param state Current state to go to charging station from
     * @return NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO actions of going to charging stations chosen by defined
     * charging station order - distance/prize...
     */
    @Override
    public List<TaxiGraphState> allReachableStates(TaxiGraphState state) {
        List<TaxiGraphState> states = new ArrayList<>();

        List<Integer> trans = this.transitions.get(state.getNodeId());

        if (trans != null){
            List<Integer> stations = chooseBestChargingStation(state, trans);
            for (Integer chargingStation : stations){
                int time = getTripTime(state.getNodeId(), chargingStation);
                if (this.applicableInState(state, chargingStation, time)){
                    addNewState(states, state,chargingStation, time,
                            getConsumption(state.getNodeId(), chargingStation));
                }
            }
        }

        return states;
    }


    private int getConsumption(int fromNodeId, int toNodeId) {
        return EnergyConsumptionEstimator.getActionEnergyConsumption(fromNodeId, toNodeId);
    }



    private List<Integer> chooseBestChargingStation(TaxiGraphState state, List<Integer> transitions){

        if (Utils.CHARGING_STATION_STATE_ORDER == null){
            return transitions
                    .stream()
                    .map(ChargingStationReader::getChargingStation)
                    .map(station -> new TripToChargingStation(state.getNodeId(), station.getRoadNode().getId()))
                    .sorted(Utils.tripToChargingStationComparator)
                    .limit(Utils.NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO)
                    .map(TripToChargingStation::getChargingStation)
                    .collect(Collectors.toList());
        } else {
            return Utils.CHARGING_STATION_STATE_ORDER.get(state, Utils.NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO);
        }
    }


    @Override
    protected boolean applicableInState(TaxiGraphState state) {
        return notChargedALot(state.getStateOfCharge());
    }


    private boolean applicableInState(TaxiGraphState state, int toNodeId, int time){
        return applicableInState(state) &&  notRunOutOfBattery(state, toNodeId)
                && shiftNotOver(state, time);
    }

}
