package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import charging.ChargingConnection;
import charging.ChargingStation;
import charging.ChargingStationUtils;
import domain.states.TaxiGraphState;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static domain.actions.ActionUtils.*;
import static utils.Utils.NUM_OF_CHARGING_LENGTH_POSSIBILITIES;

public class ChargingActionType extends GraphDefinedDomain.GraphActionType {


    public ChargingActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(aId, transitionDynamics);
    }


    @Override
    public String typeName() {
        return ActionTypes.CHARGING_IN_CHARGING_STATION.getName();
    }



    @Override
    public List<Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();

        if (this.applicableInState(state)) {
            ChargingStation station = ChargingStationUtils.getChargingStation(((TaxiGraphState)state).getNodeId());
            if (!station.getAvailableConnections().isEmpty()){
                ChargingConnection connection = chooseBestChargingConnection(station.getAvailableConnections());

                int timeToFullStateOfCharge = timeToFullStateOfCharge((TaxiGraphState)state, connection);
                int chargingTimeUnit = timeToFullStateOfCharge/NUM_OF_CHARGING_LENGTH_POSSIBILITIES;

                for(int i = 1; i <= NUM_OF_CHARGING_LENGTH_POSSIBILITIES; i++){
                    if (applicableInState(state, i * chargingTimeUnit, getEnergyCharged(connection, i * chargingTimeUnit))){
                        actions.add(new ChargingAction(this.aId, i*chargingTimeUnit, station.getId(), connection.getId()));
                    }
                }
            }

        }
        return actions;
    }


    private int getEnergyCharged(ChargingConnection connection, double chargingTime){
        return (int)((connection.getPowerKW()*(chargingTime/60))/Utils.BATTERY_CAPACITY)*100;
    }


    private int timeToFullStateOfCharge(TaxiGraphState state, ChargingConnection connection){
        double currentStateOfChargeInKW = (state.getStateOfCharge()/100.) * Utils.BATTERY_CAPACITY;
        return (int)((Utils.BATTERY_CAPACITY - currentStateOfChargeInKW)/connection.getPowerKW()*60);
    }


    private ChargingConnection chooseBestChargingConnection(List<ChargingConnection> connections){
        return connections
                .stream()
                .max(Utils.chargingConnectionComparator).get();
    }


    @Override
    protected boolean applicableInState(State s) {
        return super.applicableInState(s);
    }


    protected boolean applicableInState(State s, double chargingTime, double energyCharged) {
        return shiftNotOver(s, chargingTime) && notOverCharging(s, energyCharged);
    }
}
