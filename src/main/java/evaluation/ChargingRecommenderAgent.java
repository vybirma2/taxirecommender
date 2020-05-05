package evaluation;

import domain.TaxiRecommenderDomain;
import problemsolving.ChragingRecommender;
import problemsolving.TaxiRewardFunction;
import domain.actions.ActionTypes;
import domain.actions.MeasurableAction;
import domain.actions.TaxiActionType;
import domain.states.TaxiState;
import evaluation.Agent;
import evaluation.SimulationTaxiTrip;
import org.nustaq.serialization.FSTObjectInput;
import domain.parameterestimation.EnergyConsumptionEstimator;
import domain.utils.DistanceGraphUtils;
import domain.utils.DistanceSpeedPairTime;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChargingRecommenderAgent extends Agent {

    private ChragingRecommender chragingRecommender;


    public ChargingRecommenderAgent(TaxiRecommenderDomain domain, TaxiState startingState) throws IOException, ClassNotFoundException {
        super(domain, startingState);
        init();
    }

    private void init() throws IOException, ClassNotFoundException {
        generateReachableStates();
    }

    private void printCurrentPolicy(){
        TaxiState state = currentState;
        while (state.getMaxRewardStateId() != -1){
            System.out.println("FROM: " + state);
            System.out.println("TO: " + chragingRecommender.getReachableStates().get(state.getMaxRewardStateId()));
            System.out.println("ACTION: " + ActionTypes.getNameOfAction(state.getMaxRewardActionId()));
            System.out.println();
            state = chragingRecommender.getReachableStates().get(state.getMaxRewardStateId());
        }
    }


    private void generateReachableStates() throws IOException, ClassNotFoundException {
        chragingRecommender = new ChragingRecommender(domain.getActionTypes(),
                new ArrayList<>(domain.getEnvironment().getNodes()), currentState, domain.getParameterEstimator());
        TaxiActionType.setChragingRecommender(chragingRecommender);
        chragingRecommender.performStateSpaceAnalysis();
    }

    private void readSerializedFile(File file) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        chragingRecommender = (ChragingRecommender) in.readObject();
        in.close();
    }



    @Override
    public MeasurableAction chooseAction(TaxiState currSt, List<MeasurableAction> actions) {
        assert currentState.equals(currSt);

        for (MeasurableAction action : actions){
            if (action.getActionId() == this.currentState.getMaxRewardActionId()){
                if (getResultState(action).equals(chragingRecommender.getReachableStates().get(this.currentState.getMaxRewardStateId()))){
                    this.currentState = chragingRecommender.getReachableStates().get(this.currentState.getMaxRewardStateId());
                    return action;
                }
            }
        }

        return actions.get(0);
    }


    @Override
    public boolean tripOffer(TaxiState currSt, SimulationTaxiTrip trip) {
        assert currentState.equals(currSt);

        TaxiState resultState = getResultTripState(trip);

        TaxiState existingState = chragingRecommender.getState(resultState);
        if (existingState != null){
            if (beneficialTrip(existingState, trip)) {
                currentState = existingState;
                return true;
            }
        }

        return false;
    }


    private boolean beneficialTrip(TaxiState resultState, SimulationTaxiTrip simulationTaxiTrip){

        double distance = simulationTaxiTrip.getDistance();

        double resultStateReward = resultState.getReward();
        double tripReward = TaxiRewardFunction.getTripReward(distance);

        return resultStateReward + tripReward > this.currentState.getReward();
    }


    private TaxiState getResultTripState(SimulationTaxiTrip trip) {
        int tripConsumption = trip.getTripEnergyConsumption();
        int tripTime = new Long(trip.getTripLength()).intValue();
        DistanceSpeedPairTime toPickupPath = DistanceGraphUtils.getDistanceSpeedPairOfPath(DistanceGraphUtils.aStar(currentState.getNodeId(), trip.getFromEnvironmentNode()));


        return new TaxiState(trip.getToEnvironmentNode(), currentState.getStateOfCharge() + tripConsumption +
                EnergyConsumptionEstimator.getEnergyConsumption(toPickupPath.getDistance()),
                currentState.getTimeStamp() + tripTime + toPickupPath.getTime());
    }


    private TaxiState getResultState(MeasurableAction action){
        return new TaxiState(action.getToNodeId(),
                currentState.getStateOfCharge() + action.getConsumption(),
                currentState.getTimeStamp() + action.getLength());
    }
}
