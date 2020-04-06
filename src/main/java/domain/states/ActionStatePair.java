package domain.states;


import domain.actions.MeasurableAction;

public class ActionStatePair {

    private TaxiGraphState state;
    private MeasurableAction action;


    public ActionStatePair(TaxiGraphState state, MeasurableAction action) {
        this.state = state;
        this.action = action;
    }


    public TaxiGraphState getState() {
        return state;
    }


    public MeasurableAction getAction() {
        return action;
    }
}
