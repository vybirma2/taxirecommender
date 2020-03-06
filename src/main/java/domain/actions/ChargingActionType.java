package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import charging.ChargingConnection;
import charging.ChargingStation;
import charging.ChargingStationUtils;
import domain.states.TaxiGraphState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static utils.Utils.CHARGING_INTERVAL;
import static domain.actions.ActionUtils.*;

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

            List<ChargingConnection> connections = station.getAvailableConnections();
            for (ChargingConnection connection : connections){
                actions.add(new ChargingAction(this.aId, CHARGING_INTERVAL, station.getId(), connection.getId()));
            }
        }

        return actions;
    }


    @Override
    protected boolean applicableInState(State s) {
        return notChargingInARow(s) && shiftNotOver(s, CHARGING_INTERVAL) && notFullyCharged(s) && super.applicableInState(s);
    }
}
