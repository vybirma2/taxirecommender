package evaluation;

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



}
