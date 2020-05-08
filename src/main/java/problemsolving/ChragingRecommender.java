package problemsolving;

import domain.TaxiRewardFunction;
import domain.charging.ChargingStation;
import domain.charging.ChargingStationReader;
import domain.actions.TaxiActionType;
import domain.states.StatePredecessors;
import domain.states.TaxiState;
import domain.parameterestimation.ParameterEstimator;
import domain.utils.Utils;

import java.io.*;
import java.util.*;


public class ChragingRecommender implements Serializable {

    private final List<TaxiActionType> actionTypes;
    private final List<Integer> nodes;

    private HashMap<TaxiState, Integer> visitedStates = new HashMap<>();
    public List<TaxiState> reachableStates = new ArrayList<>();
    private StatePredecessors statePredecessors;
    private ParameterEstimator parameterEstimator;


    public ChragingRecommender(List<TaxiActionType> actionTypes, List<Integer> nodes, ParameterEstimator parameterEstimator) {
        this.actionTypes = actionTypes;
        this.nodes = nodes;
        this.parameterEstimator = parameterEstimator;
    }

    public void performStateSpaceAnalysis() {
        performReachabilitySearch();
        computePolicies();
    }

    private void performReachabilitySearch() {
        System.out.println("Starting reachability analysis...");

        for (Integer node :  nodes){
            for (int time = Utils.SHIFT_START_TIME; time <= Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH; time++){
                for (int charge = 0; charge <= 100; charge++){
                    TaxiState state = new TaxiState(node, charge, time);
                    reachableStates.add(state);
                    visitedStates.put(state, state.getId());
                }
            }
        }

        for (ChargingStation station:  ChargingStationReader.getChargingStations()){
            for (int time = Utils.SHIFT_START_TIME; time <= Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH; time++){
                for (int charge = 0; charge <= 100; charge++){
                    TaxiState state = new TaxiState(station.getRoadNode().getId(), charge, time);
                    if (!visitedStates.containsKey(state)){
                        reachableStates.add(state);
                        visitedStates.put(state, state.getId());
                    } else {
                        TaxiState.stateId--;
                    }
                }
            }
        }

        System.out.println("Reachability analysis finished with " + reachableStates.size() + " states.");
    }

    private void computePolicies(){
        System.out.println("Connection creation..." + reachableStates.size());
        statePredecessors = new StatePredecessors(reachableStates.size());
        createStateConnections();
        TaxiRewardFunction rewardFunction = new TaxiRewardFunction(reachableStates, statePredecessors, parameterEstimator);
        System.out.println("Computing reward...");
        rewardFunction.computeReward();
        System.out.println("Reward computed.");

        System.out.println("Finished reachability analysis; # states: " + reachableStates.size());
    }


    private void createStateConnections(){
        for (TaxiState state : reachableStates) {
            createConnections(state);
        }
    }


    /**
     * @param state current state
     * @return all possible transitions - future states - i.e. generator of new states, if generated state already
     * exist only updates its previous actions and previous states
     */
    public void createConnections(TaxiState state) {
        for (TaxiActionType a : actionTypes) {
            a.createConnections(state);
        }
    }


    public void addPreviousStateConnection(int successorStateId, int predecessorStateId, int actionId) {
        statePredecessors.addPredecessor(successorStateId, predecessorStateId, actionId);
    }


    public List<TaxiState> getReachableStates() {
        return reachableStates;
    }



    public TaxiState getState(TaxiState state) {
        Integer taxiState = visitedStates.get(state);
        if (taxiState == null){
            return null;
        }
        return reachableStates.get(taxiState);
    }
}
