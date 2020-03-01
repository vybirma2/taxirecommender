package domain.actions;

import domain.states.TaxiGraphState;

public interface MeasurableAction {
    double getActionTime(TaxiGraphState state);
    double getActionEnergyConsumption(TaxiGraphState state);
}
