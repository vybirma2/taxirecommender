package domain.actions;

import domain.states.TaxiGraphState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract  class TaxiActionType {

    private static HashMap<Integer, HashMap<Integer, HashMap<Integer, TaxiGraphState>>> visitedStates = new HashMap<>();
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



    protected void addNewState(List<TaxiGraphState> states, TaxiGraphState previousState, int toNodeId, int length, int energyConsumption){

        int resultTimeStamp = length + previousState.getTimeStamp();
        int resultStateOfCharge = energyConsumption + previousState.getStateOfCharge();
        int resultNodeId = toNodeId;

        if (!alreadyVisited(resultNodeId, resultTimeStamp, resultStateOfCharge)){
            TaxiGraphState newState = new TaxiGraphState(resultNodeId, resultStateOfCharge, resultTimeStamp);
            addPreviousState(newState, previousState.getId());
            states.add(newState);
            addVisitedState(newState);
        } else {
            addPreviousState(getVisitedState(resultNodeId, resultTimeStamp, resultStateOfCharge), previousState.getId());
        }
    }


    protected void addVisitedState(TaxiGraphState state){
        if (visitedStates.containsKey(state.getNodeId())){
            if (visitedStates.get(state.getNodeId()).containsKey(state.getTimeStamp())){
                visitedStates.get(state.getNodeId()).get(state.getTimeStamp()).put(state.getStateOfCharge(), state);
            } else {
                HashMap<Integer, TaxiGraphState> chargeStates = new HashMap<>();
                chargeStates.put(state.getStateOfCharge(), state);
                visitedStates.get(state.getNodeId()).put(state.getTimeStamp(), chargeStates);
            }
        } else {
            HashMap<Integer, HashMap<Integer, TaxiGraphState>> timeStates = new HashMap<>();
            HashMap<Integer, TaxiGraphState> chargeStates = new HashMap<>();
            chargeStates.put(state.getStateOfCharge(), state);
            timeStates.put(state.getTimeStamp(), chargeStates);

            visitedStates.put(state.getNodeId(), timeStates);
        }
    }

    protected TaxiGraphState getVisitedState(int nodeId, int timeStamp, int stateOfCharge){
        return visitedStates.get(nodeId).get(timeStamp).get(stateOfCharge);
    }

    abstract void addPreviousState(TaxiGraphState previousState, int stateId);

    public abstract List<TaxiGraphState> allReachableStates(TaxiGraphState var1);


    abstract boolean applicableInState(TaxiGraphState state);
}
