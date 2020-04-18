package domain.states;


public class ActionStatePair {

    private TaxiState state;
    private int actionId;


    public ActionStatePair(TaxiState state, int actionId) {
        this.state = state;
        this.actionId = actionId;
    }


    public TaxiState getState() {
        return state;
    }


    public int getAction() {
        return actionId;
    }
}
