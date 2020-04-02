package domain;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.actions.*;
import domain.states.TaxiGraphState;
import utils.Utils;

import java.util.*;

import static utils.Utils.VAR_NODE;

/**
 * Class representing model of whole planning with the main purpose of creating transitions when applying action on some state
 */
public class TaxiGraphStateModel extends GraphDefinedDomain.GraphStateModel {

    HashMap<TaxiGraphState, TaxiGraphState> states = new HashMap<>();
    HashSet<TaxiGraphState> visited = new HashSet<>();


    public TaxiGraphStateModel(Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(transitionDynamics);
    }


    /**
     * @param state current state
     * @param action action done in this states
     * @return all possible transitions - future states - i.e. generator of new states, if generated state already
     * exist only updates its previous actions and previous states
     */
    @Override
    public List<StateTransitionProb> stateTransitions(State state, Action action) {

        List<StateTransitionProb> resultTransitions = new ArrayList<>();
        Action newAction = action.copy();
        State ns = state.copy();

        int toNodeId = ((MeasurableAction)action).getToNodeId();
        int actionId = ((GraphDefinedDomain.GraphActionType.GraphAction)action).aId;

        setStateProperties(ns, newAction, actionId, toNodeId, this.getResultTimeStamp(state, newAction),
                this.getResultStateOfCharge(state, newAction), state);

        if (visited.contains(ns)){
            states.get(ns).addPreviousAction(newAction, actionId, (TaxiGraphState) state);
            return resultTransitions;
        } else {
            states.put((TaxiGraphState)ns, (TaxiGraphState)ns);
            visited.add((TaxiGraphState)ns);
        }

        StateTransitionProb tp = new StateTransitionProb(ns, 1.);
        resultTransitions.add(tp);

        return resultTransitions;
    }


    /**
     * @param state
     * @param action
     * @return result state after applying given action to given state
     */
    public State sample(State state, Action action) {
        state = state.copy();
        int actionId = ((GraphDefinedDomain.GraphActionType.GraphAction)action).aId;

        setStateProperties(state, action,actionId, ((MeasurableAction)action).getToNodeId(),
                this.getResultTimeStamp(state, action), this.getResultStateOfCharge(state, action), state);

        return state;
    }


    private void setStateProperties(State state, Action action, int actionId, int toNodeId, int resultTime,
                                    int resultStateOfCharge ,State previousState){
        ((TaxiGraphState) state).set(VAR_NODE, toNodeId);
        ((TaxiGraphState) state).set(Utils.VAR_TIMESTAMP, resultTime);
        ((TaxiGraphState) state).set(Utils.VAR_STATE_OF_CHARGE, resultStateOfCharge);
        ((TaxiGraphState) state).addPreviousAction(action, actionId, (TaxiGraphState) previousState);
        ((TaxiGraphState) state).setChanged(false);
    }


    private int getResultTimeStamp(State state, Action action){
        return ((MeasurableAction)action).getActionTime((TaxiGraphState) state) + ((TaxiGraphState)state).getTimeStamp();
    }


    private int getResultStateOfCharge(State state, Action action){
        return ((MeasurableAction)action).getActionEnergyConsumption((TaxiGraphState)state) + ((TaxiGraphState)state).getStateOfCharge();
    }
}
