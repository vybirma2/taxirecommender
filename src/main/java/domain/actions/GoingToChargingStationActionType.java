package domain.actions;

import domain.charging.ChargingStationReader;
import domain.charging.TripToChargingStation;
import domain.states.TaxiState;
import domain.parameterestimation.EnergyConsumptionEstimator;
import domain.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static domain.actions.ActionUtils.*;
import static domain.utils.DistanceGraphUtils.getTripTime;

/**
 * Class with the main purpose of returning the best available actions of going to domain.charging in given state.
 */
public class GoingToChargingStationActionType extends TaxiActionType {


    public GoingToChargingStationActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions) {
        super(actionId, transitions);
    }

    /**
     * Connections states reachable by going to charging aciton
     */
    @Override
    public void createConnections(TaxiState state) {

        List<Integer> trans = this.transitions.get(state.getNodeId());

        if (trans != null){
            List<Integer> stations = chooseBestChargingStation(state, trans);
            for (Integer chargingStation : stations){
                int time = getTripTime(state.getNodeId(), chargingStation);
                if (this.applicableInState(state, chargingStation, time)){
                    createConnectionBetweenStates(state,chargingStation, time,
                            getConsumption(state.getNodeId(), chargingStation), actionId);
                }
            }
        }
    }

    private int getConsumption(int fromNodeId, int toNodeId) {
        return EnergyConsumptionEstimator.getActionEnergyConsumption(fromNodeId, toNodeId);
    }

    private List<Integer> chooseBestChargingStation(TaxiState state, List<Integer> transitions){

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
    protected boolean applicableInState(TaxiState state) {
        return true;
    }

    private boolean applicableInState(TaxiState state, int toNodeId, int time){
        return applicableInState(state) &&  notRunOutOfBattery(state, toNodeId)
                && shiftNotOver(state, time);
    }
}
