package domain;

import domain.charging.ChargingRateType;
import domain.utils.SuccessfulPickUpParameters;
import domain.actions.ActionTypes;
import domain.states.StatePredecessors;
import domain.states.TaxiState;
import domain.states.TaxiGraphStateComparator;
import domain.parameterestimation.ParameterEstimator;
import domain.utils.Utils;

import java.util.*;

import static domain.actions.ActionUtils.*;
import static domain.utils.DistanceGraphUtils.getIntervalStart;


/**
 * Reward function containing functions to compute state rewards after generating all reachable states by
 * dynamic programing i.e. by starting with the last terminal states - the end of the shift - and continuing
 * to its start.
 */
public class TaxiRewardFunction {

    private List<TaxiState> states;
    private ParameterEstimator parameterEstimator;
    private StatePredecessors predecessors;


    public TaxiRewardFunction(List<TaxiState> states, StatePredecessors predecessors, ParameterEstimator parameterEstimator) {
        this.states = states;
        this.parameterEstimator = parameterEstimator;
        this.predecessors = predecessors;
    }


    /**
     * Performs dynamic programing by starting with terminal states added to priority queue where priority is timestamp
     * of state. Setting reward for all reachable states till the first one.
     */
    public void computeReward(){

        PriorityQueue<TaxiState> openedSet = getSortedStates(states);

        while (openedSet.size() > 1){
            TaxiState state = openedSet.poll();
            setPreviousStateReward(state);
        }
    }


    private PriorityQueue<TaxiState> getSortedStates(List<TaxiState> states){
        PriorityQueue<TaxiState> sortedStates = new PriorityQueue<>(new TaxiGraphStateComparator());
        sortedStates.addAll(states);
        return sortedStates;
    }


    /**
     * Passes through all actions through which it is possible to reach given state, gets their reward and tries
     * to offer it to the corresponding previous state
     * @param state
     * @return set of visited states to add to openSet
     */
    private void setPreviousStateReward(TaxiState state){

        for (int actionId = 0; actionId < Utils.NUM_OF_ACTION_TYPES; actionId++){
            List<Integer> previousStateNodesOfAction = predecessors.getPreviousStateNodesOfActionInState(actionId, state.getId());
            if (previousStateNodesOfAction != null){
                for (Integer previousStateNode : previousStateNodesOfAction){
                    setPreviousStateReward(actionId, states.get(previousStateNode), state);
                }
            }

        }
    }


