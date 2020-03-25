package domain.actions;

import domain.states.TaxiGraphState;

public interface MeasurableAction {

    int getActionTime(TaxiGraphState state);

    int getActionEnergyConsumption(TaxiGraphState state);
}
