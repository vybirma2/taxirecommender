package domain.actions;

import charging.ChargingConnection;
import charging.ChargingStation;
import charging.ChargingStationReader;
import domain.states.TaxiState;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static domain.actions.ActionUtils.*;
import static utils.Utils.NUM_OF_CHARGING_LENGTH_POSSIBILITIES;

/**
 * Class with the main purpose of returning the best available charging actions in given state.
 */
public class ChargingActionType extends TaxiActionType {

    public ChargingActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions) {
        super(actionId, transitions);
    }



    /**
     * Choosing the best available connection and producing charging actions of different length.
     * @param state current previousState to be charging done in
     * @return charging actions available in current previousState - equally divided into NUM_OF_CHARGING_LENGTH_POSSIBILITIES
     * intervals.
     */
    @Override
    public void addAsPredecessorToAllReachableStates(TaxiState state) {

        if (this.applicableInState(state)) {
            ChargingStation station = ChargingStationReader.getChargingStation(state.getNodeId());

            if (!station.getAvailableConnections().isEmpty()){
                ChargingConnection connection = chooseBestChargingConnection(station.getAvailableConnections());

                int timeToFullStateOfCharge = timeToFullStateOfCharge(state, connection);
                int chargingTimeUnit = timeToFullStateOfCharge/NUM_OF_CHARGING_LENGTH_POSSIBILITIES;

                for(int i = 1; i <= NUM_OF_CHARGING_LENGTH_POSSIBILITIES; i++){
                    int energyCharged = getEnergyCharged(connection, i * chargingTimeUnit);
                    if (applicableInState(state, i * chargingTimeUnit, energyCharged)){
                        addStateStateAsPreviousToState(state, state.getNodeId(), i * chargingTimeUnit, energyCharged, actionId);
                    }
                }
            }
        }
    }


    @Override
    public List<MeasurableAction> allApplicableActions(TaxiState state) {
        List<MeasurableAction> actions = new ArrayList<>();

        if (this.applicableInState(state)) {

            ChargingStation station = ChargingStationReader.getChargingStation(state.getNodeId());

            if (!station.getAvailableConnections().isEmpty()){
                ChargingConnection connection = chooseBestChargingConnection(station.getAvailableConnections());

                int timeToFullStateOfCharge = timeToFullStateOfCharge(state, connection);
                int chargingTimeUnit = timeToFullStateOfCharge/NUM_OF_CHARGING_LENGTH_POSSIBILITIES;

                for(int i = 1; i <= NUM_OF_CHARGING_LENGTH_POSSIBILITIES; i++){
                    int energyCharged = getEnergyCharged(connection, i * chargingTimeUnit);
                    if (applicableInState(state, i * chargingTimeUnit, energyCharged)){
                        actions.add(new ChargingAction(actionId, state.getNodeId(), state.getNodeId(),
                                i * chargingTimeUnit, connection.getId()));
                    }
                }

                for(int i = 5; i <= 30; i+=5){
                    int energyCharged = getEnergyCharged(connection, i);
                    if (applicableInState(state, i, energyCharged)){
                        actions.add(new ChargingAction(actionId, state.getNodeId(), state.getNodeId(),
                                i, connection.getId()));
                    }
                }
            }
        }

        return actions;
    }


    private int getEnergyCharged(ChargingConnection connection, double chargingTime){
        return (int)(((connection.getPowerKW()*(chargingTime/60.))/Utils.BATTERY_CAPACITY)*100.);
    }


    private int timeToFullStateOfCharge(TaxiState state, ChargingConnection connection){
        double currentStateOfChargeInKW = (state.getStateOfCharge()/100.) * Utils.BATTERY_CAPACITY;
        return (int)((Utils.BATTERY_CAPACITY - currentStateOfChargeInKW)/connection.getPowerKW()*60.);
    }


    private ChargingConnection chooseBestChargingConnection(List<ChargingConnection> connections){
        return connections
                .stream()
                .max(Utils.chargingConnectionComparator).get();
    }



    @Override
    protected boolean applicableInState(TaxiState state) {
        return this.transitions.containsKey(state.getNodeId());
    }


    protected boolean applicableInState(TaxiState state, int chargingTime, int energyCharged) {
        return shiftNotOver(state, chargingTime) && notOverCharging(state, energyCharged) && energyCharged > 0;
    }

}
