package domain.actions;

import domain.states.TaxiState;
import domain.parameterestimation.EnergyConsumptionEstimator;

import java.util.*;

import static domain.actions.ActionUtils.*;
import static domain.utils.DistanceGraphUtils.getTripTime;

/**
 * Class with the main purpose of returning all available actions of moving to the neighbouring node in the environment.
 */
public class NextLocationActionType extends TaxiActionType {


    public NextLocationActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions) {
        super(actionId, transitions);
    }

    /**
     * Connections states reachable by going to next location aciton
     */
    @Override
    public void createConnections(TaxiState state) {
        ArrayList<Integer> trans = transitions.get(state.getNodeId());

        if (trans != null){
            for (int neighbour : trans){
                int time = getTripTime(state.getNodeId(), neighbour);
                if (this.applicableInState(state, neighbour, time)){
                    createConnectionBetweenStates(state,neighbour, time,
                            getConsumption(state.getNodeId(), neighbour), actionId);
                }
            }
        }
    }

    private int getConsumption(int fromNodeId, int toNodeId) {
        return EnergyConsumptionEstimator.getActionEnergyConsumption(fromNodeId, toNodeId);
    }

    @Override
    protected boolean applicableInState(TaxiState state) {
        return transitions.containsKey(state.getNodeId());
    }

    private boolean applicableInState(TaxiState state, int toNodeId, int time){
        return shiftNotOver(state, time) &&
                notRunOutOfBattery(state, toNodeId);
    }
}
