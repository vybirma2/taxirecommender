package evaluation.chargingrecommenderagent;

import domain.TaxiModel;
import domain.TaxiRewardFunction;
import domain.actions.ActionTypes;
import domain.actions.ActionUtils;
import domain.actions.MeasurableAction;
import domain.states.TaxiState;
import evaluation.Agent;
import jdk.jshell.execution.Util;
import parameterestimation.EnergyConsumptionEstimator;
import parameterestimation.ParameterEstimator;
import utils.DistanceGraphUtils;
import utils.DistanceSpeedPairTime;
import utils.Utils;

import java.util.List;

public class ChargingRecommenderAgent extends Agent {

    private ReachableStatesGenerator reachableStatesGenerator;
    private TaxiRewardFunction rewardFunction;
    private ParameterEstimator parameterEstimator;
    private final TaxiState startingState;
    private TaxiState currentState;


    public ChargingRecommenderAgent(TaxiModel taxiModel, ParameterEstimator parameterEstimator, TaxiState startingState) {
        super(taxiModel);
        this.parameterEstimator = parameterEstimator;
        this.startingState = startingState;
        init();
    }

    private void init(){
        reachableStatesGenerator = new ReachableStatesGenerator(this.taxiModel);
        reachableStatesGenerator.performReachabilityFrom(startingState);
        rewardFunction = new TaxiRewardFunction(reachableStatesGenerator.getReachableStates(), parameterEstimator);
        rewardFunction.computeReward();

        TaxiState state = startingState;
        while (state.getMaxRewardStateId() != -1){
            System.out.println("FROM: " + state);
            System.out.println("TO: " + reachableStatesGenerator.getReachableStates().get(state.getMaxRewardStateId()));
            System.out.println("ACTION: " + ActionTypes.getNameOfAction(state.getMaxRewardActionId()));
            System.out.println();
            state = reachableStatesGenerator.getReachableStates().get(state.getMaxRewardStateId());
        }

        currentState = startingState;
    }

    @Override
    public MeasurableAction chooseAction(TaxiState currSt, List<MeasurableAction> actions) {
        assert currentState.equals(currSt);

        for (MeasurableAction action : actions){
            if (action.getActionId() == this.currentState.getMaxRewardActionId()){
                if (getResultState(action).equals(reachableStatesGenerator.getReachableStates().get(this.currentState.getMaxRewardStateId()))){
                    this.currentState = reachableStatesGenerator.getReachableStates().get(this.currentState.getMaxRewardStateId());
                    return action;
                }
            }
        }

        return actions.get(0);
    }


    @Override
    public boolean tripOffer(TaxiState currSt, int tripToNode) {
        assert currentState.equals(currSt);

        TaxiState resultState = getResultTripState(tripToNode);

        if (reachableStatesGenerator.getReachableStatesMap().containsKey(resultState)){
            if (beneficialTrip(resultState)) {
                currentState = reachableStatesGenerator.getReachableStatesMap().get(resultState);
                return true;
            }
        } else {
            System.out.println("Trip destination state not found!");
        }

        return false;
    }


    private boolean beneficialTrip(TaxiState resultState){
        int distance = parameterEstimator.getTaxiTripDistances()
                .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                .get(currentState.getNodeId()).get(resultState.getNodeId()).intValue();
        double resultStateReward = reachableStatesGenerator.getReachableStatesMap().get(resultState).getReward();
        double tripReward = TaxiRewardFunction.getTripReward(distance);

        return resultStateReward + tripReward > this.currentState.getReward();
    }


    private TaxiState getResultTripState(int tripToNode){
        int consumption = parameterEstimator.getTaxiTripConsumptions()
                .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                .get(currentState.getNodeId()).get(tripToNode).intValue();
        int time = parameterEstimator.getTaxiTripLengths()
                .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                .get(currentState.getNodeId()).get(tripToNode).intValue();


        return new TaxiState(tripToNode, currentState.getStateOfCharge() + consumption,
                currentState.getTimeStamp() + time);
    }


    private TaxiState getResultState(MeasurableAction action){
        return new TaxiState(action.getToNodeId(),
                currentState.getStateOfCharge() + action.getConsumption(),
                currentState.getTimeStamp() + action.getLength());
    }
}
