package domain;

import domain.actions.ActionTypes;
import domain.actions.ChargingAction;
import domain.actions.MeasurableAction;
import domain.states.ActionStatePair;
import domain.states.TaxiGraphState;
import domain.states.TaxiGraphStateComparator;
import parameterestimation.ParameterEstimator;
import utils.Utils;

import java.util.*;

import static domain.actions.ActionUtils.*;
import static utils.DistanceGraphUtils.getIntervalStart;


/**
 * Reward function containing functions to compute state rewards after generating all reachable states by
 * dynamic programing i.e. by starting with the last terminal states - the end of the shift - and continuing
 * to its start.
 */
public class TaxiGraphRewardFunction {

    private TaxiGraphTerminalFunction terminalFunction;
    private ParameterEstimator parameterEstimator;


    public TaxiGraphRewardFunction(TaxiGraphTerminalFunction terminalFunction, ParameterEstimator parameterEstimator) {
        this.terminalFunction = terminalFunction;
        this.parameterEstimator = parameterEstimator;
    }


    /**
     * Performs dynamic programing by starting with terminal states added to priority queue where priority is timestamp
     * of state. Setting reward for all reachable states till the first one.
     * @param states all reachable states
     */
    public void computeRewardForStates(Set<TaxiGraphState> states){

        PriorityQueue<TaxiGraphState> openedSet = getSortedTerminalStates(states);
        HashSet<TaxiGraphState> visited = new HashSet<>(openedSet);

        while (!openedSet.isEmpty()){
            TaxiGraphState state = openedSet.poll();
            Set<TaxiGraphState> previousState = setPreviousStateReward(state);

            if (previousState == null){
                continue;
            }
            for (TaxiGraphState taxiGraphState : previousState){
                if (!visited.contains(taxiGraphState)){
                    openedSet.add(taxiGraphState);
                    visited.add(taxiGraphState);
                }
            }

        }
    }


    private PriorityQueue<TaxiGraphState> getSortedTerminalStates(Set<TaxiGraphState> states){
        PriorityQueue<TaxiGraphState> terminalStates = new PriorityQueue<>(new TaxiGraphStateComparator());
        for (TaxiGraphState state : states){
            if (terminalFunction.isTerminal(state)){
                terminalStates.add(state);
            }
        }

        return terminalStates;
    }


    /**
     * Passes through all actions through which it is possible to reach given state, gets their reward and tries
     * to offer it to the corresponding previous state
     * @param state
     * @return set of visited states to add to openSet
     */
    private Set<TaxiGraphState> setPreviousStateReward(TaxiGraphState state){

        if (state.isStartingState()){
            return null;
        }

        HashSet<TaxiGraphState> visitedStates = new HashSet<>();

        /*for (Integer actionId : state.getPreviousActions()){
            for (ActionStatePair pair : state.getPreviousStatesOfAction(actionId)){
                setPreviousStateReward(actionId, visitedStates, pair.getAction(), pair.getState(), state);
            }
        }*/
        return visitedStates;
    }


