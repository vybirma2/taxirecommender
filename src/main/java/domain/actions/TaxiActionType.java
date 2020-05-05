package domain.actions;

import domain.states.TaxiState;
import problemsolving.ChragingRecommender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract  class TaxiActionType implements Serializable {

    private static ChragingRecommender chragingRecommender;
    protected HashMap<Integer, ArrayList<Integer>> transitions;
    protected int actionId;


    public TaxiActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions) {
        this.actionId = actionId;
        this.transitions = transitions;
    }


    protected void addStateStateAsPreviousToState(TaxiState predecessor, int toNodeId, int length, int energyConsumption, int actionId){
        int resultTimeStamp = length + predecessor.getTimeStamp();
        int resultStateOfCharge = energyConsumption + predecessor.getStateOfCharge();
        int resultNodeId = toNodeId;

        TaxiState newState = new TaxiState(resultNodeId, resultStateOfCharge, resultTimeStamp);
        if (chragingRecommender.getState(newState) == null){
            System.out.println("shgvf");
        }
        chragingRecommender.addPreviousStateConnection(chragingRecommender.getState(newState).getId(), predecessor.getId(), actionId);
    }



    public int getActionId() {
        return actionId;
    }

    public static void setChragingRecommender(ChragingRecommender statesGenerator){
        chragingRecommender = statesGenerator;
    }


    public abstract void addAsPredecessorToAllReachableStates(TaxiState state);

    public abstract List<MeasurableAction> allApplicableActions(TaxiState state);

    abstract boolean applicableInState(TaxiState state);
}
