package parameterestimation;

import java.util.*;

import static parameterestimation.ParameterEstimationUtils.*;

public class PassengerDestinationEstimator {

    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> passengerDestinationProbability;
    private ArrayList<TaxiTrip> taxiTrips;


    public PassengerDestinationEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.taxiTrips = taxiTrips;
    }


    public HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> estimateDestinationProbability(){
        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = getTimeSortedTrips(taxiTrips);
        HashMap<Integer, HashMap<Integer, Integer>> pickupsInNodes = getPickUpsInNodes(timeSortedTaxiTrips);
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> numberOfTripsToDestinationNodes
                = getNumberOfTripsToDestinationNodes(timeSortedTaxiTrips);

        //addSurroundingNodeActions(pickupsInNodes);
        //addSurroundingsNodesTripsToDestination(numberOfTripsToDestinationNodes);

        passengerDestinationProbability = getPassengerDestinationProbability(pickupsInNodes, numberOfTripsToDestinationNodes);

        return passengerDestinationProbability;
    }


    public HashMap<Integer, HashMap<Integer, Double>> estimateDestinationProbabilityComplete(){
        HashMap<Integer, Integer> pickupsInNodes = getPickUpsInNodes(taxiTrips);
        HashMap<Integer, HashMap<Integer, Integer>> numberOfTripsToDestinationNodes = getNumberOfTripsToDestinationNodes(taxiTrips);


        return getPassengerDestinationProbabilityComplete(pickupsInNodes, numberOfTripsToDestinationNodes);
    }



    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> getPassengerDestinationProbability(
            HashMap<Integer, HashMap<Integer, Integer>> pickupsInOSMNodes,
            HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> numberOfTripsToDestinationNodes
    ){
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> result = new HashMap<>();

        for (Map.Entry<Integer, HashMap<Integer, HashMap<Integer, Integer>>> timeInterval : numberOfTripsToDestinationNodes.entrySet()){
            HashMap<Integer, HashMap<Integer, Double>> timeIntervalProbabilities = new HashMap<>();

            for (Map.Entry<Integer, HashMap<Integer, Integer>> node : timeInterval.getValue().entrySet()){
                HashMap<Integer, Double> nodeProbabilities = new HashMap<>();

                for (Map.Entry<Integer, Integer> entry : node.getValue().entrySet()){
                    nodeProbabilities.put(entry.getKey(), getProbability(entry.getValue(),
                            pickupsInOSMNodes.get(timeInterval.getKey()).get(node.getKey())));
                }

                timeIntervalProbabilities.put(node.getKey(), nodeProbabilities);
            }

            result.put(timeInterval.getKey(), timeIntervalProbabilities);
        }
        return result;
    }




    private HashMap<Integer, HashMap<Integer, Double>> getPassengerDestinationProbabilityComplete(
            HashMap<Integer, Integer> pickupsInOSMNodes, HashMap<Integer, HashMap<Integer, Integer>> numberOfTripsToDestinationNodes
    ){
        HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<>();

        for (Map.Entry<Integer, HashMap<Integer, Integer>> node : numberOfTripsToDestinationNodes.entrySet()){
            HashMap<Integer, Double> nodeProbabilities = new HashMap<>();

            for (Map.Entry<Integer, Integer> entry : node.getValue().entrySet()){
                nodeProbabilities.put(entry.getKey(), getProbability(entry.getValue(),
                        pickupsInOSMNodes.get(node.getKey())));
            }

            result.put(node.getKey(), nodeProbabilities);
        }

        return result;
    }



    private Double getProbability(double numOfTripsToDestination, double numOfAllTripsFromNode){
        return numOfTripsToDestination/numOfAllTripsFromNode;
    }
}