    /**
     * Tries to set maximalReward to previous state according to the given action done. If action of picking up passenger
     * it sets reward of the current state as reward received after picking up passenger in previous node and transferring
     * to the current one.
     * @param actionId
     * @param visitedStates
     * @param action
     * @param previousState
     * @param currentState
     */
    private void setPreviousStateReward(int actionId,
                                        HashSet<TaxiGraphState> visitedStates, MeasurableAction action, TaxiGraphState previousState,
                                        TaxiGraphState currentState){
        switch (actionId){
            case 0:
            case 1:
               /* previousState.setActionReward(action, getStayingAndNextLocationReward(currentState), currentState);
                visitedStates.add(previousState);
                break;*/
            case 2:
               /* if (currentState.getMaxRewardActionId() != ActionTypes.TO_NEXT_LOCATION.getValue()){
                    previousState.setActionReward(action, getGoingToChargingStationReward(currentState), currentState);
                }*/
                visitedStates.add(previousState);
                break;
            case 3:
               /* if (currentState.getMaxRewardActionId() != ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()){
                    previousState.setActionReward(action, getChargingReward(currentState, (ChargingAction) action), currentState);
                }*/
                visitedStates.add(previousState);
                break;
            case 4:
                previousState.addAfterTaxiTripStateReward(currentState.getNodeId(), currentState.getReward());
                visitedStates.add(previousState);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + actionId);
        }
    }


    /**
     * Reward is computed as: reward_for_not_picking_passenger + reward_for_picking_passenger where:
     *
     * reward_for_not_picking_passenger = current_state_reward * (not_pick_up_probability + pick_up_probability * sum_of_probabilities_of_not_doable_trips)
     * reward_for_picking_passenger = reward_for_all_doable_trips * pick_up_probability
     *
     * @param state
     * @return reward for going to next location and staying in current node
     */
    private double getStayingAndNextLocationReward(TaxiGraphState state){
        HashMap<Integer, Double> destinationProbabilities = parameterEstimator.getDestinationProbabilitiesInNode(state.getNodeId(), state.getTimeStamp());
        Set<Double> outOfChargeTimeDestinationProbabilities = getOutOfChargeTimeProbabilities(state, destinationProbabilities);

        double sumOutOfChargeTimeProbabilities = outOfChargeTimeDestinationProbabilities.stream().reduce(0., Double::sum);
        double pickUpProbability = parameterEstimator.getPickUpProbabilityInNode(state.getNodeId(), state.getTimeStamp());

        double notPickingPassengerReward = state.getReward() * (1 - pickUpProbability + pickUpProbability * sumOutOfChargeTimeProbabilities) ;
        double pickUpPassengerReward = getPickupPassengerReward(state, destinationProbabilities, pickUpProbability);

        return notPickingPassengerReward + pickUpPassengerReward;
    }


    private double getGoingToChargingStationReward(TaxiGraphState state) {
        return state.getReward();
    }


    private double getChargingReward(TaxiGraphState state, ChargingAction action) {
        return state.getReward() + action.getChargingCost();
    }


    /**
     * @param state
     * @param destinationProbabilities probabilities of passenger commuting to some location received from parameterEstimator
     * @param pickUpProbability probability of picking up passenger
     * @return reward for picking up passenger computed as sum of rewards for trips from dataset multiplied by pickup probability
     */
    private double getPickupPassengerReward(TaxiGraphState state, HashMap<Integer, Double> destinationProbabilities,
                                            double pickUpProbability){
        SuccessfulPickUpParameters successfulPickUpParameters = getSuccessfulPickUpParameters(state, destinationProbabilities);

        ArrayList<Double> probabilities = successfulPickUpParameters.getProbabilities();
        ArrayList<Double> tripReward = successfulPickUpParameters.getTripReward();
        ArrayList<Double> futureStateReward = successfulPickUpParameters.getFutureStateReward();


        double resultReward = 0;

        for (int i = 0; i < probabilities.size(); i++){
            resultReward += probabilities.get(i) * (tripReward.get(i) + futureStateReward.get(i));
        }

        return resultReward * pickUpProbability;
    }


    /**
     * @param state
     * @param destinationProbabilities
     * @return probability of trips which are not possible to do in current state because of time or state of charge
     */
    private Set<Double> getOutOfChargeTimeProbabilities(TaxiGraphState state , HashMap<Integer, Double> destinationProbabilities){
        HashSet<Double> result = new HashSet<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> tripLengths = parameterEstimator.getTaxiTripLengths();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> tripConsumptions = parameterEstimator.getTaxiTripConsumptions();

        int startInterval = getIntervalStart(state.getTimeStamp());

        if (destinationProbabilities != null){
            for (Map.Entry<Integer, Double> entry : destinationProbabilities.entrySet()){
                if (!shiftNotOver(state, tripLengths.get(startInterval).get(state.getNodeId()).get(entry.getKey()).longValue())
                        || !notRunOutOfBattery(state, tripConsumptions.get(startInterval).get(state.getNodeId()).get(entry.getKey()).intValue())) {
                    result.add(entry.getValue());
                }
            }
        }

        return result;
    }


    /**
     * @param state
     * @param destinationProbabilities
     * @return probability of doable trips from current state
     */
    private SuccessfulPickUpParameters getSuccessfulPickUpParameters(TaxiGraphState state , HashMap<Integer, Double> destinationProbabilities){
        ArrayList<Double> probabilities = new ArrayList<>();
        ArrayList<Double> tripReward = new ArrayList<>();
        ArrayList<Double> futureStateReward = new ArrayList<>();

        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> tripLengths = parameterEstimator.getTaxiTripLengths();
        HashMap<Integer,  HashMap<Integer, HashMap<Integer, Double>>> tripConsumptions = parameterEstimator.getTaxiTripConsumptions();
        HashMap<Integer,  HashMap<Integer, HashMap<Integer, Double>>> tripDistances = parameterEstimator.getTaxiTripDistances();

        int startInterval = getIntervalStart(state.getTimeStamp());

        if (destinationProbabilities != null){
            for (Map.Entry<Integer, Double> entry : destinationProbabilities.entrySet()){
                int consumption = tripConsumptions.get(startInterval).get(state.getNodeId()).get(entry.getKey()).intValue();
                long tripLength = tripLengths.get(startInterval).get(state.getNodeId()).get(entry.getKey()).longValue();

                if (shiftNotOver(state, tripLength) && notRunOutOfBattery(state, consumption)){
                    probabilities.add(entry.getValue());
                    tripReward.add(getTripReward(tripDistances.get(startInterval).get(state.getNodeId()).get(entry.getKey())));
                    futureStateReward.add(getAfterPickUpStateReward(state, entry.getKey()));
                }
            }
        }

        return new SuccessfulPickUpParameters(probabilities, tripReward, futureStateReward);
    }


    private double getTripReward(double distance){
        return distance * Utils.TAXI_COST_FOR_KM + Utils.TAXI_START_JOURNEY_FEE;
    }


    private double getAfterPickUpStateReward(TaxiGraphState state, Integer toNodeId){
        Double result = state.getAfterTaxiTripStateReward(toNodeId);
        if (result == null){
            return Double.MIN_VALUE;
        } else {
            return result;
        }
    }
}
