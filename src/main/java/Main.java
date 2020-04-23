import cz.agents.multimodalstructures.nodes.RoadNode;
import evaluation.Simulation;
import parameterestimation.NewYorkLongitudeLatitudeReader;

import java.io.IOException;
import java.util.HashMap;

public class Main {


    public static void main(String[] args) throws IOException, ClassNotFoundException {

         Simulation simulation = new Simulation();
        //simulation.startSimulation();
        /*TaxiRecommenderDomain taxiRecommenderDomainGenerator = new TaxiRecommenderDomain(
                "prague_full.fst","prague_charging_stations_full.json",
                new KMeansEnvironment());

        try {

            evaluation.chargingrecommenderagent.ReachableStatesGenerator planner = new evaluation.chargingrecommenderagent.ReachableStatesGenerator(taxiRecommenderDomainGenerator.getTaxiModel());
            TaxiState initialState = new TaxiState(taxiRecommenderDomainGenerator.getEnvironment().getEnvironmentNodes().iterator().next().getNodeId(), Utils.STARTING_STATE_OF_CHARGE, Utils.SHIFT_START_TIME);
            planner.performReachabilityFrom(initialState);

            TaxiRewardFunction rewardFunction = new TaxiRewardFunction(planner.getReachableStates(), taxiRecommenderDomainGenerator.getParameterEstimator());
            rewardFunction.computeReward();


            TaxiState state = initialState;
            while (state.getMaxRewardStateId() != -1){
                System.out.println("FROM: " + state);
                System.out.println("TO: " + planner.getReachableStates().get(state.getMaxRewardStateId()));
                System.out.println("ACTION: " + ActionTypes.getNameOfAction(state.getMaxRewardActionId()));
                System.out.println();
                System.out.println();
                state = planner.getReachableStates().get(state.getMaxRewardStateId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
