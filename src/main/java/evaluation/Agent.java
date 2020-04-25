package evaluation;


import domain.TaxiModel;
import domain.TaxiRecommenderDomain;
import domain.actions.MeasurableAction;
import domain.states.TaxiState;
import parameterestimation.TaxiTrip;

import java.util.List;

public abstract class Agent {

    protected TaxiRecommenderDomain domain;

    public Agent(TaxiRecommenderDomain domain) {
        this.domain = domain;
    }

    public abstract MeasurableAction chooseAction(TaxiState currentState, List<MeasurableAction> actions);

    public abstract boolean tripOffer(TaxiState currentState, Integer trip);
}
