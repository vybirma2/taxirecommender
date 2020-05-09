package evaluation;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.actions.*;
import domain.charging.ChargingConnection;
import domain.charging.ChargingStation;
import domain.charging.ChargingStationReader;
import domain.charging.TripToChargingStation;
import domain.parameterestimation.TaxiTrip;
import domain.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class BaseMethodAgent extends Agent {

    private ArrayList<TaxiTrip> taxiTrips;

    private final int BATTERY_LOW_LEVEL_VALUE = 15;

    private int centreNode;
    private final HashMap<Integer, Integer> pickupsInNodes = new HashMap<>();
    private boolean inChargingStation = false;
    private boolean notChargingPreviously = false;


    private LinkedList<Integer> pathToCenterNode;

    public BaseMethodAgent(Graph<RoadNode, RoadEdge> osmGraph) {
        super(osmGraph);

        try {
            taxiTrips = Utils.DATA_SET_READER.readDataSet();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        init();
    }


    private void init(){
        int maxValue = 0;
        for (TaxiTrip trip : taxiTrips){
            if (pickupsInNodes.containsKey(trip.getFromOSMNode())){
                pickupsInNodes.replace(trip.getFromOSMNode(), pickupsInNodes.get(trip.getFromOSMNode()) + 1);
                if (maxValue < pickupsInNodes.get(trip.getFromOSMNode()) + 1){
                    maxValue = pickupsInNodes.get(trip.getFromOSMNode()) + 1;
                    centreNode = trip.getFromOSMNode();
                }
            } else {
                pickupsInNodes.put(trip.getFromOSMNode(), 1);
                if (maxValue < 1){
                    maxValue = 1;
                    centreNode = trip.getFromOSMNode();
                }
            }
        }
    }


    @Override
    public MeasurableAction getAction(SimulationState currentState) {
        if (inChargingStation && notChargingPreviously){
            return chooseCharging(currentState);
        } else if (inChargingStation) {
            return chooseFromChargingStation(currentState);
        } else if (currentState.getStateOfCharge() <= BATTERY_LOW_LEVEL_VALUE){
            return chooseGoingToCharging(currentState);
        } else if (currentState.getNodeId() == centreNode){
            return chooseStaying(currentState);
        } else {
            return chooseGoingToCenter(currentState);
        }
    }


    private MeasurableAction chooseGoingToCharging(SimulationState currentState){
        pathToCenterNode = null;
        TripToChargingStation tripToChargingStation = ChargingStationReader.getChargingStations().stream()
                .map(station -> new TripToChargingStation(currentState.getNodeId(), station.getRoadNode().getId())).min(Utils.tripToChargingStationComparator).get();
        inChargingStation = true;
        notChargingPreviously = true;
        return new GoingToChargingStationAction(ActionTypes.GOING_TO_CHARGING_STATION.getValue(), currentState.getNodeId(), tripToChargingStation.getChargingStation());
    }

    private MeasurableAction chooseCharging(SimulationState currentState){
        pathToCenterNode = null;
        ChargingStation chargingStation = ChargingStationReader.getChargingStation(currentState.getNodeId());
        if (chargingStation == null){
            System.out.println("ksdbf");
        }
        ArrayList<ChargingConnection> availableConnections = chargingStation.getAvailableConnections();
        double maxPowerKW = Integer.MIN_VALUE;
        ChargingConnection maxConnection = null;

        if (availableConnections.isEmpty()){
            throw new IllegalArgumentException("no connection i charging station");
        } else {
            for (ChargingConnection connection : availableConnections){
                if (connection.getPowerKW() > maxPowerKW){
                    maxPowerKW = connection.getPowerKW();
                    maxConnection = connection;
                }
            }
        }

        inChargingStation = true;
        notChargingPreviously = false;

        int timeToMax = timeToFullStateOfCharge(currentState, maxConnection);
        if (currentState.getTimeStamp() + timeToMax <= Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH){
            return new ChargingAction(ActionTypes.CHARGING_IN_CHARGING_STATION.getValue(),
                    currentState.getNodeId(), currentState.getNodeId(), timeToMax, maxConnection.getId());
        } else {
            return new ChargingAction(ActionTypes.CHARGING_IN_CHARGING_STATION.getValue(),
                    currentState.getNodeId(), currentState.getNodeId(),
                    Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH - currentState.getTimeStamp(), maxConnection.getId());
        }
    }

    private MeasurableAction chooseStaying(SimulationState currentState){
        inChargingStation = false;
        notChargingPreviously = false;
        return new StayingInLocationAction(ActionTypes.STAYING_IN_LOCATION.getValue(), currentState.getNodeId(),
                currentState.getNodeId(), Utils.SHIFT_LENGTH + Utils.SHIFT_START_TIME - currentState.getTimeStamp());
    }

    private MeasurableAction chooseFromChargingStation(SimulationState currentState){
        inChargingStation = false;
        notChargingPreviously = false;
        return new NextLocationAction(ActionTypes.TO_NEXT_LOCATION.getValue(), currentState.getNodeId(), centreNode);
    }


    private MeasurableAction chooseGoingToCenter(SimulationState currentState){
        inChargingStation = false;
        notChargingPreviously = false;
        MeasurableAction action = new NextLocationAction(ActionTypes.TO_NEXT_LOCATION.getValue(), currentState.getNodeId(), centreNode);
        if (actionApplicable(currentState, action)){
            return action;
        }
        return chooseGoingToCharging(currentState);
    }


    private int timeToFullStateOfCharge(SimulationState state, ChargingConnection connection){
        double currentStateOfChargeInKW = (state.getStateOfCharge()/100.) * Utils.BATTERY_CAPACITY;
        return (int)((Utils.BATTERY_CAPACITY - currentStateOfChargeInKW)/connection.getPowerKW()*60.);
    }

    @Override
    public boolean tripOffer(SimulationState currentState, SimulationTaxiTrip trip) {
        pathToCenterNode = null;
        MeasurableAction action = new PickUpPassengerAction(currentState.getNodeId(), trip.getDistance(),
                ActionTypes.PICK_UP_PASSENGER.getValue(), trip.getFromNode(), trip.getToNode(), (int) trip.getTripLength());
        return actionApplicable(currentState, action);
    }

    @Override
    public void resetAgent() {
        this.inChargingStation = false;
        this.notChargingPreviously = false;
    }


    private boolean actionApplicable(SimulationState currentState, MeasurableAction action){
        return currentState.getStateOfCharge() + action.getRestConsumption() > this.BATTERY_LOW_LEVEL_VALUE
                && currentState.getTimeStamp() + action.getTimeToFinish() <= Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH;
    }

    public void setInChargingStation(boolean inChargingStation) {
        this.inChargingStation = inChargingStation;
    }

    public void setNotChargingPreviously(boolean notChargingPreviously) {
        this.notChargingPreviously = notChargingPreviously;
    }
}
