package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.states.TaxiGraphState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static domain.actions.ActionUtils.*;
import static utils.DistanceGraphUtils.getTripTime;

public class NextLocationActionType extends GraphDefinedDomain.GraphActionType {


    public NextLocationActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(aId, transitionDynamics);
    }


    @Override
    public String typeName() {
        return ActionTypes.TO_NEXT_LOCATION.getName();
    }



    @Override
    public List<Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();

        int node = (Integer)state.get("node");
        Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>> actionMap = this.transitionDynamics.get(node);
        Set<GraphDefinedDomain.NodeTransitionProbability> transitions = actionMap.get(this.aId);

        if (transitions != null){
            for (GraphDefinedDomain.NodeTransitionProbability neighbour : transitions){
                if (this.applicableInState((TaxiGraphState) state, neighbour.transitionTo)){
                    actions.add(new NextLocationAction(this.aId, node, neighbour.transitionTo, ((TaxiGraphState)state).getTimeStamp()));
                }
            }
        }

        return actions;
    }


    @Override
    protected boolean applicableInState(State state) {
        return super.applicableInState(state);
    }


    public int getActionTime(TaxiGraphState state, int toNodeId) {
        return getTripTime(state.getNodeId(), toNodeId);
    }


    private boolean applicableInState(TaxiGraphState state, int toNodeId){
        return applicableInState(state) &&
                shiftNotOver(state, this.getActionTime(state, toNodeId)) &&
                notRunOutOfBattery(state, toNodeId, getActionTime(state, toNodeId));
    }
}
