package domain.actions;

import domain.states.TaxiState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract  class TaxiActionType {

    private static HashMap<Integer, HashMap<Integer, HashMap<Integer, TaxiState>>> visitedStates = new HashMap<>();
    protected HashMap<Integer, ArrayList<Integer>> transitions;
    protected int actionId;


    public TaxiActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions) {
        this.actionId = actionId;
        this.transitions = transitions;
    }

    protected boolean alreadyVisited(int nodeId, int timeStamp, int stateOfCharge){
        return visitedStates.containsKey(nodeId)
                && (visitedStates.get(nodeId).containsKey(timeStamp)
                && (visitedStates.get(nodeId).get(timeStamp).containsKey(stateOfCharge)));
    }



    protected void addNewState(List<TaxiState> states, TaxiState previousState, int toNodeId, int length, int energyConsumption){

        int resultTimeStamp = length + previousState.getTimeStamp();
        int resultStateOfCharge = energyConsumption + previousState.getStateOfCharge();
        int resultNodeId = toNodeId;

        if (!alreadyVisited(resultNodeId, resultTimeStamp, resultStateOfCharge)){
            TaxiState newState = new TaxiState(resultNodeId, resultStateOfCharge, resultTimeStamp);
            addPreviousState(newState, previousState.getId());
            states.add(newState);
            addVisitedState(newState);
        } else {
            addPreviousState(getVisitedState(resultNodeId, resultTimeStamp, resultStateOfCharge), previousState.getId());
        }
    }


    protected void addVisitedState(TaxiState state){
        if (visitedStates.containsKey(state.getNodeId())){
            if (visitedStates.get(state.getNodeId()).containsKey(state.getTimeStamp())){
                visitedStates.get(state.getNodeId()).get(state.getTimeStamp()).put(state.getStateOfCharge(), state);
            } else {
                HashMap<Integer, TaxiState> chargeStates = new HashMap<>();
                chargeStates.put(state.getStateOfCharge(), state);
                visitedStates.get(state.getNodeId()).put(state.getTimeStamp(), chargeStates);
            }
        } else {
            HashMap<Integer, HashMap<Integer, TaxiState>> timeStates = new HashMap<>();
            HashMap<Integer, TaxiState> chargeStates = new HashMap<>();
            chargeStates.put(state.getStateOfCharge(), state);
            timeStates.put(state.getTimeStamp(), chargeStates);

            visitedStates.put(state.getNodeId(), timeStates);
        }
    }

    protected TaxiState getVisitedState(int nodeId, int timeStamp, int stateOfCharge){
        return visitedStates.get(nodeId).get(timeStamp).get(stateOfCharge);
    }


    public int getActionId() {
        return actionId;
    }

    abstract void addPreviousState(TaxiState previousState, int stateId);

    public abstract List<TaxiState> allReachableStates(TaxiState state);


    public abstract List<MeasurableAction> allApplicableActions(TaxiState state);

    abstract boolean applicableInState(TaxiState state);
}
