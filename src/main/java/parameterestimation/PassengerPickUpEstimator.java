package parameterestimation;

import utils.Utils;

import java.util.*;

import static parameterestimation.ParameterEstimationUtils.*;
import static utils.DistanceGraphUtils.getNeighbours;
import static utils.DistanceGraphUtils.getSurroundingNodesToLevel;

public class PassengerPickUpEstimator {

    private HashMap<Integer, HashMap<Integer, Double>> passengerPickUpProbability;
    private Set<Integer> timeIntervals;
    private ArrayList<TaxiTrip> taxiTrips;

    public PassengerPickUpEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.taxiTrips = taxiTrips;
    }


    public HashMap<Integer, HashMap<Integer, Double>> estimatePickUpProbability(){
        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = getTimeSortedTrips(taxiTrips);
        HashMap<Integer, HashMap<Integer, Integer>> pickupsInOSMNodes = getPickUpsInOSMNodes(timeSortedTaxiTrips);
        HashMap<Integer, HashMap<Integer, Integer>> dropOffsInOSMNodes = getDropOffsInOSMNodes(timeSortedTaxiTrips);

        addSurroundingNodeActions(pickupsInOSMNodes);
        addSurroundingNodeActions(dropOffsInOSMNodes);


        passengerPickUpProbability = getPassengerPickUpProbability(pickupsInOSMNodes, dropOffsInOSMNodes);
        timeIntervals = passengerPickUpProbability.keySet();

        return passengerPickUpProbability;
    }


    private HashMap<Integer, HashMap<Integer, Double>> getPassengerPickUpProbability(HashMap<Integer, HashMap<Integer, Integer>> pickupsInOSMNodes,
                                                                                      HashMap<Integer, HashMap<Integer, Integer>> dropOffsInOSMNodes){
        HashMap<Integer, HashMap<Integer, Double>> result = new  HashMap<>();

        for (Map.Entry<Integer, HashMap<Integer, Integer>> timeInterval : pickupsInOSMNodes.entrySet()){
            HashMap<Integer, Double> nodeProbabilities = new HashMap<>();

            for (Map.Entry<Integer, Integer> node : timeInterval.getValue().entrySet()){

                nodeProbabilities.put(node.getKey(), getProbability(node.getValue(),
                        dropOffsInOSMNodes.get(timeInterval.getKey()).getOrDefault(node.getKey(), 0)));

            }
            result.put(timeInterval.getKey(), nodeProbabilities);
        }

        return result;
    }


    private Double getProbability(double numOfPickUps, double numOfDropOffs){
        return numOfPickUps/(numOfPickUps + numOfDropOffs);
    }


    public HashMap<Integer, HashMap<Integer, Double>> getPassengerPickUpProbability() {
        return passengerPickUpProbability;
    }

    public Set<Integer> getTimeIntervals() {
        return timeIntervals;
    }
}
