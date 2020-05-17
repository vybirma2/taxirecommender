package evaluation;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.actions.ActionTypes;
import domain.actions.MeasurableAction;
import domain.actions.PickUpPassengerAction;
import domain.charging.ChargingStationReader;
import domain.parameterestimation.ParameterEstimationUtils;
import domain.parameterestimation.TaxiTrip;
import domain.utils.DistanceGraphUtils;
import domain.utils.GraphLoader;
import domain.utils.Utils;

import java.io.IOException;
import java.util.*;

import static domain.utils.Utils.*;

/**
 * Class implementing shift simulation for two agents
 * */
public class Simulation {

    private Agent agent;
    private SimulationState currentState;
    private HashMap<Integer, ArrayList<TaxiTrip>>taxiTrips;
    private Graph<RoadNode, RoadEdge> osmGraph;
    private double resultReward = 0;
    private MeasurableAction actionInProgress = null;
    private LinkedList<Integer> nodesOnPath = null;
    private SimulationStatistics simulationStatistics;
    private ChargingRecommenderAgent chargingRecommenderAgent;
    private BaseMethodAgent baseMethodAgent;


    public Simulation() {
    }

    /**
     * Method needed to call before simulation start. Initializing all essential parameters.
     * */
    public void initSimulation() {
        try {
            osmGraph = GraphLoader.loadGraph("data/graphs/" + INPUT_GRAPH_FILE_NAME);
            DistanceGraphUtils.setOsmGraph(osmGraph);
            initTripChoosing();
            currentState = new SimulationState(getRandomNode().getId(), SHIFT_START_TIME, STARTING_STATE_OF_CHARGE);
            String chargingStationsInputFileFullPath = "data/chargingstations/" + INPUT_STATION_FILE_NAME;
            ChargingStationReader.readChargingStations(chargingStationsInputFileFullPath, INPUT_STATION_FILE_NAME);
            simulationStatistics = new SimulationStatistics();
            baseMethodAgent = new BaseMethodAgent(osmGraph);
            chargingRecommenderAgent = new ChargingRecommenderAgent(osmGraph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Setting concrete agent to start.
     * */
    public void setAgent(String agentType) {
        if (agentType.equals("base")){
            this.agent = baseMethodAgent;
            this.agent.resetAgent();
        } else {
            this.agent = chargingRecommenderAgent;
            this.agent.resetAgent();
        }
    }

    public void clearShiftSimulationResults(){
        simulationStatistics.addRewardPerShift(resultReward);
        resultReward = 0;
        currentState = new SimulationState(getRandomNode().getId(), SHIFT_START_TIME, STARTING_STATE_OF_CHARGE);
        actionInProgress = null;
        nodesOnPath = null;
        agent.resetAgent();
    }

    public void clearStatistics(){
        simulationStatistics = new SimulationStatistics();
    }

    public void switchAgents(String agentType){
        setAgent(agentType);
        simulationStatistics = new SimulationStatistics();
    }

    /**
     * Starting simulation of one shift with parameters set above and in Utils class
     */
    public void startSimulation() {

        for (int timeStamp = SHIFT_START_TIME; timeStamp < SHIFT_START_TIME + SHIFT_LENGTH; timeStamp++){
            currentState.setTimeStamp(timeStamp);

            if (currentState.getStateOfCharge() < 0){
                return;
            }

            if (isActionInProgress()){
                doStepInActionInProgress();
            }

            if (!isActionInProgress()){
                if (actionInProgress != null){
                    finishActionInProgress();
                }
                startInProgressAction(agent.getAction(currentState));
            }

            if (tripOfferPossible()){
                tryToOfferTripIfAvailable();
            }
        }
    }

    public SimulationStatistics getSimulationStatistics() {
        return simulationStatistics;
    }

    private void initTripChoosing(){
        try {
            taxiTrips = ParameterEstimationUtils.getTimeSortedTrips(DATA_SET_READER.readDataSet());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private RoadNode getRandomNode(){
        Collections.shuffle(taxiTrips.get(690));
        return osmGraph.getNode(taxiTrips.get(690).get(0).getFromOSMNode());
    }

    private boolean tripOfferPossible(){
        return actionInProgress != null && actionInProgress.getActionId() != ActionTypes.PICK_UP_PASSENGER.getValue()
                && actionInProgress.getActionId() != ActionTypes.GOING_TO_CHARGING_STATION.getValue()
                && actionInProgress.getActionId() != ActionTypes.CHARGING_IN_CHARGING_STATION.getValue();
    }

    private void startInProgressAction(MeasurableAction action){
        actionInProgress = action;
        if (action.getActionId() == ActionTypes.PICK_UP_PASSENGER.getValue()){
            simulationStatistics.addTotalEnergyConsumed(action.getRestConsumption());
            simulationStatistics.addNumOfTripsDone(1);
            simulationStatistics.addRewardFromTrips(action.getReward());

            if (currentState.getNodeId() != action.getFromNodeId()){
                nodesOnPath = DistanceGraphUtils.aStar(currentState.getNodeId(), action.getFromNodeId());
                simulationStatistics.addDistanceToReachPassenger(DistanceGraphUtils.getDistanceSpeedPairOfPath(nodesOnPath).getDistance());
                LinkedList<Integer> tripNodes = DistanceGraphUtils.aStar(action.getFromNodeId(), action.getToNodeId());
                simulationStatistics.addDistanceWithPassenger(DistanceGraphUtils.getDistanceSpeedPairOfPath(tripNodes).getDistance());
                nodesOnPath.addAll(tripNodes);
            } else {
                nodesOnPath = DistanceGraphUtils.aStar(action.getFromNodeId(), action.getToNodeId());
            }
        } else if (action.getActionId() == ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()){
            simulationStatistics.addCostOfCharging(action.getReward());
            simulationStatistics.addTotalEnergyCharged(action.getRestConsumption());
            simulationStatistics.addTimeSpentCharging(action.getActionTime());
        } else {
            simulationStatistics.addTotalEnergyConsumed(action.getRestConsumption());
            nodesOnPath = DistanceGraphUtils.aStar(currentState.getNodeId(), action.getToNodeId());
        }

        simulationStatistics.addDistanceTransferred(DistanceGraphUtils.getDistanceSpeedPairOfPath(nodesOnPath).getDistance());
    }

    private void finishActionInProgress(){
        currentState.setNodeId(actionInProgress.getToNodeId());
        currentState.setStateOfCharge(currentState.getStateOfCharge() + actionInProgress.getRestConsumption());
        this.resultReward += actionInProgress.getReward();
    }

    private boolean isActionInProgress(){
        return actionInProgress != null && actionInProgress.getTimeToFinish() > 0;
    }

    private void doStepInActionInProgress(){
        actionInProgress.setTimeToFinish(actionInProgress.getTimeToFinish() - 1);
        double timeRatio = (actionInProgress.getActionTime() - actionInProgress.getTimeToFinish())/(double)actionInProgress.getActionTime();

        if (nodesOnPath != null && !nodesOnPath.isEmpty()){
            currentState.setNodeId(nodesOnPath.get((int) Math.round(timeRatio*(nodesOnPath.size() - 1))));
        }
        int consumption = (int)Math.round(timeRatio * actionInProgress.getRestConsumption());
        currentState.setStateOfCharge(currentState.getStateOfCharge() + consumption);
        actionInProgress.setRestConsumption(actionInProgress.getRestConsumption() - consumption);
    }

    private void tryToOfferTripIfAvailable() {
        SimulationTaxiTrip trip = availableTrip();

        if (trip != null) {
            if (agent.tripOffer(currentState, trip)) {
                startInProgressAction(new PickUpPassengerAction(currentState.getNodeId(),
                        new Double(trip.getDistance()).intValue(), ActionTypes.PICK_UP_PASSENGER.getValue(),
                        trip.getFromNode(), trip.getToNode(), (int) trip.getTripLength()));
            }
        }
    }

    private SimulationTaxiTrip availableTrip(){
        double randomNumber = Math.random();

        if (randomNumber < Utils.TRIP_OFFER_PROBABILITY){
            return chooseTrip();
        } else {
            return null;
        }
    }

    private SimulationTaxiTrip chooseTrip(){
        int intervalStart = DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp());
        ArrayList<TaxiTrip> trips = taxiTrips.get(intervalStart);
        Collections.shuffle(trips);
        TaxiTrip result = trips.get(0);

        if (result.getFromOSMNode() == result.getToOSMNode()){
            return null;
        }

        return new SimulationTaxiTrip(result.getDistance(), result.getTripLength(), result.getTripEnergyConsumption(),
                result.getFromOSMNode(), result.getToOSMNode());
    }
}
