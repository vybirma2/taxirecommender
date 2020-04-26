package evaluation;


import charging.ChargingStationReader;
import domain.TaxiRecommenderDomain;
import domain.TaxiRewardFunction;
import domain.actions.MeasurableAction;
import domain.states.TaxiState;
import evaluation.chargingrecommenderagent.ChargingRecommenderAgent;
import parameterestimation.EnergyConsumptionEstimator;
import parameterestimation.ParameterEstimationUtils;
import parameterestimation.TaxiTrip;
import utils.DistanceGraphUtils;
import utils.DistanceSpeedPairTime;
import utils.Utils;
import visualization.MapVisualizer;

import java.io.IOException;
import java.util.*;

import static utils.Utils.*;


public class Simulation {

    private Agent agent;

    TaxiRecommenderDomain domainGenerator;


    private TaxiState currentState;
    private List<Integer> tripDestinations = new ArrayList<>();
    private List<Integer> tripPickups = new ArrayList<>();
    private HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTrips;


    private double resultReward = 0;


    public Simulation() throws IOException, ClassNotFoundException {
        initSimulation();
    }


    private void initSimulation() throws IOException, ClassNotFoundException {
        domainGenerator = new TaxiRecommenderDomain();
        currentState = new TaxiState(domainGenerator.getEnvironment().getEnvironmentNodes().iterator().next().getNodeId(),
                STARTING_STATE_OF_CHARGE, SHIFT_START_TIME);

        initTripChoosing();

        this.agent = new BaseMethodAgent(domainGenerator.getParameterEstimator(), currentState);
    }

    private void initTripChoosing(){
        timeSortedTrips = ParameterEstimationUtils.getTimeSortedTrips(TaxiRecommenderDomain.getTaxiTrips());
    }

    public void clearSimulationResults(){
        resultReward = 0;
        currentState = new TaxiState(domainGenerator.getEnvironment().getEnvironmentNodes().iterator().next().getNodeId(),
                STARTING_STATE_OF_CHARGE, SHIFT_START_TIME);
        this.agent.setCurrentState(currentState);
    }

    public void startSimulation() {
       // visualizeEnvironment();
        printSimulationStep();

        while (currentState.getTimeStamp() <= SHIFT_START_TIME + SHIFT_LENGTH){
           // MapVisualizer.setCurrentStateNode(domainGenerator.getEnvironment().getOsmGraph().getNode(currentState.getNodeId()));

            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            if (tripDone()){
                continue;
            }

            List<MeasurableAction> applicableActions = domainGenerator.getTaxiModel().allApplicableActionsFromState(currentState);

            /*TaxiState state = new TaxiState(102701, 14, 712);
            domainGenerator.getTaxiModel().allApplicableActionsFromState(state);
*/

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
        SimulationTaxiTrip trip = tripToDestination();

        if (trip != null) {
            if (agent.tripOffer(currentState, trip)) {
                int tripDistance = new Double(trip.getDistance()).intValue();
                int tripConsumption = trip.getTripEnergyConsumption();
                int tripTime = new Double(trip.getTripLength()).intValue();
                double tripReward = TaxiRewardFunction.getTripReward(tripDistance);
                DistanceSpeedPairTime toPickUpPath = DistanceGraphUtils.getDistanceSpeedPairOfPath(DistanceGraphUtils.aStar(currentState.getNodeId(), trip.getFromEnvironmentNode()));

                this.resultReward += tripReward;

                currentState.setNodeId(trip.getToEnvironmentNode());

                currentState.setTimeStamp(currentState.getTimeStamp() + tripTime + toPickUpPath.getTime());

                currentState.setStateOfCharge(currentState.getStateOfCharge() + tripConsumption + EnergyConsumptionEstimator.getEnergyConsumption(toPickUpPath.getDistance()));

                printSimulationStep(trip.getToEnvironmentNode(), tripDistance, tripTime, tripConsumption, tripReward);
                return true;
            }
        }
        return false;
    }


    private SimulationTaxiTrip tripToDestination(){
        double randomNumber = Math.random();

        if (randomNumber > Utils.TRIP_OFFER_PROBABILITY){
            return chooseTrip();
        } else {
            return null;
        }
    }


    private SimulationTaxiTrip chooseTrip(){
        int intervalStart = DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp());
        List<TaxiTrip> trips = timeSortedTrips.get(intervalStart);
        Collections.shuffle(trips);
        TaxiTrip result = trips.get(0);

        return new SimulationTaxiTrip(result.getDistance(), result.getTripLength(),result.getTripEnergyConsumption(),
                result.getFromEnvironmentNode(), result.getToEnvironmentNode());
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

    public double getResultReward() {
        return resultReward;
    }
}
