package domain.actions;

import domain.states.TaxiState;
import domain.parameterestimation.ParameterEstimator;

import java.util.*;

import static domain.actions.ActionUtils.*;
import static domain.utils.DistanceGraphUtils.getIntervalStart;

/**
 * Class with the main purpose of returning all available actions of picking up passenger in some node in the environment.
 */
public class PickUpPassengerActionType  extends TaxiActionType {

    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripLengths;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripConsumptions;


    /**
     * @param parameterEstimator used parameter estimator to get passenger trips information - trip lengths, consumptions...
     */
    public PickUpPassengerActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions, ParameterEstimator parameterEstimator) {
        super(actionId, transitions);
        taxiTripLengths = parameterEstimator.getTaxiTripLengths();
        taxiTripConsumptions = parameterEstimator.getTaxiTripConsumptions();
    }


    /**
     * Connections states reachable by picking up a passenger
     */
    @Override
    public void createConnections(TaxiState state) {
        ArrayList<Integer> trans = transitions.get(state.getNodeId());

        if (trans != null) {
            for (Integer neighbour : trans) {
                if (this.applicableInState(state, neighbour)) {
                    int startInterval = getIntervalStart(state.getTimeStamp());
                    createConnectionBetweenStates(state, neighbour,
                            taxiTripLengths.get(startInterval).get(state.getNodeId()).get(neighbour).intValue(),
                            taxiTripConsumptions.get(startInterval).get(state.getNodeId()).get(neighbour).intValue(), actionId);
                }
            }
        }
    }

    @Override
    boolean applicableInState(TaxiState state) {
        return transitions.containsKey(state.getNodeId());
    }

    private boolean applicableInState(TaxiState state, int toNodeId){
        int startInterval = getIntervalStart(state.getTimeStamp());

        if (taxiTripLengths.get(startInterval).containsKey(state.getNodeId())){
            if (taxiTripLengths.get(startInterval).get(state.getNodeId()).containsKey(toNodeId)){
                return applicableInState(state) && shiftNotOver(state, taxiTripLengths.get(startInterval).get(state.getNodeId()).get(toNodeId).intValue()) &&
                        notRunOutOfBattery(state.getStateOfCharge(), taxiTripConsumptions.get(startInterval).get(state.getNodeId()).get(toNodeId).intValue());
            }
        }

        return false;
    }
}
