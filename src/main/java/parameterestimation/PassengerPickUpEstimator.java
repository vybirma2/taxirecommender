package parameterestimation;

import utils.Utils;

import java.util.*;

import static utils.DistanceGraphUtils.getNeighbours;
import static utils.DistanceGraphUtils.getSurroundingNodesToLevel;

public class PassengerPickUpEstimator {

    private HashMap<Integer, HashMap<Integer, Integer>> passengerPickUpProbability;
    private Set<Integer> timeIntervals;
    private ArrayList<TaxiTrip> taxiTrips;

    public PassengerPickUpEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.taxiTrips = taxiTrips;
    }

    public HashMap<Integer, HashMap<Integer, Integer>> estimatePickUpProbability(){
        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = getTimeSortedTrips();
        HashMap<Integer, HashMap<Integer, Integer>> pickupsInOSMNodes = getPickUpsInOSMNodes(timeSortedTaxiTrips);
        HashMap<Integer, HashMap<Integer, Integer>> dropOffsInOSMNodes = getDropOffsInOSMNodes(timeSortedTaxiTrips);
        addNeighbourPickUps(pickupsInOSMNodes);
        addNeighbourDropOffs(dropOffsInOSMNodes);

        timeIntervals = passengerPickUpProbability.keySet();
        passengerPickUpProbability = getPassengerPickUpProbability(pickupsInOSMNodes, dropOffsInOSMNodes);
        return passengerPickUpProbability;
    }


    private HashMap<Integer, HashMap<Integer, Integer>> getPassengerPickUpProbability(HashMap<Integer, HashMap<Integer, Integer>> pickupsInOSMNodes,
                                                                                      HashMap<Integer, HashMap<Integer, Integer>> dropOffsInOSMNodes){
        HashMap<Integer, HashMap<Integer, Integer>> result = new  HashMap<>();

        for (Map.Entry<Integer, HashMap<Integer, Integer>> timeInterval : pickupsInOSMNodes.entrySet()){
            HashMap<Integer, Integer> nodeProbabilities = new HashMap<>();

            for (Map.Entry<Integer, Integer> node : timeInterval.getValue().entrySet()){
                nodeProbabilities.put(node.getKey(), getProbability(node.getValue(),
                        dropOffsInOSMNodes.get(timeInterval.getKey()).get(node.getKey())));
            }
            result.put(timeInterval.getKey(), nodeProbabilities);
        }

        return result;
    }


    private Integer getProbability(int numOfPickUps, int numOfDropOffs){
        return numOfPickUps/(numOfPickUps + numOfDropOffs);
    }


    private void addNeighbourPickUps(HashMap<Integer, HashMap<Integer, Integer>> pickupsInOSMNodes){

        for (Map.Entry<Integer, HashMap<Integer, Integer>> timeInterval : pickupsInOSMNodes.entrySet()){
            HashMap<Integer, Integer> summedPickups = new HashMap<>();

            for (Map.Entry<Integer, Integer> node : timeInterval.getValue().entrySet()){
                Set<Integer> neighbours = getNeighbours(node.getKey());
                int sumOfPickUps = node.getValue();

                for(Integer neighbour : neighbours){
                    if (timeInterval.getValue().containsKey(neighbour)){
                        sumOfPickUps += timeInterval.getValue().get(neighbour);
                    }
                }
                summedPickups.put(node.getKey(), sumOfPickUps);
            }
            pickupsInOSMNodes.replace(timeInterval.getKey(), summedPickups);
        }
    }


    private void addNeighbourDropOffs(HashMap<Integer, HashMap<Integer, Integer>> dropOffsInOSMNodes){

        for (Map.Entry<Integer, HashMap<Integer, Integer>> timeInterval : dropOffsInOSMNodes.entrySet()){
            HashMap<Integer, Integer> summedDropOffs = new HashMap<>();

            for (Map.Entry<Integer, Integer> node : timeInterval.getValue().entrySet()){
                Set<Integer> surroundingNodes = getSurroundingNodesToLevel(node.getKey(), ParameterEstimationUtils.NUM_OF_LEVELS_IN_DROP_OFF_SEARCH);
                int sumOfDropOffs = node.getValue();

                for(Integer neighbour : surroundingNodes){
                    if (timeInterval.getValue().containsKey(neighbour)){
                        sumOfDropOffs += timeInterval.getValue().get(neighbour);
                    }
                }
                summedDropOffs.put(node.getKey(), sumOfDropOffs);
            }
            dropOffsInOSMNodes.replace(timeInterval.getKey(), summedDropOffs);
        }
    }


    private HashMap<Integer, HashMap<Integer, Integer>> getPickUpsInOSMNodes(HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips){
        HashMap<Integer, HashMap<Integer, Integer>> pickupsInOSMNodes = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<TaxiTrip>> entry : timeSortedTaxiTrips.entrySet()){
            HashMap<Integer, Integer> intervalPickUps = new HashMap<>();

            for (TaxiTrip taxiTrip : entry.getValue()){
                if (intervalPickUps.containsKey(taxiTrip.getPickUpRoadNode().getId())){
                    intervalPickUps.replace(taxiTrip.getPickUpRoadNode().getId(), intervalPickUps.get(taxiTrip.getPickUpRoadNode().getId()) + 1 );
                } else {
                    intervalPickUps.put(taxiTrip.getPickUpRoadNode().getId(), 1);
                }
            }
            pickupsInOSMNodes.put(entry.getKey(), intervalPickUps);
        }

        return pickupsInOSMNodes;
    }


    private HashMap<Integer, HashMap<Integer, Integer>> getDropOffsInOSMNodes(HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips){
        HashMap<Integer, HashMap<Integer, Integer>> dropOffsInOSMNodes = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<TaxiTrip>> entry : timeSortedTaxiTrips.entrySet()){
            HashMap<Integer, Integer> intervalDropOffs = new HashMap<>();

            for (TaxiTrip taxiTrip : entry.getValue()){
                if (intervalDropOffs.containsKey(taxiTrip.getDestinationRoadNode().getId())){
                    intervalDropOffs.replace(taxiTrip.getDestinationRoadNode().getId(), intervalDropOffs.get(taxiTrip.getDestinationRoadNode().getId()) + 1 );
                } else {
                    intervalDropOffs.put(taxiTrip.getDestinationRoadNode().getId(), 1);
                }
            }
            dropOffsInOSMNodes.put(entry.getKey(), intervalDropOffs);
        }

        return dropOffsInOSMNodes;
    }


    private HashMap<Integer, ArrayList<TaxiTrip>> getTimeSortedTrips(){
        int estimationTime = Utils.SHIFT_START_TIME;
        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = new HashMap<>();
        ArrayList<TaxiTrip> trips = new ArrayList<>();

        Collections.sort(taxiTrips);

        for (TaxiTrip taxiTrip : taxiTrips){
            if (beforeTripTime(taxiTrip, Utils.SHIFT_START_TIME)){
                continue;
            } else if (afterTripTime(taxiTrip, estimationTime) && beforeTripTime(taxiTrip, estimationTime + Utils.ESTIMATION_EPISODE_LENGTH)){
                trips.add(taxiTrip);
            } else {
                timeSortedTaxiTrips.put(estimationTime, trips);
                trips = new ArrayList<>();
                estimationTime += Utils.ESTIMATION_EPISODE_LENGTH;
                if (estimationTime >= Utils.SHIFT_LENGTH + Utils.SHIFT_START_TIME){
                    return timeSortedTaxiTrips;
                }
                trips.add(taxiTrip);
            }
            if (afterTripTime(taxiTrip, Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH + 1)) {
                break;
            }
        }

        return timeSortedTaxiTrips;
    }


    private boolean beforeTripTime(TaxiTrip taxiTrip, double time){
        return taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes() <= time;
    }


    private boolean afterTripTime(TaxiTrip taxiTrip, double time){
        return taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes() >= time;
    }

    public HashMap<Integer, HashMap<Integer, Integer>> getPassengerPickUpProbability() {
        return passengerPickUpProbability;
    }

    public Set<Integer> getTimeIntervals() {
        return timeIntervals;
    }
}
