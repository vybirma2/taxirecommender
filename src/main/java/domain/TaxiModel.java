package domain;

import domain.actions.*;
import domain.states.TaxiState;

import java.io.Serializable;
import java.util.*;

/**
 * Class representing model of whole planning with the main purpose of creating transitions when applying action on some state
 */
public class TaxiModel implements Serializable {

    private List<TaxiActionType> actionTypes;


    public TaxiModel(List<TaxiActionType> actionTypes) {
        this.actionTypes = actionTypes;
    }


    public List<MeasurableAction> allApplicableActionsFromState(TaxiState state) {
        List<MeasurableAction> result = new ArrayList<>();

        for (TaxiActionType a : actionTypes) {
            if (a.getActionId() != ActionTypes.PICK_UP_PASSENGER.getValue()){
                result.addAll(a.allApplicableActions(state));
            }
        }
        return result;
    }


    /**
     * @param state current state
     * @return all possible transitions - future states - i.e. generator of new states, if generated state already
     * exist only updates its previous actions and previous states
     */
    public List<TaxiState> allReachableStatesFromState(TaxiState state) {
        List<TaxiState> result = new ArrayList<>();

        for (TaxiActionType a : actionTypes) {
            result.addAll(a.allReachableStates(state));
        }

        return result;
    }
}
