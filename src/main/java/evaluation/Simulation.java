package evaluation;

import charging.ChargingConnection;
import charging.ChargingStation;
import charging.ChargingStationReader;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.TaxiRecommenderDomainGenerator;
import domain.environmentrepresentation.fullenvironment.FullEnvironment;
import domain.environmentrepresentation.kmeansenvironment.KMeansEnvironment;
import parameterestimation.EnergyConsumptionEstimator;
import utils.DistanceGraphUtils;
import utils.Utils;

import java.util.*;

public class Simulation {

    private Agent agent;

    TaxiRecommenderDomainGenerator domainGenerator;
    private RoadNode startingPoint;


    private int startingStateOfCharge;
    private int startingTimeStamp;
    private int shiftLength;



    public Simulation(Agent agent, RoadNode startingPoint, int startingStateOfCharge, int startingTimeStamp, int shiftLength) {


        domainGenerator = new TaxiRecommenderDomainGenerator("prague_full.fst",
                    "prague_charging_stations_full.json", new KMeansEnvironment());


        this.agent = agent;
        this.startingPoint = startingPoint;
        this.startingStateOfCharge = startingStateOfCharge;
        this.startingTimeStamp = startingTimeStamp;
        this.shiftLength = shiftLength;
    }


    public void startSimulation(){

        SimulationState simulationState = new SimulationState(domainGenerator.getEnvironment().getOsmGraph().getNode(38958).getId(),
                startingTimeStamp, startingStateOfCharge);


        while (simulationState.getTimeStamp() <= startingTimeStamp + shiftLength){

            if (tripDone(simulationState)){
                continue;
            } else if (stayingDone(simulationState)){
                continue;
            } else if (chargingDone(simulationState)){
                continue;
            } else {

            }






        }

    }


    private void doNextStep(SimulationState simulationState){
        int nextNode = agent.getNodeToGoTo(simulationState);
        LinkedList<Integer> path = DistanceGraphUtils.aStar(simulationState.getNodeId(), nextNode);

        while (path != null && !path.isEmpty()) {
            nextNode = path.pollFirst();
            simulationState.increaseTimeStamp(DistanceGraphUtils.getTripTime(simulationState.getNodeId(), nextNode));
            simulationState.increaseStateOfCharge(EnergyConsumptionEstimator.getActionEnergyConsumption(simulationState.getNodeId(), nextNode));
            simulationState.setNodeId(domainGenerator.getEnvironment().getOsmGraph().getNode(nextNode).getId());


        }
    }

    private boolean tripDone(SimulationState simulationState){
        int interval = DistanceGraphUtils.getIntervalStart(simulationState.getTimeStamp());
        Integer trip = tripToDestination(simulationState);

        if (trip != null){
            if (agent.tripOffer(simulationState, trip)){
                simulationState.setNodeId(domainGenerator.getEnvironment().getOsmGraph().getNode(trip).getId());
                simulationState.increaseTimeStamp(domainGenerator.getParameterEstimator().getTaxiTripLengths().get(interval).get(simulationState.getNodeId()).get(trip).intValue());
                simulationState.increaseStateOfCharge(domainGenerator.getParameterEstimator().getTaxiTripConsumptions().get(interval).get(simulationState.getNodeId()).get(trip).intValue());
                return true;
            }
        }
        return false;
    }


    private boolean stayingDone(SimulationState simulationState){
        int stayingTime = agent.getStayingTime(simulationState);

        if (stayingTime != 0){
            simulationState.increaseTimeStamp(stayingTime);
            return true;
        }

        return false;
    }


    private boolean chargingDone(SimulationState simulationState){
        Integer chargingStation = agent.getChargingStation(simulationState);
        if (chargingStation != null){
            simulationState.increaseTimeStamp(DistanceGraphUtils.getTripTime(simulationState.getNodeId(), chargingStation));
            simulationState.increaseStateOfCharge(EnergyConsumptionEstimator.getActionEnergyConsumption(simulationState.getNodeId(), chargingStation));
            simulationState.setNodeId(domainGenerator.getEnvironment().getOsmGraph().getNode(chargingStation).getId());

            ChargingConnection connection = agent.chooseConnection(simulationState, ChargingStationReader.getChargingStation(simulationState.getNodeId()).getAvailableConnections());
            int chargingTime = agent.getChargingTime(simulationState);

            simulationState.increaseTimeStamp(chargingTime);
            simulationState.increaseStateOfCharge((int)(100 * ((chargingTime * connection.getPowerKW())/ Utils.BATTERY_CAPACITY)));

            return true;
        }
        return false;
    }

    private Integer tripToDestination(SimulationState simulationState){
        double pickUpProbability =
                domainGenerator.getParameterEstimator().getPickUpProbabilityInNode(simulationState.getNodeId(), simulationState.getTimeStamp());
        double randomNumber = Math.random();

        if (pickUpProbability < randomNumber){
            return chooseToNode(simulationState);
        } else {
            return null;
        }
    }


    private int chooseToNode(SimulationState simulationState){
        HashMap<Integer, Double> destinationProbabilities =
                domainGenerator.getParameterEstimator().getDestinationProbabilitiesInNode(simulationState.getNodeId(), simulationState.getTimeStamp());
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

}
