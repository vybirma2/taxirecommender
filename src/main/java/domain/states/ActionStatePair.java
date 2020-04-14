package domain.states;


import domain.actions.MeasurableAction;

public class ActionStatePair {

    private TaxiGraphState state;
    private int actionId;


    public ActionStatePair(TaxiGraphState state, int actionId) {
        this.state = state;
        this.actionId = actionId;
    }


    public TaxiGraphState getState() {
        return state;
    }


    public int getAction() {
        return actionId;
    }
}
