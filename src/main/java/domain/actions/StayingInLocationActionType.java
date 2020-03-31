package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.states.TaxiGraphState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static utils.Utils.STAYING_INTERVAL;
import static domain.actions.ActionUtils.shiftNotOver;

/**
 * Class with the main purpose of returning all available actions of staying in some node in the environment.
 */
public class StayingInLocationActionType extends GraphDefinedDomain.GraphActionType {


    public StayingInLocationActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(aId, transitionDynamics);
    }


    @Override
    public String typeName() {
        return ActionTypes.STAYING_IN_LOCATION.getName();
    }


    @Override
    public List<Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();

        if (this.applicableInState(state)) {
            actions.add(new StayingInLocationAction(this.aId, STAYING_INTERVAL, ((TaxiGraphState) state).getNodeId()));
        }

        return actions;
    }


    @Override
    protected boolean applicableInState(State s) {
        return shiftNotOver(s, STAYING_INTERVAL) && super.applicableInState(s);
    }
}
