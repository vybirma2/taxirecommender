package domain.states;


import burlap.mdp.core.action.Action;

public class ActionStatePair {

    private TaxiGraphState state;
    private Action action;


    public ActionStatePair(TaxiGraphState state, Action action) {
        this.state = state;
        this.action = action;
    }


    public TaxiGraphState getState() {
        return state;
    }

    public Action getAction() {
        return action;
    }
}
