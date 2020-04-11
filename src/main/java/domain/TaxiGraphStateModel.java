package domain;

import domain.actions.*;
import domain.states.TaxiGraphState;

import java.util.*;

/**
 * Class representing model of whole planning with the main purpose of creating transitions when applying action on some state
 */
public class TaxiGraphStateModel{

    private List<TaxiActionType> actionTypes;


    public TaxiGraphStateModel(List<TaxiActionType> actionTypes) {
        this.actionTypes = actionTypes;
    }


    /**
     * @param state current state
     * @return all possible transitions - future states - i.e. generator of new states, if generated state already
     * exist only updates its previous actions and previous states
     */

    public List<TaxiGraphState> stateTransitions(TaxiGraphState state) {
        return allReachableStatesForActionTypes(state);
    }


    private List<TaxiGraphState> allReachableStatesForActionTypes(TaxiGraphState state) {
        List<TaxiGraphState> result = new ArrayList<>();

        for (TaxiActionType a : actionTypes) {
            result.addAll(a.allReachableStates(state));
        }

        return result;
    }
}
