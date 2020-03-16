package domain;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.action.ActionUtils;
import burlap.mdp.core.state.State;
import domain.states.TaxiGraphState;
import utils.Utils;

import java.util.List;

public class TaxiGraphTerminalFunction implements TerminalFunction {

    private List<ActionType> actionTypes;


    public TaxiGraphTerminalFunction(List<ActionType> actionTypes) {
        this.actionTypes = actionTypes;
    }


    @Override
    public boolean isTerminal(State state) {
        return shiftOver(state) || runOutOfBattery(state) || noActionAvailable(state);
    }


    private boolean shiftOver(State state){
       return ((TaxiGraphState)state).getTimeStamp() >= Utils.SHIFT_LENGTH + Utils.SHIFT_START_TIME;
    }


    private boolean runOutOfBattery(State state){
        return ((TaxiGraphState)state).getStateOfCharge() <= 0;
    }


    private boolean noActionAvailable(State state) {
        List<Action> actions = ActionUtils.allApplicableActionsForTypes(this.actionTypes, state);
        return actions.isEmpty();
    }
}
