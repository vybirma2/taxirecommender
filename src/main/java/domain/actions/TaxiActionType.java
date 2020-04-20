package domain.actions;

import domain.states.TaxiState;
import evaluation.chargingrecommenderagent.ReachableStatesGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract  class TaxiActionType implements Serializable {

    private static ReachableStatesGenerator reachableStatesGenerator;
    protected HashMap<Integer, ArrayList<Integer>> transitions;
    protected int actionId;


    public TaxiActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions) {
        this.actionId = actionId;
        this.transitions = transitions;
    }


    protected void addNewState(List<TaxiState> states, TaxiState previousState, int toNodeId, int length, int energyConsumption){

        int resultTimeStamp = length + previousState.getTimeStamp();
        int resultStateOfCharge = energyConsumption + previousState.getStateOfCharge();
        int resultNodeId = toNodeId;

        TaxiState newState = new TaxiState(resultNodeId, resultStateOfCharge, resultTimeStamp);
        if (!reachableStatesGenerator.alreadyVisited(newState)){
            reachableStatesGenerator.addReachableState(newState);
            addPreviousState(newState, previousState.getId());
            states.add(newState);
        } else {
            TaxiState.stateId--;
            addPreviousState(reachableStatesGenerator.getVisitedState(newState), previousState.getId());
        }
    }



    public int getActionId() {
        return actionId;
    }

    public static void setReachableStatesGenerator(ReachableStatesGenerator statesGenerator){
        reachableStatesGenerator = statesGenerator;
    }


    abstract void addPreviousState(TaxiState previousState, int stateId);

    public abstract List<TaxiState> allReachableStates(TaxiState state);

    public abstract List<MeasurableAction> allApplicableActions(TaxiState state);

    abstract boolean applicableInState(TaxiState state);
}
