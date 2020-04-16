import domain.TaxiGraphRewardFunction;
import domain.TaxiRecommenderDomainGenerator;
import domain.actions.ActionTypes;
import domain.environmentrepresentation.fullenvironment.FullEnvironment;
import domain.environmentrepresentation.kmeansenvironment.KMeansEnvironment;
import domain.states.TaxiGraphState;
import evaluation.Simulation;
import utils.Utils;

import javax.sound.midi.Soundbank;

public class Main {


    public static void main(String[] args) {

        Simulation simulation = new Simulation(null, null, 100, 425, 60);
        simulation.startSimulation();
        /*TaxiRecommenderDomainGenerator taxiRecommenderDomainGenerator = new TaxiRecommenderDomainGenerator(
                "prague_full.fst","prague_charging_stations_full.json",
                new FullEnvironment());

        try {

            ReachableStatesGenerator planner = new ReachableStatesGenerator(taxiRecommenderDomainGenerator.getTaxiGraphStateModel());
            TaxiGraphState initialState = new TaxiGraphState(taxiRecommenderDomainGenerator.getEnvironment().getEnvironmentNodes().iterator().next().getNodeId(), 40, Utils.SHIFT_START_TIME);
            planner.performReachabilityFrom(initialState);

            TaxiGraphRewardFunction rewardFunction = new TaxiGraphRewardFunction(planner.getReachableStates(), taxiRecommenderDomainGenerator.getParameterEstimator());
            rewardFunction.computeReward();


            TaxiGraphState state = initialState;
            while (state.getMaxRewardState() != -1){
                System.out.println("FROM: " + state);
                System.out.println("TO: " + planner.getReachableStates().get(state.getMaxRewardState()));
                System.out.println("ACTION: " + ActionTypes.getNameOfAction(state.getMaxRewardAction()));
                System.out.println();
                System.out.println();
                state = planner.getReachableStates().get(state.getMaxRewardState());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
