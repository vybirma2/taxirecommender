package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.Utils;

import java.util.*;

public class StayingInLocationActionType extends GraphDefinedDomain.GraphActionType {

    public StayingInLocationActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(aId, transitionDynamics);
    }

    @Override
    public String typeName() {
        return ActionTypes.STAYING_IN_LOCATION.getName();
    }


    @Override
    public burlap.mdp.core.action.Action associatedAction(String strRep) {
        return new StayingInLocationAction(this.aId, Integer.parseInt(strRep));
    }


    @Override
    public List<Action> allApplicableActions(State s) {
        List<Action> actions = new ArrayList<>();
        int time = Utils.STAYING_INTERVAL;
        if (this.applicableInState(s)) {
            while (time <= Utils.SHIFT_LENGTH) {
                actions.add(new StayingInLocationAction(this.aId, time));
                time += Utils.STAYING_INTERVAL;
            }
        }

        return actions;
    }

}
