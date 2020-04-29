import charging.ChargingStationReader;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import de.alsclo.voronoi.Voronoi;
import de.alsclo.voronoi.graph.Edge;
import de.alsclo.voronoi.graph.Graph;
import de.alsclo.voronoi.graph.Point;
import domain.TaxiRecommenderDomain;
import domain.environmentrepresentation.EnvironmentEdge;
import domain.environmentrepresentation.EnvironmentNode;
import domain.environmentrepresentation.gridenvironment.GridEnvironment;
import domain.environmentrepresentation.kmeansenvironment.KMeansEnvironment;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPointCentroid;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;
import domain.environmentrepresentation.kmeansenvironment.kmeansenvironmentutils.ConvexHullFinder;
import evaluation.Simulation;
import parameterestimation.NewYorkLongitudeLatitudeReader;
import utils.DistanceGraphUtils;
import utils.GraphLoader;
import visualization.MapVisualizer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static utils.Utils.SHIFT_LENGTH;

public class Main {


    public static void main(String[] args) throws IOException, ClassNotFoundException {


        try {
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


           /*for (Map.Entry<PickUpPointCentroid, List<TaxiTripPickupPlace>>  entry: KMeansEnvironment.clusters.entrySet()){
                List<TaxiTripPickupPlace> hull =
                        ConvexHullFinder.getConvexHull(KMeansEnvironment.clusters.get(entry.getKey()));
                MapVisualizer.addHullPointsToMap(hull, entry.getKey());
                MapVisualizer.addPickUpPointsToMap(KMeansEnvironment.clusters.get(entry.getKey()));
            }

            List<TaxiTripPickupPlace> pickupPlaces = TaxiRecommenderDomain.getTaxiTrips()
                    .stream()
                    .map(t -> new TaxiTripPickupPlace(t.getPickUpLongitude(), t.getPickUpLatitude()))
                    .collect(Collectors.toList());

            MapVisualizer.addPickUpPointsToMap(pickupPlaces);*/

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

         /*Simulation simulation = new Simulation();

         double reward = 0;
         for (int i = 0; i < 1000; i++){
             simulation.startSimulation();
             reward += simulation.getResultReward();
             System.out.println();
             System.out.println();
             System.out.println();
             System.out.println();
             simulation.clearSimulationResults();
         }

        System.out.println(reward/1000);*/
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
