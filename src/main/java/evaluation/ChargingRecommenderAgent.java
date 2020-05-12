package evaluation;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.TaxiRecommenderDomain;
import domain.actions.*;
import domain.charging.ChargingConnection;
import domain.charging.ChargingStation;
import domain.charging.ChargingStationReader;
import domain.environmentrepresentation.EnvironmentNode;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;
import domain.parameterestimation.TaxiTrip;
import domain.utils.Utils;
import problemsolving.ChragingRecommender;
import domain.TaxiRewardFunction;
import domain.states.TaxiState;
import org.nustaq.serialization.FSTObjectInput;
import domain.parameterestimation.EnergyConsumptionEstimator;
import domain.utils.DistanceGraphUtils;
import domain.utils.DistanceSpeedPairTime;
import visualization.MapVisualizer;


import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static domain.utils.DistanceGraphUtils.getEuclideanDistanceBetweenOsmNodes;

public class ChargingRecommenderAgent extends Agent {

    private TaxiRecommenderDomain domain;
    private ChragingRecommender chargingRecommender;


    public ChargingRecommenderAgent(Graph<RoadNode, RoadEdge> osmGraph) {
        super(osmGraph);
        init();
    }


    @Override
    public MeasurableAction getAction(SimulationState currentState) {

        TaxiState currentTaxiState = getTaxiState(currentState);
        int maxRewardActionId = currentTaxiState.getMaxRewardActionId();
        int maxRewardStateId = currentTaxiState.getMaxRewardStateId();
        TaxiState nextState = chargingRecommender.reachableStates.get(maxRewardStateId);

        return getActionDone(currentState, nextState, maxRewardActionId);
    }


    @Override
    public boolean tripOffer(SimulationState currentState, SimulationTaxiTrip trip) {

        TaxiState currentTaxiState = getTaxiState(currentState);
        TaxiState resultState = getResultTripState(currentState, trip);
        TaxiState existingState = chargingRecommender.getState(resultState);

        if (existingState != null){
            return beneficialTrip(currentTaxiState, existingState, trip);
        }

        return false;
    }

    @Override
    public void resetAgent() {

    }


    private TaxiState getTaxiState(SimulationState currentState){
        if (ChargingStationReader.getChargingStation(currentState.getNodeId()) == null){
            EnvironmentNode environmentNode = getEnvironmentNode(currentState.getNodeId());
            return chargingRecommender.getState(new TaxiState(environmentNode.getNodeId(), currentState.getStateOfCharge(), currentState.getTimeStamp()));
        } else {
            return chargingRecommender.getState(new TaxiState(currentState.getNodeId(), currentState.getStateOfCharge(), currentState.getTimeStamp()));
        }
    }

    private EnvironmentNode getEnvironmentNode(int nodeId){

        return domain.getEnvironment()
                .getEnvironmentNodes()
                .stream()
                .min(Comparator.comparingDouble(node -> getEuclideanDistanceBetweenOsmNodes(nodeId, node.getNodeId())))
                .get();
    }



    private MeasurableAction getActionDone(SimulationState currentState, TaxiState nextState, int actionId){
        switch (actionId){
            case 0:
                return new NextLocationAction(actionId, currentState.getNodeId(), nextState.getNodeId());
            case 1:
                return new StayingInLocationAction(actionId, currentState.getNodeId(), currentState.getNodeId(), Utils.STAYING_INTERVAL);
            case 2:
                return new GoingToChargingStationAction(actionId, currentState.getNodeId(), nextState.getNodeId());
            case 3:
                return new ChargingAction(actionId, currentState.getNodeId(), currentState.getNodeId(),
                        nextState.getTimeStamp() - currentState.getTimeStamp(),
                        chooseConnection(ChargingStationReader.getChargingStation(nextState.getNodeId()),
                                nextState.getTimeStamp() - currentState.getTimeStamp(),
                                nextState.getStateOfCharge() - currentState.getStateOfCharge()));
            default:
                throw new IllegalArgumentException("Not doable action!");
        }
    }


    private void init() {
        domain = new TaxiRecommenderDomain(Utils.ENVIRONMENT);




/*

        try {
            //Graph<RoadNode, RoadEdge> graph = new Graph<>();
            new Thread() {
                @Override
                public void run() {
                    MapVisualizer.main(null);
                }
            }.start();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            List<TaxiTripPickupPlace> pickupPlaces = domain.getTaxiTrips()
                    .stream()
                    .map(t -> new TaxiTripPickupPlace(t.getPickUpLongitude(), t.getPickUpLatitude()))
                    .collect(Collectors.toList());

            MapVisualizer.addPickUpPointsToMap(pickupPlaces);
            MapVisualizer.addEnvironmentNodesToMap(domain.getEnvironment().getEnvironmentNodes());

            try {
                Thread.sleep(2000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


*/






        generateReachableStates();
    }


   /* private void printCurrentPolicy(){
        TaxiState state = currentState;
        while (state.getMaxRewardStateId() != -1){
            System.out.println("FROM: " + state);
            System.out.println("TO: " + chargingRecommender.getReachableStates().get(state.getMaxRewardStateId()));
            System.out.println("ACTION: " + ActionTypes.getNameOfAction(state.getMaxRewardActionId()));
            System.out.println();
            state = chargingRecommender.getReachableStates().get(state.getMaxRewardStateId());
        }
    }*/


    private void generateReachableStates() {
        chargingRecommender = new ChragingRecommender(domain.getActionTypes(),
                new ArrayList<>(domain.getEnvironment().getNodes()), domain.getParameterEstimator());
        TaxiActionType.setChragingRecommender(chargingRecommender);
        chargingRecommender.performStateSpaceAnalysis();
    }


    private int chooseConnection(ChargingStation station, int timeOfCharging, int energyCharged){
        if (station.getAvailableConnections().size() == 1){
            return station.getAvailableConnections().get(0).getId();
        } else {
            for (ChargingConnection connection : station.getAvailableConnections()){
                if ((int)(((connection.getPowerKW()*(timeOfCharging/60.))/Utils.BATTERY_CAPACITY)*100.) == energyCharged){
                    return connection.getId();
                }
            }
        }
        return 0;
    }




    private boolean beneficialTrip(TaxiState currentState, TaxiState resultState, SimulationTaxiTrip simulationTaxiTrip){

        double distance = simulationTaxiTrip.getDistance();

        double resultStateReward = resultState.getReward();
        double tripReward = TaxiRewardFunction.getTripReward(distance);

        return resultStateReward + tripReward > 0.1*currentState.getReward();
    }


    private TaxiState getResultTripState(SimulationState currentState, SimulationTaxiTrip trip) {
        int tripConsumption = trip.getTripEnergyConsumption();
        int tripTime = new Long(trip.getTripLength()).intValue();
        DistanceSpeedPairTime toPickupPath = DistanceGraphUtils.getDistanceSpeedPairOfPath(DistanceGraphUtils.aStar(currentState.getNodeId(), trip.getFromNode()));


        return new TaxiState(getEnvironmentNode(trip.getToNode()).getNodeId(), currentState.getStateOfCharge() + tripConsumption +
                EnergyConsumptionEstimator.getEnergyConsumption(toPickupPath.getDistance()),
                currentState.getTimeStamp() + tripTime + toPickupPath.getTime());
    }
}
