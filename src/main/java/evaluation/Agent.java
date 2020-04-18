package evaluation;


import domain.TaxiModel;
import domain.actions.MeasurableAction;
import domain.states.TaxiState;

import java.util.List;

public abstract class Agent {

    protected TaxiModel taxiModel;

    public Agent(TaxiModel taxiModel) {
        this.taxiModel = taxiModel;
    }

    public abstract MeasurableAction chooseAction(TaxiState currentState, List<MeasurableAction> actions);

    public abstract boolean tripOffer(TaxiState currentState, int tripToNode);
}
