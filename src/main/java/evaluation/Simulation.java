package evaluation;


import domain.TaxiRecommenderDomain;
import domain.TaxiRewardFunction;
import domain.actions.MeasurableAction;
import domain.environmentrepresentation.kmeansenvironment.KMeansEnvironment;
import domain.states.TaxiState;
import evaluation.chargingrecommenderagent.ChargingRecommenderAgent;
import utils.DistanceGraphUtils;
import utils.Utils;

import java.util.*;

public class Simulation {

    private final Agent agent;

    TaxiRecommenderDomain domainGenerator;

    private final int startingStateOfCharge;
    private final int startingTimeStamp;
    private final int shiftLength;
    private TaxiState currentState;

    private double resultReward = 0;

    public Simulation() {

        domainGenerator = new TaxiRecommenderDomain("prague_full.fst",
                    "prague_charging_stations_full.json", new KMeansEnvironment());

        this.startingStateOfCharge = Utils.STARTING_STATE_OF_CHARGE;
        this.startingTimeStamp = Utils.SHIFT_START_TIME;
        this.shiftLength = Utils.SHIFT_LENGTH;
        currentState = new TaxiState(domainGenerator.getEnvironment().getOsmGraph().getNode(13384).getId(),
                startingStateOfCharge, startingTimeStamp);
        this.agent = new ChargingRecommenderAgent(domainGenerator.getTaxiModel(), domainGenerator.getParameterEstimator(), currentState);
    }


    public void startSimulation(){

        while (currentState.getTimeStamp() <= startingTimeStamp + shiftLength){
            printCurrentSimulationState();
            if (tripDone()){
                continue;
            }
            List<MeasurableAction> applicableActions = domainGenerator.getTaxiModel().allApplicableActionsFromState(currentState);
            if (applicableActions.isEmpty()){
                return;
            }
            applyAction(agent.chooseAction(currentState, applicableActions));
        }
    }


    private void applyAction(MeasurableAction action){
        currentState.setNodeId(action.getToNodeId());
        currentState.setStateOfCharge(currentState.getStateOfCharge() + action.getConsumption());
        currentState.setTimeStamp(currentState.getTimeStamp() + action.getLength());
        this.resultReward += action.getReward();
        System.out.println("Action done: " + action);
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


                this.resultReward += TaxiRewardFunction.getTripReward(distance);

                currentState.setNodeId(trip);

                currentState.setTimeStamp(currentState.getTimeStamp() + time);

                currentState.setStateOfCharge(currentState.getStateOfCharge() + consumption);

                System.out.println("Trip to destination done: " + trip);
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


    private void printCurrentSimulationState(){
        System.out.println("Current state: " + this.currentState);
        System.out.println("Current achieved reward: " + this.resultReward);
    }

}
