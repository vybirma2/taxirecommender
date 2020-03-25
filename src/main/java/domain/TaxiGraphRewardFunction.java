package domain;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import domain.actions.ActionUtils;
import domain.states.TaxiGraphState;
import parameterestimation.ParameterEstimator;
import utils.DistanceGraphUtils;
import utils.Utils;

import java.util.*;

import static domain.actions.ActionUtils.runOutOfBattery;
import static domain.actions.ActionUtils.shiftOver;
import static utils.DistanceGraphUtils.getIntervalStart;


public class TaxiGraphRewardFunction implements RewardFunction {

    private TerminalFunction terminalFunction;
    private ParameterEstimator parameterEstimator;

    public TaxiGraphRewardFunction(TerminalFunction terminalFunction, ParameterEstimator parameterEstimator) {
        this.terminalFunction = terminalFunction;
        this.parameterEstimator = parameterEstimator;
    }

    @Override
    public double reward(State state, Action action, State state1) {
        return 0;
    }

    public void computeRewardForStates(List<State> states, List<ActionType> actionTypes){

        List<TaxiGraphState> terminalStates = new ArrayList<>();
        for (State state : states){
            if (terminalFunction.isTerminal(state)){
                terminalStates.add((TaxiGraphState)state);
                ((TaxiGraphState)state).getPreviousState().setReward(((TaxiGraphState)state).getPreviousAction(), getTerminalStateReward((TaxiGraphState)state));
            }
        }

        List<TaxiGraphState> taxiGraphStates = new ArrayList<>();
        for (State state : states){
            taxiGraphStates.add((TaxiGraphState)state);
        }
        Collections.sort(taxiGraphStates);

        System.out.println("ddd");

    }


    private double getTerminalStateReward(TaxiGraphState state){
        switch (state.getPreviousActionId()){
            case 0:
                return getNextLocationReward(state);
            case 1:
                return getStayingInLocationLocationReward(state);
            case 2:
                return getGoingToChargingStationReward(state);
            case 3:
                return getChargingReward(state);
            case 4:
                return getPickUpReward(state);
            default:
                throw new IllegalStateException("Unexpected value: " + state.getPreviousActionId());
        }
    }


    private double getNextLocationReward(TaxiGraphState state){
        HashMap<Integer, Double> destinationProbabilities = parameterEstimator.getDestinationProbabilitiesInNode(state.getNodeId(), state.getTimeStamp());
        Set<Double> outOfChargeTimeDestinationProbabilities = getOutOfChargeTimeProbabilities(state, destinationProbabilities);

        double sumOutOfChargeTimeProbabilities = outOfChargeTimeDestinationProbabilities.stream().reduce(0., Double::sum);
        double pickUpProbability = parameterEstimator.getPickUpProbabilityInNode(state.getNodeId(), state.getTimeStamp());

        double notPickingPassengerReward = state.getReward() * (1 - pickUpProbability + pickUpProbability * sumOutOfChargeTimeProbabilities) ;
        double pickUpPassengerReward = getPickupPassengerReward(state, destinationProbabilities, pickUpProbability);

        double tripEnergyCost = Utils.COST_FOR_KW * ActionUtils
                .getEnergyConsumption(DistanceGraphUtils.getDistanceBetweenNodes(state.getPreviousNode(), state.getNodeId()));

        return notPickingPassengerReward + pickUpPassengerReward + tripEnergyCost;
    }

    private double getStayingInLocationLocationReward(TaxiGraphState state){
        return 0;
    }

    private double getGoingToChargingStationReward(TaxiGraphState state){
        return 0;
    }

    private double getChargingReward(TaxiGraphState state){
        return 0;
    }

    private double getPickUpReward(TaxiGraphState state){
        return 0;
    }


