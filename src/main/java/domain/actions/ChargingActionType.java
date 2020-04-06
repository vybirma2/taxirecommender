package domain.actions;

import charging.ChargingConnection;
import charging.ChargingStation;
import charging.ChargingStationReader;
import domain.states.TaxiGraphState;
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
     * @param state current state to be charging done in
     * @return charging actions available in current state - equally divided into NUM_OF_CHARGING_LENGTH_POSSIBILITIES
     * intervals.
     */
    @Override
    public List<MeasurableAction> allApplicableActions(TaxiGraphState state) {
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
                        actions.add(new ChargingAction(this.actionId, state.getNodeId(), state.getNodeId(), state.getTimeStamp(),
                                i*chargingTimeUnit, connection.getId(), energyCharged));
                    }
                }
            }
        }
        return actions;
    }


    private int getEnergyCharged(ChargingConnection connection, double chargingTime){
        return (int)(((connection.getPowerKW()*(chargingTime/60.))/Utils.BATTERY_CAPACITY)*100.);
    }


    private int timeToFullStateOfCharge(TaxiGraphState state, ChargingConnection connection){
        double currentStateOfChargeInKW = (state.getStateOfCharge()/100.) * Utils.BATTERY_CAPACITY;
        return (int)((Utils.BATTERY_CAPACITY - currentStateOfChargeInKW)/connection.getPowerKW()*60.);
    }


    private ChargingConnection chooseBestChargingConnection(List<ChargingConnection> connections){
        return connections
                .stream()
                .max(Utils.chargingConnectionComparator).get();
    }



    @Override
    protected boolean applicableInState(TaxiGraphState state) {
        return this.transitions.containsKey(state.getNodeId());
    }


    protected boolean applicableInState(TaxiGraphState state, double chargingTime, double energyCharged) {
        return shiftNotOver(state, chargingTime) && notOverCharging(state, energyCharged) && energyCharged > 0;
    }

}
