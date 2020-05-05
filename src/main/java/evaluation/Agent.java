package evaluation;


import domain.TaxiRecommenderDomain;
import domain.actions.MeasurableAction;
import domain.states.TaxiState;

import java.util.List;

public abstract class Agent {

    protected TaxiRecommenderDomain domain;
    protected TaxiState currentState;

    public Agent(TaxiRecommenderDomain domain, TaxiState simulationState) {
        this.domain = domain;
        this.currentState = simulationState;
    }

    public void setCurrentState(TaxiState currentState) {
        this.currentState = currentState;
    }

    public abstract MeasurableAction chooseAction(TaxiState currentState, List<MeasurableAction> actions);

    public abstract boolean tripOffer(TaxiState currentState, SimulationTaxiTrip trip);
}
