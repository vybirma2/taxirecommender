package domain;

import domain.actions.*;
import domain.states.TaxiGraphState;

import java.util.*;

/**
 * Class representing model of whole planning with the main purpose of creating transitions when applying action on some state
 */
public class TaxiGraphStateModel{

    HashMap<Integer, HashMap<TaxiGraphState, TaxiGraphState>> visited = new HashMap<>();


    public TaxiGraphStateModel() {
    }


    /**
     * @param state current state
     * @param action action done in this states
     * @return all possible transitions - future states - i.e. generator of new states, if generated state already
     * exist only updates its previous actions and previous states
     */

    public TaxiGraphState stateTransitions(TaxiGraphState state, MeasurableAction action) {

        TaxiGraphState newState = state.copy();

        int toNodeId = action.getToNodeId();
        int actionId = action.getActionId();

        setStateProperties(newState, action, actionId, toNodeId, this.getResultTimeStamp(state, action),
                this.getResultStateOfCharge(state, action), state);

        if (visited.containsKey(newState.hashCode()) && visited.get(newState.hashCode()).containsKey(newState)){
            visited.get(newState.hashCode()).get(newState).addPreviousAction(action, actionId, state);
            return null;
        } else if (visited.containsKey(newState.hashCode())) {
            newState.addPreviousAction(action, actionId, state);
            visited.get(newState.hashCode()).put(newState, newState);
        }else {
            newState.addPreviousAction(action, actionId, state);
            HashMap<TaxiGraphState, TaxiGraphState> states = new HashMap<>();
            states.put(state, state);
            visited.put(newState.hashCode(), states);
        }

        return newState;
    }


    private void setStateProperties(TaxiGraphState state, MeasurableAction action, int actionId, int toNodeId, int resultTime,
                                    int resultStateOfCharge, TaxiGraphState previousState){
        state.setNodeId(toNodeId);
        state.setTimeStamp(resultTime);
        state.setStateOfCharge(resultStateOfCharge);
        state.addPreviousAction(action, actionId, previousState);
    }


    private int getResultTimeStamp(TaxiGraphState state, MeasurableAction action){
        return action.getLength() + state.getTimeStamp();
    }


    private int getResultStateOfCharge(TaxiGraphState state, MeasurableAction action){
        return action.getConsumption() + state.getStateOfCharge();
    }
}
