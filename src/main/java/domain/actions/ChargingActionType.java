package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public List<Action> allApplicableActions(State s) {
        List<Action> actions = new ArrayList<>();
        int time = Utils.CHARGING_INTERVAL;
        if (this.applicableInState(s)) {
            while (time <= Utils.SHIFT_LENGTH) {
                actions.add(new ChargingAction(this.aId, time));
                time += Utils.CHARGING_INTERVAL;
            }
        }

        return actions;
    }
}
