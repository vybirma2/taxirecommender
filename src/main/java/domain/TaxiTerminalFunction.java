package domain;

import domain.actions.TaxiActionType;
import domain.states.TaxiState;
import utils.Utils;

import java.util.List;

/**
 * Terminal function defining conditions of marking state as a terminal.
 */
public class TaxiTerminalFunction {

    private List<TaxiActionType> actionTypes;


    public TaxiTerminalFunction(List<TaxiActionType> actionTypes) {
        this.actionTypes = actionTypes;
    }


    public boolean isTerminal(TaxiState state) {
        return shiftOver(state) || runOutOfBattery(state) || noActionAvailable(state);
    }


    private boolean shiftOver(TaxiState state){
       return state.getTimeStamp() >= Utils.SHIFT_LENGTH + Utils.SHIFT_START_TIME;
    }


    private boolean runOutOfBattery(TaxiState state){
        return state.getStateOfCharge() <= 0;
    }


    private boolean noActionAvailable(TaxiState state) {
        for (TaxiActionType actionType : actionTypes){
            if (!actionType.allReachableStates(state).isEmpty()){
                return false;
            }
        }

        return true;
    }


    public List<TaxiActionType> getActionTypes() {
        return actionTypes;
    }
}
