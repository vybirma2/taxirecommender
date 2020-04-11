package parameterestimation;

import utils.Utils;

import java.util.*;

public class ParameterEstimationUtils {


    /**
     * @param timeSortedTaxiTrips taxi trips sorted into defined estimation intervals
     * @return number of trips from to some node sorted in estimation intervals
     */
    public static HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> getNumberOfTripsToDestinationNodesPerInterval(
            HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips){

        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> tripsToDestinationNode = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<TaxiTrip>> entry : timeSortedTaxiTrips.entrySet()){
            tripsToDestinationNode.put(entry.getKey(), getNumberOfTripsToDestinationNodes(entry.getValue()));
        }

        return tripsToDestinationNode;
    }


    /**
     * @param taxiTrips
     * @return number of trips from starting to some destination node
     */
    public static HashMap<Integer, HashMap<Integer, Integer>> getNumberOfTripsToDestinationNodes(ArrayList<TaxiTrip> taxiTrips){
        HashMap<Integer, HashMap<Integer, Integer>> tripsToDestinationNode = new HashMap<>();

        for (TaxiTrip taxiTrip : taxiTrips){
            if (tripsToDestinationNode.containsKey(taxiTrip.getFromEnvironmentNode().getNodeId())){
                HashMap<Integer, Integer> tripsFromNode = tripsToDestinationNode.get(taxiTrip.getFromEnvironmentNode().getNodeId());
                if (tripsFromNode.containsKey(taxiTrip.getToEnvironmentNode().getNodeId())){
                    tripsFromNode.replace(taxiTrip.getToEnvironmentNode().getNodeId(),
                            tripsFromNode.get(taxiTrip.getToEnvironmentNode().getNodeId()) + 1);
                } else {
                    tripsFromNode.put(taxiTrip.getToEnvironmentNode().getNodeId(), 1);
                }
            } else {
                HashMap<Integer, Integer> toNodeTrips = new HashMap<>();
                toNodeTrips.put(taxiTrip.getToEnvironmentNode().getNodeId(), 1);
                tripsToDestinationNode.put(taxiTrip.getFromEnvironmentNode().getNodeId(), toNodeTrips);
            }
        }

        return tripsToDestinationNode;
    }


    /**
     * @param timeSortedTaxiTrips
     * @return number of pickups in given nodes in estimation time intervals
     */
    public static HashMap<Integer, HashMap<Integer, Integer>> getPickUpsInNodesInEstimationIntervals(
            HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips){

        HashMap<Integer, HashMap<Integer, Integer>> pickupsInOSMNodes = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<TaxiTrip>> entry : timeSortedTaxiTrips.entrySet()){
            pickupsInOSMNodes.put(entry.getKey(), getPickUpsInNodes(entry.getValue()));
        }

        return pickupsInOSMNodes;
    }


    /**
     * @param taxiTrips
     * @return number of pickups in given nodes in one estimation time interval
     */
    public static HashMap<Integer, Integer> getPickUpsInNodes(ArrayList<TaxiTrip> taxiTrips){
       HashMap<Integer, Integer> pickupsInOSMNodes = new HashMap<>();

        for (TaxiTrip taxiTrip : taxiTrips){
            if (pickupsInOSMNodes.containsKey(taxiTrip.getFromEnvironmentNode().getNodeId())){
                pickupsInOSMNodes.replace(taxiTrip.getFromEnvironmentNode().getNodeId(),
                        pickupsInOSMNodes.get(taxiTrip.getFromEnvironmentNode().getNodeId()) + 1 );
            } else {
                pickupsInOSMNodes.put(taxiTrip.getFromEnvironmentNode().getNodeId(), 1);
            }
        }

        return pickupsInOSMNodes;
    }


    /**
     * @param taxiTrips
     * @return number of drop offs in given nodes in one estimation interval
     */
    public static HashMap<Integer, Integer> getDropOffsInNodes(ArrayList<TaxiTrip> taxiTrips){
        HashMap<Integer, Integer> dropOffsInOSMNodes = new HashMap<>();

        for (TaxiTrip taxiTrip : taxiTrips){
            if (dropOffsInOSMNodes.containsKey(taxiTrip.getToEnvironmentNode().getNodeId())){
                dropOffsInOSMNodes.replace(taxiTrip.getToEnvironmentNode().getNodeId(),
                        dropOffsInOSMNodes.get(taxiTrip.getToEnvironmentNode().getNodeId()) + 1 );
            } else {
                dropOffsInOSMNodes.put(taxiTrip.getToEnvironmentNode().getNodeId(), 1);
            }
        }

        return dropOffsInOSMNodes;
    }


    /**
     * @param timeSortedTaxiTrips
     * @return number of drop offs in given nodes in estimation intervals
     */
    public static HashMap<Integer, HashMap<Integer, Integer>> getDropOffsInNodesInEstimationIntervals(HashMap<Integer,
            ArrayList<TaxiTrip>> timeSortedTaxiTrips){

        HashMap<Integer, HashMap<Integer, Integer>> dropOffsInOSMNodes = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<TaxiTrip>> entry : timeSortedTaxiTrips.entrySet()){
            dropOffsInOSMNodes.put(entry.getKey(), getDropOffsInNodes(entry.getValue()));
        }

        return dropOffsInOSMNodes;
    }


    /**
     * @param taxiTrips
     * @return taxi trips sorted into estimation intervals according to the estimation_interval_length
     */
    public static HashMap<Integer, ArrayList<TaxiTrip>> getTimeSortedTrips(ArrayList<TaxiTrip> taxiTrips){
        int estimationTime = Utils.SHIFT_START_TIME;
        HashMap<Integer, ArrayList<TaxiTrip>> timeSortedTaxiTrips = new HashMap<>();
        ArrayList<TaxiTrip> trips = new ArrayList<>();

        Collections.sort(taxiTrips);

        for (TaxiTrip taxiTrip : taxiTrips){
            if (beforeTripTime(taxiTrip, estimationTime)){
                continue;
            } else if (afterTripTime(taxiTrip, estimationTime) && beforeTripTime(taxiTrip,
                    estimationTime + Utils.ESTIMATION_EPISODE_LENGTH)){
                trips.add(taxiTrip);
            } else {
                if (!trips.isEmpty()){
                    timeSortedTaxiTrips.put(estimationTime, trips);
                }

                trips = new ArrayList<>();
                estimationTime += Utils.ESTIMATION_EPISODE_LENGTH;
                if (estimationTime >= Utils.SHIFT_LENGTH + Utils.SHIFT_START_TIME){
                    return timeSortedTaxiTrips;
                }

                if (afterTripTime(taxiTrip, estimationTime) && beforeTripTime(taxiTrip,
                        estimationTime + Utils.ESTIMATION_EPISODE_LENGTH)){
                    trips.add(taxiTrip);
                }

            }
            if (afterTripTime(taxiTrip, Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH)) {
                break;
            }
        }

        return timeSortedTaxiTrips;
    }


    public static boolean beforeTripTime(TaxiTrip taxiTrip, int time){
        return taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes() < time;
    }


    public static boolean afterTripTime(TaxiTrip taxiTrip, int time){
        return taxiTrip.getStartDate().getHours() * 60 + taxiTrip.getStartDate().getMinutes() >= time;
    }
}
