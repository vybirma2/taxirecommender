package domain;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import domain.states.TaxiGraphState;

public class TaxiGraphTerminalFunction implements TerminalFunction {
    @Override
    public boolean isTerminal(State state) {
        return ((TaxiGraphState)state).getTimeStamp() >= Utils.SHIFT_LENGTH || ((TaxiGraphState)state).getStateOfCharge() <= 0;
    }
}
