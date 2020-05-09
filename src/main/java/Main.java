
import domain.parameterestimation.NewYorkDataSetReader;
import domain.parameterestimation.PragueDataSetReader;
import domain.utils.Utils;
import evaluation.Experiment;
import evaluation.Simulation;

import java.io.IOException;


public class Main {


    public static void main(String[] args) throws IOException {

        Simulation simulation = new Simulation();

        /*Utils.setUtilsParameters(8*60, 12*60, 20, new PragueDataSetReader(),
                "prague.fst", "prague_charging_stations_full.json",
                "prague", "gridworld", 2000, 2000);
        simulation.initSimulation();


        for (int startingStateOfCharge = 10; startingStateOfCharge <= 100; startingStateOfCharge+=10){
            Experiment experiment = new Experiment(simulation, 8*60, 12*60, startingStateOfCharge, new PragueDataSetReader(),
                    "prague.fst", "prague_charging_stations_full.json",
                    "prague", "gridworld", 2000, 2000);
            experiment.doExperiment();
            experiment = null;
        }


        Utils.setUtilsParameters(8*60, 12*60, 20, new PragueDataSetReader(),
                "prague.fst", "prague_charging_stations_full.json",
                "prague", "kmeans", 100);
        simulation.initSimulation();
        for (int startingStateOfCharge = 10; startingStateOfCharge <= 100; startingStateOfCharge+=10){
            Experiment experiment = new Experiment(simulation, 8*60, 12*60, startingStateOfCharge, new PragueDataSetReader(),
                    "prague.fst", "prague_charging_stations_full.json",
                    "prague", "kmeans", 100);
            experiment.doExperiment();
            experiment = null;
        }*/

        Utils.setUtilsParameters(8*60, 2*60, 20, new NewYorkDataSetReader(),
                "new_york_full.fst", "new_york_chargingstations.json",
                "new_york", "gridworld", 2000, 2000);
        simulation.initSimulation();


        for (int startingStateOfCharge = 10; startingStateOfCharge <= 20; startingStateOfCharge+=10){
            Experiment experiment = new Experiment(simulation, 8*60, 2*60, startingStateOfCharge, new NewYorkDataSetReader(),
                    "new_york_full.fst", "new_york_chargingstations.json",
                    "new_york", "gridworld", 2000, 2000);
            experiment.doExperiment();
            experiment = null;
        }







        /*Simulation simulation = new Simulation();
        double reward = 0;
        for (int i = 0; i < 100; i++){
            System.out.println(i);
            simulation.startSimulation();
            reward += simulation.getResultReward();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            simulation.clearSimulationResults();
        }

        System.out.println(reward/100);*/


        /*try {
            TaxiRecommenderDomain domain = new TaxiRecommenderDomain();

            //Graph<RoadNode, RoadEdge> graph = new Graph<>();
            new Thread() {
                @Override
                public void run() {
                    MapVisualizer.main(null);
                }
            }.start();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            for (EnvironmentEdge edge : domain.getEnvironment().getEnvironmentGraph().getEdges()){

                MapVisualizer.addBetweenStatesLine(domain.getEnvironment().getOsmGraph().getNode(edge.getFromId()), domain.getEnvironment().getOsmGraph().getNode(edge.getToId()));

            }

            for (EnvironmentNode node : domain.getEnvironment().getEnvironmentGraph().getNodes()){
                if (domain.getEnvironment().getOsmGraph().getNode(node.getNodeId()) != null){
                    MapVisualizer.addRoadNodeToMap(domain.getEnvironment().getOsmGraph().getNode(node.getNodeId()));
                }
            }


           for (Map.Entry<PickUpPointCentroid, List<TaxiTripPickupPlace>>  entry: KMeansEnvironment.clusters.entrySet()){
                List<TaxiTripPickupPlace> hull =
                        ConvexHullFinder.getConvexHull(KMeansEnvironment.clusters.get(entry.getKey()));
                MapVisualizer.addHullPointsToMap(hull, entry.getKey());
                MapVisualizer.addPickUpPointsToMap(KMeansEnvironment.clusters.get(entry.getKey()));
            }

            List<TaxiTripPickupPlace> pickupPlaces = TaxiRecommenderDomain.getTaxiTrips()
                    .stream()
                    .map(t -> new TaxiTripPickupPlace(t.getPickUpLongitude(), t.getPickUpLatitude()))
                    .collect(Collectors.toList());

            MapVisualizer.addPickUpPointsToMap(pickupPlaces);

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
*/


         /*double reward = 0;
         */

/*
        System.out.println(reward/1000);
*/
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
