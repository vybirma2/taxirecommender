package domain.actions;

import domain.charging.ChargingConnection;
import domain.charging.ChargingStation;
import domain.charging.ChargingStationReader;
import domain.states.TaxiState;
import domain.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static domain.actions.ActionUtils.*;
import static domain.utils.Utils.NUM_OF_CHARGING_LENGTH_POSSIBILITIES;

/**
 * Class with the main purpose of returning the best available domain.charging actions in given state.
 */
public class ChargingActionType extends TaxiActionType {

    public ChargingActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions) {
        super(actionId, transitions);
    }


    /**
     * Creating connections between reachable states by charging actions.
     * @param state current previousState to be domain.charging done in
     */
    @Override
    public void createConnections(TaxiState state) {

        if (this.applicableInState(state)) {
            ChargingStation station = ChargingStationReader.getChargingStation(state.getNodeId());

            if (!station.getAvailableConnections().isEmpty()){
                for (ChargingConnection connection : station.getAvailableConnections()) {
                    int timeToFullStateOfCharge = timeToFullStateOfCharge(state, connection);
                    int chargingTimeUnit = timeToFullStateOfCharge/NUM_OF_CHARGING_LENGTH_POSSIBILITIES;

                    for(int i = 1; i <= NUM_OF_CHARGING_LENGTH_POSSIBILITIES; i++){
                        int energyCharged = getEnergyCharged(connection, i * chargingTimeUnit);
                        if (applicableInState(state, i * chargingTimeUnit, energyCharged)){
                            createConnectionBetweenStates(state, state.getNodeId(), i * chargingTimeUnit, energyCharged, actionId);
                        }
                    }
                }
            }
        }
    }

    private int getEnergyCharged(ChargingConnection connection, double chargingTime){
        return (int)(((connection.getPowerKW()*(chargingTime/60.))/Utils.BATTERY_CAPACITY)*100.);
    }

    private int timeToFullStateOfCharge(TaxiState state, ChargingConnection connection){
        double currentStateOfChargeInKW = (state.getStateOfCharge()/100.) * Utils.BATTERY_CAPACITY;
        return (int)((Utils.BATTERY_CAPACITY - currentStateOfChargeInKW)/connection.getPowerKW()*60.);
    }

    @Override
    protected boolean applicableInState(TaxiState state) {
        return this.transitions.containsKey(state.getNodeId());
    }

    protected boolean applicableInState(TaxiState state, int chargingTime, int energyCharged) {
        return shiftNotOver(state, chargingTime) && notOverCharging(state, energyCharged) && energyCharged > 0;
    }
}
