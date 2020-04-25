package evaluation;


import charging.ChargingStationReader;
import domain.TaxiRecommenderDomain;
import domain.TaxiRewardFunction;
import domain.actions.MeasurableAction;
import domain.environmentrepresentation.gridenvironment.GridEnvironment;
import domain.environmentrepresentation.kmeansenvironment.KMeansEnvironment;
import domain.states.TaxiState;
import evaluation.chargingrecommenderagent.ChargingRecommenderAgent;
import jdk.jshell.execution.Util;
import utils.DistanceGraphUtils;
import utils.Utils;
import visualization.MapVisualizer;

import java.io.IOException;
import java.util.*;

import static utils.Utils.INPUT_STATION_FILE_NAME;

public class Simulation {

    private Agent agent;

    TaxiRecommenderDomain domainGenerator;

    private int startingTimeStamp;
    private int shiftLength;
    private TaxiState currentState;

    private double resultReward = 0;

    public Simulation() throws IOException, ClassNotFoundException {
        initSimulation();
    }


    private void initSimulation() throws IOException, ClassNotFoundException {
        domainGenerator = new TaxiRecommenderDomain();

        int startingStateOfCharge = Utils.STARTING_STATE_OF_CHARGE;
        this.startingTimeStamp = Utils.SHIFT_START_TIME;
        this.shiftLength = Utils.SHIFT_LENGTH;
        currentState = new TaxiState(domainGenerator.getEnvironment().getEnvironmentNodes().iterator().next().getNodeId(),
                startingStateOfCharge, startingTimeStamp);
        this.agent = new ChargingRecommenderAgent(domainGenerator, currentState);
    }

    public void startSimulation() {
        visualizeEnvironment();
        printSimulationStep();

        while (currentState.getTimeStamp() <= startingTimeStamp + shiftLength){
            MapVisualizer.setCurrentStateNode(domainGenerator.getEnvironment().getOsmGraph().getNode(currentState.getNodeId()));

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (tripDone()){
                continue;
            }

            List<MeasurableAction> applicableActions = domainGenerator.getTaxiModel().allApplicableActionsFromState(currentState);
            if (applicableActions.isEmpty()){
                return;
            }
            MeasurableAction actionDone = agent.chooseAction(currentState, applicableActions);
            applyAction(actionDone);

            printSimulationStep(actionDone);
        }
    }

    private void visualizeEnvironment(){

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

        MapVisualizer.initSimulation(domainGenerator.getEnvironment().getEnvironmentNodes(), ChargingStationReader.getChargingStations());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private void applyAction(MeasurableAction action){
        currentState.setNodeId(action.getToNodeId());
        currentState.setStateOfCharge(currentState.getStateOfCharge() + action.getConsumption());
        currentState.setTimeStamp(currentState.getTimeStamp() + action.getLength());
        this.resultReward += action.getReward();
    }


    private boolean tripDone(){
        Integer trip = tripToDestination();

        if (trip != null) {
            if (agent.tripOffer(currentState, trip)){
                int distance = domainGenerator.getParameterEstimator().getTaxiTripDistances()
                        .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                        .get(currentState.getNodeId()).get(trip).intValue();
                int consumption = domainGenerator.getParameterEstimator().getTaxiTripConsumptions()
                        .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                        .get(currentState.getNodeId()).get(trip).intValue();
                int time = domainGenerator.getParameterEstimator().getTaxiTripLengths()
                        .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                        .get(currentState.getNodeId()).get(trip).intValue();
                double reward = TaxiRewardFunction.getTripReward(distance);

                this.resultReward += reward;

                currentState.setNodeId(trip);

                currentState.setTimeStamp(currentState.getTimeStamp() + time);

                currentState.setStateOfCharge(currentState.getStateOfCharge() + consumption);

                printSimulationStep(trip, distance, time, consumption, reward);
                return true;
            }
        }
        return false;
    }



    private Integer tripToDestination(){
        double pickUpProbability =
                domainGenerator.getParameterEstimator().getPickUpProbabilityInNode(currentState.getNodeId(), currentState.getTimeStamp());
        double randomNumber = Math.random();

        if (pickUpProbability > randomNumber && pickUpProbability > 0){
            return chooseToNode();
        } else {
            return null;
        }
    }


    private int chooseToNode(){
        HashMap<Integer, Double> destinationProbabilities =
                domainGenerator.getParameterEstimator().getDestinationProbabilitiesInNode(currentState.getNodeId(), currentState.getTimeStamp());
        Random random = new Random();
        ArrayList<Integer> nodes = new ArrayList<>();

        for (Map.Entry<Integer, Double> entry : destinationProbabilities.entrySet()){
            for (int i = 0; i < (int)(100 * entry.getValue()); i++){
                nodes.add(entry.getKey());
            }
        }

        Collections.shuffle(nodes);

        return nodes.get(random.nextInt(nodes.size()));
    }

    private void printSimulationStep(MeasurableAction action){
        System.out.println("Action done: " + action);
        System.out.println("Current state: " + this.currentState);
        System.out.println("Current achieved reward: " + this.resultReward);
        System.out.println();
    }

    private void printSimulationStep(int toNodeId, double distance, int time, int consumption, double reward){
        System.out.println("Trip done: toNodeId: " + toNodeId + ", distance: " + distance + ", time: "
                + time + ", consumption: " + consumption + ", reward: " + reward);
        System.out.println("Current state: " + this.currentState);
        System.out.println("Current achieved reward: " + this.resultReward);
        System.out.println();
    }

    private void printSimulationStep(){
        System.out.println("Current state: " + this.currentState);
        System.out.println("Current achieved reward: " + this.resultReward);
        System.out.println();
    }

}
