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

public class TaxiGraphStateModel extends GraphDefinedDomain.GraphStateModel {

    HashMap<TaxiGraphState, TaxiGraphState> states = new HashMap<>();

    public TaxiGraphStateModel(Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(transitionDynamics);
    }


    @Override
    public List<StateTransitionProb> stateTransitions(State state, Action action) {

        int actionId = ((GraphDefinedDomain.GraphActionType.GraphAction)action).aId;
        int nodeId = (int)state.get(VAR_NODE);
        int toNodeId;

        List<StateTransitionProb> resultTransitions = new ArrayList<>();

        Action newAction;
        if (actionId == ActionTypes.TO_NEXT_LOCATION.getValue()) {
            toNodeId = ((NextLocationAction)action).getToNodeId();
            newAction = action.copy();
        } else if (actionId == ActionTypes.PICK_UP_PASSENGER.getValue()) {
            toNodeId = ((PickUpPassengerAction)action).getToNodeId();
            newAction = action.copy();
        }else if (actionId == ActionTypes.GOING_TO_CHARGING_STATION.getValue()){
            toNodeId = ((GoingToChargingStationAction)action).getToNodeId();
            if(!((GoingToChargingStationAction)action).applicableInState((TaxiGraphState) state)){
                return resultTransitions;
            }
            newAction = action.copy();
        } else if (actionId == ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()){
            if (((TaxiGraphState)state).isStartingState() || !((TaxiGraphState)state).isPossibleToDoAction(ActionTypes.CHARGING_IN_CHARGING_STATION.getValue())){
                return resultTransitions;
            }
            toNodeId = nodeId;
            newAction = action.copy();
        } else {
            if (((TaxiGraphState)state).isStartingState()){
                return resultTransitions;
            }
            toNodeId = nodeId;
            newAction = action.copy();
        }

        State ns = state.copy();


        ((TaxiGraphState) ns).set(VAR_NODE, toNodeId);
        ((TaxiGraphState) ns).set(Utils.VAR_TIMESTAMP, this.getResultTimeStamp(state, newAction));
        ((TaxiGraphState) ns).set(Utils.VAR_STATE_OF_CHARGE, this.getResultStateOfCharge(state, newAction));


        if (states.containsKey(ns)){
            states.get(ns).addPreviousAction(newAction, actionId, (TaxiGraphState) state);
            return resultTransitions;
        }

        ((TaxiGraphState) ns).addPreviousAction(newAction, actionId, (TaxiGraphState) state);

        states.put((TaxiGraphState)ns, (TaxiGraphState)ns);

        StateTransitionProb tp = new StateTransitionProb(ns, 1.);
        resultTransitions.add(tp);

        return resultTransitions;
    }


    public State sample(State state, Action action) {
        state = state.copy();
        int actionId = ((GraphDefinedDomain.GraphActionType.GraphAction)action).aId;


        switch (actionId){
            case 0:
                setStateProperties(state, action,actionId, ((NextLocationAction)action).getToNodeId(),
                        this.getResultTimeStamp(state, action), this.getResultStateOfCharge(state, action), state);
                break;
            case 2:
                setStateProperties(state, action, actionId, ((GoingToChargingStationAction)action).getToNodeId(),
                        this.getResultTimeStamp(state, action), this.getResultStateOfCharge(state, action), state);
                break;
            default:
                setStateProperties(state, action, actionId, ((TaxiGraphState)state).getNodeId(),
                        this.getResultTimeStamp(state, action), this.getResultStateOfCharge(state, action), state);
                break;
        }

        return state;
    }


    private void setStateProperties(State state, Action action, int actionId, int toNodeId, int resultTime, int resultStateOfCharge ,State previousState){
        ((TaxiGraphState) state).set(VAR_NODE, toNodeId);
        ((TaxiGraphState) state).set(Utils.VAR_TIMESTAMP, resultTime);
        ((TaxiGraphState) state).set(Utils.VAR_STATE_OF_CHARGE, resultStateOfCharge);
        ((TaxiGraphState) state).addPreviousAction(action, actionId, (TaxiGraphState) previousState);

    }


    private int getResultTimeStamp(State state, Action action){
        return ((MeasurableAction)action).getActionTime((TaxiGraphState) state) + ((TaxiGraphState)state).getTimeStamp();
    }


    private int getResultStateOfCharge(State state, Action action){
        return ((MeasurableAction)action).getActionEnergyConsumption((TaxiGraphState)state) + ((TaxiGraphState)state).getStateOfCharge();
    }
}