    private double getPickupPassengerReward(TaxiGraphState state, HashMap<Integer, Double> destinationProbabilities,
                                            double pickUpProbability){
        SuccessfulPickUpParameters successfulPickUpParameters = getSuccessfulPickUpParameters(state, destinationProbabilities);

        ArrayList<Double> probabilities = successfulPickUpParameters.getProbabilities();
        ArrayList<Double> energyConsumptionCost = successfulPickUpParameters.getEnergyConsumptionCost();
        ArrayList<Double> tripReward = successfulPickUpParameters.getTripReward();
        ArrayList<Double> futureStateReward = successfulPickUpParameters.getFutureStateReward();

        double resultReward = 0;

        for (int i = 0; i < probabilities.size(); i++){
            resultReward += probabilities.get(i) * (energyConsumptionCost.get(i) + tripReward.get(i) + futureStateReward.get(i));
        }

        return resultReward * pickUpProbability;
    }


    private Set<Double> getOutOfChargeTimeProbabilities(TaxiGraphState state , HashMap<Integer, Double> destinationProbabilities){
        HashSet<Double> result = new HashSet<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Long>>> tripLengths = parameterEstimator.getTaxiTripLengths();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> tripConsumptions = parameterEstimator.getTaxiTripConsumptions();

        int startInterval = getIntervalStart(state.getTimeStamp());

        if (destinationProbabilities != null){
            for (Map.Entry<Integer, Double> entry : destinationProbabilities.entrySet()){
                if (shiftOver(state.getTimeStamp(), tripLengths.get(startInterval).get(state.getNodeId()).get(entry.getKey()))
                        || runOutOfBattery(state, tripConsumptions.get(startInterval).get(state.getNodeId()).get(entry.getKey()))) {
                    result.add(entry.getValue());
                }
            }
        }


        return result;
    }

    private SuccessfulPickUpParameters getSuccessfulPickUpParameters(TaxiGraphState state , HashMap<Integer, Double> destinationProbabilities){
        ArrayList<Double> probabilities = new ArrayList<>();
        ArrayList<Double> energyConsumptionCost = new ArrayList<>();
        ArrayList<Double> tripReward = new ArrayList<>();
        ArrayList<Double> futureStateReward = new ArrayList<>();

        HashMap<Integer, HashMap<Integer, HashMap<Integer, Long>>> tripLengths = parameterEstimator.getTaxiTripLengths();
        HashMap<Integer,  HashMap<Integer, HashMap<Integer, Integer>>> tripConsumptions = parameterEstimator.getTaxiTripConsumptions();
        HashMap<Integer,  HashMap<Integer, HashMap<Integer, Double>>> tripDistances = parameterEstimator.getTaxiTripDistances();

        int startInterval = getIntervalStart(state.getTimeStamp());

        if (destinationProbabilities != null){
            for (Map.Entry<Integer, Double> entry : destinationProbabilities.entrySet()){
                double consumption = tripConsumptions.get(startInterval).get(state.getNodeId()).get(entry.getKey());
                double tripLength = tripLengths.get(startInterval).get(state.getNodeId()).get(entry.getKey());

                if (!shiftOver(state.getTimeStamp(), tripLength) && !runOutOfBattery(state, consumption)){
                    probabilities.add(entry.getValue());
                    energyConsumptionCost.add(consumption * Utils.COST_FOR_KW);
                    tripReward.add(getTripReward(tripDistances.get(startInterval).get(state.getNodeId()).get(entry.getKey())));
                    futureStateReward.add(getFutureStateReward(state, entry.getKey(),tripLength, consumption));
                }
            }
        }

        return new SuccessfulPickUpParameters(probabilities, energyConsumptionCost, tripReward, futureStateReward);
    }


    private double getTripReward(double distance){
        return distance * Utils.TAXI_COST_FOR_KM + Utils.TAXI_START_JOURNEY_FEE;
    }

    private double getFutureStateReward(TaxiGraphState state, Integer toNodeId, double tripLength, double consumption){
        return 0;
    }


}
