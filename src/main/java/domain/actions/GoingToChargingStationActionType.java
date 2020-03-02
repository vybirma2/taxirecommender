package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static domain.actions.ActionUtils.*;

public class GoingToChargingStationActionType extends GraphDefinedDomain.GraphActionType {


    public GoingToChargingStationActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(aId, transitionDynamics);
    }


    @Override
    public String typeName() {
        return ActionTypes.GOING_TO_CHARGING_STATION.getName();
    }


    @Override
    public burlap.mdp.core.action.Action associatedAction(String strRep) {
        return new GoingToChargingStationAction(this.aId, Integer.parseInt(strRep));
    }


    @Override
    public List<Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();

        if (this.applicableInState(state)){
                actions.add(new GoingToChargingStationAction(this.aId));
        }

        return actions;
    }


    @Override
    protected boolean applicableInState(State s) {
        return notChargingInARow(s) && notGoingToChargingPreviously(s) && notFullyCharged(s) && super.applicableInState(s);
    }


}
