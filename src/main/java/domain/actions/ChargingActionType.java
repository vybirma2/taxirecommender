package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static domain.Utils.CHARGING_INTERVAL;
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
    public Action associatedAction(String strRep) {
        return new ChargingAction(this.aId, Integer.parseInt(strRep));
    }


    @Override
    public List<Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();

        if (this.applicableInState(state)) {
            actions.add(new ChargingAction(this.aId, CHARGING_INTERVAL));
        }

        return actions;
    }


    @Override
    protected boolean applicableInState(State s) {
        return notChargingInARow(s) && shiftNotOver(s, CHARGING_INTERVAL) && notFullyCharged(s) && super.applicableInState(s);
    }
}
