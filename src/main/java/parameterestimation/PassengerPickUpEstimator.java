package parameterestimation;

import java.util.*;

import static parameterestimation.ParameterEstimationUtils.*;

public class PassengerPickUpEstimator {

    private HashMap<Integer, HashMap<Integer, Double>> passengerPickUpProbability;
    private Set<Integer> timeIntervals;
    private ArrayList<TaxiTrip> taxiTrips;

    public PassengerPickUpEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.taxiTrips = taxiTrips;
    }


    public HashMap<Integer, HashMap<Integer, Double>> estimatePickUpProbability(){
        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = getTimeSortedTrips(taxiTrips);
        HashMap<Integer, HashMap<Integer, Integer>> pickupsInNodes = getPickUpsInNodes(timeSortedTaxiTrips);
        HashMap<Integer, HashMap<Integer, Integer>> dropOffsInNodes = getDropOffsInNodes(timeSortedTaxiTrips);


        passengerPickUpProbability = getPassengerPickUpProbability(pickupsInNodes, dropOffsInNodes);
        timeIntervals = passengerPickUpProbability.keySet();

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
            HashMap<Integer, Double> nodeProbabilities = new HashMap<>();

            for (Map.Entry<Integer, Integer> node : timeInterval.getValue().entrySet()){

                nodeProbabilities.put(node.getKey(), getProbability(node.getValue(),
                        dropOffsInNodes.get(timeInterval.getKey()).getOrDefault(node.getKey(), 0)));

            }
            result.put(timeInterval.getKey(), nodeProbabilities);
        }

        return result;
    }


    private HashMap<Integer, Double> getPassengerPickUpProbabilityComplete(HashMap<Integer, Integer> pickupsInNodes,
                                                                                     HashMap<Integer, Integer> dropOffsInNodes){
        HashMap<Integer, Double> result = new  HashMap<>();

        for (Map.Entry<Integer, Integer> node : pickupsInNodes.entrySet()){

            result.put(node.getKey(), getProbability(node.getValue(),
                    dropOffsInNodes.getOrDefault(node.getKey(), 0)));

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
