package parameterestimation;

import domain.TaxiRecommenderDomain;
import utils.DistanceGraphUtils;

import java.io.Serializable;
import java.util.*;

import static parameterestimation.ParameterEstimationUtils.*;

/**
 * Class responsible for estimation of passenger pickup location parameters
 */
public class PassengerPickUpEstimator  implements Serializable {

    private HashMap<Integer, HashMap<Integer, Double>> passengerPickUpProbability;
    private Set<Integer> timeIntervals;
    private ArrayList<TaxiTrip> taxiTrips;


    public PassengerPickUpEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.taxiTrips = taxiTrips;
    }


    public HashMap<Integer, HashMap<Integer, Double>> estimatePickUpProbability(){
        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = getTimeSortedTrips(taxiTrips);
        HashMap<Integer, HashMap<Integer, Integer>> pickupsInNodes = getPickUpsInNodesInEstimationIntervals(timeSortedTaxiTrips);
        HashMap<Integer, HashMap<Integer, Integer>> dropOffsInNodes = getDropOffsInNodesInEstimationIntervals(timeSortedTaxiTrips);

        passengerPickUpProbability = getPassengerPickUpProbability(pickupsInNodes, dropOffsInNodes);
        timeIntervals = new HashSet<>(passengerPickUpProbability.keySet());

        return passengerPickUpProbability;
    }


    public HashMap<Integer, Double> estimatePickUpProbabilityComplete(){
        HashMap<Integer, Integer> pickupsInNodes = getPickUpsInNodes(taxiTrips);
        HashMap<Integer, Integer> dropOffsInNodes = getDropOffsInNodes(taxiTrips);

        return getPassengerPickUpProbabilityComplete(pickupsInNodes, dropOffsInNodes);
    }


    private HashMap<Integer, HashMap<Integer, Double>> getPassengerPickUpProbability(HashMap<Integer, HashMap<Integer, Integer>> pickupsInNodes,
                                                                                      HashMap<Integer, HashMap<Integer, Integer>> dropOffsInNodes){
        HashMap<Integer, HashMap<Integer, Double>> result = new  HashMap<>();

        for (Map.Entry<Integer, HashMap<Integer, Integer>> timeInterval : pickupsInNodes.entrySet()){
            HashMap<Integer, Double> nodeProbabilities =
                    getPassengerPickUpProbabilityComplete(timeInterval.getValue(), dropOffsInNodes.get(timeInterval.getKey()));
            result.put(timeInterval.getKey(), nodeProbabilities);
        }

        return result;
    }


    private HashMap<Integer, Double> getPassengerPickUpProbabilityComplete(HashMap<Integer, Integer> pickupsInNodes,
                                                                                     HashMap<Integer, Integer> dropOffsInNodes){
        HashMap<Integer, Double> result = new  HashMap<>();
        int allPickUps = 0;
        int sumInNeighboursPickUp = 0;



        for (Map.Entry<Integer, Integer> node : pickupsInNodes.entrySet()){
            allPickUps += node.getValue();
        }

        for (Map.Entry<Integer, Integer> node : pickupsInNodes.entrySet()){
            Set<Integer> neighbours = DistanceGraphUtils.getEnvironmentNeighbours(node.getKey());

            for (Integer neighbour : neighbours){
                sumInNeighboursPickUp += pickupsInNodes.getOrDefault(neighbour, 0);
            }

            result.put(node.getKey(), getProbability(node.getValue(), sumInNeighboursPickUp, allPickUps));
            sumInNeighboursPickUp = 0;

        }

        return result;
    }


    private Double getProbability(double numOfPickUps, double numOfPickUpsInNeighbours, int allPickups){
        return (numOfPickUps + numOfPickUpsInNeighbours)/(allPickups);
    }


    public HashMap<Integer, HashMap<Integer, Double>> getPassengerPickUpProbability() {
        return passengerPickUpProbability;
    }


    public Set<Integer> getTimeIntervals() {
        return timeIntervals;
    }
}
