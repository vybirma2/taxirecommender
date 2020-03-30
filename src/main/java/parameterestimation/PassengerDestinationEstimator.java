package parameterestimation;

import java.util.*;

import static parameterestimation.ParameterEstimationUtils.*;

/**
 * Class responsible for estimation of passenger destination parameters
 */
public class PassengerDestinationEstimator {

    private ArrayList<TaxiTrip> taxiTrips;


    public PassengerDestinationEstimator(ArrayList<TaxiTrip> taxiTrips) {
        this.taxiTrips = taxiTrips;
    }


    /**
     * @return probabilities of passenger commuting from some pickup node to some destination node in defined estimation
     * intervals
     */
    public HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> estimateDestinationProbability(){
        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = getTimeSortedTrips(taxiTrips);
        HashMap<Integer, HashMap<Integer, Integer>> pickupsInNodes = getPickUpsInNodesInEstimationIntervals(timeSortedTaxiTrips);
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> numberOfTripsToDestinationNodes
                = getNumberOfTripsToDestinationNodesPerInterval(timeSortedTaxiTrips);

        return getPassengerDestinationProbability(pickupsInNodes, numberOfTripsToDestinationNodes);
    }


    /**
     * @return probabilities of passenger commuting from some pickup node to some destination node during whole shift
     */
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
            HashMap<Integer, HashMap<Integer, Double>> timeIntervalProbabilities
                    = getPassengerDestinationProbabilityComplete(pickupsInOSMNodes.get(timeInterval.getKey()), timeInterval.getValue());

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
