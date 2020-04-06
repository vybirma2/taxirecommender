package domain;

import domain.actions.TaxiActionType;
import domain.states.TaxiGraphState;
import utils.Utils;

import java.util.List;

/**
 * Terminal function defining conditions of marking state as a terminal.
 */
public class TaxiGraphTerminalFunction {

    private List<TaxiActionType> actionTypes;


    public TaxiGraphTerminalFunction(List<TaxiActionType> actionTypes) {
        this.actionTypes = actionTypes;
    }


    public boolean isTerminal(TaxiGraphState state) {
        return shiftOver(state) || runOutOfBattery(state) || noActionAvailable(state);
    }


    private boolean shiftOver(TaxiGraphState state){
       return state.getTimeStamp() >= Utils.SHIFT_LENGTH + Utils.SHIFT_START_TIME;
    }


    private boolean runOutOfBattery(TaxiGraphState state){
        return state.getStateOfCharge() <= 0;
    }


    private boolean noActionAvailable(TaxiGraphState state) {
        for (TaxiActionType actionType : actionTypes){
            if (!actionType.allApplicableActions(state).isEmpty()){
                return false;
            }
        }

        return true;
    }


    public List<TaxiActionType> getActionTypes() {
        return actionTypes;
    }
}
