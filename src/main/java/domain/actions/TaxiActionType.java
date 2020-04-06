package domain.actions;

import domain.states.TaxiGraphState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract  class TaxiActionType {

    protected HashMap<Integer, ArrayList<Integer>> transitions;
    protected int actionId;

    public TaxiActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions) {
        this.actionId = actionId;
        this.transitions = transitions;
    }

    public abstract List<MeasurableAction> allApplicableActions(TaxiGraphState var1);

    abstract boolean applicableInState(TaxiGraphState state);

}