    /**
     * Tries to set maximalReward to previous state according to the given action done. If action of picking up passenger
     * it sets reward of the current state as reward received after picking up passenger in previous node and transferring
     * to the current one.
     * @param actionId
     * @param previousState

     */
    private void setPreviousStateReward(int actionId, TaxiState previousState, TaxiState currentState){
        switch (actionId){
            case 0:
            case 1:
                if (currentState.getMaxRewardActionId() != ActionTypes.GOING_TO_CHARGING_STATION.getValue()){
                    previousState.setActionReward(actionId, currentState.getId(), getStayingAndNextLocationReward(currentState));
                }
                break;
            case 2:
                if (currentState.getMaxRewardActionId() != ActionTypes.GOING_TO_CHARGING_STATION.getValue()){
                    previousState.setActionReward(actionId, currentState.getId(), getGoingToChargingStationReward(currentState));
                }
                break;
            case 3:
                previousState.setActionReward(actionId, currentState.getId(), getChargingReward(previousState, currentState));
                break;
            case 4:
                previousState.addAfterTaxiTripStateReward(currentState.getNodeId(), currentState.getReward());
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
    private double getStayingAndNextLocationReward(TaxiState state){
        HashMap<Integer, Double> destinationProbabilities = parameterEstimator.getDestinationProbabilitiesInNode(state.getNodeId(), state.getTimeStamp());
        Set<Double> outOfChargeTimeDestinationProbabilities = getOutOfChargeTimeProbabilities(state, destinationProbabilities);

        double sumOutOfChargeTimeProbabilities = outOfChargeTimeDestinationProbabilities.stream().reduce(0., Double::sum);
        double pickUpProbability = parameterEstimator.getPickUpProbabilityInNode(state.getNodeId(), state.getTimeStamp());

        double notPickingPassengerReward = state.getReward() * (1 - pickUpProbability + pickUpProbability * sumOutOfChargeTimeProbabilities) ;
        double pickUpPassengerReward = getPickupPassengerReward(state, destinationProbabilities, pickUpProbability);

        return notPickingPassengerReward + pickUpPassengerReward;
    }


    private double getGoingToChargingStationReward(TaxiState state) {
        return state.getReward();
    }


    private double getChargingReward(TaxiState previousState, TaxiState currentState) {
        return currentState.getReward() - getCostForCharging(currentState.getTimeStamp() - previousState.getTimeStamp(),
                currentState.getStateOfCharge() - previousState.getStateOfCharge());
    }


    private double getCostForCharging(int timeOfCharging, int energyCharged){
        int powerKWCharged = (int)((energyCharged/100)*Utils.BATTERY_CAPACITY);
        double timeHours = timeOfCharging/60.;

        if (powerKWCharged/timeHours < ChargingRateType.SLOW_CHARGING.getKWMax()){
            return -timeOfCharging * ChargingRateType.SLOW_CHARGING.getRate();
        } else if (powerKWCharged/timeHours < ChargingRateType.STANDARD_CHARGING.getKWMax()){
            return -timeOfCharging * ChargingRateType.STANDARD_CHARGING.getRate();
        } else {
            return -timeOfCharging * ChargingRateType.SPEED_CHARGING.getRate();
        }
    }


    /**
     * @param state
     * @param destinationProbabilities probabilities of passenger commuting to some location received from parameterEstimator
     * @param pickUpProbability probability of picking up passenger
     * @return reward for picking up passenger computed as sum of rewards for trips from dataset multiplied by pickup probability
     */
    private double getPickupPassengerReward(TaxiState state, HashMap<Integer, Double> destinationProbabilities,
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
    private Set<Double> getOutOfChargeTimeProbabilities(TaxiState state , HashMap<Integer, Double> destinationProbabilities){
        HashSet<Double> result = new HashSet<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> tripLengths = parameterEstimator.getTaxiTripLengths();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> tripConsumptions = parameterEstimator.getTaxiTripConsumptions();

        int startInterval = getIntervalStart(state.getTimeStamp());

        if (destinationProbabilities != null){
            for (Map.Entry<Integer, Double> entry : destinationProbabilities.entrySet()){
                if (!shiftNotOver(state, tripLengths.get(startInterval).get(state.getNodeId()).get(entry.getKey()).intValue())
                        || !notRunOutOfBattery(state.getStateOfCharge(), Utils.MINIMAL_STATE_OF_CHARGE + tripConsumptions.get(startInterval).get(state.getNodeId()).get(entry.getKey()).intValue())) {
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
    private SuccessfulPickUpParameters getSuccessfulPickUpParameters(TaxiState state , HashMap<Integer, Double> destinationProbabilities){
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
                int tripLength = tripLengths.get(startInterval).get(state.getNodeId()).get(entry.getKey()).intValue();

                if (shiftNotOver(state, tripLength) && notRunOutOfBattery(state.getStateOfCharge(), consumption)){
                    probabilities.add(entry.getValue());
                    tripReward.add(getTripReward(tripDistances.get(startInterval).get(state.getNodeId()).get(entry.getKey())));
                    futureStateReward.add(getAfterPickUpStateReward(state, entry.getKey()));
                }
            }
        }

        return new SuccessfulPickUpParameters(probabilities, tripReward, futureStateReward);
    }


    public static double getTripReward(double distance){
        return distance * Utils.TAXI_FARE_FOR_KM + Utils.TAXI_START_JOURNEY_FEE;
    }


    private double getAfterPickUpStateReward(TaxiState state, Integer toNodeId){
        return state.getAfterTaxiTripStateReward(toNodeId);
    }
}
